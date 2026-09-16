package com.nexus.organization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public class CreateTeamRequest {

    @NotBlank(message = "Team name is required")
    @Size(max = 255, message = "Team name must not exceed 255 characters")
    private String name;

    private UUID leadUserId;

    public CreateTeamRequest() {}

    public CreateTeamRequest(String name, UUID leadUserId) {
        this.name = name;
        this.leadUserId = leadUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getLeadUserId() {
        return leadUserId;
    }

    public void setLeadUserId(UUID leadUserId) {
        this.leadUserId = leadUserId;
    }
}
