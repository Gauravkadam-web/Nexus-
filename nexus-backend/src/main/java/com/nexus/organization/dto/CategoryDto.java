package com.nexus.organization.dto;

import com.nexus.organization.entity.Category;

import java.time.Instant;
import java.util.UUID;

public class CategoryDto {

    private UUID id;
    private UUID organizationId;
    private String name;
    private UUID parentCategoryId;
    private String parentCategoryName;
    private UUID defaultTeamId;
    private String defaultTeamName;
    private Instant createdAt;

    public CategoryDto() {}

    public static CategoryDto fromEntity(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        if (category.getOrganization() != null) {
            dto.setOrganizationId(category.getOrganization().getId());
        }
        dto.setName(category.getName());
        if (category.getParentCategory() != null) {
            dto.setParentCategoryId(category.getParentCategory().getId());
            dto.setParentCategoryName(category.getParentCategory().getName());
        }
        if (category.getDefaultTeam() != null) {
            dto.setDefaultTeamId(category.getDefaultTeam().getId());
            dto.setDefaultTeamName(category.getDefaultTeam().getName());
        }
        dto.setCreatedAt(category.getCreatedAt());
        return dto;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(UUID parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getParentCategoryName() {
        return parentCategoryName;
    }

    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }

    public UUID getDefaultTeamId() {
        return defaultTeamId;
    }

    public void setDefaultTeamId(UUID defaultTeamId) {
        this.defaultTeamId = defaultTeamId;
    }

    public String getDefaultTeamName() {
        return defaultTeamName;
    }

    public void setDefaultTeamName(String defaultTeamName) {
        this.defaultTeamName = defaultTeamName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
