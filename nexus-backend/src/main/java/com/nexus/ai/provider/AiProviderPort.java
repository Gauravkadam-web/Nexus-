package com.nexus.ai.provider;

import com.nexus.ai.dto.CaseContext;

/**
 * Port interface for AI provider operations.
 * Implementations: {@link MockAiProvider} (local/test) and {@link SpringAiChatProvider} (production).
 * The active implementation is selected via the {@code AI_PROVIDER} environment variable.
 * <p>
 * All implementations must fail gracefully — throwing {@link AiUnavailableException}
 * when the provider is unreachable, so callers can handle degradation without blocking users (US-15).
 */
public interface AiProviderPort {

    /**
     * Analyse a newly created case — returns classification, priority, severity,
     * suggested team, missing information, recommended next action, and confidence.
     *
     * @param context the case data required for analysis
     * @return a populated {@link AiAnalysisResult}
     * @throws AiUnavailableException if the provider is down or returns invalid output
     */
    AiAnalysisResult analyzeCase(CaseContext context);

    /**
     * Generate or regenerate a prose narrative summary of the case,
     * covering original report, confirmed findings, actions taken, blockers, and next steps.
     *
     * @param context the full case context including messages and investigation logs
     * @return summary text as a plain string
     * @throws AiUnavailableException if the provider is down
     */
    String generateSummary(CaseContext context);

    /**
     * Detect likely-missing information and return a plain-language description
     * of what details should be requested from the requester (US-13).
     *
     * @param context the case context
     * @return a string describing missing information, or empty string if none detected
     * @throws AiUnavailableException if the provider is down
     */
    String detectMissingInformation(CaseContext context);
}
