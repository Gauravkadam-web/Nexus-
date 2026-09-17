package com.nexus.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for AI Communication Drafting (US-30).
 * Lets operators request drafted messages for information requests, updates, resolution explanations, etc.
 */
public class AiDraftCommunicationRequest {

    @NotBlank(message = "Audience is required (e.g. REQUESTER, INTERNAL, VENDOR)")
    private String audience;

    @NotBlank(message = "Intent is required (e.g. STATUS_UPDATE, REQUEST_INFO, RESOLUTION_EXPLANATION, ESCALATION_SUMMARY)")
    private String intent;

    @Size(max = 1000, message = "Specific instructions must not exceed 1000 characters")
    private String instructions;

    public AiDraftCommunicationRequest() {}

    public AiDraftCommunicationRequest(String audience, String intent, String instructions) {
        this.audience = audience;
        this.intent = intent;
        this.instructions = instructions;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
