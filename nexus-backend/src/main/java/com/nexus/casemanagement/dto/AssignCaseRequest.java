package com.nexus.casemanagement.dto;

import java.util.UUID;

public class AssignCaseRequest {

    private UUID assignedUserId;
    private UUID assignedTeamId;

    public AssignCaseRequest() {}

    public AssignCaseRequest(UUID assignedUserId, UUID assignedTeamId) {
        this.assignedUserId = assignedUserId;
        this.assignedTeamId = assignedTeamId;
    }

    public UUID getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(UUID assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public UUID getAssignedTeamId() {
        return assignedTeamId;
    }

    public void setAssignedTeamId(UUID assignedTeamId) {
        this.assignedTeamId = assignedTeamId;
    }
}
