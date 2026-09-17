package com.nexus.ai.repository;

import com.nexus.ai.entity.AiSuggestion;
import com.nexus.ai.entity.SuggestionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link AiSuggestion} — retrieves suggestions by case and status.
 */
@Repository
public interface AiSuggestionRepository extends JpaRepository<AiSuggestion, UUID> {

    List<AiSuggestion> findByCaseEntityIdOrderByCreatedAtAsc(UUID caseId);

    List<AiSuggestion> findByCaseEntityIdAndStatusOrderByCreatedAtAsc(UUID caseId, SuggestionStatus status);
}
