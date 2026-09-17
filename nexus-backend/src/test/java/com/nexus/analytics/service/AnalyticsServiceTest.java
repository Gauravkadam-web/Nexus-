package com.nexus.analytics.service;

import com.nexus.analytics.dto.*;
import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.entity.Team;
import com.nexus.organization.repository.CategoryRepository;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnalyticsService — Unit Tests")
class AnalyticsServiceTest {

    @Mock private CaseRepository caseRepository;
    @Mock private CaseSlaRepository caseSlaRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private TeamRepository teamRepository;

    private AnalyticsService analyticsService;

    private Organization org;
    private Category networkCat;
    private Team networkTeam;

    @BeforeEach
    void setUp() {
        analyticsService = new AnalyticsService(
                caseRepository,
                caseSlaRepository,
                categoryRepository,
                teamRepository
        );

        org = new Organization("TechCorp");
        org.setId(UUID.randomUUID());

        networkCat = new Category(org, "Network Outages", null, null);
        networkCat.setId(UUID.randomUUID());

        networkTeam = new Team(org, "NetOps", null);
        networkTeam.setId(UUID.randomUUID());
    }

    @Test
    @DisplayName("US-31: getOverview returns aggregate KPIs correctly")
    void getOverview_success() {
        UUID orgId = org.getId();

        Case c1 = new Case();
        c1.setId(UUID.randomUUID());
        c1.setStatus(CaseStatus.INVESTIGATING);
        c1.setCategory(networkCat);
        c1.setCreatedAt(Instant.now().minus(2, ChronoUnit.HOURS));

        Case c2 = new Case();
        c2.setId(UUID.randomUUID());
        c2.setStatus(CaseStatus.RESOLUTION_PROPOSED);
        c2.setCategory(networkCat);
        c2.setCreatedAt(Instant.now().minus(4, ChronoUnit.HOURS));
        c2.setResolvedAt(Instant.now().minus(2, ChronoUnit.HOURS));

        when(caseRepository.findByCategoryOrganizationId(orgId)).thenReturn(List.of(c1, c2));
        when(caseSlaRepository.findByOrganizationIdAndStatus(eq(orgId), eq(SlaStatus.BREACHED), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(caseSlaRepository.findByOrganizationIdAndStatus(eq(orgId), eq(SlaStatus.MET), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new CaseSla())));

        AnalyticsOverviewResponse overview = analyticsService.getOverview(orgId);

        assertThat(overview).isNotNull();
        assertThat(overview.getTotalCases()).isEqualTo(2);
        assertThat(overview.getResolvedCases()).isEqualTo(1);
        assertThat(overview.getSlaMetPercentage()).isEqualTo(100.0);
        assertThat(overview.getAvgResolutionTimeHours()).isGreaterThan(1.9).isLessThan(2.1);
    }

    @Test
    @DisplayName("US-31: getVolumeTrends computes daily counts")
    void getVolumeTrends_success() {
        UUID orgId = org.getId();

        Case c1 = new Case();
        c1.setId(UUID.randomUUID());
        c1.setCreatedAt(Instant.now().minus(1, ChronoUnit.DAYS));
        c1.setResolvedAt(Instant.now().minus(1, ChronoUnit.DAYS));

        when(caseRepository.findByCategoryOrganizationId(orgId)).thenReturn(List.of(c1));

        List<VolumeTrendResponse> trends = analyticsService.getVolumeTrends(orgId, 7);

        assertThat(trends).hasSize(7);
        assertThat(trends.stream().mapToLong(VolumeTrendResponse::getCreatedCount).sum()).isEqualTo(1);
    }

    @Test
    @DisplayName("US-31: getCategoryBreakdown calculates distribution percentages")
    void getCategoryBreakdown_success() {
        UUID orgId = org.getId();

        Case c1 = new Case();
        c1.setId(UUID.randomUUID());
        c1.setCategory(networkCat);
        c1.setCreatedAt(Instant.now().minus(3, ChronoUnit.HOURS));
        c1.setResolvedAt(Instant.now().minus(1, ChronoUnit.HOURS));

        when(caseRepository.findByCategoryOrganizationId(orgId)).thenReturn(List.of(c1));

        List<CategoryBreakdownResponse> breakdown = analyticsService.getCategoryBreakdown(orgId);

        assertThat(breakdown).hasSize(1);
        assertThat(breakdown.get(0).getCategoryName()).isEqualTo("Network Outages");
        assertThat(breakdown.get(0).getPercentage()).isEqualTo(100.0);
        assertThat(breakdown.get(0).getAvgResolutionTimeHours()).isGreaterThan(1.9).isLessThan(2.1);
    }

    @Test
    @DisplayName("US-31: getTeamWorkloads calculates workload status correctly")
    void getTeamWorkloads_success() {
        UUID orgId = org.getId();

        Case c1 = new Case();
        c1.setId(UUID.randomUUID());
        c1.setStatus(CaseStatus.INVESTIGATING);
        c1.setAssignedTeam(networkTeam);

        when(teamRepository.findByOrganizationId(orgId)).thenReturn(List.of(networkTeam));
        when(caseRepository.findByCategoryOrganizationId(orgId)).thenReturn(List.of(c1));

        List<TeamWorkloadResponse> workloads = analyticsService.getTeamWorkloads(orgId);

        assertThat(workloads).hasSize(1);
        assertThat(workloads.get(0).getTeamName()).isEqualTo("NetOps");
        assertThat(workloads.get(0).getActiveCases()).isEqualTo(1);
        assertThat(workloads.get(0).getWorkloadStatus()).isEqualTo("NORMAL");
    }
}
