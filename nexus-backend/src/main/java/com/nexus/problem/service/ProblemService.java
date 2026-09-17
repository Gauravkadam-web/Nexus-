package com.nexus.problem.service;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.common.exception.BadRequestException;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.repository.OrganizationRepository;
import com.nexus.problem.dto.CreateProblemRequest;
import com.nexus.problem.dto.ProblemResponse;
import com.nexus.problem.dto.UpdateProblemRequest;
import com.nexus.problem.entity.Problem;
import com.nexus.problem.entity.ProblemIncidentRelation;
import com.nexus.problem.entity.ProblemStatus;
import com.nexus.problem.repository.ProblemIncidentRelationRepository;
import com.nexus.problem.repository.ProblemRepository;
import com.nexus.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProblemService {

    private static final Logger log = LoggerFactory.getLogger(ProblemService.class);

    private final ProblemRepository problemRepository;
    private final ProblemIncidentRelationRepository relationRepository;
    private final OrganizationRepository organizationRepository;
    private final CaseRepository caseRepository;

    public ProblemService(ProblemRepository problemRepository,
                          ProblemIncidentRelationRepository relationRepository,
                          OrganizationRepository organizationRepository,
                          CaseRepository caseRepository) {
        this.problemRepository = problemRepository;
        this.relationRepository = relationRepository;
        this.organizationRepository = organizationRepository;
        this.caseRepository = caseRepository;
    }

    /**
     * US-28: Create a formal Problem record for root-cause tracking.
     */
    public ProblemResponse createProblem(CreateProblemRequest req, UUID orgId, User creator) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", orgId));

        Problem problem = new Problem(org, req.getTitle(), req.getSuspectedRootCause(), creator);
        Problem saved = problemRepository.save(problem);

        if (req.getIncidentCaseIds() != null) {
            for (UUID caseId : req.getIncidentCaseIds()) {
                caseRepository.findById(caseId).ifPresent(c -> {
                    if (!relationRepository.existsByProblemIdAndCaseEntityId(saved.getId(), c.getId())) {
                        ProblemIncidentRelation rel = new ProblemIncidentRelation(saved, c);
                        relationRepository.save(rel);
                        saved.getIncidentRelations().add(rel);
                    }
                });
            }
        }

        log.info("Created Problem [{}] for Org [{}]", saved.getTitle(), orgId);
        return ProblemResponse.fromEntity(saved);
    }

    /**
     * Update problem investigation, root-cause findings, and actions.
     */
    public ProblemResponse updateProblem(UUID id, UpdateProblemRequest req, UUID orgId) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", id));

        if (!problem.getOrganization().getId().equals(orgId)) {
            throw new BadRequestException("Problem does not belong to your organization");
        }

        if (req.getTitle() != null) problem.setTitle(req.getTitle());
        if (req.getSuspectedRootCause() != null) problem.setSuspectedRootCause(req.getSuspectedRootCause());
        if (req.getConfirmedRootCause() != null) problem.setConfirmedRootCause(req.getConfirmedRootCause());
        if (req.getInvestigationNotes() != null) problem.setInvestigationNotes(req.getInvestigationNotes());
        if (req.getCorrectiveAction() != null) problem.setCorrectiveAction(req.getCorrectiveAction());
        if (req.getPreventiveAction() != null) problem.setPreventiveAction(req.getPreventiveAction());
        if (req.getStatus() != null) problem.setStatus(req.getStatus());

        Problem saved = problemRepository.save(problem);
        return ProblemResponse.fromEntity(saved);
    }

    /**
     * Get problem details by ID.
     */
    @Transactional(readOnly = true)
    public ProblemResponse getProblemById(UUID id, UUID orgId) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", id));

        if (!problem.getOrganization().getId().equals(orgId)) {
            throw new BadRequestException("Problem does not belong to your organization");
        }

        return ProblemResponse.fromEntity(problem);
    }

    /**
     * List problems in an organization.
     */
    @Transactional(readOnly = true)
    public Page<ProblemResponse> listProblems(UUID orgId, ProblemStatus status, Pageable pageable) {
        if (status != null) {
            return problemRepository.findByOrganizationIdAndStatus(orgId, status, pageable)
                    .map(ProblemResponse::fromEntity);
        }
        return problemRepository.findByOrganizationId(orgId, pageable)
                .map(ProblemResponse::fromEntity);
    }

    /**
     * Link an incident case to a problem.
     */
    public ProblemResponse linkIncident(UUID problemId, UUID caseId, UUID orgId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", problemId));

        if (!problem.getOrganization().getId().equals(orgId)) {
            throw new BadRequestException("Problem does not belong to your organization");
        }

        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId));

        if (relationRepository.existsByProblemIdAndCaseEntityId(problemId, caseId)) {
            throw new BadRequestException("Case is already linked to this problem");
        }

        ProblemIncidentRelation rel = new ProblemIncidentRelation(problem, caseEntity);
        relationRepository.save(rel);
        problem.getIncidentRelations().add(rel);

        log.info("Linked case [{}] to Problem [{}]", caseEntity.getCaseNumber(), problem.getTitle());
        return ProblemResponse.fromEntity(problem);
    }
}
