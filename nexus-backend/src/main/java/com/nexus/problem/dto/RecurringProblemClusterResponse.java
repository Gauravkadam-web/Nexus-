package com.nexus.problem.dto;

import java.util.List;
import java.util.UUID;

public class RecurringProblemClusterResponse {

    private String patternTitle;
    private String commonCategory;
    private int incidentCount;
    private List<UUID> incidentIds;
    private List<String> sampleCaseNumbers;
    private String recommendedRootCauseHypothesis;

    public RecurringProblemClusterResponse() {
    }

    public RecurringProblemClusterResponse(String patternTitle, String commonCategory, int incidentCount,
                                           List<UUID> incidentIds, List<String> sampleCaseNumbers,
                                           String recommendedRootCauseHypothesis) {
        this.patternTitle = patternTitle;
        this.commonCategory = commonCategory;
        this.incidentCount = incidentCount;
        this.incidentIds = incidentIds;
        this.sampleCaseNumbers = sampleCaseNumbers;
        this.recommendedRootCauseHypothesis = recommendedRootCauseHypothesis;
    }

    public String getPatternTitle() {
        return patternTitle;
    }

    public void setPatternTitle(String patternTitle) {
        this.patternTitle = patternTitle;
    }

    public String getCommonCategory() {
        return commonCategory;
    }

    public void setCommonCategory(String commonCategory) {
        this.commonCategory = commonCategory;
    }

    public int getIncidentCount() {
        return incidentCount;
    }

    public void setIncidentCount(int incidentCount) {
        this.incidentCount = incidentCount;
    }

    public List<UUID> getIncidentIds() {
        return incidentIds;
    }

    public void setIncidentIds(List<UUID> incidentIds) {
        this.incidentIds = incidentIds;
    }

    public List<String> getSampleCaseNumbers() {
        return sampleCaseNumbers;
    }

    public void setSampleCaseNumbers(List<String> sampleCaseNumbers) {
        this.sampleCaseNumbers = sampleCaseNumbers;
    }

    public String getRecommendedRootCauseHypothesis() {
        return recommendedRootCauseHypothesis;
    }

    public void setRecommendedRootCauseHypothesis(String recommendedRootCauseHypothesis) {
        this.recommendedRootCauseHypothesis = recommendedRootCauseHypothesis;
    }
}
