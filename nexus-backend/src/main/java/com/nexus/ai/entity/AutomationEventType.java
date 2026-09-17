package com.nexus.ai.entity;

/**
 * Event types tracked in {@code automation_events} for idempotency and observability.
 */
public enum AutomationEventType {
    AI_ANALYSIS,
    SUMMARY_UPDATE,
    DUPLICATE_DETECTION,
    ASSIGNMENT_RECOMMENDATION,
    SLA_CHECK,
    RISK_DETECTION,
    ESCALATION_TRIGGER,
    NOTIFICATION,
    AUDIT_EVENT
}
