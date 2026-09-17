package com.nexus.escalation.controller;

import com.nexus.auth.security.UserPrincipal;
import com.nexus.common.response.ApiResponse;
import com.nexus.escalation.dto.CreateEscalationRuleRequest;
import com.nexus.escalation.dto.EscalationRuleResponse;
import com.nexus.escalation.service.EscalationService;
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

@RestController
@RequestMapping("/api/v1/admin/escalation-rules")
public class AdminEscalationRuleController {

    private final EscalationService escalationService;
    private final UserRepository userRepository;

    public AdminEscalationRuleController(EscalationService escalationService, UserRepository userRepository) {
        this.escalationService = escalationService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<EscalationRuleResponse>>> listEscalationRules(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<EscalationRuleResponse> response = escalationService.getEscalationRules(principal.getOrganizationId());
        return ResponseEntity.ok(ApiResponse.success(response, "Escalation rules retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EscalationRuleResponse>> createEscalationRule(
            @Valid @RequestBody CreateEscalationRuleRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User creator = userRepository.findById(principal.getId()).orElse(null);
        EscalationRuleResponse response = escalationService.createEscalationRule(request, principal.getOrganizationId(), creator);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Escalation rule created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EscalationRuleResponse>> updateEscalationRule(
            @PathVariable UUID id,
            @Valid @RequestBody CreateEscalationRuleRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        EscalationRuleResponse response = escalationService.updateEscalationRule(id, request, principal.getOrganizationId());
        return ResponseEntity.ok(ApiResponse.success(response, "Escalation rule updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEscalationRule(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        escalationService.deleteEscalationRule(id, principal.getOrganizationId());
        return ResponseEntity.ok(ApiResponse.success(null, "Escalation rule deleted successfully"));
    }
}
