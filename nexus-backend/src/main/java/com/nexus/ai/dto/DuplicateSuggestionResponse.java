package com.nexus.ai.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO representing an AI-detected potential duplicate case (US-16).
 */
public class DuplicateSuggestionResponse {

    private UUID candidateCaseId;
    private String caseNumber;
    private String title;
    private String status;
    private BigDecimal similarityScore;
    private String matchReason;

    public DuplicateSuggestionResponse() {}

    public DuplicateSuggestionResponse(UUID candidateCaseId, String caseNumber, String title,
                                       String status, BigDecimal similarityScore, String matchReason) {
        this.candidateCaseId = candidateCaseId;
        this.caseNumber = caseNumber;
        this.title = title;
        this.status = status;
        this.similarityScore = similarityScore;
        this.matchReason = matchReason;
    }

    public UUID getCandidateCaseId() {
        return candidateCaseId;
    }

    public void setCandidateCaseId(UUID candidateCaseId) {
        this.candidateCaseId = candidateCaseId;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(BigDecimal similarityScore) {
        this.similarityScore = similarityScore;
    }

    public String getMatchReason() {
        return matchReason;
    }

    public void setMatchReason(String matchReason) {
        this.matchReason = matchReason;
    }
}
