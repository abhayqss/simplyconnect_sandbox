package com.scnsoft.eldermark.dto.twilio;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;
import java.util.stream.Stream;

public enum TwilioRoomWebhookEventType {

    ROOM_ENDED("room-ended"),
    PARTICIPANT_CONNECTED("participant-connected"),
    PARTICIPANT_DISCONNECTED("participant-disconnected");

    //track-added	Participant added a Track.
    //track-removed	Participant removed a Track.
    //track-enabled	Participant unpaused a Track.
    //track-disabled	Participant paused a Track.
    //recording-started	Recording for a Track began
    //recording-completed	Recording for a Track completed
    //recording-failed	Failure during a recording operation request

    private final String value;

    TwilioRoomWebhookEventType(String value) {
        this.value = value;
    }

    public static Optional<TwilioRoomWebhookEventType> fromValue(String value) {
        return Stream.of(TwilioRoomWebhookEventType.values())
                .filter(entry -> entry.value.equals(value))
                .findFirst();
    }

}
