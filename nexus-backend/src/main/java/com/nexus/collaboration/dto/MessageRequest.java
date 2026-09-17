package com.nexus.collaboration.dto;

import com.nexus.collaboration.entity.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MessageRequest {

    @NotNull(message = "Message type is required")
    private MessageType messageType;

    @NotBlank(message = "Message content is required")
    private String content;

    private Boolean visibleToRequester = true;

    public MessageRequest() {}

    public MessageRequest(MessageType messageType, String content, Boolean visibleToRequester) {
        this.messageType = messageType;
        this.content = content;
        this.visibleToRequester = visibleToRequester != null ? visibleToRequester : true;
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
}
