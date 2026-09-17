package com.nexus.collaboration.dto;

import jakarta.validation.constraints.NotBlank;

public class InvestigationRequest {

    @NotBlank(message = "Observation is required")
    private String observation;

    @NotBlank(message = "Action taken is required")
    private String actionTaken;

    @NotBlank(message = "Finding is required")
    private String finding;

    private String evidenceRef;

    private String followUpNeeded;

    public InvestigationRequest() {}

    public InvestigationRequest(String observation, String actionTaken, String finding, String evidenceRef, String followUpNeeded) {
        this.observation = observation;
        this.actionTaken = actionTaken;
        this.finding = finding;
        this.evidenceRef = evidenceRef;
        this.followUpNeeded = followUpNeeded;
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
}
