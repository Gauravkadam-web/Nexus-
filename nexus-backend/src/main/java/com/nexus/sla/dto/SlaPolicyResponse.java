package com.nexus.sla.dto;

import com.nexus.casemanagement.entity.Priority;
import com.nexus.sla.entity.SlaPolicy;
import java.time.Instant;
import java.util.UUID;

public class SlaPolicyResponse {

    private UUID id;
    private UUID organizationId;
    private UUID categoryId;
    private String categoryName;
    private Priority priority;
    private Integer responseTimeMinutes;
    private Integer resolutionTimeMinutes;
    private Instant createdAt;

    public SlaPolicyResponse() {
    }

    public static SlaPolicyResponse fromEntity(SlaPolicy policy) {
        SlaPolicyResponse resp = new SlaPolicyResponse();
        resp.setId(policy.getId());
        resp.setOrganizationId(policy.getOrganization() != null ? policy.getOrganization().getId() : null);
        if (policy.getCategory() != null) {
            resp.setCategoryId(policy.getCategory().getId());
            resp.setCategoryName(policy.getCategory().getName());
        }
        resp.setPriority(policy.getPriority());
        resp.setResponseTimeMinutes(policy.getResponseTimeMinutes());
        resp.setResolutionTimeMinutes(policy.getResolutionTimeMinutes());
        resp.setCreatedAt(policy.getCreatedAt());
        return resp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Integer getResponseTimeMinutes() {
        return responseTimeMinutes;
    }

    public void setResponseTimeMinutes(Integer responseTimeMinutes) {
        this.responseTimeMinutes = responseTimeMinutes;
    }

    public Integer getResolutionTimeMinutes() {
        return resolutionTimeMinutes;
    }

    public void setResolutionTimeMinutes(Integer resolutionTimeMinutes) {
        this.resolutionTimeMinutes = resolutionTimeMinutes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
