package com.nexus.analytics.service;

import com.nexus.analytics.dto.OperationalInsightResponse;
import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.organization.entity.Team;
import com.nexus.organization.repository.TeamRepository;
import com.nexus.sla.entity.CaseSla;
import com.nexus.sla.entity.SlaStatus;
import com.nexus.sla.repository.CaseSlaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Service generating automated operational insights and early warning signals (US-32).
 */
@Service
public class OperationalInsightsService {

    private final CaseRepository caseRepository;
    private final CaseSlaRepository caseSlaRepository;
    private final TeamRepository teamRepository;

    private static final Set<CaseStatus> ACTIVE_STATUSES = Set.of(
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

    public OperationalInsightsService(CaseRepository caseRepository,
                                      CaseSlaRepository caseSlaRepository,
                                      TeamRepository teamRepository) {
        this.caseRepository = caseRepository;
        this.caseSlaRepository = caseSlaRepository;
        this.teamRepository = teamRepository;
    }

    /**
     * Generates a synthesized list of operational risk and workload insights (US-32).
     */
    @Transactional(readOnly = true)
    public List<OperationalInsightResponse> generateInsights(UUID organizationId) {
        List<OperationalInsightResponse> insights = new ArrayList<>();

        List<Case> allCases = caseRepository.findByCategoryOrganizationId(organizationId);
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);

        // 1. Detect Workload Bottlenecks
        List<Team> teams = teamRepository.findByOrganizationId(organizationId);
        for (Team team : teams) {
            long activeCount = allCases.stream()
                    .filter(c -> c.getAssignedTeam() != null && c.getAssignedTeam().getId().equals(team.getId()))
                    .filter(c -> ACTIVE_STATUSES.contains(c.getStatus()))
                    .count();

            if (activeCount >= 10) {
                String severity = activeCount >= 20 ? "CRITICAL" : "HIGH";
                insights.add(new OperationalInsightResponse(
                        "WORKLOAD_BOTTLENECK",
                        severity,
                        "Team Overloaded: " + team.getName(),
                        String.format("Team '%s' currently holds %d active cases, exceeding optimal capacity thresholds.", team.getName(), activeCount),
                        "Reassign incoming cases to adjacent support squads or provision temporary triage capacity.",
                        Map.of("teamId", team.getId().toString(), "teamName", team.getName(), "activeCases", activeCount)
                ));
            }
        }

        // 2. Detect Rising Category Spikes
        Map<String, Long> recentCategoryCounts = new HashMap<>();
        for (Case c : allCases) {
            if (c.getCreatedAt() != null && c.getCreatedAt().isAfter(sevenDaysAgo) && c.getCategory() != null) {
                recentCategoryCounts.merge(c.getCategory().getName(), 1L, Long::sum);
            }
        }

        for (Map.Entry<String, Long> entry : recentCategoryCounts.entrySet()) {
            if (entry.getValue() >= 5) {
                insights.add(new OperationalInsightResponse(
                        "RISING_CATEGORY",
                        "MEDIUM",
                        "Incident Spike: " + entry.getKey(),
                        String.format("Category '%s' recorded %d new cases in the past 7 days.", entry.getKey(), entry.getValue()),
                        "Review recent changes or deploy targeted KB articles to deflect recurring incident reports.",
                        Map.of("categoryName", entry.getKey(), "recentVolume", entry.getValue())
                ));
            }
        }

        // 3. Detect SLA Risk Pressure
        List<CaseSla> atRiskSlas = caseSlaRepository.findByOrganizationIdAndStatus(organizationId, SlaStatus.AT_RISK, Pageable.unpaged())
                .getContent();
        if (!atRiskSlas.isEmpty()) {
            insights.add(new OperationalInsightResponse(
                    "SLA_RISK_SPIKE",
                    "HIGH",
                    "SLA Deadline Pressure Detected",
                    String.format("%d active cases are currently approaching SLA resolution deadlines (AT_RISK).", atRiskSlas.size()),
                    "Prioritize queue triage and review escalation paths for at-risk tickets immediately.",
                    Map.of("atRiskCount", atRiskSlas.size())
            ));
        }

        // 4. Detect Reopen Rate Anomalies
        long reopenedCount = allCases.stream().filter(c -> c.getStatus() == CaseStatus.REOPENED).count();
        if (reopenedCount >= 3) {
            insights.add(new OperationalInsightResponse(
                    "RECURRING_FAILURE",
                    "MEDIUM",
                    "Elevated Case Reopen Volume",
                    String.format("%d cases were reopened after initial resolution, signaling root-cause recurrence or incomplete fixes.", reopenedCount),
                    "Promote recurring incidents into a formal Problem record for root cause investigation.",
                    Map.of("reopenedCount", reopenedCount)
            ));
        }

        return insights;
    }
}
