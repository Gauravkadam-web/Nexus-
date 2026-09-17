package com.nexus.sla.service;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.Priority;
import com.nexus.common.exception.BadRequestException;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.repository.CategoryRepository;
import com.nexus.organization.repository.OrganizationRepository;
import com.nexus.sla.dto.BreachedCaseResponse;
import com.nexus.sla.dto.CaseSlaResponse;
import com.nexus.sla.dto.CreateSlaPolicyRequest;
import com.nexus.sla.dto.SlaPolicyResponse;
import com.nexus.sla.entity.*;
import com.nexus.sla.repository.CaseSlaRepository;
import com.nexus.sla.repository.SlaPolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class SlaService {

    private static final Logger log = LoggerFactory.getLogger(SlaService.class);

    private final SlaPolicyRepository slaPolicyRepository;
    private final CaseSlaRepository caseSlaRepository;
    private final OrganizationRepository organizationRepository;
    private final CategoryRepository categoryRepository;
    private final CaseRiskService caseRiskService;

    public SlaService(SlaPolicyRepository slaPolicyRepository,
                      CaseSlaRepository caseSlaRepository,
                      OrganizationRepository organizationRepository,
                      CategoryRepository categoryRepository,
                      CaseRiskService caseRiskService) {
        this.slaPolicyRepository = slaPolicyRepository;
        this.caseSlaRepository = caseSlaRepository;
        this.organizationRepository = organizationRepository;
        this.categoryRepository = categoryRepository;
        this.caseRiskService = caseRiskService;
    }

    /**
     * Creates a new SLA policy for an organization.
     */
    public SlaPolicyResponse createSlaPolicy(CreateSlaPolicyRequest req, UUID orgId) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", orgId));

        Category category = null;
        if (req.getCategoryId() != null) {
            category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", req.getCategoryId()));
        }

        SlaPolicy policy = new SlaPolicy(org, category, req.getPriority(),
                req.getResponseTimeMinutes(), req.getResolutionTimeMinutes());
        SlaPolicy saved = slaPolicyRepository.save(policy);
        log.info("Created SLA Policy [{}] for Org [{}], Category [{}], Priority [{}]",
                saved.getId(), orgId, req.getCategoryId(), req.getPriority());
        return SlaPolicyResponse.fromEntity(saved);
    }

    /**
     * Updates an existing SLA policy.
     */
    public SlaPolicyResponse updateSlaPolicy(UUID id, CreateSlaPolicyRequest req, UUID orgId) {
        SlaPolicy policy = slaPolicyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SlaPolicy", "id", id));

        if (!policy.getOrganization().getId().equals(orgId)) {
            throw new BadRequestException("SLA policy does not belong to your organization");
        }

        if (req.getCategoryId() != null) {
            Category cat = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", req.getCategoryId()));
            policy.setCategory(cat);
        } else {
            policy.setCategory(null);
        }

        policy.setPriority(req.getPriority());
        policy.setResponseTimeMinutes(req.getResponseTimeMinutes());
        policy.setResolutionTimeMinutes(req.getResolutionTimeMinutes());

        SlaPolicy saved = slaPolicyRepository.save(policy);
        return SlaPolicyResponse.fromEntity(saved);
    }

    /**
     * Deletes an SLA policy.
     */
    public void deleteSlaPolicy(UUID id, UUID orgId) {
        SlaPolicy policy = slaPolicyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SlaPolicy", "id", id));

        if (!policy.getOrganization().getId().equals(orgId)) {
            throw new BadRequestException("SLA policy does not belong to your organization");
        }

        slaPolicyRepository.delete(policy);
    }

    /**
     * Lists all SLA policies for an organization.
     */
    @Transactional(readOnly = true)
    public List<SlaPolicyResponse> getSlaPolicies(UUID orgId) {
        return slaPolicyRepository.findByOrganizationId(orgId).stream()
                .map(SlaPolicyResponse::fromEntity)
                .toList();
    }

    /**
     * Automatically calculates deadlines and attaches SLA tracking to a new or updated case.
     */
    public CaseSla attachSlaToCase(Case caseEntity) {
        if (caseEntity == null || caseEntity.getId() == null) {
            return null;
        }

        Optional<CaseSla> existing = caseSlaRepository.findByCaseEntityId(caseEntity.getId());
        if (existing.isPresent()) {
            return existing.get();
        }

        UUID orgId = caseEntity.getCategory() != null && caseEntity.getCategory().getOrganization() != null
                ? caseEntity.getCategory().getOrganization().getId()
                : (caseEntity.getRequester() != null && caseEntity.getRequester().getOrganization() != null
                ? caseEntity.getRequester().getOrganization().getId() : null);

        UUID catId = caseEntity.getCategory() != null ? caseEntity.getCategory().getId() : null;
        Priority priority = caseEntity.getPriority() != null ? caseEntity.getPriority() : Priority.MEDIUM;

        SlaPolicy policy = null;
        if (orgId != null) {
            if (catId != null) {
                policy = slaPolicyRepository.findByOrganizationIdAndCategoryIdAndPriority(orgId, catId, priority).orElse(null);
            }
            if (policy == null) {
                policy = slaPolicyRepository.findByOrganizationIdAndCategoryIdIsNullAndPriority(orgId, priority).orElse(null);
            }
        }

        int responseMinutes;
        int resolutionMinutes;

        if (policy != null) {
            responseMinutes = policy.getResponseTimeMinutes();
            resolutionMinutes = policy.getResolutionTimeMinutes();
        } else {
            // Default built-in fallback SLA matrix
            switch (priority) {
                case URGENT -> {
                    responseMinutes = 15;
                    resolutionMinutes = 120; // 2 hours
                }
                case HIGH -> {
                    responseMinutes = 30;
                    resolutionMinutes = 240; // 4 hours
                }
                case LOW -> {
                    responseMinutes = 240; // 4 hours
                    resolutionMinutes = 1440; // 24 hours
                }
                default -> { // MEDIUM
                    responseMinutes = 60; // 1 hour
                    resolutionMinutes = 480; // 8 hours
                }
            }
        }

        Instant createdAt = caseEntity.getCreatedAt() != null ? caseEntity.getCreatedAt() : Instant.now();
        Instant responseDeadline = createdAt.plus(Duration.ofMinutes(responseMinutes));
        Instant resolutionDeadline = createdAt.plus(Duration.ofMinutes(resolutionMinutes));

        CaseSla caseSla = new CaseSla(caseEntity, policy, responseDeadline, resolutionDeadline);
        CaseSla saved = caseSlaRepository.save(caseSla);

        // Perform initial risk evaluation
        caseRiskService.evaluateCaseRisk(caseEntity, saved);

        log.info("Attached SLA to case [{}]: Response deadline [{}], Resolution deadline [{}]",
                caseEntity.getCaseNumber(), responseDeadline, resolutionDeadline);
        return saved;
    }

    /**
     * Gets the full SLA breakdown and risk details for a case.
     */
    @Transactional(readOnly = true)
    public CaseSlaResponse getCaseSla(UUID caseId) {
        CaseSla caseSla = caseSlaRepository.findByCaseEntityId(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("CaseSla", "caseId", caseId));

        Optional<CaseRisk> riskOpt = caseRiskService.getLatestRisk(caseId);
        RiskLevel riskLevel = riskOpt.map(CaseRisk::getRiskLevel).orElse(RiskLevel.LOW);
        List<String> riskReasons = riskOpt.map(r -> List.of(r.getReasons().split("; "))).orElse(List.of());

        return CaseSlaResponse.fromEntity(caseSla, riskLevel, riskReasons);
    }

    /**
     * Records that an operator has provided the first response to a case.
     */
    public void recordCaseResponse(UUID caseId) {
        caseSlaRepository.findByCaseEntityId(caseId).ifPresent(sla -> {
            if (sla.getRespondedAt() == null) {
                sla.setRespondedAt(Instant.now());
                caseSlaRepository.save(sla);
                log.info("Recorded response time for case [{}] at [{}]", caseId, sla.getRespondedAt());
            }
        });
    }

    /**
     * Records that a case resolution has been proposed.
     */
    public void recordCaseResolution(UUID caseId) {
        caseSlaRepository.findByCaseEntityId(caseId).ifPresent(sla -> {
            Instant now = Instant.now();
            sla.setResolvedAt(now);
            if (now.isBefore(sla.getResolutionDeadline()) || now.equals(sla.getResolutionDeadline())) {
                sla.setStatus(SlaStatus.MET);
            } else {
                sla.setStatus(SlaStatus.BREACHED);
            }
            caseSlaRepository.save(sla);
            log.info("Recorded resolution for case [{}] with SLA status [{}]", caseId, sla.getStatus());
        });
    }

    /**
     * Gets all cases with breached SLAs for an organization.
     */
    @Transactional(readOnly = true)
    public Page<BreachedCaseResponse> getBreachedCases(UUID orgId, Pageable pageable) {
        return caseSlaRepository.findByOrganizationIdAndStatus(orgId, SlaStatus.BREACHED, pageable)
                .map(cs -> new BreachedCaseResponse(cs.getCaseEntity(), cs.getResponseDeadline(), cs.getResolutionDeadline()));
    }
}
