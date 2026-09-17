package com.nexus.ai.dto;

import com.nexus.ai.entity.AiSummary;

import java.time.Instant;
import java.util.UUID;

/**
 * API response for GET /cases/{id}/ai/summary — the latest versioned AI summary.
 */
public class AiSummaryResponse {

    private UUID id;
    private UUID caseId;
    private String summaryText;
    private int version;
    private Instant createdAt;

    public static AiSummaryResponse from(AiSummary summary) {
        AiSummaryResponse resp = new AiSummaryResponse();
        resp.setId(summary.getId());
        resp.setCaseId(summary.getCaseEntity().getId());
        resp.setSummaryText(summary.getSummaryText());
        resp.setVersion(summary.getVersion());
        resp.setCreatedAt(summary.getCreatedAt());
        return resp;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }

    public String getSummaryText() { return summaryText; }
    public void setSummaryText(String summaryText) { this.summaryText = summaryText; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
