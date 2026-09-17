package com.nexus.sla.service;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.entity.Priority;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Organization;
import com.nexus.sla.entity.CaseRisk;
import com.nexus.sla.entity.CaseSla;
import com.nexus.sla.entity.RiskLevel;
import com.nexus.sla.entity.SlaStatus;
import com.nexus.sla.repository.CaseRiskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaseRiskServiceTest {

    @Mock
    private CaseRiskRepository caseRiskRepository;

    @InjectMocks
    private CaseRiskService caseRiskService;

    private Case testCase;
    private CaseSla caseSla;

    @BeforeEach
    void setUp() {
        Organization org = new Organization();
        org.setId(UUID.randomUUID());

        Category cat = new Category();
        cat.setId(UUID.randomUUID());
        cat.setOrganization(org);

        testCase = new Case();
        testCase.setId(UUID.randomUUID());
        testCase.setCaseNumber("NEX-20260917-0002");
        testCase.setTitle("Database latency spike");
        testCase.setPriority(Priority.HIGH);
        testCase.setStatus(CaseStatus.INVESTIGATING);
        testCase.setCategory(cat);
        testCase.setCreatedAt(Instant.now().minus(Duration.ofMinutes(100)));
        testCase.setUpdatedAt(Instant.now().minus(Duration.ofMinutes(70)));

        // Deadline is 120 mins from created_at, 100 mins have elapsed (83% elapsed)
        Instant deadline = testCase.getCreatedAt().plus(Duration.ofMinutes(120));
        caseSla = new CaseSla(testCase, null, testCase.getCreatedAt().plus(Duration.ofMinutes(30)), deadline);
        caseSla.setStatus(SlaStatus.ON_TRACK);
    }

    @Test
    @DisplayName("evaluateCaseRisk: detects approaching deadline and inactivity")
    void testEvaluateCaseRisk() {
        when(caseRiskRepository.save(any(CaseRisk.class))).thenAnswer(inv -> inv.getArgument(0));

        CaseRisk risk = caseRiskService.evaluateCaseRisk(testCase, caseSla);

        assertThat(risk).isNotNull();
        assertThat(risk.getRiskLevel()).isIn(RiskLevel.MEDIUM, RiskLevel.HIGH);
        assertThat(risk.getReasons()).contains("Resolution deadline approaching");
        assertThat(risk.getReasons()).contains("Inactivity detected");
    }

    @Test
    @DisplayName("evaluateCaseRisk: marks HIGH risk when case is SLA breached")
    void testEvaluateCaseRiskBreached() {
        caseSla.setStatus(SlaStatus.BREACHED);
        when(caseRiskRepository.save(any(CaseRisk.class))).thenAnswer(inv -> inv.getArgument(0));

        CaseRisk risk = caseRiskService.evaluateCaseRisk(testCase, caseSla);

        assertThat(risk).isNotNull();
        assertThat(risk.getRiskLevel()).isEqualTo(RiskLevel.HIGH);
        assertThat(risk.getReasons()).contains("SLA deadline breached");
    }
}
