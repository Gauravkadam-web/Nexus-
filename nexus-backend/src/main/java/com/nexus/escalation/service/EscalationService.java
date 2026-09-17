package com.nexus.escalation.service;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.entity.Priority;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.casemanagement.statemachine.CaseLifecycleService;
import com.nexus.common.exception.BadRequestException;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.escalation.dto.CreateEscalationRuleRequest;
import com.nexus.escalation.dto.EscalateCaseRequest;
import com.nexus.escalation.dto.EscalationResponse;
import com.nexus.escalation.dto.EscalationRuleResponse;
import com.nexus.escalation.entity.*;
import com.nexus.escalation.repository.EscalationRepository;
import com.nexus.escalation.repository.EscalationRuleRepository;
import com.nexus.notification.entity.NotificationType;
import com.nexus.notification.service.NotificationService;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.repository.CategoryRepository;
import com.nexus.organization.repository.OrganizationRepository;
import com.nexus.sla.entity.CaseRisk;
import com.nexus.sla.entity.CaseSla;
import com.nexus.sla.entity.SlaStatus;
import com.nexus.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EscalationService {

    private static final Logger log = LoggerFactory.getLogger(EscalationService.class);

    private final EscalationRepository escalationRepository;
    private final EscalationRuleRepository escalationRuleRepository;
    private final OrganizationRepository organizationRepository;
    private final CategoryRepository categoryRepository;
    private final CaseRepository caseRepository;
    private final CaseLifecycleService caseLifecycleService;
    private final NotificationService notificationService;

    public EscalationService(EscalationRepository escalationRepository,
                             EscalationRuleRepository escalationRuleRepository,
                             OrganizationRepository organizationRepository,
                             CategoryRepository categoryRepository,
                             CaseRepository caseRepository,
                             CaseLifecycleService caseLifecycleService,
                             NotificationService notificationService) {
        this.escalationRepository = escalationRepository;
        this.escalationRuleRepository = escalationRuleRepository;
        this.organizationRepository = organizationRepository;
        this.categoryRepository = categoryRepository;
        this.caseRepository = caseRepository;
        this.caseLifecycleService = caseLifecycleService;
        this.notificationService = notificationService;
    }

    /**
     * Creates an admin-configured escalation rule.
     */
    public EscalationRuleResponse createEscalationRule(CreateEscalationRuleRequest req, UUID orgId, User creator) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", orgId));

        Category category = null;
        if (req.getCategoryId() != null) {
            category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", req.getCategoryId()));
        }

        EscalationRule rule = new EscalationRule(org, req.getName(), req.getConditionType(),
                req.getConditionConfig(), req.getEscalationLevel(), category, req.isActive(), creator);
        EscalationRule saved = escalationRuleRepository.save(rule);
        log.info("Created Escalation Rule [{}] for Org [{}]", saved.getName(), orgId);
        return EscalationRuleResponse.fromEntity(saved);
    }

    /**
     * Updates an existing escalation rule.
     */
    public EscalationRuleResponse updateEscalationRule(UUID id, CreateEscalationRuleRequest req, UUID orgId) {
        EscalationRule rule = escalationRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EscalationRule", "id", id));

        if (!rule.getOrganization().getId().equals(orgId)) {
            throw new BadRequestException("Escalation rule does not belong to your organization");
        }

        rule.setName(req.getName());
        rule.setConditionType(req.getConditionType());
        rule.setConditionConfig(req.getConditionConfig());
        rule.setEscalationLevel(req.getEscalationLevel());
        rule.setActive(req.isActive());

        if (req.getCategoryId() != null) {
            Category cat = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", req.getCategoryId()));
            rule.setCategory(cat);
        } else {
            rule.setCategory(null);
        }

        EscalationRule saved = escalationRuleRepository.save(rule);
        return EscalationRuleResponse.fromEntity(saved);
    }

    /**
     * Deletes an escalation rule.
     */
    public void deleteEscalationRule(UUID id, UUID orgId) {
        EscalationRule rule = escalationRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EscalationRule", "id", id));

        if (!rule.getOrganization().getId().equals(orgId)) {
            throw new BadRequestException("Escalation rule does not belong to your organization");
        }

        escalationRuleRepository.delete(rule);
    }

    /**
     * Lists all escalation rules for an organization.
     */
    @Transactional(readOnly = true)
    public List<EscalationRuleResponse> getEscalationRules(UUID orgId) {
        return escalationRuleRepository.findByOrganizationId(orgId).stream()
                .map(EscalationRuleResponse::fromEntity)
                .toList();
    }

    /**
     * Manually escalates a case with human confirmation.
     */
    public EscalationResponse escalateCase(UUID caseId, EscalateCaseRequest req, User user) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId));

        EscalationRule rule = null;
        if (req.getEscalationRuleId() != null) {
            rule = escalationRuleRepository.findById(req.getEscalationRuleId()).orElse(null);
        }

        EscalationLevel level = req.getEscalationLevel() != null ? req.getEscalationLevel() : EscalationLevel.TEAM_LEAD;

        Escalation escalation = new Escalation(
                caseEntity,
                rule,
                level,
                req.getReason(),
                EscalationStatus.CONFIRMED,
                TriggeredBy.USER,
                user
        );

        Escalation saved = escalationRepository.save(escalation);

        // Transition case status to ESCALATED
        if (caseLifecycleService.isValidTransition(caseEntity.getStatus(), CaseStatus.ESCALATED)) {
            caseEntity.setStatus(CaseStatus.ESCALATED);
            caseRepository.save(caseEntity);
        }

        // Notify appropriate staff
        String notifTitle = String.format("Case %s Escalated to %s", caseEntity.getCaseNumber(), level);
        String notifMessage = String.format("Case %s has been escalated by %s: %s",
                caseEntity.getCaseNumber(), user != null ? user.getName() : "System", req.getReason());

        if (caseEntity.getAssignedUser() != null) {
            notificationService.createNotification(caseEntity.getAssignedUser(), caseEntity,
                    NotificationType.ESCALATION, notifTitle, notifMessage);
        }

        log.info("Case [{}] escalated to [{}] by user [{}]", caseEntity.getCaseNumber(), level, user != null ? user.getEmail() : "N/A");
        return EscalationResponse.fromEntity(saved);
    }

    /**
     * Confirms a previously RECOMMENDED system escalation.
     */
    public EscalationResponse confirmRecommendedEscalation(UUID escalationId, User user) {
        Escalation escalation = escalationRepository.findById(escalationId)
                .orElseThrow(() -> new ResourceNotFoundException("Escalation", "id", escalationId));

        escalation.setStatus(EscalationStatus.CONFIRMED);
        escalation.setConfirmedBy(user);
        Escalation saved = escalationRepository.save(escalation);

        Case caseEntity = escalation.getCaseEntity();
        if (caseLifecycleService.isValidTransition(caseEntity.getStatus(), CaseStatus.ESCALATED)) {
            caseEntity.setStatus(CaseStatus.ESCALATED);
            caseRepository.save(caseEntity);
        }

        String notifTitle = String.format("Escalation Confirmed for Case %s", caseEntity.getCaseNumber());
        String notifMessage = String.format("Escalation for case %s confirmed by %s (%s)",
                caseEntity.getCaseNumber(), user.getName(), escalation.getReason());

        if (caseEntity.getAssignedUser() != null) {
            notificationService.createNotification(caseEntity.getAssignedUser(), caseEntity,
                    NotificationType.ESCALATION, notifTitle, notifMessage);
        }

        return EscalationResponse.fromEntity(saved);
    }

    /**
     * Evaluates configured escalation rules against a case and records recommended escalations.
     */
    public void evaluateEscalationRulesForCase(Case caseEntity, CaseSla caseSla, CaseRisk caseRisk) {
        if (caseEntity == null || caseEntity.getStatus() == CaseStatus.CLOSED || caseEntity.getStatus() == CaseStatus.CANCELLED) {
            return;
        }

        UUID orgId = caseEntity.getCategory() != null && caseEntity.getCategory().getOrganization() != null
                ? caseEntity.getCategory().getOrganization().getId()
                : (caseEntity.getRequester() != null && caseEntity.getRequester().getOrganization() != null
                ? caseEntity.getRequester().getOrganization().getId() : null);

        if (orgId == null) {
            return;
        }

        List<EscalationRule> activeRules = escalationRuleRepository.findByOrganizationIdAndActiveTrue(orgId);
        for (EscalationRule rule : activeRules) {
            // Check category match if rule is category-scoped
            if (rule.getCategory() != null && (caseEntity.getCategory() == null || !rule.getCategory().getId().equals(caseEntity.getCategory().getId()))) {
                continue;
            }

            boolean triggered = false;
            String reason = null;

            switch (rule.getConditionType()) {
                case SLA_BREACHED -> {
                    if (caseSla != null && caseSla.getStatus() == SlaStatus.BREACHED) {
                        triggered = true;
                        reason = "SLA resolution deadline breached";
                    }
                }
                case SLA_APPROACHING -> {
                    if (caseSla != null && caseSla.getStatus() == SlaStatus.AT_RISK) {
                        triggered = true;
                        reason = "SLA deadline approaching threshold";
                    }
                }
                case HIGH_IMPACT_INCIDENT -> {
                    if (caseEntity.getPriority() == Priority.URGENT) {
                        triggered = true;
                        reason = "High-impact urgent priority incident";
                    }
                }
                default -> {
                    // Other condition types
                }
            }

            if (triggered) {
                boolean alreadyExists = escalationRepository.existsByCaseEntityIdAndEscalationRuleIdAndStatus(
                        caseEntity.getId(), rule.getId(), EscalationStatus.RECOMMENDED);

                if (!alreadyExists) {
                    Escalation escalation = new Escalation(
                            caseEntity,
                            rule,
                            rule.getEscalationLevel(),
                            "Automated Rule [" + rule.getName() + "] triggered: " + reason,
                            EscalationStatus.RECOMMENDED,
                            TriggeredBy.SYSTEM,
                            null
                    );
                    escalationRepository.save(escalation);

                    // Notify assigned operator/team lead of recommendation
                    if (caseEntity.getAssignedUser() != null) {
                        notificationService.createNotification(
                                caseEntity.getAssignedUser(),
                                caseEntity,
                                NotificationType.ESCALATION,
                                "Recommended Escalation for " + caseEntity.getCaseNumber(),
                                "Rule [" + rule.getName() + "] recommends escalation to " + rule.getEscalationLevel()
                        );
                    }
                    log.info("Recommended escalation generated for case [{}] via rule [{}]",
                            caseEntity.getCaseNumber(), rule.getName());
                }
            }
        }
    }

    /**
     * Lists all escalations for an organization.
     */
    @Transactional(readOnly = true)
    public Page<EscalationResponse> getEscalations(UUID orgId, Pageable pageable) {
        return escalationRepository.findByOrganizationId(orgId, pageable)
                .map(EscalationResponse::fromEntity);
    }
}
