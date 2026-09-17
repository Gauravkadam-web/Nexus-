package com.nexus.casemanagement.dto;

import com.nexus.casemanagement.entity.RelationType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Request payload for creating a relation between cases (US-17, US-18).
 */
public class CreateCaseRelationRequest {

    @NotNull(message = "relatedCaseId is required")
    private UUID relatedCaseId;

    @NotNull(message = "relationType is required")
    private RelationType relationType;

    private String notes;

    public CreateCaseRelationRequest() {}

    public CreateCaseRelationRequest(UUID relatedCaseId, RelationType relationType, String notes) {
        this.relatedCaseId = relatedCaseId;
        this.relationType = relationType;
        this.notes = notes;
    }

    public UUID getRelatedCaseId() {
        return relatedCaseId;
    }

    public void setRelatedCaseId(UUID relatedCaseId) {
        this.relatedCaseId = relatedCaseId;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
