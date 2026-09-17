package com.nexus.analytics.dto;

import java.util.UUID;

public class TeamWorkloadResponse {

    private UUID teamId;
    private String teamName;
    private long activeCases;
    private long resolvedCases;
    private long slaBreachedCases;
    private String workloadStatus;

    public TeamWorkloadResponse() {}

    public TeamWorkloadResponse(UUID teamId, String teamName, long activeCases,
                                long resolvedCases, long slaBreachedCases, String workloadStatus) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.activeCases = activeCases;
        this.resolvedCases = resolvedCases;
        this.slaBreachedCases = slaBreachedCases;
        this.workloadStatus = workloadStatus;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public long getActiveCases() {
        return activeCases;
    }

    public void setActiveCases(long activeCases) {
        this.activeCases = activeCases;
    }

    public long getResolvedCases() {
        return resolvedCases;
    }

    public void setResolvedCases(long resolvedCases) {
        this.resolvedCases = resolvedCases;
    }

    public long getSlaBreachedCases() {
        return slaBreachedCases;
    }

    public void setSlaBreachedCases(long slaBreachedCases) {
        this.slaBreachedCases = slaBreachedCases;
    }

    public String getWorkloadStatus() {
        return workloadStatus;
    }

    public void setWorkloadStatus(String workloadStatus) {
        this.workloadStatus = workloadStatus;
    }
}
