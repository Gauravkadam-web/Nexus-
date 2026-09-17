package com.nexus.collaboration.service;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.collaboration.dto.CreateTaskRequest;
import com.nexus.collaboration.dto.TaskResponse;
import com.nexus.collaboration.entity.CaseTask;
import com.nexus.collaboration.entity.TaskStatus;
import com.nexus.collaboration.repository.CaseTaskRepository;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.user.entity.User;
import com.nexus.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final CaseTaskRepository taskRepository;
    private final CaseRepository caseRepository;
    private final UserRepository userRepository;

    public TaskService(CaseTaskRepository taskRepository,
                       CaseRepository caseRepository,
                       UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.caseRepository = caseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TaskResponse createTask(UUID caseId, CreateTaskRequest request) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId));

        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssigneeId()));
        }

        CaseTask task = new CaseTask(
                caseEntity,
                request.getTitle(),
                request.getDescription(),
                assignee,
                request.getPriority(),
                request.getDueDate()
        );

        CaseTask saved = taskRepository.save(task);
        return TaskResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksForCase(UUID caseId) {
        if (!caseRepository.existsById(caseId)) {
            throw new ResourceNotFoundException("Case", "id", caseId);
        }
        return taskRepository.findByCaseEntityIdOrderByCreatedAtAsc(caseId)
                .stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse updateTaskStatus(UUID taskId, TaskStatus newStatus) {
        CaseTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        task.setStatus(newStatus);
        if (newStatus == TaskStatus.COMPLETED) {
            task.setCompletedAt(Instant.now());
        } else {
            task.setCompletedAt(null);
        }

        CaseTask updated = taskRepository.save(task);
        return TaskResponse.fromEntity(updated);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getPendingTasks(UUID userId, UUID teamId) {
        List<TaskStatus> pendingStatuses = Arrays.asList(TaskStatus.PENDING, TaskStatus.IN_PROGRESS);
        List<CaseTask> tasks;

        if (userId != null) {
            tasks = taskRepository.findByAssigneeIdAndStatusIn(userId, pendingStatuses);
        } else if (teamId != null) {
            tasks = taskRepository.findByCaseEntityAssignedTeamIdAndStatusIn(teamId, pendingStatuses);
        } else {
            tasks = taskRepository.findAll().stream()
                    .filter(t -> pendingStatuses.contains(t.getStatus()))
                    .collect(Collectors.toList());
        }

        return tasks.stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
