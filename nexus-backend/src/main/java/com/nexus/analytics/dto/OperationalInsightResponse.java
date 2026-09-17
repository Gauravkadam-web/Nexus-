package com.nexus.analytics.dto;

import java.util.Map;

public class OperationalInsightResponse {

    private String insightType;
    private String severity;
    private String title;
    private String description;
    private String recommendedAction;
    private Map<String, Object> data;

    public OperationalInsightResponse() {}

    public OperationalInsightResponse(String insightType, String severity, String title,
                                      String description, String recommendedAction,
                                      Map<String, Object> data) {
        this.insightType = insightType;
        this.severity = severity;
        this.title = title;
        this.description = description;
        this.recommendedAction = recommendedAction;
        this.data = data;
    }

    public String getInsightType() {
        return insightType;
    }

    public void setInsightType(String insightType) {
        this.insightType = insightType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecommendedAction() {
        return recommendedAction;
    }

    public void setRecommendedAction(String recommendedAction) {
        this.recommendedAction = recommendedAction;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
