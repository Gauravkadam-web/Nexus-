package com.nexus.sla.repository;

import com.nexus.sla.entity.CaseRisk;
import com.nexus.sla.entity.RiskLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseRiskRepository extends JpaRepository<CaseRisk, UUID> {

    List<CaseRisk> findByCaseEntityIdOrderByDetectedAtDesc(UUID caseId);

    Optional<CaseRisk> findTopByCaseEntityIdOrderByDetectedAtDesc(UUID caseId);

    @Query("SELECT cr FROM CaseRisk cr WHERE cr.caseEntity.category.organization.id = :orgId AND cr.riskLevel IN :riskLevels ORDER BY cr.detectedAt DESC")
    Page<CaseRisk> findByOrganizationIdAndRiskLevelIn(@Param("orgId") UUID orgId,
                                                      @Param("riskLevels") Collection<RiskLevel> riskLevels,
                                                      Pageable pageable);
}
