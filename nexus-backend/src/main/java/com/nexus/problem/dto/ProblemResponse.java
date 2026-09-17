package com.nexus.problem.dto;

import com.nexus.problem.entity.Problem;
import com.nexus.problem.entity.ProblemStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ProblemResponse {

    private UUID id;
    private UUID organizationId;
    private String title;
    private String suspectedRootCause;
    private String confirmedRootCause;
    private String investigationNotes;
    private String correctiveAction;
    private String preventiveAction;
    private ProblemStatus status;
    private UUID createdById;
    private String createdByName;
    private int linkedIncidentCount;
    private List<UUID> linkedIncidentIds;
    private Instant createdAt;
    private Instant updatedAt;

    public ProblemResponse() {
    }

    public static ProblemResponse fromEntity(Problem problem) {
        ProblemResponse resp = new ProblemResponse();
        resp.setId(problem.getId());
        resp.setOrganizationId(problem.getOrganization() != null ? problem.getOrganization().getId() : null);
        resp.setTitle(problem.getTitle());
        resp.setSuspectedRootCause(problem.getSuspectedRootCause());
        resp.setConfirmedRootCause(problem.getConfirmedRootCause());
        resp.setInvestigationNotes(problem.getInvestigationNotes());
        resp.setCorrectiveAction(problem.getCorrectiveAction());
        resp.setPreventiveAction(problem.getPreventiveAction());
        resp.setStatus(problem.getStatus());
        if (problem.getCreatedBy() != null) {
            resp.setCreatedById(problem.getCreatedBy().getId());
            resp.setCreatedByName(problem.getCreatedBy().getName());
        }
        if (problem.getIncidentRelations() != null) {
            resp.setLinkedIncidentCount(problem.getIncidentRelations().size());
            resp.setLinkedIncidentIds(problem.getIncidentRelations().stream()
                    .map(rel -> rel.getCaseEntity().getId())
                    .toList());
        }
        resp.setCreatedAt(problem.getCreatedAt());
        resp.setUpdatedAt(problem.getUpdatedAt());
        return resp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSuspectedRootCause() {
        return suspectedRootCause;
    }

    public void setSuspectedRootCause(String suspectedRootCause) {
        this.suspectedRootCause = suspectedRootCause;
    }

    public String getConfirmedRootCause() {
        return confirmedRootCause;
    }

    public void setConfirmedRootCause(String confirmedRootCause) {
        this.confirmedRootCause = confirmedRootCause;
    }

    public String getInvestigationNotes() {
        return investigationNotes;
    }

    public void setInvestigationNotes(String investigationNotes) {
        this.investigationNotes = investigationNotes;
    }

    public String getCorrectiveAction() {
        return correctiveAction;
    }

    public void setCorrectiveAction(String correctiveAction) {
        this.correctiveAction = correctiveAction;
    }

    public String getPreventiveAction() {
        return preventiveAction;
    }

    public void setPreventiveAction(String preventiveAction) {
        this.preventiveAction = preventiveAction;
    }

    public ProblemStatus getStatus() {
        return status;
    }

    public void setStatus(ProblemStatus status) {
        this.status = status;
    }

    public UUID getCreatedById() {
        return createdById;
    }

    public void setCreatedById(UUID createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public int getLinkedIncidentCount() {
        return linkedIncidentCount;
    }

    public void setLinkedIncidentCount(int linkedIncidentCount) {
        this.linkedIncidentCount = linkedIncidentCount;
    }

    public List<UUID> getLinkedIncidentIds() {
        return linkedIncidentIds;
    }

    public void setLinkedIncidentIds(List<UUID> linkedIncidentIds) {
        this.linkedIncidentIds = linkedIncidentIds;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
