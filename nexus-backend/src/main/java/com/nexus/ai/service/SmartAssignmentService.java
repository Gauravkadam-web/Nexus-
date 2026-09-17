package com.nexus.ai.service;

import com.nexus.ai.dto.AssignmentRecommendationResponse;
import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Team;
import com.nexus.organization.repository.TeamRepository;
import com.nexus.user.entity.RoleType;
import com.nexus.user.entity.User;
import com.nexus.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating smart case assignment recommendations (US-19, US-20).
 * Recommends optimal team and operator considering category routing, team responsibility,
 * and real-time operator workload balancing.
 */
@Service
public class SmartAssignmentService {

    private static final Logger log = LoggerFactory.getLogger(SmartAssignmentService.class);

    private final CaseRepository caseRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public SmartAssignmentService(CaseRepository caseRepository,
                                  TeamRepository teamRepository,
                                  UserRepository userRepository) {
        this.caseRepository = caseRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    /**
     * Generates a smart assignment recommendation for a given case.
     *
     * @param caseId the UUID of the case
     * @return the assignment recommendation payload
     */
    @Transactional(readOnly = true)
    public AssignmentRecommendationResponse recommendAssignment(UUID caseId) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found: " + caseId));

        UUID orgId = caseEntity.getOrganization() != null ? caseEntity.getOrganization().getId() : null;

        // 1. Determine recommended team
        Team suggestedTeam = null;
        Category category = caseEntity.getCategory();
        if (category != null && category.getDefaultTeam() != null) {
            suggestedTeam = category.getDefaultTeam();
        } else if (caseEntity.getAssignedTeam() != null) {
            suggestedTeam = caseEntity.getAssignedTeam();
        } else if (orgId != null) {
            List<Team> orgTeams = teamRepository.findByOrganizationId(orgId);
            if (!orgTeams.isEmpty()) {
                suggestedTeam = orgTeams.get(0);
            }
        }

        // 2. Determine candidate operators in the organization
        List<User> orgUsers = orgId != null ? userRepository.findByOrganizationId(orgId) : userRepository.findAll();
        List<User> candidateOperators = orgUsers.stream()
                .filter(u -> u.getRoles().stream().anyMatch(r ->
                        r.getName() == RoleType.OPERATOR || r.getName() == RoleType.TEAM_LEAD))
                .collect(Collectors.toList());

        if (candidateOperators.isEmpty()) {
            // Fallback to any staff member
            candidateOperators = orgUsers;
        }

        // 3. Evaluate workload for each candidate
        // Active cases = status NOT IN (CLOSED, CANCELLED)
        List<Case> activeCases = caseRepository.findAll().stream()
                .filter(c -> orgId == null || (c.getOrganization() != null && c.getOrganization().getId().equals(orgId)))
                .filter(c -> c.getStatus() != CaseStatus.CLOSED && c.getStatus() != CaseStatus.CANCELLED)
                .collect(Collectors.toList());

        Map<UUID, Long> workloadMap = new HashMap<>();
        for (User op : candidateOperators) {
            long count = activeCases.stream()
                    .filter(c -> c.getAssignedUser() != null && c.getAssignedUser().getId().equals(op.getId()))
                    .count();
            workloadMap.put(op.getId(), count);
        }

        // 4. Select operator with lowest workload
        User bestOperator = candidateOperators.stream()
                .min(Comparator.comparingLong(u -> workloadMap.getOrDefault(u.getId(), 0L)))
                .orElse(null);

        int lowestWorkload = bestOperator != null ? workloadMap.getOrDefault(bestOperator.getId(), 0L).intValue() : 0;

        // 5. Build explainable reasoning & confidence
        String teamName = suggestedTeam != null ? suggestedTeam.getName() : "General Triage";
        String operatorName = bestOperator != null ? bestOperator.getName() : "Unassigned";

        StringBuilder reason = new StringBuilder();
        if (category != null && category.getDefaultTeam() != null) {
            reason.append("Routed to '").append(teamName).append("' based on category '")
                    .append(category.getName()).append("'. ");
        } else {
            reason.append("Selected team '").append(teamName).append("'. ");
        }

        if (bestOperator != null) {
            reason.append("Recommended '").append(operatorName)
                    .append("' based on lowest active workload (")
                    .append(lowestWorkload).append(" open cases).");
        }

        BigDecimal confidence = (category != null && category.getDefaultTeam() != null)
                ? new BigDecimal("0.88")
                : new BigDecimal("0.75");

        log.info("[SmartAssignment] Recommended team={} user={} (workload={}) for case {}",
                teamName, operatorName, lowestWorkload, caseEntity.getCaseNumber());

        return new AssignmentRecommendationResponse(
                suggestedTeam != null ? suggestedTeam.getId() : null,
                teamName,
                bestOperator != null ? bestOperator.getId() : null,
                operatorName,
                lowestWorkload,
                confidence,
                reason.toString()
        );
    }
}
