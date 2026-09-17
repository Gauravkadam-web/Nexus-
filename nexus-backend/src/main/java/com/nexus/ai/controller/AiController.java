package com.nexus.ai.controller;

import com.nexus.ai.dto.*;
import com.nexus.ai.entity.AiAnalysis;
import com.nexus.ai.entity.AiSummary;
import com.nexus.ai.repository.AiAnalysisRepository;
import com.nexus.ai.service.*;
import com.nexus.auth.security.UserPrincipal;
import com.nexus.common.response.ApiResponse;
import com.nexus.common.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for all AI endpoints (SRS §7.5, US-11 to US-14).
 * All endpoints require authentication; RBAC is enforced per endpoint.
 * AI outputs are always presented as recommendations, never applied automatically.
 */
@RestController
@RequestMapping("/api/v1")
public class AiController {

    private final AiAnalysisService aiAnalysisService;
    private final AiSuggestionService aiSuggestionService;
    private final AiSummaryService aiSummaryService;
    private final AiAnalysisRepository aiAnalysisRepository;
    private final CaseDuplicateDetectionService duplicateDetectionService;
    private final SmartAssignmentService smartAssignmentService;
    private final AiCopilotService aiCopilotService;

    public AiController(AiAnalysisService aiAnalysisService,
                        AiSuggestionService aiSuggestionService,
                        AiSummaryService aiSummaryService,
                        AiAnalysisRepository aiAnalysisRepository,
                        CaseDuplicateDetectionService duplicateDetectionService,
                        SmartAssignmentService smartAssignmentService,
                        AiCopilotService aiCopilotService) {
        this.aiAnalysisService = aiAnalysisService;
        this.aiSuggestionService = aiSuggestionService;
        this.aiSummaryService = aiSummaryService;
        this.aiAnalysisRepository = aiAnalysisRepository;
        this.duplicateDetectionService = duplicateDetectionService;
        this.smartAssignmentService = smartAssignmentService;
        this.aiCopilotService = aiCopilotService;
    }


    // ----------------------------------------------------------------
    // POST /cases/{id}/ai/analyze — Trigger / re-trigger AI analysis (US-11)
    // ----------------------------------------------------------------

    /**
     * Triggers or re-triggers AI analysis for a case.
     * Runs asynchronously — returns 202 Accepted immediately.
     *
     * @param caseId    the UUID of the case
     * @param principal the authenticated operator
     * @return 202 Accepted with a status message
     */
    @PostMapping("/cases/{id}/ai/analyze")
    @PreAuthorize("hasAnyRole('OPERATOR','TEAM_LEAD','MANAGER')")
    public ResponseEntity<ApiResponse<String>> triggerAnalysis(
            @PathVariable("id") UUID caseId,
            @AuthenticationPrincipal UserPrincipal principal) {
        aiAnalysisService.reanalyzeCase(caseId);
        return ResponseEntity.accepted()
                .body(ApiResponse.success("AI analysis triggered. Results will be available shortly."));
    }

    // ----------------------------------------------------------------
    // GET /cases/{id}/ai/analysis — Get latest AI analysis (US-11)
    // ----------------------------------------------------------------

