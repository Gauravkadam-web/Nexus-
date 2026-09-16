package com.nexus.casemanagement.repository;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseRepository extends JpaRepository<Case, UUID>, JpaSpecificationExecutor<Case> {

    Optional<Case> findByCaseNumber(String caseNumber);

    Page<Case> findByRequesterId(UUID requesterId, Pageable pageable);

    Page<Case> findByAssignedUserId(UUID assignedUserId, Pageable pageable);

    Page<Case> findByAssignedTeamId(UUID assignedTeamId, Pageable pageable);

    long countByCreatedAtBetween(Instant start, Instant end);

    @Query("SELECT COUNT(c) FROM Case c WHERE c.caseNumber LIKE CONCAT('NEX-', :datePattern, '-%')")
    long countByDatePattern(@Param("datePattern") String datePattern);
}
