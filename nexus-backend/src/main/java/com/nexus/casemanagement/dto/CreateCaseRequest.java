package com.nexus.casemanagement.dto;

import com.nexus.casemanagement.entity.Priority;
import com.nexus.casemanagement.entity.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public class CreateCaseRequest {

    @NotBlank(message = "Case title is required")
    @Size(max = 100, message = "Case title must not exceed 100 characters")
    private String title;

    @NotBlank(message = "Case description is required")
    private String description;

    @NotNull(message = "Category is required")
    private UUID categoryId;

    private UUID subcategoryId;

    @NotNull(message = "Severity is required")
    private Severity severity = Severity.MEDIUM;

    private Priority priority = Priority.MEDIUM;

    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    public CreateCaseRequest() {}

    public CreateCaseRequest(String title, String description, UUID categoryId, UUID subcategoryId,
                             Severity severity, Priority priority, String location) {
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.subcategoryId = subcategoryId;
        this.severity = severity != null ? severity : Severity.MEDIUM;
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public UUID getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(UUID subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
