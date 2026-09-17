package com.nexus.collaboration.dto;

import com.nexus.collaboration.entity.CaseTask;
import com.nexus.collaboration.entity.TaskPriority;
import com.nexus.collaboration.entity.TaskStatus;

import java.time.Instant;
import java.util.UUID;

public class TaskResponse {

    private UUID id;
    private UUID caseId;
    private String caseNumber;
    private String title;
    private String description;
    private UUID assigneeId;
    private String assigneeName;
    private String assigneeEmail;
    private TaskStatus status;
    private TaskPriority priority;
    private Instant dueDate;
    private Instant completedAt;
    private Instant createdAt;
    private Instant updatedAt;

    public TaskResponse() {}

    public static TaskResponse fromEntity(CaseTask task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        if (task.getCaseEntity() != null) {
            response.setCaseId(task.getCaseEntity().getId());
            response.setCaseNumber(task.getCaseEntity().getCaseNumber());
        }
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        if (task.getAssignee() != null) {
            response.setAssigneeId(task.getAssignee().getId());
            response.setAssigneeName(task.getAssignee().getName());
            response.setAssigneeEmail(task.getAssignee().getEmail());
        }
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate());
        response.setCompletedAt(task.getCompletedAt());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
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

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
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

    public UUID getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(UUID assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getAssigneeEmail() {
        return assigneeEmail;
    }

    public void setAssigneeEmail(String assigneeEmail) {
        this.assigneeEmail = assigneeEmail;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
