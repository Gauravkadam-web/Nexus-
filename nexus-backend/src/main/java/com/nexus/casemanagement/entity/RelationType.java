package com.nexus.casemanagement.entity;

/**
 * Supported relationship types between cases (SRS §6.3, US-17, US-18).
 */
public enum RelationType {
    /**
     * Case is an identical or near-identical report of another case.
     */
    DUPLICATE,

    /**
     * Case is functionally or contextually related to another case.
     */
    RELATED,

    /**
     * Case is part of a larger, overarching Master Incident.
     */
    MASTER_INCIDENT
}
