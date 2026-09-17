package com.nexus.collaboration.controller;

import com.nexus.auth.security.UserPrincipal;
import com.nexus.collaboration.dto.InvestigationRequest;
import com.nexus.collaboration.dto.InvestigationResponse;
import com.nexus.collaboration.service.InvestigationService;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.common.response.ApiResponse;
import com.nexus.user.entity.User;
import com.nexus.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for logging and retrieving formal investigation records (US-9).
 */
@RestController
@RequestMapping("/api/v1/cases/{caseId}/investigations")
@PreAuthorize("hasAnyRole('OPERATOR', 'TEAM_LEAD', 'MANAGER', 'ADMIN')")
public class InvestigationController {

    private final InvestigationService investigationService;
    private final UserRepository userRepository;

    public InvestigationController(InvestigationService investigationService, UserRepository userRepository) {
        this.investigationService = investigationService;
        this.userRepository = userRepository;
    }

    /**
     * Log a formal investigation record for a case.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<InvestigationResponse>> logInvestigation(
            @PathVariable UUID caseId,
            @Valid @RequestBody InvestigationRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User operator = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));

        InvestigationResponse response = investigationService.logInvestigation(caseId, request, operator);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Investigation record logged successfully"));
    }

    /**
     * List all investigation records for a case.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<InvestigationResponse>>> getInvestigations(@PathVariable UUID caseId) {
        List<InvestigationResponse> records = investigationService.getInvestigations(caseId);
        return ResponseEntity.ok(ApiResponse.success(records, "Investigation records retrieved successfully"));
    }
}
