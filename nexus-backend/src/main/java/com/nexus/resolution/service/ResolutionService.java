package com.nexus.resolution.service;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.casemanagement.statemachine.CaseLifecycleService;
import com.nexus.common.exception.BadRequestException;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.notification.entity.NotificationType;
import com.nexus.notification.service.NotificationService;
import com.nexus.resolution.dto.ConfirmResolutionRequest;
import com.nexus.resolution.dto.RejectResolutionRequest;
import com.nexus.resolution.dto.ResolutionResponse;
import com.nexus.resolution.dto.SubmitResolutionRequest;
import com.nexus.resolution.entity.RequesterDecision;
import com.nexus.resolution.entity.Resolution;
import com.nexus.resolution.repository.ResolutionRepository;
import com.nexus.sla.service.SlaService;
import com.nexus.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class ResolutionService {

    private static final Logger log = LoggerFactory.getLogger(ResolutionService.class);

    private final ResolutionRepository resolutionRepository;
    private final CaseRepository caseRepository;
    private final CaseLifecycleService caseLifecycleService;
    private final SlaService slaService;
    private final NotificationService notificationService;

    public ResolutionService(ResolutionRepository resolutionRepository,
                             CaseRepository caseRepository,
                             CaseLifecycleService caseLifecycleService,
                             SlaService slaService,
                             NotificationService notificationService) {
        this.resolutionRepository = resolutionRepository;
        this.caseRepository = caseRepository;
        this.caseLifecycleService = caseLifecycleService;
        this.slaService = slaService;
        this.notificationService = notificationService;
    }

    /**
     * US-26: Operator submits structured resolution findings, transitioning case to RESOLUTION_PROPOSED.
     */
    public ResolutionResponse submitResolution(UUID caseId, SubmitResolutionRequest req, User operator) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId));

        // State Machine validation
        caseLifecycleService.validateTransition(caseEntity.getStatus(), CaseStatus.RESOLUTION_PROPOSED);
        caseEntity.setStatus(CaseStatus.RESOLUTION_PROPOSED);
        caseEntity.setResolvedAt(Instant.now());
        caseRepository.save(caseEntity);

        // Record resolution in SLA engine
        slaService.recordCaseResolution(caseId);

        Resolution resolution = new Resolution(
                caseEntity,
                operator,
                req.getWhatWasDone(),
                req.getFindings(),
                req.getEvidenceRef(),
                req.getLimitations(),
                req.getResolutionMessage()
        );

        Resolution saved = resolutionRepository.save(resolution);

        // Notify Requester
        if (caseEntity.getRequester() != null) {
            notificationService.createNotification(
                    caseEntity.getRequester(),
                    caseEntity,
                    NotificationType.RESOLUTION,
                    "Resolution Proposed for " + caseEntity.getCaseNumber(),
                    "Your case has been resolved: " + req.getResolutionMessage() + ". Please confirm or reject the resolution."
            );
        }

        log.info("Resolution submitted for case [{}] by operator [{}]", caseEntity.getCaseNumber(),
                operator != null ? operator.getEmail() : "N/A");
        return ResolutionResponse.fromEntity(saved);
    }

    /**
     * US-27: Requester confirms the resolution, transitioning case to CLOSED.
     */
    public ResolutionResponse confirmResolution(UUID caseId, ConfirmResolutionRequest req, User requester) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId));

        Resolution resolution = resolutionRepository.findTopByCaseEntityIdOrderByCreatedAtDesc(caseId)
                .orElseThrow(() -> new BadRequestException("No resolution found to confirm for this case"));

        if (resolution.getRequesterDecision() != RequesterDecision.PENDING) {
            throw new BadRequestException("Resolution has already been decided: " + resolution.getRequesterDecision());
        }

        resolution.setRequesterDecision(RequesterDecision.CONFIRMED);
        if (req != null && req.getFeedback() != null) {
            resolution.setFeedback(req.getFeedback());
        }
        resolution.setDecidedAt(Instant.now());
        Resolution saved = resolutionRepository.save(resolution);

        // Transition case to CLOSED
        caseLifecycleService.validateTransition(caseEntity.getStatus(), CaseStatus.CLOSED);
        caseEntity.setStatus(CaseStatus.CLOSED);
        caseEntity.setClosedAt(Instant.now());
        caseRepository.save(caseEntity);

        // Notify assigned operator
        if (caseEntity.getAssignedUser() != null) {
            notificationService.createNotification(
                    caseEntity.getAssignedUser(),
                    caseEntity,
                    NotificationType.RESOLUTION,
                    "Resolution Confirmed: " + caseEntity.getCaseNumber(),
                    "The requester confirmed resolution for case " + caseEntity.getCaseNumber()
            );
        }

        log.info("Case [{}] confirmed and CLOSED by requester [{}]", caseEntity.getCaseNumber(),
                requester != null ? requester.getEmail() : "N/A");
        return ResolutionResponse.fromEntity(saved);
    }

    /**
     * US-27: Requester rejects the resolution, transitioning case to REOPENED.
     */
    public ResolutionResponse rejectResolution(UUID caseId, RejectResolutionRequest req, User requester) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId));

        Resolution resolution = resolutionRepository.findTopByCaseEntityIdOrderByCreatedAtDesc(caseId)
                .orElseThrow(() -> new BadRequestException("No resolution found to reject for this case"));

        if (resolution.getRequesterDecision() != RequesterDecision.PENDING) {
            throw new BadRequestException("Resolution has already been decided: " + resolution.getRequesterDecision());
        }

        resolution.setRequesterDecision(RequesterDecision.REJECTED);
        resolution.setFeedback(req.getRejectionReason());
        resolution.setDecidedAt(Instant.now());
        Resolution saved = resolutionRepository.save(resolution);

        // Reopen case: transition to REOPENED
        caseLifecycleService.validateTransition(caseEntity.getStatus(), CaseStatus.REOPENED);
        caseEntity.setStatus(CaseStatus.REOPENED);
        caseEntity.setResolvedAt(null);
        caseEntity.setClosedAt(null);
        caseRepository.save(caseEntity);


        // Notify assigned operator
        if (caseEntity.getAssignedUser() != null) {
            notificationService.createNotification(
                    caseEntity.getAssignedUser(),
                    caseEntity,
                    NotificationType.CASE_REOPENED,
                    "Case Reopened: " + caseEntity.getCaseNumber(),
                    "Requester rejected resolution: " + req.getRejectionReason()
            );
        }

        log.info("Case [{}] REOPENED by requester [{}] reason: [{}]", caseEntity.getCaseNumber(),
                requester != null ? requester.getEmail() : "N/A", req.getRejectionReason());
        return ResolutionResponse.fromEntity(saved);
    }

    /**
     * Gets the latest resolution record for a case.
     */
    @Transactional(readOnly = true)
    public ResolutionResponse getCaseResolution(UUID caseId) {
        Resolution resolution = resolutionRepository.findTopByCaseEntityIdOrderByCreatedAtDesc(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Resolution", "caseId", caseId));
        return ResolutionResponse.fromEntity(resolution);
    }
}
