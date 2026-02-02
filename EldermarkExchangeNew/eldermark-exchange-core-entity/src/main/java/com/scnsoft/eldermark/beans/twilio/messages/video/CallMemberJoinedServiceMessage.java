package com.scnsoft.eldermark.beans.twilio.messages.video;

import com.scnsoft.eldermark.beans.twilio.messages.ServiceMessageType;
import com.scnsoft.eldermark.beans.twilio.user.IdentityListItemDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CallMemberJoinedServiceMessage extends VideoCallServiceMessage {

    private String identity;
    private List<IdentityListItemDto> onCall;

    public CallMemberJoinedServiceMessage(String roomSid, String identity, List<IdentityListItemDto> onCall) {
        super(ServiceMessageType.CALL_MEMBER_JOINED, roomSid);
        this.identity = identity;
        this.onCall = onCall;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public List<IdentityListItemDto> getOnCall() {
        return onCall;
    }

    public void setOnCall(List<IdentityListItemDto> onCall) {
        this.onCall = onCall;
    }

    @Override
    public String toString() {
        return "CallMemberJoinedServiceMessage{" +
                "identity='" + identity + '\'' +
                ", onCall=" + onCall +
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
