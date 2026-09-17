package com.nexus.collaboration.dto;

import jakarta.validation.constraints.NotBlank;

public class InternalNoteRequest {

    @NotBlank(message = "Note content is required")
    private String content;

    public InternalNoteRequest() {}

    public InternalNoteRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
