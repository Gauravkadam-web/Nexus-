package com.nexus.casemanagement.service;

import com.nexus.casemanagement.dto.CaseRelationResponse;
import com.nexus.casemanagement.dto.CreateCaseRelationRequest;
import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseRelation;
import com.nexus.casemanagement.entity.RelationType;
import com.nexus.casemanagement.repository.CaseRelationRepository;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.common.exception.BadRequestException;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.user.entity.User;
import com.nexus.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service managing relationships between cases (US-17, US-18).
 * Supports DUPLICATE, RELATED, and MASTER_INCIDENT link types.
 */
@Service
public class CaseRelationService {

    private static final Logger log = LoggerFactory.getLogger(CaseRelationService.class);

    private final CaseRelationRepository caseRelationRepository;
    private final CaseRepository caseRepository;
    private final UserRepository userRepository;

    public CaseRelationService(CaseRelationRepository caseRelationRepository,
                               CaseRepository caseRepository,
                               UserRepository userRepository) {
        this.caseRelationRepository = caseRelationRepository;
        this.caseRepository = caseRepository;
        this.userRepository = userRepository;
    }

    /**
     * Links two cases with a specified relation type (DUPLICATE, RELATED, MASTER_INCIDENT).
     *
     * @param caseId     the primary case UUID
     * @param request    the relation payload
     * @param operatorId the authenticated user linking the cases
     * @return the created {@link CaseRelationResponse}
     */
    @Transactional
    public CaseRelationResponse linkCases(UUID caseId, CreateCaseRelationRequest request, UUID operatorId) {
        if (caseId.equals(request.getRelatedCaseId())) {
            throw new BadRequestException("A case cannot be related to itself.");
        }

        Case primaryCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Primary case not found: " + caseId));

        Case relatedCase = caseRepository.findById(request.getRelatedCaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Related case not found: " + request.getRelatedCaseId()));

        // Cross-organization validation
        if (primaryCase.getOrganization() != null && relatedCase.getOrganization() != null) {
            UUID pOrgId = primaryCase.getOrganization().getId();
            UUID rOrgId = relatedCase.getOrganization().getId();
            if (pOrgId != null && rOrgId != null && !pOrgId.equals(rOrgId)) {
                throw new BadRequestException("Cannot link cases belonging to different organizations.");
            }
        }

        // Check if relation already exists in either direction
        boolean exists = caseRelationRepository.existsByCaseEntityIdAndRelatedCaseEntityIdAndRelationType(
                caseId, request.getRelatedCaseId(), request.getRelationType())
                || caseRelationRepository.existsByCaseEntityIdAndRelatedCaseEntityIdAndRelationType(
                request.getRelatedCaseId(), caseId, request.getRelationType());

        if (exists) {
            throw new BadRequestException("A " + request.getRelationType() + " relation already exists between case "
                    + primaryCase.getCaseNumber() + " and case " + relatedCase.getCaseNumber());
        }

        User operator = null;
        if (operatorId != null) {
            operator = userRepository.findById(operatorId).orElse(null);
        }

        CaseRelation relation = new CaseRelation(
                primaryCase, relatedCase, request.getRelationType(), operator, request.getNotes()
        );

        CaseRelation saved = caseRelationRepository.save(relation);
        log.info("[CaseRelation] Linked case {} to case {} as {} by user {}",
                primaryCase.getCaseNumber(), relatedCase.getCaseNumber(), request.getRelationType(), operatorId);

        return CaseRelationResponse.fromEntity(saved);
    }

    /**
     * Returns all relations where the case is either the primary or the related case.
     *
     * @param caseId the UUID of the case
     * @return list of relation responses
     */
    @Transactional(readOnly = true)
    public List<CaseRelationResponse> getRelationsForCase(UUID caseId) {
        if (!caseRepository.existsById(caseId)) {
            throw new ResourceNotFoundException("Case not found: " + caseId);
        }

        return caseRelationRepository.findAllByCaseId(caseId)
                .stream()
                .map(CaseRelationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Unlinks/deletes a case relation record.
     *
     * @param relationId the UUID of the relation record
     */
    @Transactional
    public void removeRelation(UUID relationId) {
        CaseRelation relation = caseRelationRepository.findById(relationId)
                .orElseThrow(() -> new ResourceNotFoundException("Case relation not found: " + relationId));

        caseRelationRepository.delete(relation);
        log.info("[CaseRelation] Removed case relation {}", relationId);
    }

    /**
     * Retrieves all child cases linked to a Master Incident (US-18).
     *
     * @param masterCaseId the UUID of the master incident case
     * @return list of child case relations
     */
    @Transactional(readOnly = true)
    public List<CaseRelationResponse> getMasterIncidentChildren(UUID masterCaseId) {
        if (!caseRepository.existsById(masterCaseId)) {
            throw new ResourceNotFoundException("Master case not found: " + masterCaseId);
        }

        return caseRelationRepository.findMasterIncidentChildren(masterCaseId)
                .stream()
                .map(CaseRelationResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
