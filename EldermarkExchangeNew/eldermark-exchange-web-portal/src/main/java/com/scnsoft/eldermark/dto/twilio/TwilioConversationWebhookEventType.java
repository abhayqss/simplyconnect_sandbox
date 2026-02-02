package com.scnsoft.eldermark.dto.twilio;

import java.util.Optional;
import java.util.stream.Stream;

public enum TwilioConversationWebhookEventType {

    MESSAGE_ADDED("onMessageAdded"),
    PARTICIPANT_UPDATED("onParticipantUpdated");


    private final String value;

    TwilioConversationWebhookEventType(String value) {
        this.value = value;
    }

    public static Optional<TwilioConversationWebhookEventType> fromValue(String value) {
        return Stream.of(TwilioConversationWebhookEventType.values())
                .filter(entry -> entry.value.equals(value))
                .findFirst();
    }

}
