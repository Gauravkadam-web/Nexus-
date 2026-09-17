package com.nexus.collaboration.repository;

import com.nexus.collaboration.entity.InternalNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InternalNoteRepository extends JpaRepository<InternalNote, UUID> {
    List<InternalNote> findByCaseEntityIdOrderByCreatedAtAsc(UUID caseId);
}
