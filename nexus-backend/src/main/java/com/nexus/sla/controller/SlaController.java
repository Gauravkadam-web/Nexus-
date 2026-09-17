package com.nexus.sla.controller;

import com.nexus.auth.security.UserPrincipal;
import com.nexus.common.response.ApiResponse;
import com.nexus.sla.dto.AtRiskCaseResponse;
import com.nexus.sla.dto.BreachedCaseResponse;
import com.nexus.sla.dto.CaseSlaResponse;
import com.nexus.sla.service.CaseRiskService;
import com.nexus.sla.service.SlaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class SlaController {

    private final SlaService slaService;
    private final CaseRiskService caseRiskService;

    public SlaController(SlaService slaService, CaseRiskService caseRiskService) {
        this.slaService = slaService;
        this.caseRiskService = caseRiskService;
    }

    /**
     * US-21: View remaining SLA time, deadlines, and risk factors for a case.
     */
    @GetMapping("/cases/{id}/sla")
    public ResponseEntity<ApiResponse<CaseSlaResponse>> getCaseSla(@PathVariable UUID id) {
        CaseSlaResponse response = slaService.getCaseSla(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Case SLA retrieved successfully"));
    }

    /**
     * US-22: View all cases currently at risk (MEDIUM / HIGH risk).
     */
    @GetMapping("/sla/at-risk")
    @PreAuthorize("hasAnyRole('OPERATOR', 'TEAM_LEAD', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<AtRiskCaseResponse>>> getAtRiskCases(
            @AuthenticationPrincipal UserPrincipal principal,
            Pageable pageable) {
        Page<AtRiskCaseResponse> response = caseRiskService.getAtRiskCases(principal.getOrganizationId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "At-risk cases retrieved successfully"));
    }

    /**
     * US-24: View all cases with breached SLAs.
     */
    @GetMapping("/sla/breached")
    @PreAuthorize("hasAnyRole('TEAM_LEAD', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<BreachedCaseResponse>>> getBreachedCases(
            @AuthenticationPrincipal UserPrincipal principal,
            Pageable pageable) {
        Page<BreachedCaseResponse> response = slaService.getBreachedCases(principal.getOrganizationId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Breached cases retrieved successfully"));
    }
}
