package com.nexus.collaboration.repository;

import com.nexus.collaboration.entity.Investigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvestigationRepository extends JpaRepository<Investigation, UUID> {
    List<Investigation> findByCaseEntityIdOrderByCreatedAtAsc(UUID caseId);
}
