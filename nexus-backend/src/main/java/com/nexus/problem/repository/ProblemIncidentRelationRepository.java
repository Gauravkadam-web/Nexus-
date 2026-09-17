package com.nexus.problem.repository;

import com.nexus.problem.entity.ProblemIncidentRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProblemIncidentRelationRepository extends JpaRepository<ProblemIncidentRelation, UUID> {

    List<ProblemIncidentRelation> findByProblemId(UUID problemId);

    List<ProblemIncidentRelation> findByCaseEntityId(UUID caseId);

    Optional<ProblemIncidentRelation> findByProblemIdAndCaseEntityId(UUID problemId, UUID caseId);

    boolean existsByProblemIdAndCaseEntityId(UUID problemId, UUID caseId);
}
