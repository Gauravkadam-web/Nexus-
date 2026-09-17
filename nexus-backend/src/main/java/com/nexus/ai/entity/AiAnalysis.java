package com.nexus.ai.entity;

import com.nexus.casemanagement.entity.Case;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Team;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Stores the full AI analysis snapshot generated on case creation (US-11).
 * AI-derived values are NEVER written to the {@code cases} table — they live here only.
 * Operators review and Accept/Modify/Reject individual suggestions from {@link AiSuggestion}.
 */
@Entity
@Table(name = "ai_analysis")
public class AiAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suggested_category_id")
    private Category suggestedCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suggested_subcategory_id")
    private Category suggestedSubcategory;

    @Column(name = "suggested_priority", length = 20)
    private String suggestedPriority;

    @Column(name = "suggested_severity", length = 20)
    private String suggestedSeverity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suggested_team_id")
    private Team suggestedTeam;

    /**
     * JSON array of detected missing information fields, e.g. ["error_code", "device_model"].
     */
    @Column(name = "missing_information", columnDefinition = "jsonb")
    private String missingInformation;

    @Column(name = "recommended_next_action")
    private String recommendedNextAction;

    /**
     * JSON array of related case IDs with similarity scores, e.g. [{"caseId":"...", "score":0.87}].
     */
    @Column(name = "related_cases", columnDefinition = "jsonb")
    private String relatedCases;

    @Column(name = "risk_information", columnDefinition = "jsonb")
    private String riskInformation;

    /** Confidence score 0.00–1.00. */
    @Column(name = "confidence", precision = 4, scale = 2)
    private BigDecimal confidence;

    /** Provider and model name used, e.g. "openai/gpt-4o-mini". */
    @Column(name = "model_information", length = 100)
    private String modelInformation;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public AiAnalysis() {}

    // --- Getters & Setters ---

    public UUID getId() { return id; }

    public Case getCaseEntity() { return caseEntity; }
    public void setCaseEntity(Case caseEntity) { this.caseEntity = caseEntity; }

    public Category getSuggestedCategory() { return suggestedCategory; }
    public void setSuggestedCategory(Category suggestedCategory) { this.suggestedCategory = suggestedCategory; }

    public Category getSuggestedSubcategory() { return suggestedSubcategory; }
    public void setSuggestedSubcategory(Category suggestedSubcategory) { this.suggestedSubcategory = suggestedSubcategory; }

    public String getSuggestedPriority() { return suggestedPriority; }
    public void setSuggestedPriority(String suggestedPriority) { this.suggestedPriority = suggestedPriority; }

    public String getSuggestedSeverity() { return suggestedSeverity; }
    public void setSuggestedSeverity(String suggestedSeverity) { this.suggestedSeverity = suggestedSeverity; }

    public Team getSuggestedTeam() { return suggestedTeam; }
    public void setSuggestedTeam(Team suggestedTeam) { this.suggestedTeam = suggestedTeam; }

    public String getMissingInformation() { return missingInformation; }
    public void setMissingInformation(String missingInformation) { this.missingInformation = missingInformation; }

    public String getRecommendedNextAction() { return recommendedNextAction; }
    public void setRecommendedNextAction(String recommendedNextAction) { this.recommendedNextAction = recommendedNextAction; }

    public String getRelatedCases() { return relatedCases; }
    public void setRelatedCases(String relatedCases) { this.relatedCases = relatedCases; }

    public String getRiskInformation() { return riskInformation; }
    public void setRiskInformation(String riskInformation) { this.riskInformation = riskInformation; }

    public BigDecimal getConfidence() { return confidence; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }

    public String getModelInformation() { return modelInformation; }
    public void setModelInformation(String modelInformation) { this.modelInformation = modelInformation; }

    public Instant getCreatedAt() { return createdAt; }
}
