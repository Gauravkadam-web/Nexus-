package com.nexus.escalation.entity;

public enum EscalationConditionType {
    SLA_APPROACHING,
    SLA_BREACHED,
    REPEATED_REOPENING,
    MULTIPLE_FAILED_ATTEMPTS,
    OPERATOR_REQUESTED,
    HIGH_IMPACT_INCIDENT
}
