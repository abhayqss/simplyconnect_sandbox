package com.scnsoft.eldermark.service.twilio;

import java.time.Instant;

public interface VideoCallWebhookService {

    void connectedToRoom(String roomSid, String participantSid, String participantIdentity, Instant when);

    void disconnectedFromRoom(String roomSid, String participantSid, Instant when);

    void roomEnded(String roomSid, Instant when);

}
