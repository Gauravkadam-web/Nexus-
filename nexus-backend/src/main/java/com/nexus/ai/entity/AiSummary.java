package com.nexus.ai.entity;

import com.nexus.casemanagement.entity.Case;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Versioned AI-generated summary of a case, updated as the case evolves (US-12).
 * Each regeneration creates a new row with an incremented version number;
 * the latest version is the active summary.
 */
@Entity
@Table(name = "ai_summaries")
public class AiSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    @Column(name = "summary_text", nullable = false, columnDefinition = "TEXT")
    private String summaryText;

    @Column(name = "version", nullable = false)
    private int version = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public AiSummary() {}

    public AiSummary(Case caseEntity, String summaryText, int version) {
        this.caseEntity = caseEntity;
        this.summaryText = summaryText;
        this.version = version;
    }

    // --- Getters & Setters ---

    public UUID getId() { return id; }

    public Case getCaseEntity() { return caseEntity; }
    public void setCaseEntity(Case caseEntity) { this.caseEntity = caseEntity; }

    public String getSummaryText() { return summaryText; }
    public void setSummaryText(String summaryText) { this.summaryText = summaryText; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public Instant getCreatedAt() { return createdAt; }
}
