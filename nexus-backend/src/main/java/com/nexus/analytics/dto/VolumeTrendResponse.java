package com.nexus.analytics.dto;

public class VolumeTrendResponse {

    private String period;
    private long createdCount;
    private long resolvedCount;
    private long breachedCount;

    public VolumeTrendResponse() {}

    public VolumeTrendResponse(String period, long createdCount, long resolvedCount, long breachedCount) {
        this.period = period;
        this.createdCount = createdCount;
        this.resolvedCount = resolvedCount;
        this.breachedCount = breachedCount;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public long getCreatedCount() {
        return createdCount;
    }

    public void setCreatedCount(long createdCount) {
        this.createdCount = createdCount;
    }

    public long getResolvedCount() {
        return resolvedCount;
    }

    public void setResolvedCount(long resolvedCount) {
        this.resolvedCount = resolvedCount;
    }

    public long getBreachedCount() {
        return breachedCount;
    }

    public void setBreachedCount(long breachedCount) {
        this.breachedCount = breachedCount;
    }
}
