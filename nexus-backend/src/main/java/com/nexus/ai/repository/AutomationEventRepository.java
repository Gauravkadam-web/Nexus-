package com.nexus.ai.repository;

import com.nexus.ai.entity.AutomationEvent;
import com.nexus.ai.entity.AutomationEventStatus;
import com.nexus.ai.entity.AutomationEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link AutomationEvent} — used to enforce idempotency
 * before re-running any automated job.
 */
@Repository
public interface AutomationEventRepository extends JpaRepository<AutomationEvent, UUID> {

    /**
     * Check if a successful event of this type already exists for the case.
     * Used to prevent duplicate AI analysis runs (idempotency guard).
     *
     * @param caseId    the case UUID
     * @param eventType the type of automation event
     * @param status    the status to check for (typically SUCCESS)
     * @return an Optional containing the most recent matching event
     */
    Optional<AutomationEvent> findTopByCaseEntityIdAndEventTypeAndStatusOrderByCreatedAtDesc(
            UUID caseId, AutomationEventType eventType, AutomationEventStatus status);
}
