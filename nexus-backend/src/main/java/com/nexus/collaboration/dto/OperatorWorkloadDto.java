package com.nexus.collaboration.dto;

import java.util.UUID;

public class OperatorWorkloadDto {

    private UUID operatorId;
    private String operatorName;
    private String operatorEmail;
    private long assignedCasesCount;
    private long pendingTasksCount;
    private long inProgressTasksCount;

    public OperatorWorkloadDto() {}

    public OperatorWorkloadDto(UUID operatorId, String operatorName, String operatorEmail, long assignedCasesCount, long pendingTasksCount, long inProgressTasksCount) {
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.operatorEmail = operatorEmail;
        this.assignedCasesCount = assignedCasesCount;
        this.pendingTasksCount = pendingTasksCount;
        this.inProgressTasksCount = inProgressTasksCount;
    }

    public UUID getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(UUID operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorEmail() {
        return operatorEmail;
    }

    public void setOperatorEmail(String operatorEmail) {
        this.operatorEmail = operatorEmail;
    }

    public long getAssignedCasesCount() {
        return assignedCasesCount;
    }

    public void setAssignedCasesCount(long assignedCasesCount) {
        this.assignedCasesCount = assignedCasesCount;
    }

    public long getPendingTasksCount() {
        return pendingTasksCount;
    }

    public void setPendingTasksCount(long pendingTasksCount) {
        this.pendingTasksCount = pendingTasksCount;
    }

    public long getInProgressTasksCount() {
        return inProgressTasksCount;
    }

    public void setInProgressTasksCount(long inProgressTasksCount) {
        this.inProgressTasksCount = inProgressTasksCount;
    }
}
