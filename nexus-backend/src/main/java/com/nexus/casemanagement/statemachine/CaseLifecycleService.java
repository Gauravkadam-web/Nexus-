package com.nexus.casemanagement.statemachine;

import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.common.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * State machine service governing strict case lifecycle transitions as defined in SRS §8.1 and PRD §6.
 */
@Service
public class CaseLifecycleService {

    private static final Map<CaseStatus, Set<CaseStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(CaseStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(CaseStatus.REPORTED, EnumSet.of(
                CaseStatus.UNDERSTOOD,
                CaseStatus.CANCELLED,
                CaseStatus.DUPLICATE
        ));

        ALLOWED_TRANSITIONS.put(CaseStatus.UNDERSTOOD, EnumSet.of(
                CaseStatus.ASSIGNED,
                CaseStatus.CANCELLED,
                CaseStatus.DUPLICATE
        ));

        ALLOWED_TRANSITIONS.put(CaseStatus.ASSIGNED, EnumSet.of(
                CaseStatus.INVESTIGATING,
                CaseStatus.WAITING_FOR_INFO,
                CaseStatus.CANCELLED
        ));

        ALLOWED_TRANSITIONS.put(CaseStatus.INVESTIGATING, EnumSet.of(
                CaseStatus.WAITING_FOR_INFO,
                CaseStatus.AT_RISK,
                CaseStatus.ESCALATED,
                CaseStatus.RESOLUTION_PROPOSED,
                CaseStatus.RELATED
        ));

        ALLOWED_TRANSITIONS.put(CaseStatus.WAITING_FOR_INFO, EnumSet.of(
                CaseStatus.INVESTIGATING
        ));

        ALLOWED_TRANSITIONS.put(CaseStatus.AT_RISK, EnumSet.of(
                CaseStatus.INVESTIGATING,
                CaseStatus.ESCALATED
        ));

        ALLOWED_TRANSITIONS.put(CaseStatus.ESCALATED, EnumSet.of(
                CaseStatus.INVESTIGATING,
                CaseStatus.RESOLUTION_PROPOSED
        ));

        ALLOWED_TRANSITIONS.put(CaseStatus.RESOLUTION_PROPOSED, EnumSet.of(
                CaseStatus.CLOSED,
                CaseStatus.INVESTIGATING
        ));

        ALLOWED_TRANSITIONS.put(CaseStatus.CLOSED, EnumSet.of(
                CaseStatus.REOPENED
        ));

        ALLOWED_TRANSITIONS.put(CaseStatus.REOPENED, EnumSet.of(
                CaseStatus.INVESTIGATING
        ));

        ALLOWED_TRANSITIONS.put(CaseStatus.DUPLICATE, EnumSet.noneOf(CaseStatus.class));
        ALLOWED_TRANSITIONS.put(CaseStatus.RELATED, EnumSet.of(CaseStatus.INVESTIGATING));
        ALLOWED_TRANSITIONS.put(CaseStatus.CANCELLED, EnumSet.noneOf(CaseStatus.class));
    }

    /**
     * Checks if a transition from currentStatus to targetStatus is valid.
     */
    public boolean isValidTransition(CaseStatus currentStatus, CaseStatus targetStatus) {
        if (currentStatus == null || targetStatus == null) {
            return false;
        }
        if (currentStatus == targetStatus) {
            return true;
        }
        Set<CaseStatus> validNextStates = ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Collections.emptySet());
        return validNextStates.contains(targetStatus);
    }

    /**
     * Asserts that a transition is valid; throws BadRequestException if invalid.
     */
    public void validateTransition(CaseStatus currentStatus, CaseStatus targetStatus) {
        if (!isValidTransition(currentStatus, targetStatus)) {
            throw new BadRequestException(String.format(
                    "Invalid case status transition from '%s' to '%s'. Permissible next states are: %s",
                    currentStatus,
                    targetStatus,
                    ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Collections.emptySet())
            ));
        }
    }

    /**
     * Gets permissible next statuses for a given status.
     */
    public Set<CaseStatus> getNextPossibleStatuses(CaseStatus currentStatus) {
        return Collections.unmodifiableSet(ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Collections.emptySet()));
    }
}
