package com.nexus.ai.entity;

/**
 * Lifecycle status of an AI suggestion.
 * Transitions: PENDING → ACCEPTED | MODIFIED | REJECTED.
 * Once a decision is made it is immutable — per PRD §7 human-in-the-loop rules.
 */
public enum SuggestionStatus {
    PENDING,
    ACCEPTED,
    MODIFIED,
    REJECTED
}
