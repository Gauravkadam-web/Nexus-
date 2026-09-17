package com.nexus.sla.service;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.entity.Priority;
import com.nexus.sla.dto.AtRiskCaseResponse;
import com.nexus.sla.entity.CaseRisk;
import com.nexus.sla.entity.CaseSla;
import com.nexus.sla.entity.RiskLevel;
import com.nexus.sla.entity.SlaStatus;
import com.nexus.sla.repository.CaseRiskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CaseRiskService {

    private static final Logger log = LoggerFactory.getLogger(CaseRiskService.class);

    private final CaseRiskRepository caseRiskRepository;

    public CaseRiskService(CaseRiskRepository caseRiskRepository) {
        this.caseRiskRepository = caseRiskRepository;
    }

    /**
     * Evaluates case risk factors and persists risk evaluation.
     */
    public CaseRisk evaluateCaseRisk(Case caseEntity, CaseSla caseSla) {
        List<String> reasons = new ArrayList<>();
        Instant now = Instant.now();
        boolean hasCriticalFactor = false;

        if (caseSla != null) {
            Instant createdAt = caseEntity.getCreatedAt();
            long totalResMinutes = Duration.between(createdAt, caseSla.getResolutionDeadline()).toMinutes();
            if (totalResMinutes > 0) {
                long elapsed = Duration.between(createdAt, now).toMinutes();
                double consumedPct = (double) elapsed / totalResMinutes * 100.0;
                if (consumedPct >= 85.0 && caseSla.getStatus() != SlaStatus.MET) {
                    reasons.add(String.format("Resolution deadline approaching (%.0f%% elapsed)", consumedPct));
                    hasCriticalFactor = true;
                } else if (consumedPct >= 70.0 && caseSla.getStatus() != SlaStatus.MET) {
                    reasons.add(String.format("Resolution deadline approaching (%.0f%% elapsed)", consumedPct));
                }
            }

            if (caseSla.getRespondedAt() == null) {
                long totalRespMinutes = Duration.between(createdAt, caseSla.getResponseDeadline()).toMinutes();
                if (totalRespMinutes > 0) {
                    long elapsedResp = Duration.between(createdAt, now).toMinutes();
                    double consumedRespPct = (double) elapsedResp / totalRespMinutes * 100.0;
                    if (consumedRespPct >= 75.0) {
                        reasons.add(String.format("Response deadline approaching (%.0f%% elapsed)", consumedRespPct));
                    }
                }
            }

            if (caseSla.getStatus() == SlaStatus.BREACHED) {
                reasons.add("SLA deadline breached");
                hasCriticalFactor = true;
            }
        }

        if (caseEntity.getStatus() == CaseStatus.ESCALATED) {
            reasons.add("Case is currently escalated");
            hasCriticalFactor = true;
        }

        if ((caseEntity.getPriority() == Priority.URGENT || caseEntity.getPriority() == Priority.HIGH)
                && (caseEntity.getStatus() == CaseStatus.REPORTED || caseEntity.getStatus() == CaseStatus.UNDERSTOOD)) {
            reasons.add("High priority case pending assignment or initial investigation");
        }

        if (caseEntity.getUpdatedAt() != null &&
                (caseEntity.getStatus() == CaseStatus.ASSIGNED || caseEntity.getStatus() == CaseStatus.INVESTIGATING)) {
            long minutesSinceUpdate = Duration.between(caseEntity.getUpdatedAt(), now).toMinutes();
            if (minutesSinceUpdate >= 60) {
                reasons.add(String.format("Inactivity detected (%d min since last update)", minutesSinceUpdate));
            }
        }

        RiskLevel riskLevel;
        if (hasCriticalFactor || reasons.size() >= 3) {
            riskLevel = RiskLevel.HIGH;
        } else if (!reasons.isEmpty()) {
            riskLevel = RiskLevel.MEDIUM;
        } else {
            riskLevel = RiskLevel.LOW;
        }

        String joinedReasons = String.join("; ", reasons);
        if (joinedReasons.isBlank()) {
            joinedReasons = "Case progressing on track";
        }

        CaseRisk caseRisk = new CaseRisk(caseEntity, riskLevel, joinedReasons);
        CaseRisk saved = caseRiskRepository.save(caseRisk);
        log.debug("Evaluated risk for case [{}] - Level: {}, Reasons: {}", caseEntity.getCaseNumber(), riskLevel, joinedReasons);
        return saved;
    }

    /**
     * Retrieves the latest risk record for a case.
     */
    @Transactional(readOnly = true)
    public Optional<CaseRisk> getLatestRisk(UUID caseId) {
        return caseRiskRepository.findTopByCaseEntityIdOrderByDetectedAtDesc(caseId);
    }

    /**
     * Gets all at-risk cases (MEDIUM or HIGH risk) for an organization.
     */
    @Transactional(readOnly = true)
    public Page<AtRiskCaseResponse> getAtRiskCases(UUID orgId, Pageable pageable) {
        return caseRiskRepository.findByOrganizationIdAndRiskLevelIn(orgId, List.of(RiskLevel.MEDIUM, RiskLevel.HIGH), pageable)
                .map(cr -> {
                    Instant deadline = null;
                    Long remaining = null;
                    // Provide list of parsed reasons
                    List<String> reasonsList = List.of(cr.getReasons().split("; "));
                    return new AtRiskCaseResponse(cr.getCaseEntity(), cr.getRiskLevel(), reasonsList, deadline, remaining);
                });
    }
}
