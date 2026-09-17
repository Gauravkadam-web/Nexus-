package com.nexus.ai.service;

import com.nexus.ai.dto.DuplicateSuggestionResponse;
import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.common.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for detecting potential duplicate and related cases (US-16).
 * Analyzes case title, description, and category against active cases in the organization.
 */
@Service
public class CaseDuplicateDetectionService {

    private static final Logger log = LoggerFactory.getLogger(CaseDuplicateDetectionService.class);

    private static final Set<String> STOP_WORDS = Set.of(
            "the", "a", "an", "is", "are", "was", "were", "and", "or", "in", "on", "at", "to", "for",
            "with", "by", "from", "of", "about", "not", "cannot", "getting", "since", "this", "my", "i", "we"
    );

    private final CaseRepository caseRepository;

    public CaseDuplicateDetectionService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    /**
     * Finds potential duplicate cases for a given case.
     *
     * @param caseId the UUID of the target case
     * @return list of potential duplicate suggestions sorted by similarity score descending
     */
    @Transactional(readOnly = true)
    public List<DuplicateSuggestionResponse> findPotentialDuplicates(UUID caseId) {
        Case targetCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found: " + caseId));

        UUID targetOrgId = targetCase.getOrganization() != null ? targetCase.getOrganization().getId() : null;

        // Fetch other non-cancelled cases in the same organization
        List<Case> orgCases = caseRepository.findAll().stream()
                .filter(c -> !c.getId().equals(caseId))
                .filter(c -> targetOrgId == null || (c.getOrganization() != null && targetOrgId.equals(c.getOrganization().getId())))
                .filter(c -> c.getStatus() != CaseStatus.CANCELLED)
                .collect(Collectors.toList());

        Set<String> targetTokens = tokenize(targetCase.getTitle() + " " + targetCase.getDescription());

        List<DuplicateSuggestionResponse> candidates = new ArrayList<>();

        for (Case candidate : orgCases) {
            Set<String> candidateTokens = tokenize(candidate.getTitle() + " " + candidate.getDescription());

            double similarity = calculateJaccardSimilarity(targetTokens, candidateTokens);

            // Category match bonus
            boolean categoryMatch = targetCase.getCategory() != null
                    && candidate.getCategory() != null
                    && targetCase.getCategory().getId().equals(candidate.getCategory().getId());

            if (categoryMatch) {
                similarity = Math.min(1.0, similarity + 0.15);
            }

            // If similarity is above 20%, consider it a candidate
            if (similarity >= 0.20) {
                Set<String> commonWords = new HashSet<>(targetTokens);
                commonWords.retainAll(candidateTokens);

                String matchReason = buildMatchReason(commonWords, categoryMatch, targetCase.getCategory() != null ? targetCase.getCategory().getName() : null);

                BigDecimal score = BigDecimal.valueOf(similarity).setScale(2, RoundingMode.HALF_UP);
                candidates.add(new DuplicateSuggestionResponse(
                        candidate.getId(),
                        candidate.getCaseNumber(),
                        candidate.getTitle(),
                        candidate.getStatus().name(),
                        score,
                        matchReason
                ));
            }
        }

        // Sort descending by score and return top 5
        candidates.sort((a, b) -> b.getSimilarityScore().compareTo(a.getSimilarityScore()));
        log.info("[DuplicateDetection] Found {} potential duplicate candidates for case {}",
                candidates.size(), targetCase.getCaseNumber());

        return candidates.stream().limit(5).collect(Collectors.toList());
    }

    // ---- Helpers ----

    private Set<String> tokenize(String text) {
        if (text == null || text.isBlank()) return Set.of();
        return Arrays.stream(text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").split("\\s+"))
                .filter(w -> w.length() > 2 && !STOP_WORDS.contains(w))
                .collect(Collectors.toSet());
    }

    private double calculateJaccardSimilarity(Set<String> tokens1, Set<String> tokens2) {
        if (tokens1.isEmpty() || tokens2.isEmpty()) return 0.0;
        Set<String> intersection = new HashSet<>(tokens1);
        intersection.retainAll(tokens2);
        Set<String> union = new HashSet<>(tokens1);
        union.addAll(tokens2);
        return (double) intersection.size() / union.size();
    }

    private String buildMatchReason(Set<String> commonWords, boolean categoryMatch, String categoryName) {
        List<String> reasons = new ArrayList<>();
        if (categoryMatch && categoryName != null) {
            reasons.add("Same category (" + categoryName + ")");
        }
        if (!commonWords.isEmpty()) {
            reasons.add("Common keywords: " + String.join(", ", commonWords.stream().limit(4).collect(Collectors.toList())));
        }
        return String.join("; ", reasons);
    }
}
