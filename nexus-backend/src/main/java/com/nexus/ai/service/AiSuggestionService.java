package com.nexus.ai.service;

import com.nexus.ai.dto.SuggestionDecisionRequest;
import com.nexus.ai.entity.AiSuggestion;
import com.nexus.ai.entity.SuggestionStatus;
import com.nexus.ai.repository.AiSuggestionRepository;
import com.nexus.common.exception.BadRequestException;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.user.entity.User;
import com.nexus.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Handles the human-in-the-loop decision flow for AI suggestions (US-14).
 *
 * <h3>Rules (non-negotiable per PRD §4)</h3>
 * <ul>
 *   <li>AI recommends; humans decide. Every suggestion starts as {@code PENDING}.</li>
 *   <li>Once a decision (ACCEPTED / MODIFIED / REJECTED) is recorded, it is immutable.</li>
 *   <li>MODIFIED decisions must include a {@code modifiedValue}.</li>
 * </ul>
 */
@Service
public class AiSuggestionService {

    private static final Logger log = LoggerFactory.getLogger(AiSuggestionService.class);

    private final AiSuggestionRepository aiSuggestionRepository;
    private final UserRepository userRepository;

    public AiSuggestionService(AiSuggestionRepository aiSuggestionRepository,
                               UserRepository userRepository) {
        this.aiSuggestionRepository = aiSuggestionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Returns all suggestions for a case, ordered by creation time.
     *
     * @param caseId the UUID of the case
     * @return list of suggestions
     */
    @Transactional(readOnly = true)
    public List<AiSuggestion> getSuggestionsForCase(UUID caseId) {
        return aiSuggestionRepository.findByCaseEntityIdOrderByCreatedAtAsc(caseId);
    }

    /**
     * Records an operator's Accept / Modify / Reject decision on a pending AI suggestion.
     * Decisions are immutable — this method throws if the suggestion is already decided.
     *
     * @param suggestionId the UUID of the AI suggestion
     * @param request      the decision payload
     * @param operatorId   the UUID of the operator making the decision
     * @return the updated {@link AiSuggestion}
     */
    @Transactional
    public AiSuggestion decideSuggestion(UUID suggestionId,
                                         SuggestionDecisionRequest request,
                                         UUID operatorId) {
        AiSuggestion suggestion = aiSuggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new ResourceNotFoundException("AI suggestion not found: " + suggestionId));

        if (suggestion.getStatus() != SuggestionStatus.PENDING) {
            throw new BadRequestException(
                    "Suggestion " + suggestionId + " has already been decided (" + suggestion.getStatus() + "). " +
                    "AI suggestion decisions are immutable once recorded.");
        }

        User operator = userRepository.findById(operatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Operator not found: " + operatorId));

        SuggestionStatus newStatus = request.getStatus();
        if (newStatus == SuggestionStatus.PENDING) {
            throw new BadRequestException("Cannot set status to PENDING. Choose ACCEPTED, MODIFIED, or REJECTED.");
        }

        if (newStatus == SuggestionStatus.MODIFIED) {
            if (request.getModifiedValue() == null || request.getModifiedValue().isBlank()) {
                throw new BadRequestException("A modifiedValue must be provided when status is MODIFIED.");
            }
            suggestion.setModifiedValue(request.getModifiedValue());
        }

        suggestion.setStatus(newStatus);
        suggestion.setDecidedBy(operator);
        suggestion.setDecidedAt(Instant.now());
        suggestion.setOverrideReason(request.getOverrideReason());

        AiSuggestion saved = aiSuggestionRepository.save(suggestion);
        log.info("[AI] Suggestion {} decided as {} by operator {}",
                suggestionId, newStatus, operatorId);
        return saved;
    }
}
