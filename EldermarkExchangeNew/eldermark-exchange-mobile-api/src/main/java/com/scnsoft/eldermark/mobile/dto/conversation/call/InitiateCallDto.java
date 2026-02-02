package com.scnsoft.eldermark.mobile.dto.conversation.call;

import java.util.Set;

public class InitiateCallDto {
    private String conversationSid;
    private Set<Long> employeeIds;
    private String friendlyName;
    private Long participatingClientId;

    public String getConversationSid() {
        return conversationSid;
    }

    public void setConversationSid(String conversationSid) {
        this.conversationSid = conversationSid;
    }

    public Set<Long> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(Set<Long> employeeIds) {
        this.employeeIds = employeeIds;
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
}
