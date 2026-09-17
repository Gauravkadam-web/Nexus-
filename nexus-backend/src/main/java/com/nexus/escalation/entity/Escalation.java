package com.nexus.escalation.entity;

import com.nexus.casemanagement.entity.Case;
import com.nexus.user.entity.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "escalations")
public class Escalation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escalation_rule_id")
    private EscalationRule escalationRule;

    @Enumerated(EnumType.STRING)
    @Column(name = "escalation_level", nullable = false, length = 30)
    private EscalationLevel escalationLevel;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EscalationStatus status = EscalationStatus.RECOMMENDED;

    @Enumerated(EnumType.STRING)
    @Column(name = "triggered_by", nullable = false, length = 30)
    private TriggeredBy triggeredBy = TriggeredBy.SYSTEM;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_by")
    private User confirmedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Escalation() {
    }

    public Escalation(Case caseEntity, EscalationRule escalationRule, EscalationLevel escalationLevel,
                      String reason, EscalationStatus status, TriggeredBy triggeredBy, User confirmedBy) {
        this.caseEntity = caseEntity;
        this.escalationRule = escalationRule;
        this.escalationLevel = escalationLevel;
        this.reason = reason;
        this.status = status;
        this.triggeredBy = triggeredBy;
        this.confirmedBy = confirmedBy;
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

    public EscalationRule getEscalationRule() {
        return escalationRule;
    }

    public void setEscalationRule(EscalationRule escalationRule) {
        this.escalationRule = escalationRule;
    }

    public EscalationLevel getEscalationLevel() {
        return escalationLevel;
    }

    public void setEscalationLevel(EscalationLevel escalationLevel) {
        this.escalationLevel = escalationLevel;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public EscalationStatus getStatus() {
        return status;
    }

    public void setStatus(EscalationStatus status) {
        this.status = status;
    }

    public TriggeredBy getTriggeredBy() {
        return triggeredBy;
    }

    public void setTriggeredBy(TriggeredBy triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    public User getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(User confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
