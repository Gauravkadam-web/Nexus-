package com.nexus.ai.entity;

/**
 * Types of AI-generated suggestions that operators can Accept / Modify / Reject.
 * Mapped to the {@code suggestion_type} column in {@code ai_suggestions}.
 */
public enum SuggestionType {
    CATEGORY,
    PRIORITY,
    SEVERITY,
    ASSIGNMENT,
    DUPLICATE,
    ROOT_CAUSE
}
