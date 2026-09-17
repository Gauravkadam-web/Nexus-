package com.nexus.resolution.repository;

import com.nexus.resolution.entity.RequesterDecision;
import com.nexus.resolution.entity.Resolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResolutionRepository extends JpaRepository<Resolution, UUID> {

    List<Resolution> findByCaseEntityIdOrderByCreatedAtDesc(UUID caseId);

    Optional<Resolution> findTopByCaseEntityIdOrderByCreatedAtDesc(UUID caseId);

    Optional<Resolution> findTopByCaseEntityIdAndRequesterDecision(UUID caseId, RequesterDecision requesterDecision);
}
