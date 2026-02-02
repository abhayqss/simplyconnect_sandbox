package com.scnsoft.eldermark.beans.twilio.messages.video;

import com.scnsoft.eldermark.beans.twilio.messages.ServiceMessage;
import com.scnsoft.eldermark.beans.twilio.messages.ServiceMessageType;

import java.util.Map;

public abstract class VideoCallServiceMessage extends ServiceMessage {

    protected String roomSid;

    public VideoCallServiceMessage(ServiceMessageType type, String roomSid) {
        super(type);
        this.roomSid = roomSid;
    }

    public String getRoomSid() {
        return roomSid;
    }

    public void setRoomSid(String roomSid) {
        this.roomSid = roomSid;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "roomSid='" + roomSid + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public Map<String, String> toPushNotificationData(String recipientIdentity) {
        var map = super.toPushNotificationData(recipientIdentity);
        map.put("roomSid", roomSid);
        return map;
    }
}
