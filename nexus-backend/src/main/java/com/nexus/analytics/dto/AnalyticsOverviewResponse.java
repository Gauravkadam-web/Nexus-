package com.nexus.analytics.dto;

public class AnalyticsOverviewResponse {

    private long totalCases;
    private long openCases;
    private long resolvedCases;
    private long closedCases;
    private long slaBreachedCases;
    private double slaMetPercentage;
    private double avgResolutionTimeHours;
    private double reopenedRatePercentage;

    public AnalyticsOverviewResponse() {}

    public AnalyticsOverviewResponse(long totalCases, long openCases, long resolvedCases, long closedCases,
                                     long slaBreachedCases, double slaMetPercentage,
                                     double avgResolutionTimeHours, double reopenedRatePercentage) {
        this.totalCases = totalCases;
        this.openCases = openCases;
        this.resolvedCases = resolvedCases;
        this.closedCases = closedCases;
        this.slaBreachedCases = slaBreachedCases;
        this.slaMetPercentage = Math.round(slaMetPercentage * 100.0) / 100.0;
        this.avgResolutionTimeHours = Math.round(avgResolutionTimeHours * 100.0) / 100.0;
        this.reopenedRatePercentage = Math.round(reopenedRatePercentage * 100.0) / 100.0;
    }

    public long getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(long totalCases) {
        this.totalCases = totalCases;
    }

    public long getOpenCases() {
        return openCases;
    }

    public void setOpenCases(long openCases) {
        this.openCases = openCases;
    }

    public long getResolvedCases() {
        return resolvedCases;
    }

    public void setResolvedCases(long resolvedCases) {
        this.resolvedCases = resolvedCases;
    }

    public long getClosedCases() {
        return closedCases;
    }

    public void setClosedCases(long closedCases) {
        this.closedCases = closedCases;
    }

    public long getSlaBreachedCases() {
        return slaBreachedCases;
    }

    public void setSlaBreachedCases(long slaBreachedCases) {
        this.slaBreachedCases = slaBreachedCases;
    }

    public double getSlaMetPercentage() {
        return slaMetPercentage;
    }

    public void setSlaMetPercentage(double slaMetPercentage) {
        this.slaMetPercentage = slaMetPercentage;
    }

    public double getAvgResolutionTimeHours() {
        return avgResolutionTimeHours;
    }

    public void setAvgResolutionTimeHours(double avgResolutionTimeHours) {
        this.avgResolutionTimeHours = avgResolutionTimeHours;
    }

    public double getReopenedRatePercentage() {
        return reopenedRatePercentage;
    }

    public void setReopenedRatePercentage(double reopenedRatePercentage) {
        this.reopenedRatePercentage = reopenedRatePercentage;
    }
}
