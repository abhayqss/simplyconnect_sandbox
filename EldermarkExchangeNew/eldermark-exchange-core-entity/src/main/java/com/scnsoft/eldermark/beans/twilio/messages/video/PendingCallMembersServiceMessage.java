package com.scnsoft.eldermark.beans.twilio.messages.video;

import com.scnsoft.eldermark.beans.twilio.messages.ServiceMessageType;
import com.scnsoft.eldermark.beans.twilio.user.IdentityListItemDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PendingCallMembersServiceMessage extends VideoCallServiceMessage {
    private List<IdentityListItemDto> callees;

    public PendingCallMembersServiceMessage(String roomSid, List<IdentityListItemDto> callees) {
        super(ServiceMessageType.CALL_PENDING_MEMBERS, roomSid);
        this.callees = callees;
    }

    public List<IdentityListItemDto> getCallees() {
        return callees;
    }

    public void setCallees(List<IdentityListItemDto> callees) {
        this.callees = callees;
    }

    @Override
    public String toString() {
        return "PendingCallMembersServiceMessage{" +
                "callees=" + callees +
                ", roomSid='" + roomSid + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public Map<String, String> toPushNotificationData(String recipientIdentity) {
        return Collections.emptyMap();
    }
}
