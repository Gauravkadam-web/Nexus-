package com.nexus.collaboration.entity;

import com.nexus.casemanagement.entity.Case;
import com.nexus.user.entity.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing an investigation record capturing observations, actions taken, findings, and follow-ups.
 */
@Entity
@Table(name = "investigations")
public class Investigation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", nullable = false)
    private User operator;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String observation;

    @Column(name = "action_taken", nullable = false, columnDefinition = "TEXT")
    private String actionTaken;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String finding;

    @Column(name = "evidence_ref", length = 500)
    private String evidenceRef;

    @Column(name = "follow_up_needed", columnDefinition = "TEXT")
    private String followUpNeeded;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Investigation() {}

    public Investigation(Case caseEntity, User operator, String observation, String actionTaken, String finding, String evidenceRef, String followUpNeeded) {
        this.caseEntity = caseEntity;
        this.operator = operator;
        this.observation = observation;
        this.actionTaken = actionTaken;
        this.finding = finding;
        this.evidenceRef = evidenceRef;
        this.followUpNeeded = followUpNeeded;
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

    public User getOperator() {
        return operator;
    }

    public void setOperator(User operator) {
        this.operator = operator;
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
