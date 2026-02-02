package com.scnsoft.eldermark.hl7v2.facade;

import ca.uhn.hl7v2.model.Message;

public class MessageAndLogProcessingResult {

    private final Message responseMessage;
    private final Long hl7LogId;
    private final boolean success;

    public MessageAndLogProcessingResult(Message responseMessage, Long hl7LogId, boolean success) {
        this.responseMessage = responseMessage;
        this.hl7LogId = hl7LogId;
        this.success = success;
    }

    public Message getResponseMessage() {
        return responseMessage;
    }

    public Long getHl7LogId() {
        return hl7LogId;
    }

    public boolean isSuccess() {
        return success;
    }
}
