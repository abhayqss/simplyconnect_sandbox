package com.scnsoft.eldermark.mobile.dto;

public abstract class ChatVideoUserDetails {

    private String conversationAccessToken;
    private boolean areConversationsEnabled;
    private String serviceConversationSid;
    private boolean areVideoCallsEnabled;
    private boolean isDocuTrackEnabled;

    public String getConversationAccessToken() {
        return conversationAccessToken;
    }

    public void setConversationAccessToken(String conversationAccessToken) {
        this.conversationAccessToken = conversationAccessToken;
    }

    public boolean getAreConversationsEnabled() {
        return areConversationsEnabled;
    }

    public void setAreConversationsEnabled(boolean areConversationsEnabled) {
        this.areConversationsEnabled = areConversationsEnabled;
    }

    public String getServiceConversationSid() {
        return serviceConversationSid;
    }

    public void setServiceConversationSid(String serviceConversationSid) {
        this.serviceConversationSid = serviceConversationSid;
    }

    public boolean getAreVideoCallsEnabled() {
        return areVideoCallsEnabled;
    }

    public void setAreVideoCallsEnabled(boolean areVideoCallsEnabled) {
        this.areVideoCallsEnabled = areVideoCallsEnabled;
    }

    public boolean getIsDocuTrackEnabled() {
        return isDocuTrackEnabled;
    }

    public void setIsDocuTrackEnabled(boolean docuTrackEnabled) {
        isDocuTrackEnabled = docuTrackEnabled;
    }
}
