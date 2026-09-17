package com.nexus.ai.entity;

/**
 * What triggered the automation event — user action, system scheduler, or AI pipeline.
 */
public enum AutomationTriggerType {
    EVENT,
    SCHEDULED,
    USER,
    AI
}
