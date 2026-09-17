package com.nexus.ai.provider;

import com.nexus.ai.dto.CaseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mock AI provider for local development and testing (AI_PROVIDER=mock).
 * Returns deterministic, canned responses without making any external API calls.
 * This ensures tests run offline and no AI costs are incurred in dev/CI.
 */
@Component
@ConditionalOnProperty(name = "nexus.ai.provider", havingValue = "mock", matchIfMissing = true)
public class MockAiProvider implements AiProviderPort {

    private static final Logger log = LoggerFactory.getLogger(MockAiProvider.class);

    @Override
    public AiAnalysisResult analyzeCase(CaseContext context) {
        log.info("[MockAI] analyzeCase called for case: {}", context.getCaseNumber());

        AiAnalysisResult result = new AiAnalysisResult();
        result.setSuggestedPriority("HIGH");
        result.setSuggestedSeverity("MEDIUM");
        result.setSuggestedCategoryName(context.getCategoryName());
        result.setSuggestedTeamName("Support Team");
        result.setMissingInformation(List.of("error_message", "device_model", "steps_to_reproduce"));
        result.setRecommendedNextAction(
                "Request the exact error message from the requester and check recent system logs.");
        result.setRiskInformation("{\"level\":\"LOW\",\"factors\":[]}");
        result.setConfidence(new BigDecimal("0.82"));
        result.setModelInformation("mock/nexus-ai-v1");
        return result;
    }

    @Override
    public String generateSummary(CaseContext context) {
        log.info("[MockAI] generateSummary called for case: {}", context.getCaseNumber());
        return String.format(
                "Case %s — %s. Original report: %s. " +
                "Status: %s. " +
                "Confirmed findings: Under investigation. " +
                "Remaining work: Awaiting additional information from requester.",
                context.getCaseNumber(),
                context.getTitle(),
                context.getDescription(),
                context.getCurrentStatus()
        );
    }

    @Override
    public String detectMissingInformation(CaseContext context) {
        log.info("[MockAI] detectMissingInformation called for case: {}", context.getCaseNumber());
        return "The following information appears to be missing: exact error message or code, " +
               "device model and operating system version, and steps to reproduce the issue.";
    }
}
