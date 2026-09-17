package com.nexus.collaboration.dto;

import com.nexus.collaboration.entity.CaseAttachment;

import java.time.Instant;
import java.util.UUID;

public class AttachmentResponse {

    private UUID id;
    private UUID caseId;
    private UUID uploadedById;
    private String uploadedByName;
    private String storagePath;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private Instant createdAt;

    public AttachmentResponse() {}

    public static AttachmentResponse fromEntity(CaseAttachment attachment) {
        AttachmentResponse response = new AttachmentResponse();
        response.setId(attachment.getId());
        if (attachment.getCaseEntity() != null) {
            response.setCaseId(attachment.getCaseEntity().getId());
        }
        if (attachment.getUploadedBy() != null) {
            response.setUploadedById(attachment.getUploadedBy().getId());
            response.setUploadedByName(attachment.getUploadedBy().getName());
        }
        response.setStoragePath(attachment.getStoragePath());
        response.setFileName(attachment.getFileName());
        response.setFileType(attachment.getFileType());
        response.setFileSize(attachment.getFileSize());
        response.setCreatedAt(attachment.getCreatedAt());
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

    public UUID getUploadedById() {
        return uploadedById;
    }

    public void setUploadedById(UUID uploadedById) {
        this.uploadedById = uploadedById;
    }

    public String getUploadedByName() {
        return uploadedByName;
    }

    public void setUploadedByName(String uploadedByName) {
        this.uploadedByName = uploadedByName;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
