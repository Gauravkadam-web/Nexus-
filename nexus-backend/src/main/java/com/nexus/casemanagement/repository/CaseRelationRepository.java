package com.nexus.casemanagement.repository;

import com.nexus.casemanagement.entity.CaseRelation;
import com.nexus.casemanagement.entity.RelationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaseRelationRepository extends JpaRepository<CaseRelation, UUID> {

    @Query("SELECT r FROM CaseRelation r JOIN FETCH r.caseEntity JOIN FETCH r.relatedCaseEntity LEFT JOIN FETCH r.linkedBy WHERE r.caseEntity.id = :caseId OR r.relatedCaseEntity.id = :caseId ORDER BY r.createdAt DESC")
    List<CaseRelation> findAllByCaseId(@Param("caseId") UUID caseId);

    @Query("SELECT r FROM CaseRelation r JOIN FETCH r.relatedCaseEntity LEFT JOIN FETCH r.linkedBy WHERE r.caseEntity.id = :masterCaseId AND r.relationType = com.nexus.casemanagement.entity.RelationType.MASTER_INCIDENT ORDER BY r.createdAt DESC")
    List<CaseRelation> findMasterIncidentChildren(@Param("masterCaseId") UUID masterCaseId);

    boolean existsByCaseEntityIdAndRelatedCaseEntityIdAndRelationType(UUID caseId, UUID relatedCaseId, RelationType relationType);

    List<CaseRelation> findByCaseEntityIdAndRelationType(UUID caseId, RelationType relationType);

    List<CaseRelation> findByRelatedCaseEntityIdAndRelationType(UUID relatedCaseId, RelationType relationType);
}
