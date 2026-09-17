package com.nexus.sla.service;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.Priority;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.repository.CategoryRepository;
import com.nexus.organization.repository.OrganizationRepository;
import com.nexus.sla.dto.CaseSlaResponse;
import com.nexus.sla.dto.CreateSlaPolicyRequest;
import com.nexus.sla.dto.SlaPolicyResponse;
import com.nexus.sla.entity.CaseRisk;
import com.nexus.sla.entity.CaseSla;
import com.nexus.sla.entity.RiskLevel;
import com.nexus.sla.entity.SlaPolicy;
import com.nexus.sla.entity.SlaStatus;
import com.nexus.sla.repository.CaseRiskRepository;
import com.nexus.sla.repository.CaseSlaRepository;
import com.nexus.sla.repository.SlaPolicyRepository;
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
class SlaServiceTest {

    @Mock
    private SlaPolicyRepository slaPolicyRepository;

    @Mock
    private CaseSlaRepository caseSlaRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CaseRiskRepository caseRiskRepository;

    private CaseRiskService caseRiskService;
    private SlaService slaService;

    private Organization org;
    private Category cat;
    private Case testCase;

    @BeforeEach
    void setUp() {
        caseRiskService = new CaseRiskService(caseRiskRepository);
        slaService = new SlaService(slaPolicyRepository, caseSlaRepository, organizationRepository, categoryRepository, caseRiskService);

        org = new Organization();
        org.setId(UUID.randomUUID());
        org.setName("Acme Corp");

        cat = new Category();
        cat.setId(UUID.randomUUID());
        cat.setName("IT Support");
        cat.setOrganization(org);

        testCase = new Case();
        testCase.setId(UUID.randomUUID());
        testCase.setCaseNumber("NEX-20260917-0001");
        testCase.setTitle("VPN connection failed");
        testCase.setPriority(Priority.HIGH);
        testCase.setCategory(cat);
        testCase.setCreatedAt(Instant.now());
    }

    @Test
    @DisplayName("createSlaPolicy: creates and returns SlaPolicyResponse")
    void testCreateSlaPolicy() {
        CreateSlaPolicyRequest req = new CreateSlaPolicyRequest(cat.getId(), Priority.HIGH, 30, 240);

        when(organizationRepository.findById(org.getId())).thenReturn(Optional.of(org));
        when(categoryRepository.findById(cat.getId())).thenReturn(Optional.of(cat));
        when(slaPolicyRepository.save(any(SlaPolicy.class))).thenAnswer(inv -> {
            SlaPolicy p = inv.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });

        SlaPolicyResponse resp = slaService.createSlaPolicy(req, org.getId());

        assertThat(resp).isNotNull();
        assertThat(resp.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(resp.getResponseTimeMinutes()).isEqualTo(30);
        assertThat(resp.getResolutionTimeMinutes()).isEqualTo(240);
        assertThat(resp.getCategoryName()).isEqualTo("IT Support");
    }

    @Test
    @DisplayName("attachSlaToCase: matches policy and calculates response/resolution deadlines")
    void testAttachSlaToCase() {
        SlaPolicy policy = new SlaPolicy(org, cat, Priority.HIGH, 45, 180);
        policy.setId(UUID.randomUUID());

        when(caseSlaRepository.findByCaseEntityId(testCase.getId())).thenReturn(Optional.empty());
        when(slaPolicyRepository.findByOrganizationIdAndCategoryIdAndPriority(org.getId(), cat.getId(), Priority.HIGH))
                .thenReturn(Optional.of(policy));
        when(caseSlaRepository.save(any(CaseSla.class))).thenAnswer(inv -> inv.getArgument(0));
        when(caseRiskRepository.save(any(CaseRisk.class))).thenAnswer(inv -> inv.getArgument(0));

        CaseSla attached = slaService.attachSlaToCase(testCase);

        assertThat(attached).isNotNull();
        assertThat(attached.getSlaPolicy()).isEqualTo(policy);
        assertThat(attached.getResponseDeadline()).isAfter(testCase.getCreatedAt());
        assertThat(attached.getResolutionDeadline()).isAfter(attached.getResponseDeadline());
        verify(caseRiskRepository, times(1)).save(any(CaseRisk.class));
    }

    @Test
    @DisplayName("getCaseSla: returns CaseSlaResponse with consumed percentages and risk factors")
    void testGetCaseSla() {
        CaseSla caseSla = new CaseSla(testCase, null, Instant.now().plusSeconds(1800), Instant.now().plusSeconds(7200));
        caseSla.setId(UUID.randomUUID());

        CaseRisk caseRisk = new CaseRisk(testCase, RiskLevel.MEDIUM, "Resolution deadline approaching (>70% elapsed)");

        when(caseSlaRepository.findByCaseEntityId(testCase.getId())).thenReturn(Optional.of(caseSla));
        when(caseRiskRepository.findTopByCaseEntityIdOrderByDetectedAtDesc(testCase.getId())).thenReturn(Optional.of(caseRisk));

        CaseSlaResponse resp = slaService.getCaseSla(testCase.getId());

        assertThat(resp).isNotNull();
        assertThat(resp.getCaseNumber()).isEqualTo("NEX-20260917-0001");
        assertThat(resp.getRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
        assertThat(resp.getRiskReasons()).contains("Resolution deadline approaching (>70% elapsed)");
    }

    @Test
    @DisplayName("recordCaseResolution: sets status to MET if resolved within deadline")
    void testRecordCaseResolutionMet() {
        CaseSla caseSla = new CaseSla(testCase, null, Instant.now().plusSeconds(1800), Instant.now().plusSeconds(7200));
        when(caseSlaRepository.findByCaseEntityId(testCase.getId())).thenReturn(Optional.of(caseSla));

        slaService.recordCaseResolution(testCase.getId());

        assertThat(caseSla.getStatus()).isEqualTo(SlaStatus.MET);
        assertThat(caseSla.getResolvedAt()).isNotNull();
        verify(caseSlaRepository, times(1)).save(caseSla);
    }
}
