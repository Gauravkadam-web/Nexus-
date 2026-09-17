package com.nexus.ai.dto;

/**
 * Response payload returned by AI Communication Drafting (US-30).
 */
public class AiDraftCommunicationResponse {

    private String draftSubject;
    private String draftBody;
    private String tone;
    private String recipientRole;

    public AiDraftCommunicationResponse() {}

    public AiDraftCommunicationResponse(String draftSubject, String draftBody, String tone, String recipientRole) {
        this.draftSubject = draftSubject;
        this.draftBody = draftBody;
        this.tone = tone;
        this.recipientRole = recipientRole;
    }

    public String getDraftSubject() {
        return draftSubject;
    }

    public void setDraftSubject(String draftSubject) {
        this.draftSubject = draftSubject;
    }

    public String getDraftBody() {
        return draftBody;
    }

    public void setDraftBody(String draftBody) {
        this.draftBody = draftBody;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public String getRecipientRole() {
        return recipientRole;
    }

    public void setRecipientRole(String recipientRole) {
        this.recipientRole = recipientRole;
    }
}
