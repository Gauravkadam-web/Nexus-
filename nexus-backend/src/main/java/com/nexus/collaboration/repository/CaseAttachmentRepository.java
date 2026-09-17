package com.nexus.collaboration.repository;

import com.nexus.collaboration.entity.CaseAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaseAttachmentRepository extends JpaRepository<CaseAttachment, UUID> {
    List<CaseAttachment> findByCaseEntityIdOrderByCreatedAtDesc(UUID caseId);
}
