package com.nexus.analytics.controller;

import com.nexus.analytics.dto.*;
import com.nexus.analytics.service.AnalyticsService;
import com.nexus.analytics.service.OperationalInsightsService;
import com.nexus.auth.security.UserPrincipal;
import com.nexus.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for Manager Analytics and Operational Insights (US-31, US-32).
 */
@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final OperationalInsightsService operationalInsightsService;

    public AnalyticsController(AnalyticsService analyticsService,
                               OperationalInsightsService operationalInsightsService) {
        this.analyticsService = analyticsService;
        this.operationalInsightsService = operationalInsightsService;
    }

    /**
     * US-31: Overview KPIs (volume, resolution time, SLA met %, reopen rate).
     */
    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<AnalyticsOverviewResponse>> getOverview(
            @AuthenticationPrincipal UserPrincipal principal) {
        UUID orgId = principal != null ? principal.getOrganizationId() : null;
        AnalyticsOverviewResponse response = analyticsService.getOverview(orgId);
        return ResponseEntity.ok(ApiResponse.success(response, "Analytics overview retrieved successfully"));
    }

    /**
     * US-31: Volume trends over time.
     */
    @GetMapping("/trends")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<VolumeTrendResponse>>> getVolumeTrends(
            @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal UserPrincipal principal) {
        UUID orgId = principal != null ? principal.getOrganizationId() : null;
        List<VolumeTrendResponse> response = analyticsService.getVolumeTrends(orgId, days);
        return ResponseEntity.ok(ApiResponse.success(response, "Volume trends retrieved successfully"));
    }

    /**
     * US-31: Category breakdown & resolution velocity.
     */
    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<CategoryBreakdownResponse>>> getCategoryBreakdown(
            @AuthenticationPrincipal UserPrincipal principal) {
        UUID orgId = principal != null ? principal.getOrganizationId() : null;
        List<CategoryBreakdownResponse> response = analyticsService.getCategoryBreakdown(orgId);
        return ResponseEntity.ok(ApiResponse.success(response, "Category breakdown retrieved successfully"));
    }

    /**
     * US-31: Team workloads & capacity distribution.
     */
    @GetMapping("/teams")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<TeamWorkloadResponse>>> getTeamWorkloads(
            @AuthenticationPrincipal UserPrincipal principal) {
        UUID orgId = principal != null ? principal.getOrganizationId() : null;
        List<TeamWorkloadResponse> response = analyticsService.getTeamWorkloads(orgId);
        return ResponseEntity.ok(ApiResponse.success(response, "Team workloads retrieved successfully"));
    }

    /**
     * US-32: Automated Operational Insights and anomaly signals.
     */
    @GetMapping("/operational-insights")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<OperationalInsightResponse>>> getOperationalInsights(
            @AuthenticationPrincipal UserPrincipal principal) {
        UUID orgId = principal != null ? principal.getOrganizationId() : null;
        List<OperationalInsightResponse> response = operationalInsightsService.generateInsights(orgId);
        return ResponseEntity.ok(ApiResponse.success(response, "Operational insights generated successfully"));
    }
}
