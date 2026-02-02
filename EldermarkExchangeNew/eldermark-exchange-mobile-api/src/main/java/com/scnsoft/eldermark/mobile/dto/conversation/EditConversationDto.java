package com.scnsoft.eldermark.mobile.dto.conversation;

import java.util.Set;

public class EditConversationDto {

    private String conversationSid;
    private String friendlyName;
    private Long participatingClientId;

    private Set<Long> addedEmployeeIds;

    private Set<Long> removedEmployeeIds;

    public String getConversationSid() {
        return conversationSid;
    }

    public void setConversationSid(String conversationSid) {
        this.conversationSid = conversationSid;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public Long getParticipatingClientId() {
        return participatingClientId;
    }

    public void setParticipatingClientId(Long participatingClientId) {
        this.participatingClientId = participatingClientId;
    }

    public Set<Long> getAddedEmployeeIds() {
        return addedEmployeeIds;
    }

    public void setAddedEmployeeIds(Set<Long> addedEmployeeIds) {
        this.addedEmployeeIds = addedEmployeeIds;
    }

    public Set<Long> getRemovedEmployeeIds() {
        return removedEmployeeIds;
    }

    public void setRemovedEmployeeIds(Set<Long> removedEmployeeIds) {
        this.removedEmployeeIds = removedEmployeeIds;
    }
}
