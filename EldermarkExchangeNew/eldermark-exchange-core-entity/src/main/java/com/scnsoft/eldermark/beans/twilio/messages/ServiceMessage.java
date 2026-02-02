package com.scnsoft.eldermark.beans.twilio.messages;


import java.util.HashMap;
import java.util.Map;

public abstract class ServiceMessage {

    protected ServiceMessageType type;

    public ServiceMessage(ServiceMessageType type) {
        this.type = type;
    }

    public ServiceMessageType getType() {
        return type;
    }

    public void setType(ServiceMessageType type) {
        this.type = type;
    }

    public Map<String, String> toPushNotificationData(String recipientIdentity) {
        var map = new HashMap<String, String>();
        map.put("type", type.name());
        return map;
    }
}

