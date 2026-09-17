package com.nexus.collaboration.controller;

import com.nexus.auth.security.UserPrincipal;
import com.nexus.collaboration.dto.InternalNoteRequest;
import com.nexus.collaboration.dto.InternalNoteResponse;
import com.nexus.collaboration.service.InternalNoteService;
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
 * Controller for internal private notes (US-8).
 * Restricted strictly to staff roles (OPERATOR, TEAM_LEAD, MANAGER, ADMIN).
 */
@RestController
@RequestMapping("/api/v1/cases/{caseId}/notes")
@PreAuthorize("hasAnyRole('OPERATOR', 'TEAM_LEAD', 'MANAGER', 'ADMIN')")
public class InternalNoteController {

    private final InternalNoteService internalNoteService;
    private final UserRepository userRepository;

    public InternalNoteController(InternalNoteService internalNoteService, UserRepository userRepository) {
        this.internalNoteService = internalNoteService;
        this.userRepository = userRepository;
    }

    /**
     * Add an internal note to a case.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<InternalNoteResponse>> addNote(
            @PathVariable UUID caseId,
            @Valid @RequestBody InternalNoteRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User author = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));

        InternalNoteResponse response = internalNoteService.addNote(caseId, request, author);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Internal note added successfully"));
    }

    /**
     * List all internal notes for a case.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<InternalNoteResponse>>> getNotes(@PathVariable UUID caseId) {
        List<InternalNoteResponse> notes = internalNoteService.getNotes(caseId);
        return ResponseEntity.ok(ApiResponse.success(notes, "Internal notes retrieved successfully"));
    }
}
