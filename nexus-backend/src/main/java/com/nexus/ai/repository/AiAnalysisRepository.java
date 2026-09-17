package com.nexus.ai.repository;

import com.nexus.ai.entity.AiAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link AiAnalysis} — retrieves the latest analysis for a case.
 */
@Repository
public interface AiAnalysisRepository extends JpaRepository<AiAnalysis, UUID> {

    Optional<AiAnalysis> findTopByCaseEntityIdOrderByCreatedAtDesc(UUID caseId);

    boolean existsByCaseEntityId(UUID caseId);
}
