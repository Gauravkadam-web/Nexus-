package com.nexus.collaboration.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamWorkloadResponse {

    private UUID teamId;
    private String teamName;
    private long totalOpenCases;
    private long casesWaitingForInfo;
    private long totalPendingTasks;
    private long unassignedTasksCount;
    private List<OperatorWorkloadDto> operators = new ArrayList<>();

    public TeamWorkloadResponse() {}

    public TeamWorkloadResponse(UUID teamId, String teamName, long totalOpenCases, long casesWaitingForInfo, long totalPendingTasks, long unassignedTasksCount, List<OperatorWorkloadDto> operators) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.totalOpenCases = totalOpenCases;
        this.casesWaitingForInfo = casesWaitingForInfo;
        this.totalPendingTasks = totalPendingTasks;
        this.unassignedTasksCount = unassignedTasksCount;
        this.operators = operators != null ? operators : new ArrayList<>();
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

    public long getTotalOpenCases() {
        return totalOpenCases;
    }

    public void setTotalOpenCases(long totalOpenCases) {
        this.totalOpenCases = totalOpenCases;
    }

    public long getCasesWaitingForInfo() {
        return casesWaitingForInfo;
    }

    public void setCasesWaitingForInfo(long casesWaitingForInfo) {
        this.casesWaitingForInfo = casesWaitingForInfo;
    }

    public long getTotalPendingTasks() {
        return totalPendingTasks;
    }

    public void setTotalPendingTasks(long totalPendingTasks) {
        this.totalPendingTasks = totalPendingTasks;
    }

    public long getUnassignedTasksCount() {
        return unassignedTasksCount;
    }

    public void setUnassignedTasksCount(long unassignedTasksCount) {
        this.unassignedTasksCount = unassignedTasksCount;
    }

    public List<OperatorWorkloadDto> getOperators() {
        return operators;
    }

    public void setOperators(List<OperatorWorkloadDto> operators) {
        this.operators = operators;
    }
}
