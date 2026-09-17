package com.nexus.escalation.dto;

import com.nexus.escalation.entity.EscalationConditionType;
import com.nexus.escalation.entity.EscalationLevel;
import com.nexus.escalation.entity.EscalationRule;
import java.time.Instant;
import java.util.UUID;

public class EscalationRuleResponse {

    private UUID id;
    private UUID organizationId;
    private String name;
    private EscalationConditionType conditionType;
    private String conditionConfig;
    private EscalationLevel escalationLevel;
    private UUID categoryId;
    private String categoryName;
    private boolean active;
    private UUID createdBy;
    private Instant createdAt;

    public EscalationRuleResponse() {
    }

    public static EscalationRuleResponse fromEntity(EscalationRule rule) {
        EscalationRuleResponse resp = new EscalationRuleResponse();
        resp.setId(rule.getId());
        resp.setOrganizationId(rule.getOrganization() != null ? rule.getOrganization().getId() : null);
        resp.setName(rule.getName());
        resp.setConditionType(rule.getConditionType());
        resp.setConditionConfig(rule.getConditionConfig());
        resp.setEscalationLevel(rule.getEscalationLevel());
        if (rule.getCategory() != null) {
            resp.setCategoryId(rule.getCategory().getId());
            resp.setCategoryName(rule.getCategory().getName());
        }
        resp.setActive(rule.isActive());
        resp.setCreatedBy(rule.getCreatedBy() != null ? rule.getCreatedBy().getId() : null);
        resp.setCreatedAt(rule.getCreatedAt());
        return resp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
