package com.nexus.organization.controller;

import com.nexus.auth.security.UserPrincipal;
import com.nexus.common.response.ApiResponse;
import com.nexus.organization.dto.CreateTeamRequest;
import com.nexus.organization.dto.TeamDto;
import com.nexus.organization.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeamDto>>> getTeams(@AuthenticationPrincipal UserPrincipal principal) {
        List<TeamDto> teams = teamService.getTeams(principal);
        return ResponseEntity.ok(ApiResponse.success(teams, "Teams retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<TeamDto>> createTeam(
            @Valid @RequestBody CreateTeamRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        TeamDto team = teamService.createTeam(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(team, "Team created successfully"));
    }
}
