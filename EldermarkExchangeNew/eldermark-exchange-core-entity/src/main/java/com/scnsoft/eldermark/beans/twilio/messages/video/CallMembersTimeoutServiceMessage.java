package com.scnsoft.eldermark.beans.twilio.messages.video;

import com.scnsoft.eldermark.beans.twilio.messages.ServiceMessageType;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class CallMembersTimeoutServiceMessage extends VideoCallServiceMessage {

    private Set<String> identities;

    public CallMembersTimeoutServiceMessage(String roomSid, Set<String> identities) {
        super(ServiceMessageType.CALL_MEMBERS_TIMEOUT, roomSid);
        this.identities = identities;
    }

    public Set<String> getIdentities() {
        return identities;
    }

    public void setIdentities(Set<String> identities) {
        this.identities = identities;
    }

    @Override
    public String toString() {
        return "CallMembersTimeoutServiceMessage{" +
                "identities=" + identities +
                ", roomSid='" + roomSid + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public Map<String, String> toPushNotificationData(String recipientIdentity) {
        if (identities != null && identities.contains(recipientIdentity)) {
            return super.toPushNotificationData(recipientIdentity);
        }
        return Collections.emptyMap();
    }
}
