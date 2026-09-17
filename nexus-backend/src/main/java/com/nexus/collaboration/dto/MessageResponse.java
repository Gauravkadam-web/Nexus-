package com.nexus.collaboration.dto;

import com.nexus.collaboration.entity.CaseMessage;
import com.nexus.collaboration.entity.MessageType;

import java.time.Instant;
import java.util.UUID;

public class MessageResponse {

    private UUID id;
    private UUID caseId;
    private UUID senderId;
    private String senderName;
    private String senderEmail;
    private MessageType messageType;
    private String content;
    private Boolean visibleToRequester;
    private Instant createdAt;

    public MessageResponse() {}

    public static MessageResponse fromEntity(CaseMessage message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setCaseId(message.getCaseEntity().getId());
        if (message.getSender() != null) {
            response.setSenderId(message.getSender().getId());
            response.setSenderName(message.getSender().getName());
            response.setSenderEmail(message.getSender().getEmail());
        }
        response.setMessageType(message.getMessageType());
        response.setContent(message.getContent());
        response.setVisibleToRequester(message.getVisibleToRequester());
        response.setCreatedAt(message.getCreatedAt());
        return response;
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

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getVisibleToRequester() {
        return visibleToRequester;
    }

    public void setVisibleToRequester(Boolean visibleToRequester) {
        this.visibleToRequester = visibleToRequester;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
