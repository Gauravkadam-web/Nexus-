package com.nexus.problem.service;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.problem.dto.RecurringProblemClusterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RecurringProblemDetectionService {

    private static final Logger log = LoggerFactory.getLogger(RecurringProblemDetectionService.class);

    private final CaseRepository caseRepository;

    public RecurringProblemDetectionService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    /**
     * US-28: Automatically detects recurring problem patterns across organization incidents.
     * Groups similar cases by category and key token overlaps.
     */
    public List<RecurringProblemClusterResponse> detectRecurringProblemPatterns(UUID orgId) {
        List<Case> cases = caseRepository.findAll().stream()
                .filter(c -> c.getCategory() != null && c.getCategory().getOrganization() != null
                        && c.getCategory().getOrganization().getId().equals(orgId))
                .toList();

        if (cases.isEmpty()) {
            return List.of();
        }

        // Group by Category ID
        Map<String, List<Case>> categoryGroups = cases.stream()
                .collect(Collectors.groupingBy(c -> c.getCategory().getName()));

        List<RecurringProblemClusterResponse> clusters = new ArrayList<>();

        for (Map.Entry<String, List<Case>> entry : categoryGroups.entrySet()) {
            String catName = entry.getKey();
            List<Case> catCases = entry.getValue();

            if (catCases.size() >= 2) {
                List<UUID> incidentIds = catCases.stream().map(Case::getId).toList();
                List<String> caseNumbers = catCases.stream().map(Case::getCaseNumber).limit(5).toList();

                String patternTitle = String.format("Recurring %s Incidents (%d reported)", catName, catCases.size());
                String hypothesis = String.format(
                        "Multiple incidents (%d cases) detected in category '%s'. Potential systemic failure or infrastructure instability.",
                        catCases.size(), catName
                );

                clusters.add(new RecurringProblemClusterResponse(
                        patternTitle, catName, catCases.size(), incidentIds, caseNumbers, hypothesis
                ));
            }
        }

        log.info("Detected [{}] recurring problem patterns for Org [{}]", clusters.size(), orgId);
        return clusters;
    }
}
