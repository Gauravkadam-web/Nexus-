package com.nexus.problem.service;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.Priority;
import com.nexus.casemanagement.entity.Severity;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.repository.OrganizationRepository;
import com.nexus.problem.dto.CreateProblemRequest;
import com.nexus.problem.dto.ProblemResponse;
import com.nexus.problem.dto.UpdateProblemRequest;
import com.nexus.problem.entity.Problem;
import com.nexus.problem.entity.ProblemIncidentRelation;
import com.nexus.problem.entity.ProblemStatus;
import com.nexus.problem.repository.ProblemIncidentRelationRepository;
import com.nexus.problem.repository.ProblemRepository;
import com.nexus.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProblemService — Unit Tests")
class ProblemServiceTest {

    @Mock private ProblemRepository problemRepository;
    @Mock private ProblemIncidentRelationRepository relationRepository;
    @Mock private OrganizationRepository organizationRepository;
    @Mock private CaseRepository caseRepository;

    private ProblemService problemService;

    private Organization org;
    private User manager;
    private Case incidentCase;

    @BeforeEach
    void setUp() {
        problemService = new ProblemService(
                problemRepository,
                relationRepository,
                organizationRepository,
                caseRepository
        );

        org = new Organization("Test Org");
        org.setId(UUID.randomUUID());

        manager = new User(org, "Manager", "mgr@nexus.com", "pass");
        manager.setId(UUID.randomUUID());

        Category cat = new Category(org, "Network", null, null);
        cat.setId(UUID.randomUUID());

        incidentCase = new Case();
        incidentCase.setId(UUID.randomUUID());
        incidentCase.setCaseNumber("CAS-2026-0001");
        incidentCase.setTitle("Core Switch Flapping");
        incidentCase.setDescription("Switch flapped 12 times");
        incidentCase.setCategory(cat);
        incidentCase.setPriority(Priority.HIGH);
        incidentCase.setSeverity(Severity.HIGH);
        incidentCase.setRequester(manager);
    }

    @Test
    @DisplayName("US-28: Create problem persists problem record with OPEN status")
    void createProblem_success() {
        when(organizationRepository.findById(org.getId())).thenReturn(Optional.of(org));
        when(problemRepository.save(any(Problem.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateProblemRequest req = new CreateProblemRequest();
        req.setTitle("Core Switch Firmware Instability");
        req.setSuspectedRootCause("Memory leak in ASIC firmware version 3.2");

        ProblemResponse response = problemService.createProblem(req, org.getId(), manager);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Core Switch Firmware Instability");
        assertThat(response.getSuspectedRootCause()).isEqualTo("Memory leak in ASIC firmware version 3.2");
        assertThat(response.getStatus()).isEqualTo(ProblemStatus.OPEN);
    }

    @Test
    @DisplayName("US-28: Link incident to problem associates case")
    void linkIncident_success() {
        Problem problem = new Problem(org, "Core Switch Issue", "Suspected bug", manager);
        problem.setId(UUID.randomUUID());
        UUID problemId = problem.getId();
        UUID caseId = incidentCase.getId();

        when(problemRepository.findById(problemId)).thenReturn(Optional.of(problem));
        when(caseRepository.findById(caseId)).thenReturn(Optional.of(incidentCase));
        when(relationRepository.existsByProblemIdAndCaseEntityId(problemId, caseId)).thenReturn(false);
        when(relationRepository.save(any(ProblemIncidentRelation.class))).thenAnswer(inv -> inv.getArgument(0));

        ProblemResponse response = problemService.linkIncident(problemId, caseId, org.getId());

        assertThat(response).isNotNull();
        verify(relationRepository).save(any(ProblemIncidentRelation.class));
    }

    @Test
    @DisplayName("US-28: Update problem changes status, rootCause, and corrective actions")
    void updateProblem_success() {
        Problem problem = new Problem(org, "Core Switch Issue", "Suspected bug", manager);
        problem.setId(UUID.randomUUID());
        UUID problemId = problem.getId();

        when(problemRepository.findById(problemId)).thenReturn(Optional.of(problem));
        when(problemRepository.save(any(Problem.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateProblemRequest req = new UpdateProblemRequest();
        req.setStatus(ProblemStatus.RESOLVED);
        req.setConfirmedRootCause("Memory leak in ASIC firmware version 3.2");
        req.setCorrectiveAction("Upgraded to firmware version 3.4 across all switches");

        ProblemResponse response = problemService.updateProblem(problemId, req, org.getId());

        assertThat(response.getStatus()).isEqualTo(ProblemStatus.RESOLVED);
        assertThat(response.getConfirmedRootCause()).isEqualTo("Memory leak in ASIC firmware version 3.2");
        assertThat(response.getCorrectiveAction()).isEqualTo("Upgraded to firmware version 3.4 across all switches");
    }
}
