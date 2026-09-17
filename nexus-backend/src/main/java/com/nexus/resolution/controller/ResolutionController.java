package com.nexus.resolution.controller;

import com.nexus.auth.security.UserPrincipal;
import com.nexus.common.response.ApiResponse;
import com.nexus.resolution.dto.ConfirmResolutionRequest;
import com.nexus.resolution.dto.RejectResolutionRequest;
import com.nexus.resolution.dto.ResolutionResponse;
import com.nexus.resolution.dto.SubmitResolutionRequest;
import com.nexus.resolution.service.ResolutionService;
import com.nexus.user.entity.User;
import com.nexus.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cases")
public class ResolutionController {

    private final ResolutionService resolutionService;
    private final UserRepository userRepository;

    public ResolutionController(ResolutionService resolutionService, UserRepository userRepository) {
        this.resolutionService = resolutionService;
        this.userRepository = userRepository;
    }

    /**
     * US-26: Operator submits structured resolution findings.
     */
    @PostMapping("/{id}/resolution")
    @PreAuthorize("hasAnyRole('OPERATOR', 'TEAM_LEAD', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ResolutionResponse>> submitResolution(
            @PathVariable UUID id,
            @Valid @RequestBody SubmitResolutionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User operator = userRepository.findById(principal.getId()).orElse(null);
        ResolutionResponse response = resolutionService.submitResolution(id, request, operator);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Resolution submitted successfully"));
    }

    /**
     * Get resolution details for a case.
     */
    @GetMapping("/{id}/resolution")
    public ResponseEntity<ApiResponse<ResolutionResponse>> getResolution(@PathVariable UUID id) {
        ResolutionResponse response = resolutionService.getCaseResolution(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Resolution retrieved successfully"));
    }

    /**
     * US-27: Requester confirms resolution outcome (closes case).
     */
    @PutMapping("/{id}/resolution/confirm")
    public ResponseEntity<ApiResponse<ResolutionResponse>> confirmResolution(
            @PathVariable UUID id,
            @RequestBody(required = false) ConfirmResolutionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User requester = userRepository.findById(principal.getId()).orElse(null);
        ResolutionResponse response = resolutionService.confirmResolution(id, request, requester);
        return ResponseEntity.ok(ApiResponse.success(response, "Resolution confirmed and case closed"));
    }

    /**
     * US-27: Requester rejects resolution outcome (reopens case).
     */
    @PutMapping("/{id}/resolution/reject")
    public ResponseEntity<ApiResponse<ResolutionResponse>> rejectResolution(
            @PathVariable UUID id,
            @Valid @RequestBody RejectResolutionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User requester = userRepository.findById(principal.getId()).orElse(null);
        ResolutionResponse response = resolutionService.rejectResolution(id, request, requester);
        return ResponseEntity.ok(ApiResponse.success(response, "Resolution rejected and case reopened"));
    }
}
