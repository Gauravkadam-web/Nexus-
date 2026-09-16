package com.nexus.casemanagement.dto;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.entity.Priority;
import com.nexus.casemanagement.entity.Severity;
import com.nexus.organization.dto.CategoryDto;
import com.nexus.organization.dto.TeamDto;
import com.nexus.user.dto.UserDto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class CaseDetailResponse {

    private UUID id;
    private String caseNumber;
    private String title;
    private String description;
    private CategoryDto category;
    private CategoryDto subcategory;
    private Severity severity;
    private Priority priority;
    private CaseStatus status;
    private UserDto requester;
    private TeamDto assignedTeam;
    private UserDto assignedUser;
    private String location;
    private Instant resolvedAt;
    private Instant closedAt;
    private Instant createdAt;
    private Instant updatedAt;
    private Set<CaseStatus> nextPossibleStatuses;

    public CaseDetailResponse() {}

    public static CaseDetailResponse fromEntity(Case c, Set<CaseStatus> nextPossibleStatuses) {
        CaseDetailResponse dto = new CaseDetailResponse();
        dto.setId(c.getId());
        dto.setCaseNumber(c.getCaseNumber());
        dto.setTitle(c.getTitle());
        dto.setDescription(c.getDescription());
        if (c.getCategory() != null) {
            dto.setCategory(CategoryDto.fromEntity(c.getCategory()));
        }
        if (c.getSubcategory() != null) {
            dto.setSubcategory(CategoryDto.fromEntity(c.getSubcategory()));
        }
        dto.setSeverity(c.getSeverity());
        dto.setPriority(c.getPriority());
        dto.setStatus(c.getStatus());
        if (c.getRequester() != null) {
            dto.setRequester(UserDto.fromEntity(c.getRequester()));
        }
        if (c.getAssignedTeam() != null) {
            dto.setAssignedTeam(TeamDto.fromEntity(c.getAssignedTeam()));
        }
        if (c.getAssignedUser() != null) {
            dto.setAssignedUser(UserDto.fromEntity(c.getAssignedUser()));
        }
        dto.setLocation(c.getLocation());
        dto.setResolvedAt(c.getResolvedAt());
        dto.setClosedAt(c.getClosedAt());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());
        dto.setNextPossibleStatuses(nextPossibleStatuses);
        return dto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    public CategoryDto getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(CategoryDto subcategory) {
        this.subcategory = subcategory;
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

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }

    public UserDto getRequester() {
        return requester;
    }

    public void setRequester(UserDto requester) {
        this.requester = requester;
    }

    public TeamDto getAssignedTeam() {
        return assignedTeam;
    }

    public void setAssignedTeam(TeamDto assignedTeam) {
        this.assignedTeam = assignedTeam;
    }

    public UserDto getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(UserDto assignedUser) {
        this.assignedUser = assignedUser;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
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

    public Set<CaseStatus> getNextPossibleStatuses() {
        return nextPossibleStatuses;
    }

    public void setNextPossibleStatuses(Set<CaseStatus> nextPossibleStatuses) {
        this.nextPossibleStatuses = nextPossibleStatuses;
    }
}
