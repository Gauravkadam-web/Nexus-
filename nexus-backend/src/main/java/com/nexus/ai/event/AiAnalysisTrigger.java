package com.nexus.ai.event;

import com.nexus.ai.service.AiAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listens for {@link CaseCreatedEvent} and asynchronously triggers AI case analysis after transaction commit.
 * Runs in the {@code nexusAiExecutor} thread pool — never blocks the case creation HTTP response.
 * If AI analysis fails, it is logged and marked in {@code automation_events}; the case is unaffected (US-15).
 */
@Component
public class AiAnalysisTrigger {

    private static final Logger log = LoggerFactory.getLogger(AiAnalysisTrigger.class);

    private final AiAnalysisService aiAnalysisService;

    public AiAnalysisTrigger(AiAnalysisService aiAnalysisService) {
        this.aiAnalysisService = aiAnalysisService;
    }

    /**
     * Receives the case-created event after commit and delegates to {@link AiAnalysisService#analyzeCase(java.util.UUID)}.
     *
     * @param event the case-created event containing the new case UUID
     */
    @Async("nexusAiExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCaseCreated(CaseCreatedEvent event) {
        log.info("[AI Trigger] Received CaseCreatedEvent for case: {}", event.getCaseId());
        try {
            aiAnalysisService.analyzeCase(event.getCaseId());
        } catch (Exception ex) {
            // Final safety net — ensure trigger thread never propagates exceptions
            log.error("[AI Trigger] Unexpected error during AI analysis trigger for case {}: {}",
                    event.getCaseId(), ex.getMessage());
        }
    }
}
