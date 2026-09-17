package com.nexus.sla.service;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.escalation.service.EscalationService;
import com.nexus.notification.entity.NotificationType;
import com.nexus.notification.repository.NotificationRepository;
import com.nexus.notification.service.NotificationService;
import com.nexus.sla.entity.CaseRisk;
import com.nexus.sla.entity.CaseSla;
import com.nexus.sla.entity.SlaStatus;
import com.nexus.sla.repository.CaseSlaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class SlaSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(SlaSchedulerService.class);

    private final CaseSlaRepository caseSlaRepository;
    private final CaseRiskService caseRiskService;
    private final EscalationService escalationService;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    public SlaSchedulerService(CaseSlaRepository caseSlaRepository,
                               CaseRiskService caseRiskService,
                               EscalationService escalationService,
                               NotificationService notificationService,
                               NotificationRepository notificationRepository) {
        this.caseSlaRepository = caseSlaRepository;
        this.caseRiskService = caseRiskService;
        this.escalationService = escalationService;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Periodic background task running every 60 seconds to scan active cases,
     * evaluate SLA thresholds, update risk states, and trigger alerts/escalations idempotently.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void scanAndEvaluateSla() {
        log.debug("[SlaScheduler] Starting periodic SLA scan...");
        Instant now = Instant.now();

        List<CaseSla> activeSlas = caseSlaRepository.findByStatusIn(List.of(SlaStatus.ON_TRACK, SlaStatus.AT_RISK));

        for (CaseSla sla : activeSlas) {
            Case caseEntity = sla.getCaseEntity();

            // Skip closed, cancelled, or duplicate cases
            if (caseEntity.getStatus() == CaseStatus.CLOSED ||
                    caseEntity.getStatus() == CaseStatus.CANCELLED ||
                    caseEntity.getStatus() == CaseStatus.DUPLICATE) {
                continue;
            }

            Instant createdAt = caseEntity.getCreatedAt();
            long totalMinutes = Duration.between(createdAt, sla.getResolutionDeadline()).toMinutes();
            long elapsedMinutes = Duration.between(createdAt, now).toMinutes();

            if (now.isAfter(sla.getResolutionDeadline())) {
                // BREACHED
                if (sla.getStatus() != SlaStatus.BREACHED) {
                    sla.setStatus(SlaStatus.BREACHED);
                    caseSlaRepository.save(sla);

                    // Send notification if not already sent
                    if (caseEntity.getAssignedUser() != null) {
                        boolean alreadyNotified = notificationRepository.existsByUserIdAndCaseEntityIdAndType(
                                caseEntity.getAssignedUser().getId(), caseEntity.getId(), NotificationType.SLA_BREACH);
                        if (!alreadyNotified) {
                            notificationService.createNotification(
                                    caseEntity.getAssignedUser(),
                                    caseEntity,
                                    NotificationType.SLA_BREACH,
                                    "SLA Breached: " + caseEntity.getCaseNumber(),
                                    "Resolution deadline missed for case " + caseEntity.getCaseNumber()
                            );
                        }
                    }
                    log.warn("[SlaScheduler] Case [{}] marked as SLA BREACHED", caseEntity.getCaseNumber());
                }
            } else if (totalMinutes > 0 && ((double) elapsedMinutes / totalMinutes) >= 0.75) {
                // AT_RISK (> 75% elapsed)
                if (sla.getStatus() == SlaStatus.ON_TRACK) {
                    sla.setStatus(SlaStatus.AT_RISK);
                    caseSlaRepository.save(sla);

                    if (caseEntity.getAssignedUser() != null) {
                        boolean alreadyNotified = notificationRepository.existsByUserIdAndCaseEntityIdAndType(
                                caseEntity.getAssignedUser().getId(), caseEntity.getId(), NotificationType.SLA_WARNING);
                        if (!alreadyNotified) {
                            notificationService.createNotification(
                                    caseEntity.getAssignedUser(),
                                    caseEntity,
                                    NotificationType.SLA_WARNING,
                                    "SLA Warning: " + caseEntity.getCaseNumber(),
                                    "Case " + caseEntity.getCaseNumber() + " is approaching resolution deadline (>75% elapsed)"
                            );
                        }
                    }
                    log.info("[SlaScheduler] Case [{}] marked as AT_RISK", caseEntity.getCaseNumber());
                }
            }

            // Evaluate risk factors and escalation rules
            CaseRisk latestRisk = caseRiskService.evaluateCaseRisk(caseEntity, sla);
            escalationService.evaluateEscalationRulesForCase(caseEntity, sla, latestRisk);
        }
    }
}
