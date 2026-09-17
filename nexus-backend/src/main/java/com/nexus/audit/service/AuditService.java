package com.nexus.audit.service;

import com.nexus.audit.dto.AuditFilterRequest;
import com.nexus.audit.dto.AuditLogResponse;
import com.nexus.audit.entity.AuditLog;
import com.nexus.audit.repository.AuditLogRepository;
import com.nexus.user.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service managing immutable audit logs (US-33).
 * Operates append-only — records all state transitions, assignments, and governance events.
 */
@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Appends an immutable audit record to the governance trail.
     * Uses REQUIRES_NEW to ensure audit records persist even if calling transaction encounters downstream exceptions.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditLog logEvent(String entityType, UUID entityId, String action, User actor,
                             String oldValue, String newValue, String source, String ipAddress) {
        String actorName = actor != null ? actor.getName() : "System";
        String actorRole = actor != null && !actor.getRoles().isEmpty() 
                ? actor.getRoles().iterator().next().getName().name() 
                : "SYSTEM";

        AuditLog auditLog = new AuditLog(
                entityType,
                entityId,
                action,
                actor,
                actorName,
                actorRole,
                oldValue,
                newValue,
                source != null ? source : "USER",
                ipAddress
        );

        AuditLog saved = auditLogRepository.save(auditLog);
        log.info("[AUDIT] Action [{}] recorded on [{}:{}] by [{}] ({})",
                action, entityType, entityId, actorName, actorRole);
        return saved;
    }

    /**
     * Gets paginated audit trail for a specific case.
     */
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getCaseAuditTrail(UUID caseId, Pageable pageable) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc("CASE", caseId, pageable)
                .map(AuditLogResponse::fromEntity);
    }

    /**
     * Gets complete chronological timeline of audit entries for a case.
     */
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getCaseAuditTimeline(UUID caseId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtAsc("CASE", caseId)
                .stream()
                .map(AuditLogResponse::fromEntity)
                .toList();
    }

    /**
     * Searches and filters organization audit logs (Admin query).
     */
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> searchAuditLogs(AuditFilterRequest filter, Pageable pageable) {
        Specification<AuditLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getEntityType() != null && !filter.getEntityType().isBlank()) {
                predicates.add(cb.equal(root.get("entityType"), filter.getEntityType()));
            }
            if (filter.getEntityId() != null) {
                predicates.add(cb.equal(root.get("entityId"), filter.getEntityId()));
            }
            if (filter.getAction() != null && !filter.getAction().isBlank()) {
                predicates.add(cb.equal(root.get("action"), filter.getAction()));
            }
            if (filter.getActorId() != null) {
                predicates.add(cb.equal(root.get("actor").get("id"), filter.getActorId()));
            }
            if (filter.getSource() != null && !filter.getSource().isBlank()) {
                predicates.add(cb.equal(root.get("source"), filter.getSource()));
            }
            if (filter.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getStartDate()));
            }
            if (filter.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getEndDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return auditLogRepository.findAll(spec, pageable).map(AuditLogResponse::fromEntity);
    }
}
