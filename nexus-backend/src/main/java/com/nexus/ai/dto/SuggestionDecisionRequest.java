package com.nexus.ai.dto;

import com.nexus.ai.entity.SuggestionStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for the operator's Accept / Modify / Reject decision on an AI suggestion (US-14).
 */
public class SuggestionDecisionRequest {

    @NotNull(message = "status is required (ACCEPTED, MODIFIED, or REJECTED)")
    private SuggestionStatus status;

    /**
     * Required when status = MODIFIED. Contains the operator's corrected value as a JSON string.
     * Example: {@code {"value":"HIGH"}}
     */
    private String modifiedValue;

    /** Optional explanation for overriding the AI recommendation. */
    private String overrideReason;

    public SuggestionDecisionRequest() {}

    public SuggestionStatus getStatus() { return status; }
    public void setStatus(SuggestionStatus status) { this.status = status; }

    public String getModifiedValue() { return modifiedValue; }
    public void setModifiedValue(String modifiedValue) { this.modifiedValue = modifiedValue; }

    public String getOverrideReason() { return overrideReason; }
    public void setOverrideReason(String overrideReason) { this.overrideReason = overrideReason; }
}
