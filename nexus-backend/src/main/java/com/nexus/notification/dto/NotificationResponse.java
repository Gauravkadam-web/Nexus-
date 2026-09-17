package com.nexus.notification.dto;

import com.nexus.notification.entity.Notification;
import com.nexus.notification.entity.NotificationType;
import java.time.Instant;
import java.util.UUID;

public class NotificationResponse {

    private UUID id;
    private UUID userId;
    private UUID caseId;
    private String caseNumber;
    private NotificationType type;
    private String title;
    private String message;
    private boolean read;
    private Instant createdAt;

    public NotificationResponse() {
    }

    public static NotificationResponse fromEntity(Notification notif) {
        NotificationResponse resp = new NotificationResponse();
        resp.setId(notif.getId());
        resp.setUserId(notif.getUser().getId());
        if (notif.getCaseEntity() != null) {
            resp.setCaseId(notif.getCaseEntity().getId());
            resp.setCaseNumber(notif.getCaseEntity().getCaseNumber());
        }
        resp.setType(notif.getType());
        resp.setTitle(notif.getTitle());
        resp.setMessage(notif.getMessage());
        resp.setRead(notif.isRead());
        resp.setCreatedAt(notif.getCreatedAt());
        return resp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getCaseId() {
        return caseId;
    }

    public void setCaseId(UUID caseId) {
        this.caseId = caseId;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
