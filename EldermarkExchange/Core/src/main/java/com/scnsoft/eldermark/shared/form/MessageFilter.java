package com.scnsoft.eldermark.shared.form;

import com.scnsoft.eldermark.shared.DirectMessageType;

/**
 * Created by stsiushkevich on 24-04-2015.
 */
public class MessageFilter {
    private DirectMessageType messageType;

    public DirectMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(DirectMessageType messageType) {
        this.messageType = messageType;
    }
}
