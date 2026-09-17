package com.nexus.ai.provider;

import java.math.BigDecimal;
import java.util.List;

/**
 * Internal value object holding the raw result of an AI case analysis.
 * This is NOT persisted directly — the service maps it into {@link com.nexus.ai.entity.AiAnalysis}
 * and individual {@link com.nexus.ai.entity.AiSuggestion} rows.
 */
public class AiAnalysisResult {

    private String suggestedPriority;
    private String suggestedSeverity;
    private String suggestedCategoryName;
    private String suggestedTeamName;
    private List<String> missingInformation;
    private String recommendedNextAction;
    private String riskInformation;
    private BigDecimal confidence;
    private String modelInformation;

    public AiAnalysisResult() {}

    // --- Getters & Setters ---

    public String getSuggestedPriority() { return suggestedPriority; }
    public void setSuggestedPriority(String suggestedPriority) { this.suggestedPriority = suggestedPriority; }

    public String getSuggestedSeverity() { return suggestedSeverity; }
    public void setSuggestedSeverity(String suggestedSeverity) { this.suggestedSeverity = suggestedSeverity; }

    public String getSuggestedCategoryName() { return suggestedCategoryName; }
    public void setSuggestedCategoryName(String suggestedCategoryName) { this.suggestedCategoryName = suggestedCategoryName; }

    public String getSuggestedTeamName() { return suggestedTeamName; }
    public void setSuggestedTeamName(String suggestedTeamName) { this.suggestedTeamName = suggestedTeamName; }

    public List<String> getMissingInformation() { return missingInformation; }
    public void setMissingInformation(List<String> missingInformation) { this.missingInformation = missingInformation; }

    public String getRecommendedNextAction() { return recommendedNextAction; }
    public void setRecommendedNextAction(String recommendedNextAction) { this.recommendedNextAction = recommendedNextAction; }

    public String getRiskInformation() { return riskInformation; }
    public void setRiskInformation(String riskInformation) { this.riskInformation = riskInformation; }

    public BigDecimal getConfidence() { return confidence; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }

    public String getModelInformation() { return modelInformation; }
    public void setModelInformation(String modelInformation) { this.modelInformation = modelInformation; }
}
