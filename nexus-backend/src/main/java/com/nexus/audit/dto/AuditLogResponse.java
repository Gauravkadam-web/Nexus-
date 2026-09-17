package com.nexus.audit.dto;

import com.nexus.audit.entity.AuditLog;
import java.time.Instant;
import java.util.UUID;

public class AuditLogResponse {

    private UUID id;
    private String entityType;
    private UUID entityId;
    private String action;
    private UUID actorId;
    private String actorName;
    private String actorRole;
    private String oldValue;
    private String newValue;
    private String source;
    private String ipAddress;
    private Instant createdAt;

    public AuditLogResponse() {
    }

    public static AuditLogResponse fromEntity(AuditLog log) {
        AuditLogResponse resp = new AuditLogResponse();
        resp.setId(log.getId());
        resp.setEntityType(log.getEntityType());
        resp.setEntityId(log.getEntityId());
        resp.setAction(log.getAction());
        if (log.getActor() != null) {
            resp.setActorId(log.getActor().getId());
        }
        resp.setActorName(log.getActorName());
        resp.setActorRole(log.getActorRole());
        resp.setOldValue(log.getOldValue());
        resp.setNewValue(log.getNewValue());
        resp.setSource(log.getSource());
        resp.setIpAddress(log.getIpAddress());
        resp.setCreatedAt(log.getCreatedAt());
        return resp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public UUID getActorId() {
        return actorId;
    }

    public void setActorId(UUID actorId) {
        this.actorId = actorId;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getActorRole() {
        return actorRole;
    }

    public void setActorRole(String actorRole) {
        this.actorRole = actorRole;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
