package com.nexus.ai.entity;

import com.nexus.casemanagement.entity.Case;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Records every automated job execution for idempotency and observability (SRS §6.7).
 * Scheduled jobs and event-driven AI triggers must check for an existing SUCCESS event
 * before running again — preventing duplicate analysis/summary generation.
 */
@Entity
@Table(name = "automation_events")
public class AutomationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private AutomationEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false, length = 20)
    private AutomationTriggerType triggerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AutomationEventStatus status = AutomationEventStatus.PENDING;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "result", columnDefinition = "jsonb")
    private String result;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (this.startedAt == null) this.startedAt = Instant.now();
    }

    public AutomationEvent() {}

    public AutomationEvent(Case caseEntity, AutomationEventType eventType, AutomationTriggerType triggerType) {
        this.caseEntity = caseEntity;
        this.eventType = eventType;
        this.triggerType = triggerType;
        this.status = AutomationEventStatus.PENDING;
    }

    // --- Getters & Setters ---

    public UUID getId() { return id; }

    public Case getCaseEntity() { return caseEntity; }
    public void setCaseEntity(Case caseEntity) { this.caseEntity = caseEntity; }

    public AutomationEventType getEventType() { return eventType; }
    public void setEventType(AutomationEventType eventType) { this.eventType = eventType; }

    public AutomationTriggerType getTriggerType() { return triggerType; }
    public void setTriggerType(AutomationTriggerType triggerType) { this.triggerType = triggerType; }

    public AutomationEventStatus getStatus() { return status; }
    public void setStatus(AutomationEventStatus status) { this.status = status; }

    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Instant getCreatedAt() { return createdAt; }
}
