package com.nexus.collaboration.controller;

import com.nexus.auth.security.UserPrincipal;
import com.nexus.collaboration.dto.AttachmentResponse;
import com.nexus.collaboration.service.AttachmentService;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.common.response.ApiResponse;
import com.nexus.user.entity.User;
import com.nexus.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Controller for uploading and listing case evidence and attachments.
 */
@RestController
@RequestMapping("/api/v1/cases/{caseId}/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final UserRepository userRepository;

    public AttachmentController(AttachmentService attachmentService, UserRepository userRepository) {
        this.attachmentService = attachmentService;
        this.userRepository = userRepository;
    }

    /**
     * Upload an attachment or evidence document for a case.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AttachmentResponse>> uploadAttachment(
            @PathVariable UUID caseId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));

        AttachmentResponse response = attachmentService.uploadAttachment(caseId, file, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Attachment uploaded successfully"));
    }

    /**
     * List all attachments for a case.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AttachmentResponse>>> getAttachments(@PathVariable UUID caseId) {
        List<AttachmentResponse> attachments = attachmentService.getAttachments(caseId);
        return ResponseEntity.ok(ApiResponse.success(attachments, "Attachments retrieved successfully"));
    }
}
