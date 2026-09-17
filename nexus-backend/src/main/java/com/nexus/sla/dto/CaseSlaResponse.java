package com.nexus.sla.dto;

import com.nexus.sla.entity.CaseSla;
import com.nexus.sla.entity.RiskLevel;
import com.nexus.sla.entity.SlaStatus;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class CaseSlaResponse {

    private UUID id;
    private UUID caseId;
    private String caseNumber;
    private UUID slaPolicyId;
    private Instant responseDeadline;
    private Instant resolutionDeadline;
    private SlaStatus status;
    private Long remainingResponseMinutes;
    private Long remainingResolutionMinutes;
    private Double responseConsumedPercent;
    private Double resolutionConsumedPercent;
    private Instant respondedAt;
    private Instant resolvedAt;
    private RiskLevel riskLevel;
    private List<String> riskReasons;

    public CaseSlaResponse() {
    }

    public static CaseSlaResponse fromEntity(CaseSla caseSla, RiskLevel riskLevel, List<String> riskReasons) {
        CaseSlaResponse resp = new CaseSlaResponse();
        resp.setId(caseSla.getId());
        resp.setCaseId(caseSla.getCaseEntity().getId());
        resp.setCaseNumber(caseSla.getCaseEntity().getCaseNumber());
        resp.setSlaPolicyId(caseSla.getSlaPolicy() != null ? caseSla.getSlaPolicy().getId() : null);
        resp.setResponseDeadline(caseSla.getResponseDeadline());
        resp.setResolutionDeadline(caseSla.getResolutionDeadline());
        resp.setStatus(caseSla.getStatus());
        resp.setRespondedAt(caseSla.getRespondedAt());
        resp.setResolvedAt(caseSla.getResolvedAt());
        resp.setRiskLevel(riskLevel != null ? riskLevel : RiskLevel.LOW);
        resp.setRiskReasons(riskReasons != null ? riskReasons : List.of());

        Instant now = Instant.now();
        Instant createdAt = caseSla.getCaseEntity().getCreatedAt();

        // Calculate remaining minutes
        long remResp = Duration.between(now, caseSla.getResponseDeadline()).toMinutes();
        long remRes = Duration.between(now, caseSla.getResolutionDeadline()).toMinutes();
        resp.setRemainingResponseMinutes(remResp);
        resp.setRemainingResolutionMinutes(remRes);

        // Calculate consumed percentage
        long totalRespMinutes = Duration.between(createdAt, caseSla.getResponseDeadline()).toMinutes();
        if (totalRespMinutes > 0) {
            long elapsedResp = Duration.between(createdAt, caseSla.getRespondedAt() != null ? caseSla.getRespondedAt() : now).toMinutes();
            double pct = Math.min(100.0, Math.max(0.0, (double) elapsedResp / totalRespMinutes * 100.0));
            resp.setResponseConsumedPercent(Math.round(pct * 10.0) / 10.0);
        } else {
            resp.setResponseConsumedPercent(0.0);
        }

        long totalResMinutes = Duration.between(createdAt, caseSla.getResolutionDeadline()).toMinutes();
        if (totalResMinutes > 0) {
            long elapsedRes = Duration.between(createdAt, caseSla.getResolvedAt() != null ? caseSla.getResolvedAt() : now).toMinutes();
            double pct = Math.min(100.0, Math.max(0.0, (double) elapsedRes / totalResMinutes * 100.0));
            resp.setResolutionConsumedPercent(Math.round(pct * 10.0) / 10.0);
        } else {
            resp.setResolutionConsumedPercent(0.0);
        }

        return resp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCaseId() {
        return caseId;
    }

    public void setCaseId(UUID caseId) {
        this.caseId = caseId;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public UUID getSlaPolicyId() {
        return slaPolicyId;
    }

    public void setSlaPolicyId(UUID slaPolicyId) {
        this.slaPolicyId = slaPolicyId;
    }

    public Instant getResponseDeadline() {
        return responseDeadline;
    }

    public void setResponseDeadline(Instant responseDeadline) {
        this.responseDeadline = responseDeadline;
    }

    public Instant getResolutionDeadline() {
        return resolutionDeadline;
    }

    public void setResolutionDeadline(Instant resolutionDeadline) {
        this.resolutionDeadline = resolutionDeadline;
    }

    public SlaStatus getStatus() {
        return status;
    }

    public void setStatus(SlaStatus status) {
        this.status = status;
    }

    public Long getRemainingResponseMinutes() {
        return remainingResponseMinutes;
    }

    public void setRemainingResponseMinutes(Long remainingResponseMinutes) {
        this.remainingResponseMinutes = remainingResponseMinutes;
    }

    public Long getRemainingResolutionMinutes() {
        return remainingResolutionMinutes;
    }

    public void setRemainingResolutionMinutes(Long remainingResolutionMinutes) {
        this.remainingResolutionMinutes = remainingResolutionMinutes;
    }

    public Double getResponseConsumedPercent() {
        return responseConsumedPercent;
    }

    public void setResponseConsumedPercent(Double responseConsumedPercent) {
        this.responseConsumedPercent = responseConsumedPercent;
    }

    public Double getResolutionConsumedPercent() {
        return resolutionConsumedPercent;
    }

    public void setResolutionConsumedPercent(Double resolutionConsumedPercent) {
        this.resolutionConsumedPercent = resolutionConsumedPercent;
    }

    public Instant getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(Instant respondedAt) {
        this.respondedAt = respondedAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public List<String> getRiskReasons() {
        return riskReasons;
    }

    public void setRiskReasons(List<String> riskReasons) {
        this.riskReasons = riskReasons;
    }
}
