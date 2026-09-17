package com.nexus.ai.repository;

import com.nexus.ai.entity.AiSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link AiSummary} — retrieves the latest versioned summary for a case.
 */
@Repository
public interface AiSummaryRepository extends JpaRepository<AiSummary, UUID> {

    Optional<AiSummary> findTopByCaseEntityIdOrderByVersionDesc(UUID caseId);

    int countByCaseEntityId(UUID caseId);
}
