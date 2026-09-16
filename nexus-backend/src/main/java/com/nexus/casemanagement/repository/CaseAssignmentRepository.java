package com.nexus.casemanagement.repository;

import com.nexus.casemanagement.entity.CaseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaseAssignmentRepository extends JpaRepository<CaseAssignment, UUID> {
    List<CaseAssignment> findByCaseEntityIdOrderByAssignedAtDesc(UUID caseId);
}
