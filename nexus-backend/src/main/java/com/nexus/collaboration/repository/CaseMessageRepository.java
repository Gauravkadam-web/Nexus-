package com.nexus.collaboration.repository;

import com.nexus.collaboration.entity.CaseMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaseMessageRepository extends JpaRepository<CaseMessage, UUID> {
    List<CaseMessage> findByCaseEntityIdOrderByCreatedAtAsc(UUID caseId);
    List<CaseMessage> findByCaseEntityIdAndVisibleToRequesterTrueOrderByCreatedAtAsc(UUID caseId);
}
