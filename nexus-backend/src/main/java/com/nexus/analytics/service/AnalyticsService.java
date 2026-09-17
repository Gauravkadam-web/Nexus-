package com.nexus.analytics.service;

import com.nexus.analytics.dto.*;
import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Team;
import com.nexus.organization.repository.CategoryRepository;
import com.nexus.organization.repository.TeamRepository;
import com.nexus.sla.entity.CaseSla;
import com.nexus.sla.entity.SlaStatus;
import com.nexus.sla.repository.CaseSlaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Service calculating operational metrics, SLAs, and performance analytics (US-31).
 */
@Service
public class AnalyticsService {

    private final CaseRepository caseRepository;
    private final CaseSlaRepository caseSlaRepository;
    private final CategoryRepository categoryRepository;
    private final TeamRepository teamRepository;

    private static final Set<CaseStatus> OPEN_STATUSES = Set.of(
            CaseStatus.REPORTED,
            CaseStatus.UNDERSTOOD,
            CaseStatus.ASSIGNED,
            CaseStatus.INVESTIGATING,
            CaseStatus.WAITING_FOR_INFO,
            CaseStatus.AT_RISK,
            CaseStatus.ESCALATED,
            CaseStatus.RESOLUTION_PROPOSED,
            CaseStatus.REOPENED
    );

    public AnalyticsService(CaseRepository caseRepository,
                            CaseSlaRepository caseSlaRepository,
                            CategoryRepository categoryRepository,
                            TeamRepository teamRepository) {
        this.caseRepository = caseRepository;
        this.caseSlaRepository = caseSlaRepository;
        this.categoryRepository = categoryRepository;
        this.teamRepository = teamRepository;
    }

    /**
     * Computes high-level organization performance overview (US-31).
     */
    @Transactional(readOnly = true)
    public AnalyticsOverviewResponse getOverview(UUID organizationId) {
        List<Case> cases = caseRepository.findByCategoryOrganizationId(organizationId);

        long totalCases = cases.size();
        if (totalCases == 0) {
            return new AnalyticsOverviewResponse(0, 0, 0, 0, 0, 100.0, 0.0, 0.0);
        }

        long openCases = cases.stream().filter(c -> OPEN_STATUSES.contains(c.getStatus())).count();
        long resolvedCases = cases.stream().filter(c -> c.getStatus() == CaseStatus.RESOLUTION_PROPOSED).count();
        long closedCases = cases.stream().filter(c -> c.getStatus() == CaseStatus.CLOSED).count();
        long reopenedCases = cases.stream().filter(c -> c.getStatus() == CaseStatus.REOPENED).count();

        // Calculate Average Resolution Time
        List<Case> resolvedOrClosedCases = cases.stream()
                .filter(c -> c.getResolvedAt() != null && c.getCreatedAt() != null)
                .toList();

        double avgResolutionHours = 0.0;
        if (!resolvedOrClosedCases.isEmpty()) {
            double totalSeconds = resolvedOrClosedCases.stream()
                    .mapToDouble(c -> Duration.between(c.getCreatedAt(), c.getResolvedAt()).getSeconds())
                    .sum();
            avgResolutionHours = (totalSeconds / resolvedOrClosedCases.size()) / 3600.0;
        }

        // SLA performance
        long breachedCount = caseSlaRepository.findByOrganizationIdAndStatus(organizationId, SlaStatus.BREACHED, Pageable.unpaged())
                .getTotalElements();
        long metCount = caseSlaRepository.findByOrganizationIdAndStatus(organizationId, SlaStatus.MET, Pageable.unpaged())
                .getTotalElements();
        long totalEvaluatedSlas = breachedCount + metCount;

        double slaMetPercentage = totalEvaluatedSlas > 0 
                ? ((double) metCount / totalEvaluatedSlas) * 100.0 
                : 100.0;

        double reopenedRate = ((double) reopenedCases / totalCases) * 100.0;

        return new AnalyticsOverviewResponse(
                totalCases,
                openCases,
                resolvedCases,
                closedCases,
                breachedCount,
                slaMetPercentage,
                avgResolutionHours,
                reopenedRate
        );
    }

    /**
     * Computes daily volume trend over the specified window (US-31).
     */
    @Transactional(readOnly = true)
    public List<VolumeTrendResponse> getVolumeTrends(UUID organizationId, int days) {
        int windowDays = days > 0 && days <= 90 ? days : 7;
        Instant start = Instant.now().minus(windowDays, ChronoUnit.DAYS);

        List<Case> cases = caseRepository.findByCategoryOrganizationId(organizationId);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);

