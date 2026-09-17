package com.nexus.problem.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

public class CreateProblemRequest {

    @NotBlank(message = "Problem title is required")
    private String title;

    private String suspectedRootCause;

    private List<UUID> incidentCaseIds;

    public CreateProblemRequest() {
    }

    public CreateProblemRequest(String title, String suspectedRootCause, List<UUID> incidentCaseIds) {
        this.title = title;
        this.suspectedRootCause = suspectedRootCause;
        this.incidentCaseIds = incidentCaseIds;
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

    public List<UUID> getIncidentCaseIds() {
        return incidentCaseIds;
    }

    public void setIncidentCaseIds(List<UUID> incidentCaseIds) {
        this.incidentCaseIds = incidentCaseIds;
    }
}
