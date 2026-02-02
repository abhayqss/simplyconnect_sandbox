package com.scnsoft.eldermark.dto.notification.signature;

import java.time.Instant;

public class SignatureRequestNotificationChatDto {
    private String recipientName;
    private Long clientId;
    private String message;
    private String url;
    private String templateName;
    private String communityName;
    private Instant dateExpires;
    private Long requestId;

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public Instant getDateExpires() {
        return dateExpires;
    }

    public void setDateExpires(Instant dateExpires) {
        this.dateExpires = dateExpires;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
}
