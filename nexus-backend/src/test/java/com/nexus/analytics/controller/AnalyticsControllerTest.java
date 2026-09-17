package com.nexus.analytics.controller;

import com.nexus.analytics.dto.*;
import com.nexus.analytics.service.AnalyticsService;
import com.nexus.analytics.service.OperationalInsightsService;
import com.nexus.auth.security.UserPrincipal;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.common.response.ApiResponse;
import com.nexus.organization.repository.CategoryRepository;
import com.nexus.organization.repository.TeamRepository;
import com.nexus.sla.repository.CaseSlaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnalyticsController — Unit Tests")
class AnalyticsControllerTest {

    @Mock private CaseRepository caseRepository;
    @Mock private CaseSlaRepository caseSlaRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private TeamRepository teamRepository;

    private AnalyticsController analyticsController;
    private UserPrincipal managerPrincipal;
    private UUID orgId;

    @BeforeEach
    void setUp() {
        AnalyticsService analyticsService = new AnalyticsService(
                caseRepository,
                caseSlaRepository,
                categoryRepository,
                teamRepository
        );
        OperationalInsightsService insightsService = new OperationalInsightsService(
                caseRepository,
                caseSlaRepository,
                teamRepository
        );

        analyticsController = new AnalyticsController(analyticsService, insightsService);

        orgId = UUID.randomUUID();
        managerPrincipal = new UserPrincipal(
                UUID.randomUUID(),
                orgId,
                "Manager",
                "manager@nexus.com",
                "hash",
                true,
                List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_MANAGER"))
        );
    }

    @Test
    @DisplayName("US-31: Overview endpoint returns successful API response")
    void getOverview_success() {
        when(caseRepository.findByCategoryOrganizationId(orgId)).thenReturn(List.of());

        ResponseEntity<ApiResponse<AnalyticsOverviewResponse>> response =
                analyticsController.getOverview(managerPrincipal);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getTotalCases()).isEqualTo(0);
    }

    @Test
    @DisplayName("US-31: Trends endpoint returns volume list")
    void getVolumeTrends_success() {
        when(caseRepository.findByCategoryOrganizationId(orgId)).thenReturn(List.of());

        ResponseEntity<ApiResponse<List<VolumeTrendResponse>>> response =
                analyticsController.getVolumeTrends(7, managerPrincipal);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(7);
    }

    @Test
    @DisplayName("US-31: Category breakdown endpoint returns list")
    void getCategoryBreakdown_success() {
        when(caseRepository.findByCategoryOrganizationId(orgId)).thenReturn(List.of());

        ResponseEntity<ApiResponse<List<CategoryBreakdownResponse>>> response =
                analyticsController.getCategoryBreakdown(managerPrincipal);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("US-31: Team workloads endpoint returns list")
    void getTeamWorkloads_success() {
        when(teamRepository.findByOrganizationId(orgId)).thenReturn(List.of());
        when(caseRepository.findByCategoryOrganizationId(orgId)).thenReturn(List.of());

        ResponseEntity<ApiResponse<List<TeamWorkloadResponse>>> response =
                analyticsController.getTeamWorkloads(managerPrincipal);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("US-32: Operational insights endpoint returns generated insights")
    void getOperationalInsights_success() {
        when(caseRepository.findByCategoryOrganizationId(orgId)).thenReturn(List.of());
        when(teamRepository.findByOrganizationId(orgId)).thenReturn(List.of());
        when(caseSlaRepository.findByOrganizationIdAndStatus(eq(orgId), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        ResponseEntity<ApiResponse<List<OperationalInsightResponse>>> response =
                analyticsController.getOperationalInsights(managerPrincipal);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
    }
}
