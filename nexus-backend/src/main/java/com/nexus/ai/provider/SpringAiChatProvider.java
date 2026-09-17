package com.nexus.ai.provider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.ai.dto.CaseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Production AI provider using Spring AI's {@link ChatModel} abstraction.
 * Works with OpenAI, Anthropic, and Google Gemini via Spring AI configuration.
 * Selected when {@code nexus.ai.provider=openai|anthropic|gemini}.
 * <p>
 * Prompts are structured to request JSON output; responses are parsed and validated.
 * On failure (API down, malformed JSON, timeout), throws {@link AiUnavailableException}
 * so the caller can handle graceful degradation (US-15).
 */
@Component
@ConditionalOnProperty(name = "nexus.ai.provider", havingValue = "openai")
public class SpringAiChatProvider implements AiProviderPort {

    private static final Logger log = LoggerFactory.getLogger(SpringAiChatProvider.class);

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    public SpringAiChatProvider(ChatModel chatModel, ObjectMapper objectMapper) {
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
    }

    @Override
    public AiAnalysisResult analyzeCase(CaseContext context) {
        String prompt = buildAnalysisPrompt(context);
        try {
            String rawResponse = chatModel.call(new Prompt(prompt))
                    .getResult()
                    .getOutput()
                    .getText();

            return parseAnalysisResponse(rawResponse, context);
        } catch (Exception ex) {
            log.error("[SpringAI] analyzeCase failed for case {}: {}", context.getCaseNumber(), ex.getMessage());
            throw new AiUnavailableException("AI analysis unavailable: " + ex.getMessage(), ex);
        }
    }

    @Override
    public String generateSummary(CaseContext context) {
        String prompt = buildSummaryPrompt(context);
        try {
            return chatModel.call(new Prompt(prompt))
                    .getResult()
                    .getOutput()
                    .getText()
                    .trim();
        } catch (Exception ex) {
            log.error("[SpringAI] generateSummary failed for case {}: {}", context.getCaseNumber(), ex.getMessage());
            throw new AiUnavailableException("AI summary unavailable: " + ex.getMessage(), ex);
        }
    }

    @Override
    public String detectMissingInformation(CaseContext context) {
        String prompt = buildMissingInfoPrompt(context);
        try {
            return chatModel.call(new Prompt(prompt))
                    .getResult()
                    .getOutput()
                    .getText()
                    .trim();
        } catch (Exception ex) {
            log.error("[SpringAI] detectMissingInformation failed for case {}: {}", context.getCaseNumber(), ex.getMessage());
            throw new AiUnavailableException("AI missing-info detection unavailable: " + ex.getMessage(), ex);
        }
    }

    // ---- Prompt builders ----

    private String buildAnalysisPrompt(CaseContext ctx) {
        return String.format("""
            You are an expert case management analyst. Analyze the following case and respond in valid JSON only.
            
            Case: %s
            Title: %s
            Description: %s
            Category: %s
            Current Priority: %s
            Current Severity: %s
            
            Respond with ONLY valid JSON in this exact format:
            {
              "suggestedPriority": "LOW|MEDIUM|HIGH|URGENT",
              "suggestedSeverity": "LOW|MEDIUM|HIGH|CRITICAL",
              "missingInformation": ["field1", "field2"],
              "recommendedNextAction": "string",
              "riskInformation": "string",
              "confidence": 0.00
            }
            
            Important: Present these as recommendations only. Do NOT state things as confirmed facts.
            """,
                ctx.getCaseNumber(), ctx.getTitle(), ctx.getDescription(),
                ctx.getCategoryName(), ctx.getCurrentPriority(), ctx.getCurrentSeverity()
        );
    }

    private String buildSummaryPrompt(CaseContext ctx) {
        return String.format("""
            Summarize the following case in 3-5 sentences covering:
            original report, current status, confirmed findings, actions taken, and remaining work.
            Use cautious language — do not state AI assumptions as confirmed facts.
            
            Case %s: %s
            Description: %s
            Status: %s
            History: %s
            """,
                ctx.getCaseNumber(), ctx.getTitle(), ctx.getDescription(),
                ctx.getCurrentStatus(), ctx.getConversationHistory()
        );
    }

    private String buildMissingInfoPrompt(CaseContext ctx) {
        return String.format("""
            Identify what information is likely missing from this case report to enable effective investigation.
            Be specific and concise. Do not invent facts.
            
            Case: %s
            Description: %s
            Category: %s
            """,
                ctx.getTitle(), ctx.getDescription(), ctx.getCategoryName()
        );
    }

    @SuppressWarnings("unchecked")
    private AiAnalysisResult parseAnalysisResponse(String rawJson, CaseContext ctx) {
        try {
            Map<String, Object> parsed = objectMapper.readValue(rawJson, new TypeReference<>() {});
            AiAnalysisResult result = new AiAnalysisResult();
            result.setSuggestedPriority((String) parsed.get("suggestedPriority"));
            result.setSuggestedSeverity((String) parsed.get("suggestedSeverity"));
            result.setMissingInformation((List<String>) parsed.getOrDefault("missingInformation", List.of()));
            result.setRecommendedNextAction((String) parsed.get("recommendedNextAction"));
            result.setRiskInformation((String) parsed.getOrDefault("riskInformation", "{}"));
            Object conf = parsed.get("confidence");
            if (conf != null) {
                result.setConfidence(new BigDecimal(conf.toString()));
            }
            result.setModelInformation("openai/gpt-4o");
            return result;
        } catch (Exception ex) {
            log.error("[SpringAI] Failed to parse AI response for case {}: {}", ctx.getCaseNumber(), rawJson);
            throw new AiUnavailableException("AI returned invalid JSON response", ex);
        }
    }
}
