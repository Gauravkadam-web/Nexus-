package com.nexus.resolution.entity;

import com.nexus.casemanagement.entity.Case;
import com.nexus.user.entity.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resolutions")
public class Resolution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by", nullable = false)
    private User submittedBy;

    @Column(name = "what_was_done", nullable = false, columnDefinition = "TEXT")
    private String whatWasDone;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String findings;

    @Column(name = "evidence_ref", length = 255)
    private String evidenceRef;

    @Column(columnDefinition = "TEXT")
    private String limitations;

    @Column(name = "resolution_message", nullable = false, columnDefinition = "TEXT")
    private String resolutionMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "requester_decision", nullable = false, length = 30)
    private RequesterDecision requesterDecision = RequesterDecision.PENDING;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "decided_at")
    private Instant decidedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Resolution() {
    }

    public Resolution(Case caseEntity, User submittedBy, String whatWasDone, String findings,
                      String evidenceRef, String limitations, String resolutionMessage) {
        this.caseEntity = caseEntity;
        this.submittedBy = submittedBy;
        this.whatWasDone = whatWasDone;
        this.findings = findings;
        this.evidenceRef = evidenceRef;
        this.limitations = limitations;
        this.resolutionMessage = resolutionMessage;
        this.requesterDecision = RequesterDecision.PENDING;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Case getCaseEntity() {
        return caseEntity;
    }

    public void setCaseEntity(Case caseEntity) {
        this.caseEntity = caseEntity;
    }

    public User getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(User submittedBy) {
        this.submittedBy = submittedBy;
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
