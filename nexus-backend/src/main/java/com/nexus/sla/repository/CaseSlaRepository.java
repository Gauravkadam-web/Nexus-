package com.nexus.sla.repository;

import com.nexus.sla.entity.CaseSla;
import com.nexus.sla.entity.SlaStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseSlaRepository extends JpaRepository<CaseSla, UUID> {

    Optional<CaseSla> findByCaseEntityId(UUID caseId);

    List<CaseSla> findByStatusIn(Collection<SlaStatus> statuses);

    @Query("SELECT cs FROM CaseSla cs WHERE cs.caseEntity.category.organization.id = :orgId AND cs.status = :status")
    Page<CaseSla> findByOrganizationIdAndStatus(@Param("orgId") UUID orgId, @Param("status") SlaStatus status, Pageable pageable);

    @Query("SELECT cs FROM CaseSla cs WHERE cs.caseEntity.category.organization.id = :orgId AND cs.status IN :statuses")
    Page<CaseSla> findByOrganizationIdAndStatusIn(@Param("orgId") UUID orgId, @Param("statuses") Collection<SlaStatus> statuses, Pageable pageable);

    @Query("SELECT cs FROM CaseSla cs WHERE cs.status IN ('ON_TRACK', 'AT_RISK') AND cs.resolutionDeadline < :now")
    List<CaseSla> findActiveOverdue(@Param("now") Instant now);
}
