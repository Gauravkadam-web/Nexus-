package com.nexus.resolution.dto;

import com.nexus.resolution.entity.RequesterDecision;
import com.nexus.resolution.entity.Resolution;
import java.time.Instant;
import java.util.UUID;

public class ResolutionResponse {

    private UUID id;
    private UUID caseId;
    private String caseNumber;
    private UUID submittedById;
    private String submittedByName;
    private String whatWasDone;
    private String findings;
    private String evidenceRef;
    private String limitations;
    private String resolutionMessage;
    private RequesterDecision requesterDecision;
    private String feedback;
    private Instant decidedAt;
    private Instant createdAt;

    public ResolutionResponse() {
    }

    public static ResolutionResponse fromEntity(Resolution res) {
        ResolutionResponse resp = new ResolutionResponse();
        resp.setId(res.getId());
        resp.setCaseId(res.getCaseEntity().getId());
        resp.setCaseNumber(res.getCaseEntity().getCaseNumber());
        if (res.getSubmittedBy() != null) {
            resp.setSubmittedById(res.getSubmittedBy().getId());
            resp.setSubmittedByName(res.getSubmittedBy().getName());
        }
        resp.setWhatWasDone(res.getWhatWasDone());
        resp.setFindings(res.getFindings());
        resp.setEvidenceRef(res.getEvidenceRef());
        resp.setLimitations(res.getLimitations());
        resp.setResolutionMessage(res.getResolutionMessage());
        resp.setRequesterDecision(res.getRequesterDecision());
        resp.setFeedback(res.getFeedback());
        resp.setDecidedAt(res.getDecidedAt());
        resp.setCreatedAt(res.getCreatedAt());
        return resp;
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

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public UUID getSubmittedById() {
        return submittedById;
    }

    public void setSubmittedById(UUID submittedById) {
        this.submittedById = submittedById;
    }

    public String getSubmittedByName() {
        return submittedByName;
    }

    public void setSubmittedByName(String submittedByName) {
        this.submittedByName = submittedByName;
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

    public RequesterDecision getRequesterDecision() {
        return requesterDecision;
    }

    public void setRequesterDecision(RequesterDecision requesterDecision) {
        this.requesterDecision = requesterDecision;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(Instant decidedAt) {
        this.decidedAt = decidedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
