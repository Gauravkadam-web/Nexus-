package com.nexus.analytics.dto;

import java.util.UUID;

public class CategoryBreakdownResponse {

    private UUID categoryId;
    private String categoryName;
    private long totalCases;
    private double percentage;
    private double avgResolutionTimeHours;

    public CategoryBreakdownResponse() {}

    public CategoryBreakdownResponse(UUID categoryId, String categoryName, long totalCases,
                                     double percentage, double avgResolutionTimeHours) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.totalCases = totalCases;
        this.percentage = Math.round(percentage * 100.0) / 100.0;
        this.avgResolutionTimeHours = Math.round(avgResolutionTimeHours * 100.0) / 100.0;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(long totalCases) {
        this.totalCases = totalCases;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public double getAvgResolutionTimeHours() {
        return avgResolutionTimeHours;
    }

    public void setAvgResolutionTimeHours(double avgResolutionTimeHours) {
        this.avgResolutionTimeHours = avgResolutionTimeHours;
    }
}
