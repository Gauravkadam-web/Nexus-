package com.nexus.escalation.dto;

import com.nexus.escalation.entity.EscalationConditionType;
import com.nexus.escalation.entity.EscalationLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CreateEscalationRuleRequest {

    @NotBlank(message = "Rule name is required")
    private String name;

    @NotNull(message = "Condition type is required")
    private EscalationConditionType conditionType;

    private String conditionConfig;

    @NotNull(message = "Escalation level is required")
    private EscalationLevel escalationLevel;

    private UUID categoryId;

    private boolean active = true;

    public CreateEscalationRuleRequest() {
    }

    public CreateEscalationRuleRequest(String name, EscalationConditionType conditionType,
                                       String conditionConfig, EscalationLevel escalationLevel,
                                       UUID categoryId, boolean active) {
        this.name = name;
        this.conditionType = conditionType;
        this.conditionConfig = conditionConfig;
        this.escalationLevel = escalationLevel;
        this.categoryId = categoryId;
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EscalationConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(EscalationConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public String getConditionConfig() {
        return conditionConfig;
    }

    public void setConditionConfig(String conditionConfig) {
        this.conditionConfig = conditionConfig;
    }

    public EscalationLevel getEscalationLevel() {
        return escalationLevel;
    }

    public void setEscalationLevel(EscalationLevel escalationLevel) {
        this.escalationLevel = escalationLevel;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
