package com.scnsoft.eldermark.beans.twilio.messages.video;

import com.scnsoft.eldermark.beans.twilio.messages.ServiceMessageType;

import java.util.Collections;
import java.util.Map;

public class CallMemberMuteServiceMessage extends VideoCallServiceMessage {

    private String identity;
    private String actorIdentity;

    public CallMemberMuteServiceMessage(String roomSid, String identity, String actorIdentity) {
        super(ServiceMessageType.CALL_MEMBER_MUTE, roomSid);
        this.identity = identity;
        this.actorIdentity = actorIdentity;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getActorIdentity() {
        return actorIdentity;
    }

    public void setActorIdentity(String actorIdentity) {
        this.actorIdentity = actorIdentity;
    }

    @Override
    public String toString() {
        return "CallMemberMuteServiceMessage{" +
                "identity='" + identity + '\'' +
                ", actorIdentity='" + actorIdentity + '\'' +
                ", roomSid='" + roomSid + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public Map<String, String> toPushNotificationData(String recipientIdentity) {
        return Collections.emptyMap();
    }
}
