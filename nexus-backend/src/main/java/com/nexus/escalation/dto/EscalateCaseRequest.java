package com.nexus.escalation.dto;

import com.nexus.escalation.entity.EscalationLevel;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class EscalateCaseRequest {

    @NotBlank(message = "Escalation reason is required")
    private String reason;

    private EscalationLevel escalationLevel = EscalationLevel.TEAM_LEAD;

    private UUID escalationRuleId;

    public EscalateCaseRequest() {
    }

    public EscalateCaseRequest(String reason, EscalationLevel escalationLevel, UUID escalationRuleId) {
        this.reason = reason;
        this.escalationLevel = escalationLevel != null ? escalationLevel : EscalationLevel.TEAM_LEAD;
        this.escalationRuleId = escalationRuleId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public EscalationLevel getEscalationLevel() {
        return escalationLevel;
    }

    public void setEscalationLevel(EscalationLevel escalationLevel) {
        this.escalationLevel = escalationLevel;
    }

    public UUID getEscalationRuleId() {
        return escalationRuleId;
    }

    public void setEscalationRuleId(UUID escalationRuleId) {
        this.escalationRuleId = escalationRuleId;
    }
}
