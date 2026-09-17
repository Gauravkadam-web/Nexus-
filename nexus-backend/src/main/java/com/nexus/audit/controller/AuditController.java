package com.nexus.audit.controller;

import com.nexus.audit.dto.AuditFilterRequest;
import com.nexus.audit.dto.AuditLogResponse;
import com.nexus.audit.service.AuditService;
import com.nexus.auth.security.UserPrincipal;
import com.nexus.common.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for viewing immutable audit trails and governance logs (US-33).
 */
@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Search and filter system-wide audit records (US-33).
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> searchAuditLogs(
            @ModelAttribute AuditFilterRequest filter,
            Pageable pageable) {
        Page<AuditLogResponse> logs = auditService.searchAuditLogs(filter, pageable);
        return ResponseEntity.ok(ApiResponse.success(logs, "Audit logs retrieved successfully"));
    }

    /**
     * Get paginated audit trail for a specific case.
     */
    @GetMapping("/case/{caseId}")
    @PreAuthorize("hasAnyRole('AGENT', 'TEAM_LEAD', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getCaseAuditTrail(
            @PathVariable UUID caseId,
            Pageable pageable) {
        Page<AuditLogResponse> trail = auditService.getCaseAuditTrail(caseId, pageable);
        return ResponseEntity.ok(ApiResponse.success(trail, "Case audit trail retrieved successfully"));
    }

    /**
     * Get chronological audit timeline for a specific case.
     */
    @GetMapping("/case/{caseId}/timeline")
    @PreAuthorize("hasAnyRole('AGENT', 'TEAM_LEAD', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getCaseAuditTimeline(
            @PathVariable UUID caseId) {
        List<AuditLogResponse> timeline = auditService.getCaseAuditTimeline(caseId);
        return ResponseEntity.ok(ApiResponse.success(timeline, "Case audit timeline retrieved successfully"));
    }
}
