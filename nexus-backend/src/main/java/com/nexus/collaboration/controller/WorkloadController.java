package com.nexus.collaboration.controller;

import com.nexus.collaboration.dto.TeamWorkloadResponse;
import com.nexus.collaboration.service.WorkloadService;
import com.nexus.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller for Team Lead & Manager workload monitoring (US-10).
 */
@RestController
@RequestMapping("/api/v1/collaboration/workload")
@PreAuthorize("hasAnyRole('TEAM_LEAD', 'MANAGER', 'ADMIN')")
public class WorkloadController {

    private final WorkloadService workloadService;

    public WorkloadController(WorkloadService workloadService) {
        this.workloadService = workloadService;
    }

    /**
     * Get workload metrics and pending task breakdown for a team (US-10).
     */
    @GetMapping("/team")
    public ResponseEntity<ApiResponse<TeamWorkloadResponse>> getTeamWorkload(@RequestParam UUID teamId) {
        TeamWorkloadResponse response = workloadService.getTeamWorkload(teamId);
        return ResponseEntity.ok(ApiResponse.success(response, "Team workload retrieved successfully"));
    }
}