    /**
     * Returns the latest AI analysis snapshot for a case.
     *
     * @param caseId the UUID of the case
     * @return 200 OK with the analysis, or 404 if no analysis exists yet
     */
    @GetMapping("/cases/{id}/ai/analysis")
    @PreAuthorize("hasAnyRole('REQUESTER','OPERATOR','TEAM_LEAD','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<AiAnalysisResponse>> getAnalysis(
            @PathVariable("id") UUID caseId) {
        AiAnalysis analysis = aiAnalysisRepository
                .findTopByCaseEntityIdOrderByCreatedAtDesc(caseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No AI analysis found for case: " + caseId));
        return ResponseEntity.ok(ApiResponse.success(AiAnalysisResponse.from(analysis)));
    }

    // ----------------------------------------------------------------
    // GET /cases/{id}/ai/summary — Get current AI summary (US-12)
    // ----------------------------------------------------------------

    /**
     * Returns the latest versioned AI summary for a case.
     *
     * @param caseId the UUID of the case
     * @return 200 OK with the summary, or 404 if none exists yet
     */
    @GetMapping("/cases/{id}/ai/summary")
    @PreAuthorize("hasAnyRole('REQUESTER','OPERATOR','TEAM_LEAD','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<AiSummaryResponse>> getSummary(
            @PathVariable("id") UUID caseId) {
        AiSummary summary = aiSummaryService.getLatestSummary(caseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No AI summary found for case: " + caseId));
        return ResponseEntity.ok(ApiResponse.success(AiSummaryResponse.from(summary)));
    }

    // ----------------------------------------------------------------
    // POST /cases/{id}/ai/summary/regenerate — Regenerate summary (US-12)
    // ----------------------------------------------------------------

    /**
     * Operator-triggered summary regeneration. Creates a new versioned summary.
     *
     * @param caseId    the UUID of the case
     * @param principal the authenticated operator
     * @return 200 OK with the new summary
     */
    @PostMapping("/cases/{id}/ai/summary/regenerate")
    @PreAuthorize("hasAnyRole('OPERATOR','TEAM_LEAD')")
    public ResponseEntity<ApiResponse<AiSummaryResponse>> regenerateSummary(
            @PathVariable("id") UUID caseId,
            @AuthenticationPrincipal UserPrincipal principal) {
        AiSummary summary = aiSummaryService.regenerateSummary(caseId);
        return ResponseEntity.ok(ApiResponse.success(AiSummaryResponse.from(summary)));
    }

    // ----------------------------------------------------------------
    // PUT /ai/suggestions/{id} — Accept / Modify / Reject suggestion (US-14)
    // ----------------------------------------------------------------

    /**
     * Records an operator's decision on an AI suggestion.
     * Decisions are immutable — once decided, the suggestion cannot be changed.
     *
     * @param suggestionId the UUID of the AI suggestion
     * @param request      the decision payload (status, optional modifiedValue, overrideReason)
     * @param principal    the authenticated operator
     * @return 200 OK with the updated suggestion
     */
    @PutMapping("/ai/suggestions/{id}")
    @PreAuthorize("hasAnyRole('OPERATOR','TEAM_LEAD')")
    public ResponseEntity<ApiResponse<AiSuggestionResponse>> decideSuggestion(
            @PathVariable("id") UUID suggestionId,
            @Valid @RequestBody SuggestionDecisionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        var decided = aiSuggestionService.decideSuggestion(suggestionId, request, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(AiSuggestionResponse.from(decided)));
    }

    // ----------------------------------------------------------------
    // GET /cases/{id}/ai/suggestions — List all suggestions for a case
    // ----------------------------------------------------------------

    /**
     * Returns all AI suggestions for a case (all statuses), ordered by creation time.
     *
     * @param caseId the UUID of the case
     * @return list of suggestion responses
     */
    @GetMapping("/cases/{id}/ai/suggestions")
    @PreAuthorize("hasAnyRole('OPERATOR','TEAM_LEAD','MANAGER')")
    public ResponseEntity<ApiResponse<List<AiSuggestionResponse>>> getSuggestions(
            @PathVariable("id") UUID caseId) {
        List<AiSuggestionResponse> suggestions = aiSuggestionService
                .getSuggestionsForCase(caseId)
                .stream()
                .map(AiSuggestionResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(suggestions));
    }

    // ----------------------------------------------------------------
    // GET /cases/{id}/ai/duplicates — AI Duplicate Detection (US-16)
    // ----------------------------------------------------------------

    /**
     * Identifies potential duplicate or related cases based on text and category similarity.
     *
     * @param caseId the UUID of the target case
     * @return list of potential duplicate suggestions
     */
    @GetMapping("/cases/{id}/ai/duplicates")
    @PreAuthorize("hasAnyRole('OPERATOR','TEAM_LEAD','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<List<DuplicateSuggestionResponse>>> getPotentialDuplicates(
            @PathVariable("id") UUID caseId) {
        List<DuplicateSuggestionResponse> duplicates = duplicateDetectionService.findPotentialDuplicates(caseId);
        return ResponseEntity.ok(ApiResponse.success(duplicates));
    }

    // ----------------------------------------------------------------
    // GET /cases/{id}/ai/assignment-recommendation — Smart Assignment (US-19, US-20)
    // ----------------------------------------------------------------

    /**
     * Generates a smart operator assignment recommendation factoring category affinity and workload.
     *
     * @param caseId the UUID of the case
     * @return recommended assignment details with reasoning
     */
    @GetMapping("/cases/{id}/ai/assignment-recommendation")
    @PreAuthorize("hasAnyRole('OPERATOR','TEAM_LEAD','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<AssignmentRecommendationResponse>> getAssignmentRecommendation(
            @PathVariable("id") UUID caseId) {
        AssignmentRecommendationResponse recommendation = smartAssignmentService.recommendAssignment(caseId);
        return ResponseEntity.ok(ApiResponse.success(recommendation));
    }

    // ----------------------------------------------------------------
    // POST /cases/{id}/ai/copilot — Ask AI Operator Copilot (US-29)
    // ----------------------------------------------------------------

    /**
     * Interacts with the AI Operator Copilot to query case-scoped insights (US-29).
     *
     * @param caseId  the UUID of the case
     * @param request the question payload
     * @return 200 OK with the Copilot's answer and cited sources
     */
    @PostMapping("/cases/{id}/ai/copilot")
    @PreAuthorize("hasAnyRole('OPERATOR','TEAM_LEAD','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<AiCopilotResponse>> askCopilot(
            @PathVariable("id") UUID caseId,
            @Valid @RequestBody AiCopilotRequest request) {
        AiCopilotResponse response = aiCopilotService.askCopilot(caseId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ----------------------------------------------------------------
    // POST /cases/{id}/ai/draft-communication — Draft Professional Communication (US-30)
    // ----------------------------------------------------------------

    /**
     * Uses AI to draft context-aware messages for requesters or internal stakeholders (US-30).
     *
     * @param caseId  the UUID of the case
     * @param request audience, intent, and operator guidelines
     * @return 200 OK with the generated communication draft
     */
    @PostMapping("/cases/{id}/ai/draft-communication")
    @PreAuthorize("hasAnyRole('OPERATOR','TEAM_LEAD','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<AiDraftCommunicationResponse>> draftCommunication(
            @PathVariable("id") UUID caseId,
            @Valid @RequestBody AiDraftCommunicationRequest request) {
        AiDraftCommunicationResponse response = aiCopilotService.draftCommunication(caseId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

