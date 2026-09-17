package com.nexus.escalation.repository;

import com.nexus.escalation.entity.Escalation;
import com.nexus.escalation.entity.EscalationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EscalationRepository extends JpaRepository<Escalation, UUID> {

    List<Escalation> findByCaseEntityIdOrderByCreatedAtDesc(UUID caseId);

    @Query("SELECT e FROM Escalation e WHERE e.caseEntity.category.organization.id = :orgId ORDER BY e.createdAt DESC")
    Page<Escalation> findByOrganizationId(@Param("orgId") UUID orgId, Pageable pageable);

    boolean existsByCaseEntityIdAndEscalationRuleIdAndStatus(UUID caseId, UUID escalationRuleId, EscalationStatus status);
}
