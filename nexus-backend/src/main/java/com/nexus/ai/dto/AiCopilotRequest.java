package com.nexus.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for the AI Operator Copilot (US-29).
 * Operators can ask case-scoped questions (e.g. "what happened", "what is blocking", "what should I do next").
 */
public class AiCopilotRequest {

    @NotBlank(message = "Question cannot be blank")
    @Size(max = 500, message = "Question must not exceed 500 characters")
    private String question;

    public AiCopilotRequest() {}

    public AiCopilotRequest(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
