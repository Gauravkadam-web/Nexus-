package com.nexus.collaboration.service;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.collaboration.dto.InvestigationRequest;
import com.nexus.collaboration.dto.InvestigationResponse;
import com.nexus.collaboration.entity.Investigation;
import com.nexus.collaboration.repository.InvestigationRepository;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InvestigationService {

    private final InvestigationRepository investigationRepository;
    private final CaseRepository caseRepository;

    public InvestigationService(InvestigationRepository investigationRepository, CaseRepository caseRepository) {
        this.investigationRepository = investigationRepository;
        this.caseRepository = caseRepository;
    }

    @Transactional
    public InvestigationResponse logInvestigation(UUID caseId, InvestigationRequest request, User operator) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId));

        Investigation investigation = new Investigation(
                caseEntity,
                operator,
                request.getObservation(),
                request.getActionTaken(),
                request.getFinding(),
                request.getEvidenceRef(),
                request.getFollowUpNeeded()
        );

        Investigation saved = investigationRepository.save(investigation);
        return InvestigationResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<InvestigationResponse> getInvestigations(UUID caseId) {
        if (!caseRepository.existsById(caseId)) {
            throw new ResourceNotFoundException("Case", "id", caseId);
        }
        return investigationRepository.findByCaseEntityIdOrderByCreatedAtAsc(caseId)
                .stream()
                .map(InvestigationResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
