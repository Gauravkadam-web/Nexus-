package com.nexus.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.ai.dto.CaseContext;
import com.nexus.ai.entity.*;
import com.nexus.ai.provider.AiAnalysisResult;
import com.nexus.ai.provider.AiProviderPort;
import com.nexus.ai.provider.AiUnavailableException;
import com.nexus.ai.repository.*;
import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.common.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Orchestrates AI case analysis — the primary AI service for Phase 3 (US-11, US-13).
 *
 * <h3>Flow</h3>
 * <ol>
 *   <li>Triggered asynchronously after case creation via {@link CaseCreatedEvent}.</li>
 *   <li>Checks {@code automation_events} for an existing SUCCESS record (idempotency guard).</li>
 *   <li>Calls {@link AiProviderPort#analyzeCase(CaseContext)}.</li>
 *   <li>Persists {@link AiAnalysis} + individual {@link AiSuggestion} rows (status = PENDING).</li>
 *   <li>On AI failure: marks the automation event as FAILED — case is NOT blocked (US-15).</li>
 * </ol>
 */
@Service
public class AiAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AiAnalysisService.class);

    private final AiProviderPort aiProvider;
    private final AiAnalysisRepository aiAnalysisRepository;
    private final AiSuggestionRepository aiSuggestionRepository;
    private final AutomationEventRepository automationEventRepository;
    private final CaseRepository caseRepository;
    private final ObjectMapper objectMapper;

    public AiAnalysisService(AiProviderPort aiProvider,
                             AiAnalysisRepository aiAnalysisRepository,
                             AiSuggestionRepository aiSuggestionRepository,
                             AutomationEventRepository automationEventRepository,
                             CaseRepository caseRepository,
                             ObjectMapper objectMapper) {
        this.aiProvider = aiProvider;
        this.aiAnalysisRepository = aiAnalysisRepository;
        this.aiSuggestionRepository = aiSuggestionRepository;
        this.automationEventRepository = automationEventRepository;
        this.caseRepository = caseRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Asynchronously analyses a case immediately after creation.
     * Runs in the {@code nexus-ai-executor} thread pool — does not block the HTTP response.
     * Idempotent: a prior SUCCESS event skips re-analysis.
     *
     * @param caseId the UUID of the newly created case
     */
    @Async("nexusAiExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void analyzeCase(UUID caseId) {
        Case caseEntity = caseRepository.findById(caseId).orElse(null);
        if (caseEntity == null) {
            log.warn("[AI] analyzeCase skipped — case not found: {}", caseId);
            return;
        }

        // Idempotency guard — skip if already analysed successfully
        boolean alreadyDone = automationEventRepository
                .findTopByCaseEntityIdAndEventTypeAndStatusOrderByCreatedAtDesc(
                        caseId, AutomationEventType.AI_ANALYSIS, AutomationEventStatus.SUCCESS)
                .isPresent();
        if (alreadyDone) {
            log.info("[AI] analyzeCase skipped (idempotent) for case: {}", caseEntity.getCaseNumber());
            return;
        }

        // Create a PENDING automation event
        AutomationEvent event = new AutomationEvent(caseEntity, AutomationEventType.AI_ANALYSIS,
                AutomationTriggerType.EVENT);
        event = automationEventRepository.save(event);

        try {
            CaseContext context = buildContext(caseEntity, "");
            AiAnalysisResult result = aiProvider.analyzeCase(context);

            // Persist the full analysis snapshot
            AiAnalysis analysis = new AiAnalysis();
            analysis.setCaseEntity(caseEntity);
            analysis.setSuggestedPriority(result.getSuggestedPriority());
            analysis.setSuggestedSeverity(result.getSuggestedSeverity());
            analysis.setMissingInformation(toJson(result.getMissingInformation()));
            analysis.setRecommendedNextAction(result.getRecommendedNextAction());
            analysis.setRelatedCases("[]");
            analysis.setRiskInformation(result.getRiskInformation() != null ? result.getRiskInformation() : "{}");
            analysis.setConfidence(result.getConfidence());
            analysis.setModelInformation(result.getModelInformation());
            aiAnalysisRepository.save(analysis);

            // Persist individual PENDING suggestions for operator review
            saveSuggestionIfPresent(caseEntity, SuggestionType.PRIORITY, result.getSuggestedPriority(),
                    result.getConfidence() != null ? result.getConfidence().toPlainString() : null);
            saveSuggestionIfPresent(caseEntity, SuggestionType.SEVERITY, result.getSuggestedSeverity(),
                    result.getConfidence() != null ? result.getConfidence().toPlainString() : null);

            // Mark event SUCCESS
            event.setStatus(AutomationEventStatus.SUCCESS);
            event.setCompletedAt(Instant.now());
            event.setResult("{\"analysisId\":\"" + analysis.getId() + "\"}");
            automationEventRepository.save(event);

            log.info("[AI] analyzeCase SUCCESS for case: {} (confidence={})",
                    caseEntity.getCaseNumber(), result.getConfidence());

        } catch (AiUnavailableException ex) {
            // US-15: Graceful degradation — mark event FAILED, do NOT rethrow.
            event.setStatus(AutomationEventStatus.FAILED);
            event.setCompletedAt(Instant.now());
            event.setErrorMessage(ex.getMessage());
            automationEventRepository.save(event);
            log.error("[AI] analyzeCase FAILED for case {} — AI unavailable: {}",
                    caseEntity.getCaseNumber(), ex.getMessage());
        }
    }

    /**
     * Re-triggers analysis on demand (operator can re-run from the UI).
     *
     * @param caseId the UUID of the case
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void reanalyzeCase(UUID caseId) {
        // Re-trigger ignores the idempotency check — operator explicitly requested it
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found: " + caseId));

        AutomationEvent event = new AutomationEvent(caseEntity, AutomationEventType.AI_ANALYSIS,
                AutomationTriggerType.USER);
        event = automationEventRepository.save(event);

        try {
            CaseContext context = buildContext(caseEntity, "");
            AiAnalysisResult result = aiProvider.analyzeCase(context);

            AiAnalysis analysis = new AiAnalysis();
            analysis.setCaseEntity(caseEntity);
            analysis.setSuggestedPriority(result.getSuggestedPriority());
            analysis.setSuggestedSeverity(result.getSuggestedSeverity());
            analysis.setMissingInformation(toJson(result.getMissingInformation()));
            analysis.setRecommendedNextAction(result.getRecommendedNextAction());
            analysis.setRelatedCases("[]");
            analysis.setRiskInformation(result.getRiskInformation() != null ? result.getRiskInformation() : "{}");
            analysis.setConfidence(result.getConfidence());
            analysis.setModelInformation(result.getModelInformation());
            aiAnalysisRepository.save(analysis);

            saveSuggestionIfPresent(caseEntity, SuggestionType.PRIORITY, result.getSuggestedPriority(),
                    result.getConfidence() != null ? result.getConfidence().toPlainString() : null);
            saveSuggestionIfPresent(caseEntity, SuggestionType.SEVERITY, result.getSuggestedSeverity(),
                    result.getConfidence() != null ? result.getConfidence().toPlainString() : null);

            event.setStatus(AutomationEventStatus.SUCCESS);
            event.setCompletedAt(Instant.now());
            automationEventRepository.save(event);

        } catch (AiUnavailableException ex) {
            event.setStatus(AutomationEventStatus.FAILED);
            event.setCompletedAt(Instant.now());
            event.setErrorMessage(ex.getMessage());
            automationEventRepository.save(event);
            throw ex; // Re-throw for operator-triggered re-analysis so the API can return an error
        }
    }

    // ---- Helpers ----

    /**
     * Builds a {@link CaseContext} from a case entity and conversation history string.
     */
    public CaseContext buildContext(Case caseEntity, String conversationHistory) {
        CaseContext context = new CaseContext();
        context.setCaseNumber(caseEntity.getCaseNumber());
        context.setTitle(caseEntity.getTitle());
        context.setDescription(caseEntity.getDescription());
        context.setCategoryName(caseEntity.getCategory() != null ? caseEntity.getCategory().getName() : "Unknown");
        context.setSubcategoryName(caseEntity.getSubcategory() != null ? caseEntity.getSubcategory().getName() : null);
        context.setCurrentPriority(caseEntity.getPriority() != null ? caseEntity.getPriority().name() : "MEDIUM");
        context.setCurrentSeverity(caseEntity.getSeverity() != null ? caseEntity.getSeverity().name() : "MEDIUM");
        context.setCurrentStatus(caseEntity.getStatus() != null ? caseEntity.getStatus().name() : "REPORTED");
        context.setConversationHistory(conversationHistory);
        return context;
    }

    private void saveSuggestionIfPresent(Case caseEntity, SuggestionType type, String value, String confidence) {
        if (value == null || value.isBlank()) return;
        String json = String.format("{\"value\":\"%s\",\"confidence\":%s}",
                value, confidence != null ? confidence : "null");
        AiSuggestion suggestion = new AiSuggestion(caseEntity, type, json);
        aiSuggestionRepository.save(suggestion);
    }

    private String toJson(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list != null ? list : List.of());
        } catch (Exception e) {
            return "[]";
        }
    }
}
