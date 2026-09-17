package com.nexus.analytics.service;

import com.nexus.analytics.dto.OperationalInsightResponse;
import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.entity.Team;
import com.nexus.organization.repository.TeamRepository;
import com.nexus.sla.entity.CaseSla;
import com.nexus.sla.entity.SlaStatus;
import com.nexus.sla.repository.CaseSlaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OperationalInsightsService — Unit Tests")
class OperationalInsightsServiceTest {

    @Mock private CaseRepository caseRepository;
    @Mock private CaseSlaRepository caseSlaRepository;
    @Mock private TeamRepository teamRepository;

    private OperationalInsightsService insightsService;

    private Organization org;
    private Team dbTeam;
    private Category dbCat;

    @BeforeEach
    void setUp() {
        insightsService = new OperationalInsightsService(
                caseRepository,
                caseSlaRepository,
                teamRepository
        );

        org = new Organization("Enterprise");
        org.setId(UUID.randomUUID());

        dbTeam = new Team(org, "DBA Team", null);
        dbTeam.setId(UUID.randomUUID());

        dbCat = new Category(org, "Database Incidents", null, null);
        dbCat.setId(UUID.randomUUID());
    }

    @Test
    @DisplayName("US-32: generateInsights detects team workload bottleneck when active cases exceed threshold")
    void generateInsights_workloadBottleneck() {
        UUID orgId = org.getId();

        List<Case> cases = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Case c = new Case();
            c.setId(UUID.randomUUID());
            c.setStatus(CaseStatus.INVESTIGATING);
            c.setAssignedTeam(dbTeam);
            c.setCategory(dbCat);
            c.setCreatedAt(Instant.now());
            cases.add(c);
        }

        when(caseRepository.findByCategoryOrganizationId(orgId)).thenReturn(cases);
        when(teamRepository.findByOrganizationId(orgId)).thenReturn(List.of(dbTeam));
        when(caseSlaRepository.findByOrganizationIdAndStatus(eq(orgId), eq(SlaStatus.AT_RISK), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        List<OperationalInsightResponse> insights = insightsService.generateInsights(orgId);

        assertThat(insights).isNotEmpty();
        assertThat(insights.stream().anyMatch(i -> i.getInsightType().equals("WORKLOAD_BOTTLENECK"))).isTrue();
        assertThat(insights.stream().anyMatch(i -> i.getInsightType().equals("RISING_CATEGORY"))).isTrue();
    }

    @Test
    @DisplayName("US-32: generateInsights detects SLA risk pressure")
    void generateInsights_slaPressure() {
        UUID orgId = org.getId();

        when(caseRepository.findByCategoryOrganizationId(orgId)).thenReturn(List.of());
        when(teamRepository.findByOrganizationId(orgId)).thenReturn(List.of());
        when(caseSlaRepository.findByOrganizationIdAndStatus(eq(orgId), eq(SlaStatus.AT_RISK), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new CaseSla(), new CaseSla())));

        List<OperationalInsightResponse> insights = insightsService.generateInsights(orgId);

        assertThat(insights).hasSize(1);
        assertThat(insights.get(0).getInsightType()).isEqualTo("SLA_RISK_SPIKE");
        assertThat(insights.get(0).getSeverity()).isEqualTo("HIGH");
    }
}
