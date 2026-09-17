package com.nexus.ai.service;

import com.nexus.ai.entity.*;
import com.nexus.ai.provider.AiProviderPort;
import com.nexus.ai.provider.AiUnavailableException;
import com.nexus.ai.repository.*;
import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.collaboration.repository.CaseMessageRepository;
import com.nexus.common.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Manages AI-generated case summaries — creates the initial summary and handles on-demand regeneration (US-12).
 * Each regeneration appends a new versioned row; the latest version is served to the UI.
 */
@Service
public class AiSummaryService {

    private static final Logger log = LoggerFactory.getLogger(AiSummaryService.class);

    private final AiProviderPort aiProvider;
    private final AiSummaryRepository aiSummaryRepository;
    private final AutomationEventRepository automationEventRepository;
    private final CaseRepository caseRepository;
    private final CaseMessageRepository caseMessageRepository;
    private final AiAnalysisService aiAnalysisService;

    public AiSummaryService(AiProviderPort aiProvider,
                            AiSummaryRepository aiSummaryRepository,
                            AutomationEventRepository automationEventRepository,
                            CaseRepository caseRepository,
                            CaseMessageRepository caseMessageRepository,
                            AiAnalysisService aiAnalysisService) {
        this.aiProvider = aiProvider;
        this.aiSummaryRepository = aiSummaryRepository;
        this.automationEventRepository = automationEventRepository;
        this.caseRepository = caseRepository;
        this.caseMessageRepository = caseMessageRepository;
        this.aiAnalysisService = aiAnalysisService;
    }

    /**
     * Retrieves the latest summary for a case, or returns empty if none exists yet.
     *
     * @param caseId the UUID of the case
     * @return Optional containing the latest {@link AiSummary}
     */
    @Transactional(readOnly = true)
    public Optional<AiSummary> getLatestSummary(UUID caseId) {
        return aiSummaryRepository.findTopByCaseEntityIdOrderByVersionDesc(caseId);
    }

    /**
     * Regenerates the summary for a case on operator request.
     * Builds conversation history from visible messages and calls the AI provider.
     * Fails gracefully — if AI is unavailable, throws AiUnavailableException to the caller.
     *
     * @param caseId the UUID of the case
     * @return the newly created {@link AiSummary}
     */
    @Transactional
    public AiSummary regenerateSummary(UUID caseId) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found: " + caseId));

        // Build conversation history from case messages (public messages only)
        String conversationHistory = buildConversationHistory(caseId);

        // Determine next version number
        int nextVersion = aiSummaryRepository.countByCaseEntityId(caseId) + 1;

        AutomationEvent event = new AutomationEvent(caseEntity,
                AutomationEventType.SUMMARY_UPDATE, AutomationTriggerType.USER);
        event = automationEventRepository.save(event);

        try {
            var context = aiAnalysisService.buildContext(caseEntity, conversationHistory);
            String summaryText = aiProvider.generateSummary(context);

            AiSummary summary = new AiSummary(caseEntity, summaryText, nextVersion);
            summary = aiSummaryRepository.save(summary);

            event.setStatus(AutomationEventStatus.SUCCESS);
            event.setCompletedAt(Instant.now());
            automationEventRepository.save(event);

            log.info("[AI] Summary regenerated for case {} (version={})",
                    caseEntity.getCaseNumber(), nextVersion);
            return summary;

        } catch (AiUnavailableException ex) {
            event.setStatus(AutomationEventStatus.FAILED);
            event.setCompletedAt(Instant.now());
            event.setErrorMessage(ex.getMessage());
            automationEventRepository.save(event);
            log.error("[AI] Summary regeneration FAILED for case {}: {}", caseEntity.getCaseNumber(), ex.getMessage());
            throw ex;
        }
    }

    // ---- Helpers ----

    private String buildConversationHistory(UUID caseId) {
        return caseMessageRepository.findByCaseEntityIdOrderByCreatedAtAsc(caseId)
                .stream()
                .filter(m -> Boolean.TRUE.equals(m.getVisibleToRequester()))
                .map(m -> String.format("[%s] %s: %s",
                        m.getCreatedAt(), m.getMessageType(), m.getContent()))
                .collect(Collectors.joining("\n"));
    }
}
