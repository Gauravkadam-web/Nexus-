package com.nexus.collaboration.controller;

import com.nexus.auth.security.UserPrincipal;
import com.nexus.collaboration.dto.MessageRequest;
import com.nexus.collaboration.dto.MessageResponse;
import com.nexus.collaboration.service.MessageService;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.common.response.ApiResponse;
import com.nexus.user.entity.User;
import com.nexus.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller managing case communication messages and information request flows (US-6, US-7, US-8).
 */
@RestController
@RequestMapping("/api/v1/cases/{caseId}/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;

    public MessageController(MessageService messageService, UserRepository userRepository) {
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    /**
     * Post a new message on a case (US-6, US-7, US-8).
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @PathVariable UUID caseId,
            @Valid @RequestBody MessageRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User sender = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));

        MessageResponse response = messageService.sendMessage(caseId, request, sender);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Message sent successfully"));
    }

    /**
     * List all messages for a case, filtered by user visibility role (US-8).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessages(
            @PathVariable UUID caseId,
            @AuthenticationPrincipal UserPrincipal principal) {
        User currentUser = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));

        List<MessageResponse> messages = messageService.getMessages(caseId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(messages, "Messages retrieved successfully"));
    }
}
