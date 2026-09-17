package com.nexus.collaboration.dto;

import com.nexus.collaboration.entity.InternalNote;

import java.time.Instant;
import java.util.UUID;

public class InternalNoteResponse {

    private UUID id;
    private UUID caseId;
    private UUID authorId;
    private String authorName;
    private String authorEmail;
    private String content;
    private Instant createdAt;

    public InternalNoteResponse() {}

    public static InternalNoteResponse fromEntity(InternalNote note) {
        InternalNoteResponse response = new InternalNoteResponse();
        response.setId(note.getId());
        response.setCaseId(note.getCaseEntity().getId());
        if (note.getAuthor() != null) {
            response.setAuthorId(note.getAuthor().getId());
            response.setAuthorName(note.getAuthor().getName());
            response.setAuthorEmail(note.getAuthor().getEmail());
        }
        response.setContent(note.getContent());
        response.setCreatedAt(note.getCreatedAt());
        return response;
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

    public UUID getAuthorId() {
        return authorId;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
