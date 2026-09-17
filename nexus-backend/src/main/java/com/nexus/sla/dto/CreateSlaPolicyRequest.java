package com.nexus.sla.dto;

import com.nexus.casemanagement.entity.Priority;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CreateSlaPolicyRequest {

    private UUID categoryId;

    @NotNull(message = "Priority is required")
    private Priority priority;

    @NotNull(message = "Response time minutes is required")
    @Min(value = 1, message = "Response time must be at least 1 minute")
    private Integer responseTimeMinutes;

    @NotNull(message = "Resolution time minutes is required")
    @Min(value = 1, message = "Resolution time must be at least 1 minute")
    private Integer resolutionTimeMinutes;

    public CreateSlaPolicyRequest() {
    }

    public CreateSlaPolicyRequest(UUID categoryId, Priority priority, Integer responseTimeMinutes, Integer resolutionTimeMinutes) {
        this.categoryId = categoryId;
        this.priority = priority;
        this.responseTimeMinutes = responseTimeMinutes;
        this.resolutionTimeMinutes = resolutionTimeMinutes;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
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
}
