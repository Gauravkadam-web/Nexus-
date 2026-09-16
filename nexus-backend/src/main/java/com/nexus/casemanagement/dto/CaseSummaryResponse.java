package com.nexus.casemanagement.dto;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.entity.Priority;
import com.nexus.casemanagement.entity.Severity;

import java.time.Instant;
import java.util.UUID;

public class CaseSummaryResponse {

    private UUID id;
    private String caseNumber;
    private String title;
    private String categoryName;
    private Severity severity;
    private Priority priority;
    private CaseStatus status;
    private String requesterName;
    private String assignedTeamName;
    private String assignedUserName;
    private Instant createdAt;
    private Instant updatedAt;

    public CaseSummaryResponse() {}

    public static CaseSummaryResponse fromEntity(Case c) {
        CaseSummaryResponse dto = new CaseSummaryResponse();
        dto.setId(c.getId());
        dto.setCaseNumber(c.getCaseNumber());
        dto.setTitle(c.getTitle());
        if (c.getCategory() != null) {
            dto.setCategoryName(c.getCategory().getName());
        }
        dto.setSeverity(c.getSeverity());
        dto.setPriority(c.getPriority());
        dto.setStatus(c.getStatus());
        if (c.getRequester() != null) {
            dto.setRequesterName(c.getRequester().getName());
        }
        if (c.getAssignedTeam() != null) {
            dto.setAssignedTeamName(c.getAssignedTeam().getName());
        }
        if (c.getAssignedUser() != null) {
            dto.setAssignedUserName(c.getAssignedUser().getName());
        }
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());
        return dto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getAssignedTeamName() {
        return assignedTeamName;
    }

    public void setAssignedTeamName(String assignedTeamName) {
        this.assignedTeamName = assignedTeamName;
    }

    public String getAssignedUserName() {
        return assignedUserName;
    }

    public void setAssignedUserName(String assignedUserName) {
        this.assignedUserName = assignedUserName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
