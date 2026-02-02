package com.scnsoft.eldermark.beans.twilio.messages.video;

import com.scnsoft.eldermark.beans.twilio.messages.ServiceMessageType;

import java.util.Collections;
import java.util.Map;

public class CallMemberDeclinedServiceMessage extends VideoCallServiceMessage {

    private String identity;

    public CallMemberDeclinedServiceMessage(String roomSid, String identity) {
        super(ServiceMessageType.CALL_MEMBER_DECLINED, roomSid);
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
        return "CallMemberDeclinedServiceMessage{" +
                "identity='" + identity + '\'' +
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
