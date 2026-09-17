package com.nexus.collaboration.service;

import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.collaboration.dto.TeamWorkloadResponse;
import com.nexus.collaboration.repository.CaseTaskRepository;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.entity.Team;
import com.nexus.organization.repository.TeamRepository;
import com.nexus.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private CaseRepository caseRepository;

    @Mock
    private CaseTaskRepository taskRepository;

    @InjectMocks
    private WorkloadService workloadService;

    private Team team;
    private User member1;

    @BeforeEach
    void setUp() {
        Organization org = new Organization();
        org.setId(UUID.randomUUID());

        member1 = new User();
        member1.setId(UUID.randomUUID());
        member1.setName("Operator Bob");
        member1.setEmail("bob@nexus.com");

        team = new Team(org, "Network Operations", member1);
        team.setId(UUID.randomUUID());
        team.setMembers(Set.of(member1));
    }

    @Test
    @DisplayName("US-10: Team Lead retrieves team workload and operator pending metrics")
    void testGetTeamWorkload() {
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));
        when(caseRepository.countByAssignedTeamIdAndStatusIn(eq(team.getId()), any())).thenReturn(5L);
        when(caseRepository.countByAssignedTeamIdAndStatus(eq(team.getId()), eq(CaseStatus.WAITING_FOR_INFO))).thenReturn(2L);
        when(taskRepository.countByCaseEntityAssignedTeamIdAndStatusIn(eq(team.getId()), any())).thenReturn(8L);
        when(taskRepository.countByCaseEntityAssignedTeamIdAndAssigneeIsNullAndStatusIn(eq(team.getId()), any())).thenReturn(1L);

        when(caseRepository.countByAssignedUserIdAndStatusIn(eq(member1.getId()), any())).thenReturn(3L);
        when(taskRepository.countByAssigneeIdAndStatusIn(eq(member1.getId()), any())).thenReturn(2L);

        TeamWorkloadResponse response = workloadService.getTeamWorkload(team.getId());

        assertThat(response).isNotNull();
        assertThat(response.getTeamName()).isEqualTo("Network Operations");
        assertThat(response.getTotalOpenCases()).isEqualTo(5L);
        assertThat(response.getCasesWaitingForInfo()).isEqualTo(2L);
        assertThat(response.getTotalPendingTasks()).isEqualTo(8L);
        assertThat(response.getUnassignedTasksCount()).isEqualTo(1L);
        assertThat(response.getOperators()).hasSize(1);
        assertThat(response.getOperators().get(0).getAssignedCasesCount()).isEqualTo(3L);
    }
}
