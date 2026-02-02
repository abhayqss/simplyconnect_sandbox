package com.scnsoft.eldermark.beans.twilio.messages.video;

import com.scnsoft.eldermark.beans.twilio.messages.ServiceMessageType;

import java.util.Collections;
import java.util.Map;

public class CallMemberLeftServiceMessage extends VideoCallServiceMessage {

    private String identity;

    public CallMemberLeftServiceMessage(String roomSid, String identity) {
        super(ServiceMessageType.CALL_MEMBER_LEFT, roomSid);
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    @Override
    public String toString() {
        return "CallMemberLeftServiceMessage{" +
                "identity='" + identity + '\'' +
                ", roomSid='" + roomSid + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public Map<String, String> toPushNotificationData(String recipientIdentity) {
        return Collections.emptyMap();
    }
}
