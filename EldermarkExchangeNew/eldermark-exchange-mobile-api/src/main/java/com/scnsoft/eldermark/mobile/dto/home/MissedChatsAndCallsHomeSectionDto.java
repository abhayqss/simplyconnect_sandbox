package com.scnsoft.eldermark.mobile.dto.home;

import java.util.List;

public class MissedChatsAndCallsHomeSectionDto {

    private Long avatarId;
    private String avatarName;

    private String firstName;
    private String lastName;
    private String middleName;

    private String groupChatName;

    private Long dateTime;

    private Long callHistoryId;
    private String conversationSid;

    private List<Long> participatingEmployeeIds;

    private boolean isConversationDisconnected;

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getGroupChatName() {
        return groupChatName;
    }

    public void setGroupChatName(String groupChatName) {
        this.groupChatName = groupChatName;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public Long getCallHistoryId() {
        return callHistoryId;
    }

    public void setCallHistoryId(Long callHistoryId) {
        this.callHistoryId = callHistoryId;
    }

    public String getConversationSid() {
        return conversationSid;
    }

    public void setConversationSid(String conversationSid) {
        this.conversationSid = conversationSid;
    }

    public List<Long> getParticipatingEmployeeIds() {
        return participatingEmployeeIds;
    }

    public void setParticipatingEmployeeIds(List<Long> participatingEmployeeIds) {
        this.participatingEmployeeIds = participatingEmployeeIds;
    }

    public boolean getIsConversationDisconnected() {
        return isConversationDisconnected;
    }

    public void setIsConversationDisconnected(boolean conversationDisconnected) {
        isConversationDisconnected = conversationDisconnected;
    }
}
