package com.scnsoft.eldermark.mobile.dto;

import javax.validation.constraints.NotEmpty;

public class RoomTokenLoginDto {

    @NotEmpty
    private String roomToken;

    public String getRoomToken() {
        return roomToken;
    }

    public void setRoomToken(String roomToken) {
        this.roomToken = roomToken;
    }
}
