package com.nexus.ai.service;

import com.nexus.ai.dto.AssignmentRecommendationResponse;
import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.entity.Team;
import com.nexus.organization.repository.TeamRepository;
import com.nexus.user.entity.Role;
import com.nexus.user.entity.RoleType;
import com.nexus.user.entity.User;
import com.nexus.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SmartAssignmentService — Unit Tests")
class SmartAssignmentServiceTest {

    @Mock CaseRepository caseRepository;
    @Mock TeamRepository teamRepository;
    @Mock UserRepository userRepository;

    @InjectMocks SmartAssignmentService service;

    private Organization org;
    private Team networkTeam;
    private Category networkCategory;
    private Case testCase;
    private User operator1; // has 3 active cases
    private User operator2; // has 0 active cases
    private UUID caseId;

    @BeforeEach
    void setUp() {
        org = new Organization("Acme Corp");
        org.setId(UUID.randomUUID());
        caseId = UUID.randomUUID();

        networkTeam = new Team(org, "Network Team", null);
        networkTeam.setId(UUID.randomUUID());
        networkCategory = new Category(org, "Network Issues", null, networkTeam);
        networkCategory.setId(UUID.randomUUID());

        testCase = new Case();
        testCase.setId(caseId);
        testCase.setCaseNumber("NX-2026-0010");
        testCase.setTitle("VPN Outage");
        testCase.setCategory(networkCategory);

        Role opRole = new Role(RoleType.OPERATOR);

        operator1 = new User();
        operator1.setId(UUID.randomUUID());
        operator1.setName("Alice Busy");
        operator1.setRoles(Set.of(opRole));

        operator2 = new User();
        operator2.setId(UUID.randomUUID());
        operator2.setName("Bob Free");
        operator2.setRoles(Set.of(opRole));
    }

    @Test
    @DisplayName("recommendAssignment — selects operator with lowest workload and category team")
    void recommendAssignment_picksLowestWorkloadOperator() {
        when(caseRepository.findById(caseId)).thenReturn(Optional.of(testCase));
        when(userRepository.findByOrganizationId(org.getId())).thenReturn(List.of(operator1, operator2));

        // Create 3 active cases assigned to operator1
        Case c1 = new Case(); c1.setCategory(networkCategory); c1.setStatus(CaseStatus.INVESTIGATING); c1.setAssignedUser(operator1);
        Case c2 = new Case(); c2.setCategory(networkCategory); c2.setStatus(CaseStatus.INVESTIGATING); c2.setAssignedUser(operator1);
        Case c3 = new Case(); c3.setCategory(networkCategory); c3.setStatus(CaseStatus.WAITING_FOR_INFO); c3.setAssignedUser(operator1);

        when(caseRepository.findAll()).thenReturn(List.of(c1, c2, c3));

        AssignmentRecommendationResponse resp = service.recommendAssignment(caseId);

        assertThat(resp).isNotNull();
        assertThat(resp.getSuggestedTeamId()).isEqualTo(networkTeam.getId());
        assertThat(resp.getSuggestedTeamName()).isEqualTo("Network Team");
        assertThat(resp.getSuggestedUserId()).isEqualTo(operator2.getId());
        assertThat(resp.getSuggestedUserName()).isEqualTo("Bob Free");
        assertThat(resp.getCurrentWorkloadCount()).isEqualTo(0);
        assertThat(resp.getReasoning()).contains("Bob Free");
        assertThat(resp.getReasoning()).contains("lowest active workload");
    }
}
