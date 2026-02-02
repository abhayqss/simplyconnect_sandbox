package com.scnsoft.eldermark.dto;

public class ClientAssociatedContactDto {
    private Long id;
    private String fullName;
    private Long avatarId;
    private Boolean canView;
    private Boolean canCreate;
    private String conversationSid;
    private boolean canStartConversation;
    private boolean canStartVideoCall;
    private boolean canViewCallHistory;
    private boolean chatEnabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public Boolean getCanView() {
        return canView;
    }

    public void setCanView(Boolean canView) {
        this.canView = canView;
    }

    public Boolean getCanCreate() {
        return canCreate;
    }

    public void setCanCreate(Boolean canCreate) {
        this.canCreate = canCreate;
    }

    public String getConversationSid() {
        return conversationSid;
    }

    public void setConversationSid(String conversationSid) {
        this.conversationSid = conversationSid;
    }

    public boolean getCanStartConversation() {
        return canStartConversation;
    }

    public void setCanStartConversation(boolean canStartConversation) {
        this.canStartConversation = canStartConversation;
    }

    public boolean getCanStartVideoCall() {
        return canStartVideoCall;
    }

    public void setCanStartVideoCall(boolean canStartVideoCall) {
        this.canStartVideoCall = canStartVideoCall;
    }

    public boolean getCanViewCallHistory() {
        return canViewCallHistory;
    }

    public void setCanViewCallHistory(boolean canViewCallHistory) {
        this.canViewCallHistory = canViewCallHistory;
    }

    public boolean getChatEnabled() {
        return chatEnabled;
    }

    public void setChatEnabled(boolean chatEnabled) {
        this.chatEnabled = chatEnabled;
    }
}
