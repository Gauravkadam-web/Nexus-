package com.nexus.resolution.dto;

public class ConfirmResolutionRequest {

    private String feedback;

    public ConfirmResolutionRequest() {
    }

    public ConfirmResolutionRequest(String feedback) {
        this.feedback = feedback;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
