package com.nexus.ai.dto;

/**
 * Internal DTO carrying all case data required for AI provider operations.
 * Passed into {@link com.nexus.ai.provider.AiProviderPort} — never serialized to API responses.
 * AI receives only the data required for the specific operation (SRS §9 AI Security).
 */
public class CaseContext {

    private String caseNumber;
    private String title;
    private String description;
    private String categoryName;
    private String subcategoryName;
    private String currentPriority;
    private String currentSeverity;
    private String currentStatus;
    /** Concatenated public messages and investigation logs for summary generation. */
    private String conversationHistory;

    public CaseContext() {}

    // --- Getters & Setters ---

    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getSubcategoryName() { return subcategoryName; }
    public void setSubcategoryName(String subcategoryName) { this.subcategoryName = subcategoryName; }

    public String getCurrentPriority() { return currentPriority; }
    public void setCurrentPriority(String currentPriority) { this.currentPriority = currentPriority; }

    public String getCurrentSeverity() { return currentSeverity; }
    public void setCurrentSeverity(String currentSeverity) { this.currentSeverity = currentSeverity; }

    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }

    public String getConversationHistory() { return conversationHistory; }
    public void setConversationHistory(String conversationHistory) { this.conversationHistory = conversationHistory; }
}
