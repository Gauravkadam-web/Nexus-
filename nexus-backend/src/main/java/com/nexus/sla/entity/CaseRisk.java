package com.nexus.sla.entity;

import com.nexus.casemanagement.entity.Case;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "case_risk")
public class CaseRisk {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 30)
    private RiskLevel riskLevel = RiskLevel.LOW;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reasons;

    @Column(name = "detected_at", nullable = false)
    private Instant detectedAt = Instant.now();

    public CaseRisk() {
    }

    public CaseRisk(Case caseEntity, RiskLevel riskLevel, String reasons) {
        this.caseEntity = caseEntity;
        this.riskLevel = riskLevel;
        this.reasons = reasons;
        this.detectedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Case getCaseEntity() {
        return caseEntity;
    }

    public void setCaseEntity(Case caseEntity) {
        this.caseEntity = caseEntity;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getReasons() {
        return reasons;
    }

    public void setReasons(String reasons) {
        this.reasons = reasons;
    }

    public Instant getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(Instant detectedAt) {
        this.detectedAt = detectedAt;
    }
}
