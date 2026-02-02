package com.scnsoft.eldermark.dto.twilio;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class TwilioRoomWebhookDto {

    @JsonProperty("RoomSid")
    private String roomSid;

    @JsonProperty(value = "StatusCallbackEvent")
    private String statusCallbackEvent;

    @JsonProperty("Timestamp")
//    @JsonFormat(shape = JsonFormat.Shape.STRING) //, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX"
    private Instant timestamp;

    @JsonProperty("ParticipantSid")
    private String participantSid;

    @JsonProperty("ParticipantIdentity")
    private String participantIdentity;

    public String getRoomSid() {
        return roomSid;
    }

    public void setRoomSid(String roomSid) {
        this.roomSid = roomSid;
    }

    public String getStatusCallbackEvent() {
        return statusCallbackEvent;
    }

    public void setStatusCallbackEvent(String statusCallbackEvent) {
        this.statusCallbackEvent = statusCallbackEvent;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getParticipantSid() {
        return participantSid;
    }

    public void setParticipantSid(String participantSid) {
        this.participantSid = participantSid;
    }

    public String getParticipantIdentity() {
        return participantIdentity;
    }

    public void setParticipantIdentity(String participantIdentity) {
        this.participantIdentity = participantIdentity;
    }

    @Override
    public String toString() {
        return "TwilioRoomWebhookDto{" +
                "roomSid='" + roomSid + '\'' +
                ", statusCallbackEvent='" + statusCallbackEvent + '\'' +
                ", timestamp=" + timestamp +
                ", participantSid='" + participantSid + '\'' +
                ", participantIdentity='" + participantIdentity + '\'' +
                '}';
    }
}
