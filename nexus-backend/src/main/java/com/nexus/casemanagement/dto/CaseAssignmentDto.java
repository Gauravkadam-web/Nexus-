package com.nexus.casemanagement.dto;

import com.nexus.casemanagement.entity.CaseAssignment;
import com.nexus.organization.dto.TeamDto;
import com.nexus.user.dto.UserDto;

import java.time.Instant;
import java.util.UUID;

public class CaseAssignmentDto {

    private UUID id;
    private UUID caseId;
    private UserDto assignedUser;
    private TeamDto assignedTeam;
    private UserDto assignedBy;
    private Instant assignedAt;

    public CaseAssignmentDto() {}

    public static CaseAssignmentDto fromEntity(CaseAssignment a) {
        CaseAssignmentDto dto = new CaseAssignmentDto();
        dto.setId(a.getId());
        if (a.getCaseEntity() != null) {
            dto.setCaseId(a.getCaseEntity().getId());
        }
        if (a.getAssignedUser() != null) {
            dto.setAssignedUser(UserDto.fromEntity(a.getAssignedUser()));
        }
        if (a.getAssignedTeam() != null) {
            dto.setAssignedTeam(TeamDto.fromEntity(a.getAssignedTeam()));
        }
        if (a.getAssignedBy() != null) {
            dto.setAssignedBy(UserDto.fromEntity(a.getAssignedBy()));
        }
        dto.setAssignedAt(a.getAssignedAt());
        return dto;
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

    public UserDto getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(UserDto assignedUser) {
        this.assignedUser = assignedUser;
    }

    public TeamDto getAssignedTeam() {
        return assignedTeam;
    }

    public void setAssignedTeam(TeamDto assignedTeam) {
        this.assignedTeam = assignedTeam;
    }

    public UserDto getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(UserDto assignedBy) {
        this.assignedBy = assignedBy;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }
}
