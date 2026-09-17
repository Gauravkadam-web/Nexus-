package com.nexus.resolution.dto;

import jakarta.validation.constraints.NotBlank;

public class RejectResolutionRequest {

    @NotBlank(message = "Rejection reason is required")
    private String rejectionReason;

    public RejectResolutionRequest() {
    }

    public RejectResolutionRequest(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
