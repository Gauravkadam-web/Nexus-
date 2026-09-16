package com.nexus.organization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public class CreateCategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 255, message = "Category name must not exceed 255 characters")
    private String name;

    private UUID parentCategoryId;

    private UUID defaultTeamId;

    public CreateCategoryRequest() {}

    public CreateCategoryRequest(String name, UUID parentCategoryId, UUID defaultTeamId) {
        this.name = name;
        this.parentCategoryId = parentCategoryId;
        this.defaultTeamId = defaultTeamId;
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

    public UUID getDefaultTeamId() {
        return defaultTeamId;
    }

    public void setDefaultTeamId(UUID defaultTeamId) {
        this.defaultTeamId = defaultTeamId;
    }
}
