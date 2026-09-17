package com.nexus.collaboration.service;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.collaboration.dto.InternalNoteRequest;
import com.nexus.collaboration.dto.InternalNoteResponse;
import com.nexus.collaboration.entity.InternalNote;
import com.nexus.collaboration.repository.InternalNoteRepository;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InternalNoteService {

    private final InternalNoteRepository internalNoteRepository;
    private final CaseRepository caseRepository;

    public InternalNoteService(InternalNoteRepository internalNoteRepository, CaseRepository caseRepository) {
        this.internalNoteRepository = internalNoteRepository;
        this.caseRepository = caseRepository;
    }

    @Transactional
    public InternalNoteResponse addNote(UUID caseId, InternalNoteRequest request, User author) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId));

        InternalNote note = new InternalNote(caseEntity, author, request.getContent());
        InternalNote saved = internalNoteRepository.save(note);
        return InternalNoteResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<InternalNoteResponse> getNotes(UUID caseId) {
        if (!caseRepository.existsById(caseId)) {
            throw new ResourceNotFoundException("Case", "id", caseId);
        }
        return internalNoteRepository.findByCaseEntityIdOrderByCreatedAtAsc(caseId)
                .stream()
                .map(InternalNoteResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
