package com.scnsoft.eldermark.service.twilio;

public interface TwilioAccessTokenService {

    String generateChatToken(String identity, String chatServiceSid);

    String generateVideoToken(String identity, String roomSid);

    TwilioToken parse(String token);
}
