package com.nexus.casemanagement.dto;

import com.nexus.casemanagement.entity.CaseRelation;
import com.nexus.casemanagement.entity.RelationType;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload representing a relationship between two cases (US-17, US-18).
 */
public class CaseRelationResponse {

    private UUID id;
    private UUID caseId;
    private String caseNumber;
    private String caseTitle;
    private UUID relatedCaseId;
    private String relatedCaseNumber;
    private String relatedCaseTitle;
    private RelationType relationType;
    private String linkedByName;
    private String notes;
    private Instant createdAt;

    public CaseRelationResponse() {}

    public static CaseRelationResponse fromEntity(CaseRelation rel) {
        CaseRelationResponse resp = new CaseRelationResponse();
        resp.setId(rel.getId());
        if (rel.getCaseEntity() != null) {
            resp.setCaseId(rel.getCaseEntity().getId());
            resp.setCaseNumber(rel.getCaseEntity().getCaseNumber());
            resp.setCaseTitle(rel.getCaseEntity().getTitle());
        }
        if (rel.getRelatedCaseEntity() != null) {
            resp.setRelatedCaseId(rel.getRelatedCaseEntity().getId());
            resp.setRelatedCaseNumber(rel.getRelatedCaseEntity().getCaseNumber());
            resp.setRelatedCaseTitle(rel.getRelatedCaseEntity().getTitle());
        }
        resp.setRelationType(rel.getRelationType());
        resp.setLinkedByName(rel.getLinkedBy() != null ? rel.getLinkedBy().getName() : "AI System");
        resp.setNotes(rel.getNotes());
        resp.setCreatedAt(rel.getCreatedAt());
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

    public String getCaseTitle() {
        return caseTitle;
    }

    public void setCaseTitle(String caseTitle) {
        this.caseTitle = caseTitle;
    }

    public UUID getRelatedCaseId() {
        return relatedCaseId;
    }

    public void setRelatedCaseId(UUID relatedCaseId) {
        this.relatedCaseId = relatedCaseId;
    }

    public String getRelatedCaseNumber() {
        return relatedCaseNumber;
    }

    public void setRelatedCaseNumber(String relatedCaseNumber) {
        this.relatedCaseNumber = relatedCaseNumber;
    }

    public String getRelatedCaseTitle() {
        return relatedCaseTitle;
    }

    public void setRelatedCaseTitle(String relatedCaseTitle) {
        this.relatedCaseTitle = relatedCaseTitle;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    public String getLinkedByName() {
        return linkedByName;
    }

    public void setLinkedByName(String linkedByName) {
        this.linkedByName = linkedByName;
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
