package com.scnsoft.eldermark.mobile.dto.conversation.call;

import java.util.Set;

public class AddAlreadyInChatCallParticipantsDto extends CallRoomDto {

    private Set<Long> employeeIds;
    private String friendlyName;

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

}
