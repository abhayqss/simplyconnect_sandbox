package com.scnsoft.eldermark.beans.twilio.messages.video;


import com.scnsoft.eldermark.beans.twilio.messages.ServiceMessageType;

import java.util.Map;

public class CallConversationUpdatedServiceMessage extends VideoCallServiceMessage {

    private String conversationSid;

    public CallConversationUpdatedServiceMessage(String roomSid, String conversationSid) {
        super(ServiceMessageType.CALL_CONVERSATION_UPDATED, roomSid);
        this.conversationSid = conversationSid;
    }

    public String getConversationSid() {
        return conversationSid;
    }

    public void setConversationSid(String conversationSid) {
        this.conversationSid = conversationSid;
    }

    @Override
    public String toString() {
        return "CallConversationUpdatedServiceMessage{" +
                "conversationSid='" + conversationSid + '\'' +
                ", roomSid='" + roomSid + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public Map<String, String> toPushNotificationData(String recipientIdentity) {
        var map = super.toPushNotificationData(recipientIdentity);
        map.put("conversationSid", conversationSid);

        return map;
    }
}
