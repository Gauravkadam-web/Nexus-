package com.nexus.collaboration.controller;

import com.nexus.collaboration.dto.CreateTaskRequest;
import com.nexus.collaboration.dto.TaskResponse;
import com.nexus.collaboration.dto.UpdateTaskStatusRequest;
import com.nexus.collaboration.service.TaskService;
import com.nexus.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for managing investigation tasks (US-9, US-10).
 */
@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Create an investigation task attached to a case (US-9).
     */
    @PostMapping("/api/v1/cases/{caseId}/tasks")
    @PreAuthorize("hasAnyRole('OPERATOR', 'TEAM_LEAD', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @PathVariable UUID caseId,
            @Valid @RequestBody CreateTaskRequest request) {
        TaskResponse response = taskService.createTask(caseId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Task created successfully"));
    }

    /**
     * List all investigation tasks for a case (US-9).
     */
    @GetMapping("/api/v1/cases/{caseId}/tasks")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksForCase(@PathVariable UUID caseId) {
        List<TaskResponse> tasks = taskService.getTasksForCase(caseId);
        return ResponseEntity.ok(ApiResponse.success(tasks, "Tasks retrieved successfully"));
    }

    /**
     * Update status of an investigation task (US-9).
     */
    @PatchMapping("/api/v1/tasks/{id}/status")
    @PreAuthorize("hasAnyRole('OPERATOR', 'TEAM_LEAD', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskStatusRequest request) {
        TaskResponse response = taskService.updateTaskStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success(response, "Task status updated successfully"));
    }

    /**
     * List pending tasks filtered by user or team (US-10).
     */
    @GetMapping("/api/v1/tasks/pending")
    @PreAuthorize("hasAnyRole('OPERATOR', 'TEAM_LEAD', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getPendingTasks(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID teamId) {
        List<TaskResponse> tasks = taskService.getPendingTasks(userId, teamId);
        return ResponseEntity.ok(ApiResponse.success(tasks, "Pending tasks retrieved successfully"));
    }
}
