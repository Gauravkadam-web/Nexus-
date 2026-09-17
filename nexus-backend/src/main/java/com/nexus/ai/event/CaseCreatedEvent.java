package com.nexus.ai.event;

import java.util.UUID;

/**
 * Spring application event fired after a new case is successfully persisted.
 * Consumed by {@link AiAnalysisTrigger} to asynchronously trigger AI analysis (US-11).
 */
public class CaseCreatedEvent {

    private final UUID caseId;

    public CaseCreatedEvent(UUID caseId) {
        this.caseId = caseId;
    }

    /**
     * Returns the UUID of the newly created case.
     *
     * @return case UUID
     */
    public UUID getCaseId() {
        return caseId;
    }
}
