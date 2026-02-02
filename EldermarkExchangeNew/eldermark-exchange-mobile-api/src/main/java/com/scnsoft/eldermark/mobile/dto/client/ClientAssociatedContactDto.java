package com.scnsoft.eldermark.mobile.dto.client;

public class ClientAssociatedContactDto {
    private Long id;
    private String twilioUserSid;
    private String conversationSid;
    private boolean canStartConversation;
    private boolean canCall;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTwilioUserSid() {
        return twilioUserSid;
    }

    public void setTwilioUserSid(String twilioUserSid) {
        this.twilioUserSid = twilioUserSid;
    }

    public boolean isCanStartConversation() {
        return canStartConversation;
    }

    public void setCanStartConversation(boolean canStartConversation) {
        this.canStartConversation = canStartConversation;
    }

    public String getConversationSid() {
        return conversationSid;
    }

    public void setConversationSid(String conversationSid) {
        this.conversationSid = conversationSid;
    }

    public boolean isCanCall() {
        return canCall;
    }

    public void setCanCall(boolean canCall) {
        this.canCall = canCall;
    }
}
