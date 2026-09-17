package com.nexus.problem.repository;

import com.nexus.problem.entity.Problem;
import com.nexus.problem.entity.ProblemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, UUID> {

    List<Problem> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId);

    Page<Problem> findByOrganizationId(UUID organizationId, Pageable pageable);

    Page<Problem> findByOrganizationIdAndStatus(UUID organizationId, ProblemStatus status, Pageable pageable);
}
