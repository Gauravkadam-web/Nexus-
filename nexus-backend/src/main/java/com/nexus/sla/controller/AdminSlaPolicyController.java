package com.nexus.sla.controller;

import com.nexus.auth.security.UserPrincipal;
import com.nexus.common.response.ApiResponse;
import com.nexus.sla.dto.CreateSlaPolicyRequest;
import com.nexus.sla.dto.SlaPolicyResponse;
import com.nexus.sla.service.SlaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/sla-policies")
public class AdminSlaPolicyController {

    private final SlaService slaService;

    public AdminSlaPolicyController(SlaService slaService) {
        this.slaService = slaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<SlaPolicyResponse>>> listSlaPolicies(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<SlaPolicyResponse> response = slaService.getSlaPolicies(principal.getOrganizationId());
        return ResponseEntity.ok(ApiResponse.success(response, "SLA policies retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SlaPolicyResponse>> createSlaPolicy(
            @Valid @RequestBody CreateSlaPolicyRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        SlaPolicyResponse response = slaService.createSlaPolicy(request, principal.getOrganizationId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "SLA policy created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SlaPolicyResponse>> updateSlaPolicy(
            @PathVariable UUID id,
            @Valid @RequestBody CreateSlaPolicyRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        SlaPolicyResponse response = slaService.updateSlaPolicy(id, request, principal.getOrganizationId());
        return ResponseEntity.ok(ApiResponse.success(response, "SLA policy updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSlaPolicy(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        slaService.deleteSlaPolicy(id, principal.getOrganizationId());
        return ResponseEntity.ok(ApiResponse.success(null, "SLA policy deleted successfully"));
    }
}
