package com.nexus.resolution.dto;

import jakarta.validation.constraints.NotBlank;

public class SubmitResolutionRequest {

    @NotBlank(message = "What was done is required")
    private String whatWasDone;

    @NotBlank(message = "Findings are required")
    private String findings;

    private String evidenceRef;

    private String limitations;

    @NotBlank(message = "Resolution message for requester is required")
    private String resolutionMessage;

    public SubmitResolutionRequest() {
    }

    public SubmitResolutionRequest(String whatWasDone, String findings, String evidenceRef, String limitations, String resolutionMessage) {
        this.whatWasDone = whatWasDone;
        this.findings = findings;
        this.evidenceRef = evidenceRef;
        this.limitations = limitations;
        this.resolutionMessage = resolutionMessage;
    }

    public String getWhatWasDone() {
        return whatWasDone;
    }

    public void setWhatWasDone(String whatWasDone) {
        this.whatWasDone = whatWasDone;
    }

    public String getFindings() {
        return findings;
    }

    public void setFindings(String findings) {
        this.findings = findings;
    }

    public String getEvidenceRef() {
        return evidenceRef;
    }

    public void setEvidenceRef(String evidenceRef) {
        this.evidenceRef = evidenceRef;
    }

    public String getLimitations() {
        return limitations;
    }

    public void setLimitations(String limitations) {
        this.limitations = limitations;
    }

    public String getResolutionMessage() {
        return resolutionMessage;
    }

    public void setResolutionMessage(String resolutionMessage) {
        this.resolutionMessage = resolutionMessage;
    }
}
