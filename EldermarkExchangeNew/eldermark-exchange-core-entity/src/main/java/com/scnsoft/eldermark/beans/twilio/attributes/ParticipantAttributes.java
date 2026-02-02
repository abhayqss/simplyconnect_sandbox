package com.scnsoft.eldermark.beans.twilio.attributes;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ParticipantAttributes {
    private boolean isOwner;
    private Long clientId;

    public boolean getIsOwner() {
        return isOwner;
    }

    public ParticipantAttributes setIsOwner(boolean owner) {
        isOwner = owner;
        return this;
    }

    public Long getClientId() {
        return clientId;
    }

    public ParticipantAttributes setClientId(Long clientId) {
        this.clientId = clientId;
        return this;
    }
}
