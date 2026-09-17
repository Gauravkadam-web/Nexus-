package com.nexus.escalation.service;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.entity.Priority;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.casemanagement.statemachine.CaseLifecycleService;
import com.nexus.escalation.dto.CreateEscalationRuleRequest;
import com.nexus.escalation.dto.EscalateCaseRequest;
import com.nexus.escalation.dto.EscalationResponse;
import com.nexus.escalation.dto.EscalationRuleResponse;
import com.nexus.escalation.entity.*;
import com.nexus.escalation.repository.EscalationRepository;
import com.nexus.escalation.repository.EscalationRuleRepository;
import com.nexus.notification.entity.Notification;
import com.nexus.notification.repository.NotificationRepository;
import com.nexus.notification.service.NotificationService;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.repository.CategoryRepository;
import com.nexus.organization.repository.OrganizationRepository;
import com.nexus.sla.entity.CaseRisk;
import com.nexus.sla.entity.CaseSla;
import com.nexus.sla.entity.RiskLevel;
import com.nexus.sla.entity.SlaStatus;
import com.nexus.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EscalationServiceTest {

    @Mock
    private EscalationRepository escalationRepository;

    @Mock
    private EscalationRuleRepository escalationRuleRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CaseRepository caseRepository;

    @Mock
    private NotificationRepository notificationRepository;

    private CaseLifecycleService caseLifecycleService;
    private NotificationService notificationService;
    private EscalationService escalationService;

    private Organization org;
    private Category cat;
    private Case testCase;
    private User testUser;

    @BeforeEach
    void setUp() {
        caseLifecycleService = new CaseLifecycleService();
        notificationService = new NotificationService(notificationRepository);
        escalationService = new EscalationService(
                escalationRepository,
                escalationRuleRepository,
                organizationRepository,
                categoryRepository,
                caseRepository,
                caseLifecycleService,
                notificationService
        );

        org = new Organization();
        org.setId(UUID.randomUUID());
        org.setName("Acme Corp");

        cat = new Category();
        cat.setId(UUID.randomUUID());
        cat.setName("Infrastructure");
        cat.setOrganization(org);

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setName("John Operator");
        testUser.setEmail("operator@nexus.local");
        testUser.setOrganization(org);

        testCase = new Case();
        testCase.setId(UUID.randomUUID());
        testCase.setCaseNumber("NEX-20260917-0003");
        testCase.setTitle("Production cluster crash");
        testCase.setStatus(CaseStatus.INVESTIGATING);
        testCase.setPriority(Priority.URGENT);
        testCase.setCategory(cat);
        testCase.setAssignedUser(testUser);
        testCase.setCreatedAt(Instant.now());
    }

    @Test
    @DisplayName("createEscalationRule: saves and returns EscalationRuleResponse")
    void testCreateEscalationRule() {
        CreateEscalationRuleRequest req = new CreateEscalationRuleRequest(
                "Breach Rule", EscalationConditionType.SLA_BREACHED, null, EscalationLevel.MANAGER, null, true
        );

        when(organizationRepository.findById(org.getId())).thenReturn(Optional.of(org));
        when(escalationRuleRepository.save(any(EscalationRule.class))).thenAnswer(inv -> {
            EscalationRule r = inv.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });

        EscalationRuleResponse resp = escalationService.createEscalationRule(req, org.getId(), testUser);

        assertThat(resp).isNotNull();
        assertThat(resp.getName()).isEqualTo("Breach Rule");
        assertThat(resp.getConditionType()).isEqualTo(EscalationConditionType.SLA_BREACHED);
        assertThat(resp.getEscalationLevel()).isEqualTo(EscalationLevel.MANAGER);
    }

    @Test
    @DisplayName("escalateCase: manually escalates case, changes status to ESCALATED and sends notification")
    void testEscalateCase() {
        EscalateCaseRequest req = new EscalateCaseRequest("Customer issue critical", EscalationLevel.MANAGER, null);

        when(caseRepository.findById(testCase.getId())).thenReturn(Optional.of(testCase));
        when(escalationRepository.save(any(Escalation.class))).thenAnswer(inv -> {
            Escalation e = inv.getArgument(0);
            e.setId(UUID.randomUUID());
            return e;
        });
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        EscalationResponse resp = escalationService.escalateCase(testCase.getId(), req, testUser);

        assertThat(resp).isNotNull();
        assertThat(resp.getStatus()).isEqualTo(EscalationStatus.CONFIRMED);
        assertThat(resp.getEscalationLevel()).isEqualTo(EscalationLevel.MANAGER);
        assertThat(testCase.getStatus()).isEqualTo(CaseStatus.ESCALATED);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("evaluateEscalationRulesForCase: creates RECOMMENDED escalation when rule condition matches")
    void testEvaluateEscalationRulesForCase() {
        EscalationRule rule = new EscalationRule(
                org, "Auto SLA Breach Rule", EscalationConditionType.SLA_BREACHED, null,
                EscalationLevel.MANAGER, null, true, testUser
        );
        rule.setId(UUID.randomUUID());

        CaseSla caseSla = new CaseSla(testCase, null, Instant.now().minusSeconds(3600), Instant.now().minusSeconds(1800));
        caseSla.setStatus(SlaStatus.BREACHED);

        CaseRisk caseRisk = new CaseRisk(testCase, RiskLevel.HIGH, "SLA deadline breached");

        when(escalationRuleRepository.findByOrganizationIdAndActiveTrue(org.getId())).thenReturn(List.of(rule));
        when(escalationRepository.existsByCaseEntityIdAndEscalationRuleIdAndStatus(
                testCase.getId(), rule.getId(), EscalationStatus.RECOMMENDED)).thenReturn(false);

        escalationService.evaluateEscalationRulesForCase(testCase, caseSla, caseRisk);

        verify(escalationRepository, times(1)).save(any(Escalation.class));
    }
}
