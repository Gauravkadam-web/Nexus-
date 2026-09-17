package com.nexus.resolution.service;

import com.nexus.ai.repository.AutomationEventRepository;
import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.entity.Priority;
import com.nexus.casemanagement.entity.Severity;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.casemanagement.statemachine.CaseLifecycleService;
import com.nexus.notification.entity.Notification;
import com.nexus.notification.entity.NotificationType;
import com.nexus.notification.repository.NotificationRepository;
import com.nexus.notification.service.NotificationService;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.repository.CategoryRepository;
import com.nexus.organization.repository.OrganizationRepository;
import com.nexus.resolution.dto.ConfirmResolutionRequest;
import com.nexus.resolution.dto.RejectResolutionRequest;
import com.nexus.resolution.dto.ResolutionResponse;
import com.nexus.resolution.dto.SubmitResolutionRequest;
import com.nexus.resolution.entity.RequesterDecision;
import com.nexus.resolution.entity.Resolution;
import com.nexus.resolution.repository.ResolutionRepository;
import com.nexus.sla.entity.CaseSla;
import com.nexus.sla.entity.SlaStatus;
import com.nexus.sla.repository.CaseRiskRepository;
import com.nexus.sla.repository.CaseSlaRepository;
import com.nexus.sla.repository.SlaPolicyRepository;
import com.nexus.sla.service.CaseRiskService;
import com.nexus.sla.service.SlaService;
import com.nexus.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResolutionService — Unit Tests")
class ResolutionServiceTest {

    @Mock private ResolutionRepository resolutionRepository;
    @Mock private CaseRepository caseRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private CaseSlaRepository caseSlaRepository;
    @Mock private SlaPolicyRepository slaPolicyRepository;
    @Mock private OrganizationRepository organizationRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private CaseRiskRepository caseRiskRepository;
    @Mock private AutomationEventRepository automationEventRepository;

    private CaseLifecycleService caseLifecycleService;
    private NotificationService notificationService;
    private CaseRiskService caseRiskService;
    private SlaService slaService;
    private ResolutionService resolutionService;

    private Organization org;
    private Category cat;
    private User requester;
    private User operator;
    private Case testCase;

    @BeforeEach
    void setUp() {
        caseLifecycleService = new CaseLifecycleService();
        notificationService = new NotificationService(notificationRepository);
        caseRiskService = new CaseRiskService(caseRiskRepository);
        slaService = new SlaService(slaPolicyRepository, caseSlaRepository, organizationRepository, categoryRepository, caseRiskService);


        resolutionService = new ResolutionService(
                resolutionRepository,
                caseRepository,
                caseLifecycleService,
                slaService,
                notificationService
        );

        org = new Organization("Test Org");
        org.setId(UUID.randomUUID());

        cat = new Category(org, "IT", null, null);
        cat.setId(UUID.randomUUID());

        requester = new User(org, "Requester", "req@nexus.com", "pass");
        requester.setId(UUID.randomUUID());

        operator = new User(org, "Operator", "op@nexus.com", "pass");
        operator.setId(UUID.randomUUID());

        testCase = new Case();
        testCase.setId(UUID.randomUUID());
        testCase.setCaseNumber("CAS-2026-0001");
        testCase.setTitle("Database down");
        testCase.setDescription("DB unresponsive");
        testCase.setCategory(cat);
        testCase.setPriority(Priority.HIGH);
        testCase.setSeverity(Severity.HIGH);
        testCase.setRequester(requester);
        testCase.setStatus(CaseStatus.INVESTIGATING);
        testCase.setAssignedUser(operator);
    }

    @Test
    @DisplayName("US-26: Submit resolution transitions status to RESOLUTION_PROPOSED and saves resolution")
    void submitResolution_success() {
        UUID caseId = testCase.getId();
        when(caseRepository.findById(caseId)).thenReturn(Optional.of(testCase));
        when(resolutionRepository.save(any(Resolution.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CaseSla caseSla = new CaseSla(testCase, null, Instant.now().plusSeconds(3600), Instant.now().plusSeconds(7200));
        when(caseSlaRepository.findByCaseEntityId(caseId)).thenReturn(Optional.of(caseSla));

        SubmitResolutionRequest req = new SubmitResolutionRequest();
        req.setWhatWasDone("Replaced failed power supply in DB node 2");
        req.setFindings("Power unit surge caused hardware failure");
        req.setResolutionMessage("Database is back online and healthy.");

        ResolutionResponse response = resolutionService.submitResolution(caseId, req, operator);

        assertThat(response).isNotNull();
        assertThat(response.getWhatWasDone()).isEqualTo("Replaced failed power supply in DB node 2");
        assertThat(response.getFindings()).isEqualTo("Power unit surge caused hardware failure");
        assertThat(response.getRequesterDecision()).isEqualTo(RequesterDecision.PENDING);
        assertThat(testCase.getStatus()).isEqualTo(CaseStatus.RESOLUTION_PROPOSED);

        verify(notificationRepository).save(any(Notification.class));
        verify(caseSlaRepository).save(any(CaseSla.class));
    }

    @Test
    @DisplayName("US-27: Confirm resolution transitions case to CLOSED and marks resolution as CONFIRMED")
    void confirmResolution_success() {
        UUID caseId = testCase.getId();
        testCase.setStatus(CaseStatus.RESOLUTION_PROPOSED);

        Resolution resolution = new Resolution(testCase, operator, "Replaced power supply", "Hardware surge", null, null, "DB is online");
        when(caseRepository.findById(caseId)).thenReturn(Optional.of(testCase));
        when(resolutionRepository.findTopByCaseEntityIdOrderByCreatedAtDesc(caseId)).thenReturn(Optional.of(resolution));
        when(resolutionRepository.save(any(Resolution.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ConfirmResolutionRequest req = new ConfirmResolutionRequest();
        req.setFeedback("Thank you, verified working!");

        ResolutionResponse response = resolutionService.confirmResolution(caseId, req, requester);

        assertThat(response.getRequesterDecision()).isEqualTo(RequesterDecision.CONFIRMED);
        assertThat(response.getFeedback()).isEqualTo("Thank you, verified working!");
        assertThat(testCase.getStatus()).isEqualTo(CaseStatus.CLOSED);

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    @DisplayName("US-27: Reject resolution transitions case to REOPENED and marks resolution as REJECTED")
    void rejectResolution_success() {
        UUID caseId = testCase.getId();
        testCase.setStatus(CaseStatus.RESOLUTION_PROPOSED);

        Resolution resolution = new Resolution(testCase, operator, "Replaced power supply", "Hardware surge", null, null, "DB is online");
        when(caseRepository.findById(caseId)).thenReturn(Optional.of(testCase));
        when(resolutionRepository.findTopByCaseEntityIdOrderByCreatedAtDesc(caseId)).thenReturn(Optional.of(resolution));
        when(resolutionRepository.save(any(Resolution.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RejectResolutionRequest req = new RejectResolutionRequest();
        req.setRejectionReason("The database is still timing out on heavy queries.");

        ResolutionResponse response = resolutionService.rejectResolution(caseId, req, requester);

        assertThat(response.getRequesterDecision()).isEqualTo(RequesterDecision.REJECTED);
        assertThat(response.getFeedback()).isEqualTo("The database is still timing out on heavy queries.");
        assertThat(testCase.getStatus()).isEqualTo(CaseStatus.REOPENED);
    }
}
