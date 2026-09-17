package com.nexus.sla.dto;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.entity.Priority;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class BreachedCaseResponse {

    private UUID caseId;
    private String caseNumber;
    private String title;
    private Priority priority;
    private CaseStatus status;
    private String assignedOperatorName;
    private String assignedTeamName;
    private Instant responseDeadline;
    private Instant resolutionDeadline;
    private Long breachDurationMinutes;

    public BreachedCaseResponse() {
    }

    public BreachedCaseResponse(Case caseEntity, Instant responseDeadline, Instant resolutionDeadline) {
        this.caseId = caseEntity.getId();
        this.caseNumber = caseEntity.getCaseNumber();
        this.title = caseEntity.getTitle();
        this.priority = caseEntity.getPriority();
        this.status = caseEntity.getStatus();
        this.assignedOperatorName = caseEntity.getAssignedUser() != null ? caseEntity.getAssignedUser().getName() : "Unassigned";
        this.assignedTeamName = caseEntity.getAssignedTeam() != null ? caseEntity.getAssignedTeam().getName() : "Unassigned";
        this.responseDeadline = responseDeadline;
        this.resolutionDeadline = resolutionDeadline;

        Instant now = Instant.now();
        if (resolutionDeadline != null && now.isAfter(resolutionDeadline)) {
            this.breachDurationMinutes = Duration.between(resolutionDeadline, now).toMinutes();
        } else if (responseDeadline != null && now.isAfter(responseDeadline)) {
            this.breachDurationMinutes = Duration.between(responseDeadline, now).toMinutes();
        } else {
            this.breachDurationMinutes = 0L;
        }
    }

    public UUID getCaseId() {
        return caseId;
    }

    public void setCaseId(UUID caseId) {
        this.caseId = caseId;
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

    public String getAssignedOperatorName() {
        return assignedOperatorName;
    }

    public void setAssignedOperatorName(String assignedOperatorName) {
        this.assignedOperatorName = assignedOperatorName;
    }

    public String getAssignedTeamName() {
        return assignedTeamName;
    }

    public void setAssignedTeamName(String assignedTeamName) {
        this.assignedTeamName = assignedTeamName;
    }

    public Instant getResponseDeadline() {
        return responseDeadline;
    }

    public void setResponseDeadline(Instant responseDeadline) {
        this.responseDeadline = responseDeadline;
    }

    public Instant getResolutionDeadline() {
        return resolutionDeadline;
    }

    public void setResolutionDeadline(Instant resolutionDeadline) {
        this.resolutionDeadline = resolutionDeadline;
    }

    public Long getBreachDurationMinutes() {
        return breachDurationMinutes;
    }

    public void setBreachDurationMinutes(Long breachDurationMinutes) {
        this.breachDurationMinutes = breachDurationMinutes;
    }
}
