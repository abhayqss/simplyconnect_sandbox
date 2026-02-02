package com.scnsoft.eldermark.mobile.dto.conversation.call;

public class RoomTokenDto {

    private String roomSid;
    private String token;

    public RoomTokenDto(String roomSid, String token) {
        this.roomSid = roomSid;
        this.token = token;
    }

    public String getRoomSid() {
        return roomSid;
    }

    public void setRoomSid(String roomSid) {
        this.roomSid = roomSid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
