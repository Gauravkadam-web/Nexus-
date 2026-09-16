package com.nexus.casemanagement.statemachine;

import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.common.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CaseLifecycleServiceTest {

    private CaseLifecycleService lifecycleService;

    @BeforeEach
    void setUp() {
        lifecycleService = new CaseLifecycleService();
    }

    @Test
    void validTransitions_ShouldPass() {
        // REPORTED -> UNDERSTOOD
        assertTrue(lifecycleService.isValidTransition(CaseStatus.REPORTED, CaseStatus.UNDERSTOOD));
        // REPORTED -> CANCELLED
        assertTrue(lifecycleService.isValidTransition(CaseStatus.REPORTED, CaseStatus.CANCELLED));
        // UNDERSTOOD -> ASSIGNED
        assertTrue(lifecycleService.isValidTransition(CaseStatus.UNDERSTOOD, CaseStatus.ASSIGNED));
        // ASSIGNED -> INVESTIGATING
        assertTrue(lifecycleService.isValidTransition(CaseStatus.ASSIGNED, CaseStatus.INVESTIGATING));
        // INVESTIGATING -> WAITING_FOR_INFO
        assertTrue(lifecycleService.isValidTransition(CaseStatus.INVESTIGATING, CaseStatus.WAITING_FOR_INFO));
        // WAITING_FOR_INFO -> INVESTIGATING
        assertTrue(lifecycleService.isValidTransition(CaseStatus.WAITING_FOR_INFO, CaseStatus.INVESTIGATING));
        // INVESTIGATING -> RESOLUTION_PROPOSED
        assertTrue(lifecycleService.isValidTransition(CaseStatus.INVESTIGATING, CaseStatus.RESOLUTION_PROPOSED));
        // RESOLUTION_PROPOSED -> CLOSED
        assertTrue(lifecycleService.isValidTransition(CaseStatus.RESOLUTION_PROPOSED, CaseStatus.CLOSED));
        // CLOSED -> REOPENED
        assertTrue(lifecycleService.isValidTransition(CaseStatus.CLOSED, CaseStatus.REOPENED));
        // REOPENED -> INVESTIGATING
        assertTrue(lifecycleService.isValidTransition(CaseStatus.REOPENED, CaseStatus.INVESTIGATING));
    }

    @Test
    void invalidTransitions_ShouldFail() {
        // REPORTED cannot go directly to CLOSED
        assertFalse(lifecycleService.isValidTransition(CaseStatus.REPORTED, CaseStatus.CLOSED));
        // CLOSED cannot go directly to INVESTIGATING without REOPENED
        assertFalse(lifecycleService.isValidTransition(CaseStatus.CLOSED, CaseStatus.INVESTIGATING));
        // CANCELLED cannot transition anywhere
        assertFalse(lifecycleService.isValidTransition(CaseStatus.CANCELLED, CaseStatus.INVESTIGATING));

        assertThrows(BadRequestException.class, () ->
                lifecycleService.validateTransition(CaseStatus.REPORTED, CaseStatus.CLOSED)
        );
    }
}
