package com.nexus.organization.dto;

import com.nexus.organization.entity.Team;

import java.time.Instant;
import java.util.UUID;

public class TeamDto {

    private UUID id;
    private UUID organizationId;
    private String name;
    private UUID leadUserId;
    private String leadUserName;
    private int memberCount;
    private Instant createdAt;

    public TeamDto() {}

    public static TeamDto fromEntity(Team team) {
        TeamDto dto = new TeamDto();
        dto.setId(team.getId());
        if (team.getOrganization() != null) {
            dto.setOrganizationId(team.getOrganization().getId());
        }
        dto.setName(team.getName());
        if (team.getLeadUser() != null) {
            dto.setLeadUserId(team.getLeadUser().getId());
            dto.setLeadUserName(team.getLeadUser().getName());
        }
        dto.setMemberCount(team.getMembers() != null ? team.getMembers().size() : 0);
        dto.setCreatedAt(team.getCreatedAt());
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

    public UUID getLeadUserId() {
        return leadUserId;
    }

    public void setLeadUserId(UUID leadUserId) {
        this.leadUserId = leadUserId;
    }

    public String getLeadUserName() {
        return leadUserName;
    }

    public void setLeadUserName(String leadUserName) {
        this.leadUserName = leadUserName;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
