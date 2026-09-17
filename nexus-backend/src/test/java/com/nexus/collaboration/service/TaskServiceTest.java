package com.nexus.collaboration.service;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.collaboration.dto.CreateTaskRequest;
import com.nexus.collaboration.dto.TaskResponse;
import com.nexus.collaboration.entity.CaseTask;
import com.nexus.collaboration.entity.TaskPriority;
import com.nexus.collaboration.entity.TaskStatus;
import com.nexus.collaboration.repository.CaseTaskRepository;
import com.nexus.user.entity.User;
import com.nexus.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private CaseTaskRepository taskRepository;

    @Mock
    private CaseRepository caseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private Case testCase;
    private User assignee;

    @BeforeEach
    void setUp() {
        testCase = new Case();
        testCase.setId(UUID.randomUUID());
        testCase.setCaseNumber("NEX-20260917-0001");

        assignee = new User();
        assignee.setId(UUID.randomUUID());
        assignee.setName("Operator Alice");
        assignee.setEmail("alice@nexus.com");
    }

    @Test
    @DisplayName("US-9: Create investigation task successfully")
    void testCreateTask() {
        when(caseRepository.findById(testCase.getId())).thenReturn(Optional.of(testCase));
        when(userRepository.findById(assignee.getId())).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any(CaseTask.class))).thenAnswer(i -> {
            CaseTask t = i.getArgument(0);
            t.setId(UUID.randomUUID());
            return t;
        });

        CreateTaskRequest request = new CreateTaskRequest("Check VPN gateway logs", "Inspect auth failures", assignee.getId(), TaskPriority.HIGH, Instant.now().plusSeconds(3600));
        TaskResponse response = taskService.createTask(testCase.getId(), request);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Check VPN gateway logs");
        assertThat(response.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(response.getPriority()).isEqualTo(TaskPriority.HIGH);
        assertThat(response.getAssigneeId()).isEqualTo(assignee.getId());
    }

    @Test
    @DisplayName("US-9: Updating task status to COMPLETED sets completedAt timestamp")
    void testCompleteTask() {
        CaseTask task = new CaseTask(testCase, "Task 1", "Desc", assignee, TaskPriority.MEDIUM, null);
        task.setId(UUID.randomUUID());

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(CaseTask.class))).thenAnswer(i -> i.getArgument(0));

        TaskResponse response = taskService.updateTaskStatus(task.getId(), TaskStatus.COMPLETED);

        assertThat(response.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(response.getCompletedAt()).isNotNull();
        verify(taskRepository).save(task);
    }
}
