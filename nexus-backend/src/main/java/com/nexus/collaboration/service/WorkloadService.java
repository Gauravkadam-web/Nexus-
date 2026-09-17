package com.nexus.collaboration.service;

import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.collaboration.dto.OperatorWorkloadDto;
import com.nexus.collaboration.dto.TeamWorkloadResponse;
import com.nexus.collaboration.entity.TaskStatus;
import com.nexus.collaboration.repository.CaseTaskRepository;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.organization.entity.Team;
import com.nexus.organization.repository.TeamRepository;
import com.nexus.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkloadService {

    private static final List<CaseStatus> OPEN_CASE_STATUSES = Arrays.asList(
            CaseStatus.REPORTED,
            CaseStatus.UNDERSTOOD,
            CaseStatus.ASSIGNED,
            CaseStatus.INVESTIGATING,
            CaseStatus.WAITING_FOR_INFO,
            CaseStatus.AT_RISK,
            CaseStatus.ESCALATED,
            CaseStatus.REOPENED
    );

    private static final List<TaskStatus> PENDING_TASK_STATUSES = Arrays.asList(
            TaskStatus.PENDING,
            TaskStatus.IN_PROGRESS
    );

    private final TeamRepository teamRepository;
    private final CaseRepository caseRepository;
    private final CaseTaskRepository taskRepository;

    public WorkloadService(TeamRepository teamRepository,
                           CaseRepository caseRepository,
                           CaseTaskRepository taskRepository) {
        this.teamRepository = teamRepository;
        this.caseRepository = caseRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public TeamWorkloadResponse getTeamWorkload(UUID teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));

        long totalOpenCases = caseRepository.countByAssignedTeamIdAndStatusIn(teamId, OPEN_CASE_STATUSES);
        long casesWaitingForInfo = caseRepository.countByAssignedTeamIdAndStatus(teamId, CaseStatus.WAITING_FOR_INFO);
        long totalPendingTasks = taskRepository.countByCaseEntityAssignedTeamIdAndStatusIn(teamId, PENDING_TASK_STATUSES);
        long unassignedTasks = taskRepository.countByCaseEntityAssignedTeamIdAndAssigneeIsNullAndStatusIn(teamId, PENDING_TASK_STATUSES);

        List<OperatorWorkloadDto> operatorWorkloads = team.getMembers().stream()
                .map(member -> {
                    long assignedCases = caseRepository.countByAssignedUserIdAndStatusIn(member.getId(), OPEN_CASE_STATUSES);
                    long pendingTasks = taskRepository.countByAssigneeIdAndStatusIn(member.getId(), List.of(TaskStatus.PENDING));
                    long inProgressTasks = taskRepository.countByAssigneeIdAndStatusIn(member.getId(), List.of(TaskStatus.IN_PROGRESS));
                    return new OperatorWorkloadDto(
                            member.getId(),
                            member.getName(),
                            member.getEmail(),
                            assignedCases,
                            pendingTasks,
                            inProgressTasks
                    );
                })
                .collect(Collectors.toList());

        return new TeamWorkloadResponse(
                team.getId(),
                team.getName(),
                totalOpenCases,
                casesWaitingForInfo,
                totalPendingTasks,
                unassignedTasks,
                operatorWorkloads
        );
    }
}
