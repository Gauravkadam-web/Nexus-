package com.nexus.ai.dto;

import com.nexus.ai.entity.AiAnalysis;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * API response for GET /cases/{id}/ai/analysis — the latest AI analysis snapshot.
 * AI-derived values are clearly labeled as recommendations, not confirmed case data.
 */
public class AiAnalysisResponse {

    private UUID id;
    private UUID caseId;
    private String suggestedPriority;
    private String suggestedSeverity;
    private String suggestedCategoryId;
    private String suggestedCategoryName;
    private String suggestedTeamId;
    private String suggestedTeamName;
    /** JSON array string of missing information fields. */
    private String missingInformation;
    private String recommendedNextAction;
    private String relatedCases;
    private String riskInformation;
    private BigDecimal confidence;
    private String modelInformation;
    private Instant createdAt;

    public static AiAnalysisResponse from(AiAnalysis analysis) {
        AiAnalysisResponse resp = new AiAnalysisResponse();
        resp.setId(analysis.getId());
        resp.setCaseId(analysis.getCaseEntity().getId());
        resp.setSuggestedPriority(analysis.getSuggestedPriority());
        resp.setSuggestedSeverity(analysis.getSuggestedSeverity());
        if (analysis.getSuggestedCategory() != null) {
            resp.setSuggestedCategoryId(analysis.getSuggestedCategory().getId().toString());
            resp.setSuggestedCategoryName(analysis.getSuggestedCategory().getName());
        }
        if (analysis.getSuggestedTeam() != null) {
            resp.setSuggestedTeamId(analysis.getSuggestedTeam().getId().toString());
            resp.setSuggestedTeamName(analysis.getSuggestedTeam().getName());
        }
        resp.setMissingInformation(analysis.getMissingInformation());
        resp.setRecommendedNextAction(analysis.getRecommendedNextAction());
        resp.setRelatedCases(analysis.getRelatedCases());
        resp.setRiskInformation(analysis.getRiskInformation());
        resp.setConfidence(analysis.getConfidence());
        resp.setModelInformation(analysis.getModelInformation());
        resp.setCreatedAt(analysis.getCreatedAt());
        return resp;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }

    public String getSuggestedPriority() { return suggestedPriority; }
    public void setSuggestedPriority(String suggestedPriority) { this.suggestedPriority = suggestedPriority; }

    public String getSuggestedSeverity() { return suggestedSeverity; }
    public void setSuggestedSeverity(String suggestedSeverity) { this.suggestedSeverity = suggestedSeverity; }

    public String getSuggestedCategoryId() { return suggestedCategoryId; }
    public void setSuggestedCategoryId(String suggestedCategoryId) { this.suggestedCategoryId = suggestedCategoryId; }

    public String getSuggestedCategoryName() { return suggestedCategoryName; }
    public void setSuggestedCategoryName(String suggestedCategoryName) { this.suggestedCategoryName = suggestedCategoryName; }

    public String getSuggestedTeamId() { return suggestedTeamId; }
    public void setSuggestedTeamId(String suggestedTeamId) { this.suggestedTeamId = suggestedTeamId; }

    public String getSuggestedTeamName() { return suggestedTeamName; }
    public void setSuggestedTeamName(String suggestedTeamName) { this.suggestedTeamName = suggestedTeamName; }

    public String getMissingInformation() { return missingInformation; }
    public void setMissingInformation(String missingInformation) { this.missingInformation = missingInformation; }

    public String getRecommendedNextAction() { return recommendedNextAction; }
    public void setRecommendedNextAction(String recommendedNextAction) { this.recommendedNextAction = recommendedNextAction; }

    public String getRelatedCases() { return relatedCases; }
    public void setRelatedCases(String relatedCases) { this.relatedCases = relatedCases; }

    public String getRiskInformation() { return riskInformation; }
    public void setRiskInformation(String riskInformation) { this.riskInformation = riskInformation; }

    public BigDecimal getConfidence() { return confidence; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }

    public String getModelInformation() { return modelInformation; }
    public void setModelInformation(String modelInformation) { this.modelInformation = modelInformation; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
