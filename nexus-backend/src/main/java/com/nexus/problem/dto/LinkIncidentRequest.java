package com.nexus.problem.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class LinkIncidentRequest {

    @NotNull(message = "Case ID is required")
    private UUID caseId;

    public LinkIncidentRequest() {
    }

    public LinkIncidentRequest(UUID caseId) {
        this.caseId = caseId;
    }

    public UUID getCaseId() {
        return caseId;
    }

    public void setCaseId(UUID caseId) {
        this.caseId = caseId;
    }
}
