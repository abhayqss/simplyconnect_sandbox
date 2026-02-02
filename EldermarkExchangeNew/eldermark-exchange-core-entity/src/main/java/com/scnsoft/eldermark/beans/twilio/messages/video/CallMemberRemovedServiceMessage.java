package com.scnsoft.eldermark.beans.twilio.messages.video;

import com.scnsoft.eldermark.beans.twilio.messages.ServiceMessageType;

import java.util.Collections;
import java.util.Map;

public class CallMemberRemovedServiceMessage extends VideoCallServiceMessage {

    private String identity;
    private String actorIdentity;

    public CallMemberRemovedServiceMessage(String roomSid, String identity, String actorIdentity) {
        super(ServiceMessageType.CALL_MEMBER_REMOVED, roomSid);
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
        return "CallMemberRemovedServiceMessage{" +
                "identity='" + identity + '\'' +
                ", actorIdentity='" + actorIdentity + '\'' +
                ", roomSid='" + roomSid + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public Map<String, String> toPushNotificationData(String recipientIdentity) {
        return identity.equals(recipientIdentity) ?
                super.toPushNotificationData(recipientIdentity) :
                Collections.emptyMap();
    }
}
