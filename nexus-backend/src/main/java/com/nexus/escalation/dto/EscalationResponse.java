package com.nexus.escalation.dto;

import com.nexus.escalation.entity.Escalation;
import com.nexus.escalation.entity.EscalationLevel;
import com.nexus.escalation.entity.EscalationStatus;
import com.nexus.escalation.entity.TriggeredBy;
import java.time.Instant;
import java.util.UUID;

public class EscalationResponse {

    private UUID id;
    private UUID caseId;
    private String caseNumber;
    private UUID escalationRuleId;
    private String escalationRuleName;
    private EscalationLevel escalationLevel;
    private String reason;
    private EscalationStatus status;
    private TriggeredBy triggeredBy;
    private UUID confirmedById;
    private String confirmedByName;
    private Instant createdAt;

    public EscalationResponse() {
    }

    public static EscalationResponse fromEntity(Escalation escalation) {
        EscalationResponse resp = new EscalationResponse();
        resp.setId(escalation.getId());
        resp.setCaseId(escalation.getCaseEntity().getId());
        resp.setCaseNumber(escalation.getCaseEntity().getCaseNumber());
        if (escalation.getEscalationRule() != null) {
            resp.setEscalationRuleId(escalation.getEscalationRule().getId());
            resp.setEscalationRuleName(escalation.getEscalationRule().getName());
        }
        resp.setEscalationLevel(escalation.getEscalationLevel());
        resp.setReason(escalation.getReason());
        resp.setStatus(escalation.getStatus());
        resp.setTriggeredBy(escalation.getTriggeredBy());
        if (escalation.getConfirmedBy() != null) {
            resp.setConfirmedById(escalation.getConfirmedBy().getId());
            resp.setConfirmedByName(escalation.getConfirmedBy().getName());
        }
        resp.setCreatedAt(escalation.getCreatedAt());
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

    public UUID getEscalationRuleId() {
        return escalationRuleId;
    }

    public void setEscalationRuleId(UUID escalationRuleId) {
        this.escalationRuleId = escalationRuleId;
    }

    public String getEscalationRuleName() {
        return escalationRuleName;
    }

    public void setEscalationRuleName(String escalationRuleName) {
        this.escalationRuleName = escalationRuleName;
    }

    public EscalationLevel getEscalationLevel() {
        return escalationLevel;
    }

    public void setEscalationLevel(EscalationLevel escalationLevel) {
        this.escalationLevel = escalationLevel;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public EscalationStatus getStatus() {
        return status;
    }

    public void setStatus(EscalationStatus status) {
        this.status = status;
    }

    public TriggeredBy getTriggeredBy() {
        return triggeredBy;
    }

    public void setTriggeredBy(TriggeredBy triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    public UUID getConfirmedById() {
        return confirmedById;
    }

    public void setConfirmedById(UUID confirmedById) {
        this.confirmedById = confirmedById;
    }

    public String getConfirmedByName() {
        return confirmedByName;
    }

    public void setConfirmedByName(String confirmedByName) {
        this.confirmedByName = confirmedByName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
