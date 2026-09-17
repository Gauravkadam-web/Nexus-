package com.nexus.casemanagement.entity;

import com.nexus.user.entity.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing a relationship between two cases (SRS §6.3, US-17, US-18).
 * Supports DUPLICATE, RELATED, and MASTER_INCIDENT relationship types.
 */
@Entity
@Table(name = "case_relations")
public class CaseRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_case_id", nullable = false)
    private Case relatedCaseEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "relation_type", nullable = false, length = 30)
    private RelationType relationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_by")
    private User linkedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public CaseRelation() {}

    public CaseRelation(Case caseEntity, Case relatedCaseEntity, RelationType relationType, User linkedBy, String notes) {
        this.caseEntity = caseEntity;
        this.relatedCaseEntity = relatedCaseEntity;
        this.relationType = relationType;
        this.linkedBy = linkedBy;
        this.notes = notes;
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

    public Case getRelatedCaseEntity() {
        return relatedCaseEntity;
    }

    public void setRelatedCaseEntity(Case relatedCaseEntity) {
        this.relatedCaseEntity = relatedCaseEntity;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    public User getLinkedBy() {
        return linkedBy;
    }

    public void setLinkedBy(User linkedBy) {
        this.linkedBy = linkedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
