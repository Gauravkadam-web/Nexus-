package com.nexus.ai.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response payload returned by the AI Operator Copilot (US-29).
 */
public class AiCopilotResponse {

    private String question;
    private String answer;
    private BigDecimal confidence;
    private List<String> sourcesUsed;

    public AiCopilotResponse() {}

    public AiCopilotResponse(String question, String answer, BigDecimal confidence, List<String> sourcesUsed) {
        this.question = question;
        this.answer = answer;
        this.confidence = confidence;
        this.sourcesUsed = sourcesUsed;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public BigDecimal getConfidence() {
        return confidence;
    }

    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }

    public List<String> getSourcesUsed() {
        return sourcesUsed;
    }

    public void setSourcesUsed(List<String> sourcesUsed) {
        this.sourcesUsed = sourcesUsed;
    }
}
