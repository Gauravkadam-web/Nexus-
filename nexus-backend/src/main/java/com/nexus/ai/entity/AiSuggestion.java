package com.nexus.ai.entity;

import com.nexus.casemanagement.entity.Case;
import com.nexus.user.entity.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * An individual AI recommendation that an Operator must explicitly Accept, Modify, or Reject (US-14).
 * Status is PENDING on creation and transitions once — decisions are immutable.
 * The {@code suggested_value} and {@code modified_value} columns carry JSON payloads.
 */
@Entity
@Table(name = "ai_suggestions")
public class AiSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "suggestion_type", nullable = false, length = 30)
    private SuggestionType suggestionType;

    /** JSON payload of the AI-suggested value, e.g. {"priority":"HIGH","confidence":0.82}. */
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "suggested_value", nullable = false, columnDefinition = "jsonb")
    private String suggestedValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SuggestionStatus status = SuggestionStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decided_by")
    private User decidedBy;

    @Column(name = "override_reason")
    private String overrideReason;

    /** JSON payload of the operator's modified value, populated only when status = MODIFIED. */
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "modified_value", columnDefinition = "jsonb")
    private String modifiedValue;

    @Column(name = "decided_at")
    private Instant decidedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public AiSuggestion() {}

    public AiSuggestion(Case caseEntity, SuggestionType suggestionType, String suggestedValue) {
        this.caseEntity = caseEntity;
        this.suggestionType = suggestionType;
        this.suggestedValue = suggestedValue;
        this.status = SuggestionStatus.PENDING;
    }

    // --- Getters & Setters ---

    public UUID getId() { return id; }

    public Case getCaseEntity() { return caseEntity; }
    public void setCaseEntity(Case caseEntity) { this.caseEntity = caseEntity; }

    public SuggestionType getSuggestionType() { return suggestionType; }
    public void setSuggestionType(SuggestionType suggestionType) { this.suggestionType = suggestionType; }

    public String getSuggestedValue() { return suggestedValue; }
    public void setSuggestedValue(String suggestedValue) { this.suggestedValue = suggestedValue; }

    public SuggestionStatus getStatus() { return status; }
    public void setStatus(SuggestionStatus status) { this.status = status; }

    public User getDecidedBy() { return decidedBy; }
    public void setDecidedBy(User decidedBy) { this.decidedBy = decidedBy; }

    public String getOverrideReason() { return overrideReason; }
    public void setOverrideReason(String overrideReason) { this.overrideReason = overrideReason; }

    public String getModifiedValue() { return modifiedValue; }
    public void setModifiedValue(String modifiedValue) { this.modifiedValue = modifiedValue; }

    public Instant getDecidedAt() { return decidedAt; }
    public void setDecidedAt(Instant decidedAt) { this.decidedAt = decidedAt; }

    public Instant getCreatedAt() { return createdAt; }
}
