package com.scnsoft.eldermark.beans.twilio.messages.video;

import com.scnsoft.eldermark.beans.twilio.messages.ServiceMessageType;

public class EndCallServiceMessage extends VideoCallServiceMessage {

    public EndCallServiceMessage(String roomSid) {
        super(ServiceMessageType.CALL_END, roomSid);
    }
}
