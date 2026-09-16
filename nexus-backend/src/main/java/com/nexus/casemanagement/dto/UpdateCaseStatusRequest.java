package com.nexus.casemanagement.dto;

import com.nexus.casemanagement.entity.CaseStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateCaseStatusRequest {

    @NotNull(message = "New status is required")
    private CaseStatus status;

    @Size(max = 500, message = "Status update reason must not exceed 500 characters")
    private String reason;

    public UpdateCaseStatusRequest() {}

    public UpdateCaseStatusRequest(CaseStatus status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