        Map<String, long[]> dayStats = new LinkedHashMap<>();
        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        for (int i = windowDays - 1; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            dayStats.put(d.toString(), new long[]{0, 0, 0}); // [created, resolved, breached]
        }

        for (Case c : cases) {
            if (c.getCreatedAt() != null && c.getCreatedAt().isAfter(start)) {
                String createdKey = dtf.format(c.getCreatedAt());
                if (dayStats.containsKey(createdKey)) {
                    dayStats.get(createdKey)[0]++;
                }
            }
            if (c.getResolvedAt() != null && c.getResolvedAt().isAfter(start)) {
                String resolvedKey = dtf.format(c.getResolvedAt());
                if (dayStats.containsKey(resolvedKey)) {
                    dayStats.get(resolvedKey)[1]++;
                }
            }
        }

        List<VolumeTrendResponse> trends = new ArrayList<>();
        for (Map.Entry<String, long[]> entry : dayStats.entrySet()) {
            trends.add(new VolumeTrendResponse(
                    entry.getKey(),
                    entry.getValue()[0],
                    entry.getValue()[1],
                    entry.getValue()[2]
            ));
        }

        return trends;
    }

    /**
     * Computes breakdown of case volume and resolution times across categories (US-31).
     */
    @Transactional(readOnly = true)
    public List<CategoryBreakdownResponse> getCategoryBreakdown(UUID organizationId) {
        List<Case> cases = caseRepository.findByCategoryOrganizationId(organizationId);
        long totalCases = cases.size();

        Map<UUID, List<Case>> categoryMap = new HashMap<>();
        for (Case c : cases) {
            if (c.getCategory() != null) {
                categoryMap.computeIfAbsent(c.getCategory().getId(), k -> new ArrayList<>()).add(c);
            }
        }

        List<CategoryBreakdownResponse> response = new ArrayList<>();
        for (Map.Entry<UUID, List<Case>> entry : categoryMap.entrySet()) {
            List<Case> catCases = entry.getValue();
            String catName = catCases.get(0).getCategory().getName();
            long count = catCases.size();
            double pct = totalCases > 0 ? ((double) count / totalCases) * 100.0 : 0.0;

            List<Case> resolved = catCases.stream().filter(c -> c.getResolvedAt() != null).toList();
            double avgHours = 0.0;
            if (!resolved.isEmpty()) {
                double totalSecs = resolved.stream()
                        .mapToDouble(c -> Duration.between(c.getCreatedAt(), c.getResolvedAt()).getSeconds())
                        .sum();
                avgHours = (totalSecs / resolved.size()) / 3600.0;
            }

            response.add(new CategoryBreakdownResponse(entry.getKey(), catName, count, pct, avgHours));
        }

        response.sort((a, b) -> Long.compare(b.getTotalCases(), a.getTotalCases()));
        return response;
    }

    /**
     * Computes workload distribution and health status per team (US-31).
     */
    @Transactional(readOnly = true)
    public List<TeamWorkloadResponse> getTeamWorkloads(UUID organizationId) {
        List<Team> teams = teamRepository.findByOrganizationId(organizationId);
        List<Case> cases = caseRepository.findByCategoryOrganizationId(organizationId);

        List<TeamWorkloadResponse> response = new ArrayList<>();

        for (Team team : teams) {
            List<Case> teamCases = cases.stream()
                    .filter(c -> c.getAssignedTeam() != null && c.getAssignedTeam().getId().equals(team.getId()))
                    .toList();

            long activeCases = teamCases.stream().filter(c -> OPEN_STATUSES.contains(c.getStatus())).count();
            long resolvedCases = teamCases.stream().filter(c -> c.getStatus() == CaseStatus.RESOLUTION_PROPOSED || c.getStatus() == CaseStatus.CLOSED).count();

            String status = "NORMAL";
            if (activeCases > 20) {
                status = "CRITICAL";
            } else if (activeCases > 10) {
                status = "HIGH";
            }

            response.add(new TeamWorkloadResponse(
                    team.getId(),
                    team.getName(),
                    activeCases,
                    resolvedCases,
                    0L,
                    status
            ));
        }

        response.sort((a, b) -> Long.compare(b.getActiveCases(), a.getActiveCases()));
        return response;
    }
}
