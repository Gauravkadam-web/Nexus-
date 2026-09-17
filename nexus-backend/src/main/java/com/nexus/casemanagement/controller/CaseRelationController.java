package com.nexus.casemanagement.controller;

import com.nexus.auth.security.UserPrincipal;
import com.nexus.casemanagement.dto.CaseRelationResponse;
import com.nexus.casemanagement.dto.CreateCaseRelationRequest;
import com.nexus.casemanagement.service.CaseRelationService;
import com.nexus.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for case relations and master incident linking (SRS §7.3, US-17, US-18).
 */
@RestController
@RequestMapping("/api/v1/cases")
public class CaseRelationController {

    private final CaseRelationService caseRelationService;

    public CaseRelationController(CaseRelationService caseRelationService) {
        this.caseRelationService = caseRelationService;
    }

    /**
     * Links two cases with a specified relation type (DUPLICATE, RELATED, MASTER_INCIDENT).
     *
     * @param caseId    the primary case UUID
     * @param request   the relation payload
     * @param principal the authenticated operator
     * @return 201 Created with the relation response
     */
    @PostMapping("/{id}/relations")
    @PreAuthorize("hasAnyRole('OPERATOR','TEAM_LEAD','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<CaseRelationResponse>> linkCases(
            @PathVariable("id") UUID caseId,
            @Valid @RequestBody CreateCaseRelationRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        UUID operatorId = principal != null ? principal.getId() : null;
        CaseRelationResponse response = caseRelationService.linkCases(caseId, request, operatorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * Lists all relations for a given case.
     *
     * @param caseId the case UUID
     * @return 200 OK with the list of relations
     */
    @GetMapping("/{id}/relations")
    @PreAuthorize("hasAnyRole('REQUESTER','OPERATOR','TEAM_LEAD','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<List<CaseRelationResponse>>> getRelations(
            @PathVariable("id") UUID caseId) {
        List<CaseRelationResponse> responses = caseRelationService.getRelationsForCase(caseId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Removes an existing case relation.
     *
     * @param relationId the relation UUID
     * @return 200 OK with success message
     */
    @DeleteMapping("/relations/{relationId}")
    @PreAuthorize("hasAnyRole('OPERATOR','TEAM_LEAD','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<String>> removeRelation(
            @PathVariable("relationId") UUID relationId) {
        caseRelationService.removeRelation(relationId);
        return ResponseEntity.ok(ApiResponse.success("Case relation removed successfully"));
    }

    /**
     * Lists all child cases linked to a Master Incident (US-18).
     *
     * @param masterCaseId the master incident case UUID
     * @return 200 OK with list of child case relations
     */
    @GetMapping("/{id}/master-incident/children")
    @PreAuthorize("hasAnyRole('REQUESTER','OPERATOR','TEAM_LEAD','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<List<CaseRelationResponse>>> getMasterIncidentChildren(
            @PathVariable("id") UUID masterCaseId) {
        List<CaseRelationResponse> responses = caseRelationService.getMasterIncidentChildren(masterCaseId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
