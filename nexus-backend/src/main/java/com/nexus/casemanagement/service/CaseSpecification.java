package com.nexus.casemanagement.service;

import com.nexus.casemanagement.dto.CaseSearchRequest;
import com.nexus.casemanagement.entity.Case;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Dynamic JPA Specification for advanced Case searching and filtering (US-34).
 */
public final class CaseSpecification {

    private CaseSpecification() {}

    public static Specification<Case> withFilters(CaseSearchRequest request, UUID organizationId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Organization isolation
            if (organizationId != null) {
                Predicate categoryOrg = cb.equal(root.get("category").get("organization").get("id"), organizationId);
                Predicate requesterOrg = cb.equal(root.get("requester").get("organization").get("id"), organizationId);
                predicates.add(cb.or(categoryOrg, requesterOrg));
            }

            // Keyword text query across case number, title, and description
            if (request.getQuery() != null && !request.getQuery().isBlank()) {
                String pattern = "%" + request.getQuery().trim().toLowerCase() + "%";
                Predicate caseNumMatch = cb.like(cb.lower(root.get("caseNumber")), pattern);
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), pattern);
                predicates.add(cb.or(caseNumMatch, titleMatch, descMatch));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (request.getSeverity() != null) {
                predicates.add(cb.equal(root.get("severity"), request.getSeverity()));
            }

            if (request.getPriority() != null) {
                predicates.add(cb.equal(root.get("priority"), request.getPriority()));
            }

            if (request.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), request.getCategoryId()));
            }

            if (request.getAssignedUserId() != null) {
                predicates.add(cb.equal(root.get("assignedUser").get("id"), request.getAssignedUserId()));
            }

            if (request.getAssignedTeamId() != null) {
                predicates.add(cb.equal(root.get("assignedTeam").get("id"), request.getAssignedTeamId()));
            }

            if (request.getRequesterId() != null) {
                predicates.add(cb.equal(root.get("requester").get("id"), request.getRequesterId()));
            }

            if (request.getLocation() != null && !request.getLocation().isBlank()) {
                String locPattern = "%" + request.getLocation().trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("location")), locPattern));
            }

            if (request.getCreatedAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), request.getCreatedAfter()));
            }

            if (request.getCreatedBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), request.getCreatedBefore()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
