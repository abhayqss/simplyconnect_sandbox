package com.scnsoft.eldermark.dto.notification.referral;

import com.scnsoft.eldermark.entity.referral.ReferralRequestNotificationType;

public class ReferralRequestNotificationMailDto {
    private String subject;
    private String recipientName;
    private String email;
    private String clientName;
    private String category;
    private String service;
    private String priority;
    private String requestedBy;
    private String url;
    private String community;
    private ReferralRequestNotificationType type;
    private String typePhrase;
    private String templateFile;

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ReferralRequestNotificationType getType() {
        return type;
    }

    public void setType(ReferralRequestNotificationType type) {
        this.type = type;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTypePhrase() {
        return typePhrase;
    }

    public void setTypePhrase(String typePhrase) {
        this.typePhrase = typePhrase;
    }

    public String getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }
}
