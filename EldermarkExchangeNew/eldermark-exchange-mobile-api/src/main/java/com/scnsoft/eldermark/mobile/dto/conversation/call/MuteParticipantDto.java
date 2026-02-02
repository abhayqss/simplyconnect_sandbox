package com.scnsoft.eldermark.mobile.dto.conversation.call;

import javax.validation.constraints.NotNull;

public class MuteParticipantDto {

    @NotNull
    private String roomSid;

    @NotNull
    private Long employeeId;

    public String getRoomSid() {
        return roomSid;
    }

    public void setRoomSid(String roomSid) {
        this.roomSid = roomSid;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
}
