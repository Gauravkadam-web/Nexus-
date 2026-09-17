package com.nexus.escalation.controller;

import com.nexus.auth.security.UserPrincipal;
import com.nexus.common.response.ApiResponse;
import com.nexus.escalation.dto.EscalateCaseRequest;
import com.nexus.escalation.dto.EscalationResponse;
import com.nexus.escalation.service.EscalationService;
import com.nexus.user.entity.User;
import com.nexus.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class EscalationController {

    private final EscalationService escalationService;
    private final UserRepository userRepository;

    public EscalationController(EscalationService escalationService, UserRepository userRepository) {
        this.escalationService = escalationService;
        this.userRepository = userRepository;
    }

    /**
     * US-25: Manually escalate a case.
     */
    @PostMapping("/cases/{id}/escalate")
    @PreAuthorize("hasAnyRole('OPERATOR', 'TEAM_LEAD', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<EscalationResponse>> escalateCase(
            @PathVariable UUID id,
            @Valid @RequestBody EscalateCaseRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getId()).orElse(null);
        EscalationResponse response = escalationService.escalateCase(id, request, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Case escalated successfully"));
    }

    /**
     * Confirms a recommended system escalation.
     */
    @PostMapping("/escalations/{id}/confirm")
    @PreAuthorize("hasAnyRole('TEAM_LEAD', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<EscalationResponse>> confirmEscalation(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getId()).orElse(null);
        EscalationResponse response = escalationService.confirmRecommendedEscalation(id, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Escalation confirmed successfully"));
    }

    /**
     * List all escalations in organization.
     */
    @GetMapping("/escalations")
    @PreAuthorize("hasAnyRole('TEAM_LEAD', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<EscalationResponse>>> getEscalations(
            @AuthenticationPrincipal UserPrincipal principal,
            Pageable pageable) {
        Page<EscalationResponse> response = escalationService.getEscalations(principal.getOrganizationId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Escalations retrieved successfully"));
    }
}
