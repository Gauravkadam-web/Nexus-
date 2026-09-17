package com.nexus.sla.dto;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.entity.Priority;
import com.nexus.sla.entity.RiskLevel;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class AtRiskCaseResponse {

    private UUID caseId;
    private String caseNumber;
    private String title;
    private Priority priority;
    private CaseStatus status;
    private String assignedOperatorName;
    private String assignedTeamName;
    private RiskLevel riskLevel;
    private List<String> riskReasons;
    private Instant resolutionDeadline;
    private Long remainingMinutes;

    public AtRiskCaseResponse() {
    }

    public AtRiskCaseResponse(Case caseEntity, RiskLevel riskLevel, List<String> riskReasons,
                              Instant resolutionDeadline, Long remainingMinutes) {
        this.caseId = caseEntity.getId();
        this.caseNumber = caseEntity.getCaseNumber();
        this.title = caseEntity.getTitle();
        this.priority = caseEntity.getPriority();
        this.status = caseEntity.getStatus();
        this.assignedOperatorName = caseEntity.getAssignedUser() != null ? caseEntity.getAssignedUser().getName() : "Unassigned";
        this.assignedTeamName = caseEntity.getAssignedTeam() != null ? caseEntity.getAssignedTeam().getName() : "Unassigned";
        this.riskLevel = riskLevel;
        this.riskReasons = riskReasons;
        this.resolutionDeadline = resolutionDeadline;
        this.remainingMinutes = remainingMinutes;
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

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public List<String> getRiskReasons() {
        return riskReasons;
    }

    public void setRiskReasons(List<String> riskReasons) {
        this.riskReasons = riskReasons;
    }

    public Instant getResolutionDeadline() {
        return resolutionDeadline;
    }

    public void setResolutionDeadline(Instant resolutionDeadline) {
        this.resolutionDeadline = resolutionDeadline;
    }

    public Long getRemainingMinutes() {
        return remainingMinutes;
    }

    public void setRemainingMinutes(Long remainingMinutes) {
        this.remainingMinutes = remainingMinutes;
    }
}
