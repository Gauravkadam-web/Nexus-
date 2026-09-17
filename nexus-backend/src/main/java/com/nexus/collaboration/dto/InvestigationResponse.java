package com.nexus.collaboration.dto;

import com.nexus.collaboration.entity.Investigation;

import java.time.Instant;
import java.util.UUID;

public class InvestigationResponse {

    private UUID id;
    private UUID caseId;
    private UUID operatorId;
    private String operatorName;
    private String operatorEmail;
    private String observation;
    private String actionTaken;
    private String finding;
    private String evidenceRef;
    private String followUpNeeded;
    private Instant createdAt;

    public InvestigationResponse() {}

    public static InvestigationResponse fromEntity(Investigation investigation) {
        InvestigationResponse response = new InvestigationResponse();
        response.setId(investigation.getId());
        if (investigation.getCaseEntity() != null) {
            response.setCaseId(investigation.getCaseEntity().getId());
        }
        if (investigation.getOperator() != null) {
            response.setOperatorId(investigation.getOperator().getId());
            response.setOperatorName(investigation.getOperator().getName());
            response.setOperatorEmail(investigation.getOperator().getEmail());
        }
        response.setObservation(investigation.getObservation());
        response.setActionTaken(investigation.getActionTaken());
        response.setFinding(investigation.getFinding());
        response.setEvidenceRef(investigation.getEvidenceRef());
        response.setFollowUpNeeded(investigation.getFollowUpNeeded());
        response.setCreatedAt(investigation.getCreatedAt());
        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCaseId() {
        return caseId;
    }

    public void setCaseId(UUID caseId) {
        this.caseId = caseId;
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

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public String getFinding() {
        return finding;
    }

    public void setFinding(String finding) {
        this.finding = finding;
    }

    public String getEvidenceRef() {
        return evidenceRef;
    }

    public void setEvidenceRef(String evidenceRef) {
        this.evidenceRef = evidenceRef;
    }

    public String getFollowUpNeeded() {
        return followUpNeeded;
    }

    public void setFollowUpNeeded(String followUpNeeded) {
        this.followUpNeeded = followUpNeeded;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
