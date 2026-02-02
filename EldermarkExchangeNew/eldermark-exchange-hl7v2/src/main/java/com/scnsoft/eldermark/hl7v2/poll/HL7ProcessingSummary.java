package com.scnsoft.eldermark.hl7v2.poll;

import ca.uhn.hl7v2.model.Message;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import org.apache.commons.lang3.NotImplementedException;

import java.time.Instant;

public abstract class HL7ProcessingSummary extends ProcessingSummary {
    private Instant receivedDatetime;
    @JsonIgnore
    private Message responseMessage;

    private String responseMessageRaw;
    private Long hl7MessageLogId;

    public Instant getReceivedDatetime() {
        return receivedDatetime;
    }

    public void setReceivedDatetime(Instant receivedDatetime) {
        this.receivedDatetime = receivedDatetime;
    }

    public Message getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(Message responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseMessageRaw() {
        return responseMessageRaw;
    }

    public void setResponseMessageRaw(String responseMessageRaw) {
        this.responseMessageRaw = responseMessageRaw;
    }

    @Override
    protected boolean shouldSetOkStatus() {
        throw new NotImplementedException();
    }

    @Override
    protected String buildWarnMessage() {
        throw new NotImplementedException();
    }

    public void setHl7MessageLogId(Long hl7MessageLogId) {
        this.hl7MessageLogId = hl7MessageLogId;
    }

    public Long getHl7MessageLogId() {
        return hl7MessageLogId;
    }
}
