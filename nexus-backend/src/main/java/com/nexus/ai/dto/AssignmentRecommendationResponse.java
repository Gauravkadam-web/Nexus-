package com.nexus.ai.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO representing an AI smart assignment recommendation (US-19, US-20).
 */
public class AssignmentRecommendationResponse {

    private UUID suggestedTeamId;
    private String suggestedTeamName;
    private UUID suggestedUserId;
    private String suggestedUserName;
    private int currentWorkloadCount;
    private BigDecimal confidence;
    private String reasoning;

    public AssignmentRecommendationResponse() {}

    public AssignmentRecommendationResponse(UUID suggestedTeamId, String suggestedTeamName,
                                            UUID suggestedUserId, String suggestedUserName,
                                            int currentWorkloadCount, BigDecimal confidence,
                                            String reasoning) {
        this.suggestedTeamId = suggestedTeamId;
        this.suggestedTeamName = suggestedTeamName;
        this.suggestedUserId = suggestedUserId;
        this.suggestedUserName = suggestedUserName;
        this.currentWorkloadCount = currentWorkloadCount;
        this.confidence = confidence;
        this.reasoning = reasoning;
    }

    public UUID getSuggestedTeamId() {
        return suggestedTeamId;
    }

    public void setSuggestedTeamId(UUID suggestedTeamId) {
        this.suggestedTeamId = suggestedTeamId;
    }

    public String getSuggestedTeamName() {
        return suggestedTeamName;
    }

    public void setSuggestedTeamName(String suggestedTeamName) {
        this.suggestedTeamName = suggestedTeamName;
    }

    public UUID getSuggestedUserId() {
        return suggestedUserId;
    }

    public void setSuggestedUserId(UUID suggestedUserId) {
        this.suggestedUserId = suggestedUserId;
    }

    public String getSuggestedUserName() {
        return suggestedUserName;
    }

    public void setSuggestedUserName(String suggestedUserName) {
        this.suggestedUserName = suggestedUserName;
    }

    public int getCurrentWorkloadCount() {
        return currentWorkloadCount;
    }

    public void setCurrentWorkloadCount(int currentWorkloadCount) {
        this.currentWorkloadCount = currentWorkloadCount;
    }

    public BigDecimal getConfidence() {
        return confidence;
    }

    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }
}
