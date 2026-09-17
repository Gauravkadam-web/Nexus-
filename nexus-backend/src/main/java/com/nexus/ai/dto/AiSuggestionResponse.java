package com.nexus.ai.dto;

import com.nexus.ai.entity.AiSuggestion;
import com.nexus.ai.entity.SuggestionStatus;
import com.nexus.ai.entity.SuggestionType;

import java.time.Instant;
import java.util.UUID;

/**
 * API response for an individual AI suggestion, including its current decision status.
 */
public class AiSuggestionResponse {

    private UUID id;
    private UUID caseId;
    private SuggestionType suggestionType;
    private String suggestedValue;
    private SuggestionStatus status;
    private UUID decidedById;
    private String decidedByName;
    private String modifiedValue;
    private String overrideReason;
    private Instant decidedAt;
    private Instant createdAt;

    public static AiSuggestionResponse from(AiSuggestion suggestion) {
        AiSuggestionResponse resp = new AiSuggestionResponse();
        resp.setId(suggestion.getId());
        resp.setCaseId(suggestion.getCaseEntity().getId());
        resp.setSuggestionType(suggestion.getSuggestionType());
        resp.setSuggestedValue(suggestion.getSuggestedValue());
        resp.setStatus(suggestion.getStatus());
        if (suggestion.getDecidedBy() != null) {
            resp.setDecidedById(suggestion.getDecidedBy().getId());
            resp.setDecidedByName(suggestion.getDecidedBy().getName());
        }
        resp.setModifiedValue(suggestion.getModifiedValue());
        resp.setOverrideReason(suggestion.getOverrideReason());
        resp.setDecidedAt(suggestion.getDecidedAt());
        resp.setCreatedAt(suggestion.getCreatedAt());
        return resp;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }

    public SuggestionType getSuggestionType() { return suggestionType; }
    public void setSuggestionType(SuggestionType suggestionType) { this.suggestionType = suggestionType; }

    public String getSuggestedValue() { return suggestedValue; }
    public void setSuggestedValue(String suggestedValue) { this.suggestedValue = suggestedValue; }

    public SuggestionStatus getStatus() { return status; }
    public void setStatus(SuggestionStatus status) { this.status = status; }

    public UUID getDecidedById() { return decidedById; }
    public void setDecidedById(UUID decidedById) { this.decidedById = decidedById; }

    public String getDecidedByName() { return decidedByName; }
    public void setDecidedByName(String decidedByName) { this.decidedByName = decidedByName; }

    public String getModifiedValue() { return modifiedValue; }
    public void setModifiedValue(String modifiedValue) { this.modifiedValue = modifiedValue; }

    public String getOverrideReason() { return overrideReason; }
    public void setOverrideReason(String overrideReason) { this.overrideReason = overrideReason; }

    public Instant getDecidedAt() { return decidedAt; }
    public void setDecidedAt(Instant decidedAt) { this.decidedAt = decidedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
