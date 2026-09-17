package com.nexus.collaboration.entity;

import com.nexus.casemanagement.entity.Case;
import com.nexus.user.entity.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing a communication message in a case's chronological conversation stream.
 */
@Entity
@Table(name = "case_messages")
public class CaseMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false, length = 50)
    private MessageType messageType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "visible_to_requester", nullable = false)
    private Boolean visibleToRequester = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public CaseMessage() {}

    public CaseMessage(Case caseEntity, User sender, MessageType messageType, String content, Boolean visibleToRequester) {
        this.caseEntity = caseEntity;
        this.sender = sender;
        this.messageType = messageType;
        this.content = content;
        this.visibleToRequester = visibleToRequester != null ? visibleToRequester : true;
        this.createdAt = Instant.now();
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

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
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
