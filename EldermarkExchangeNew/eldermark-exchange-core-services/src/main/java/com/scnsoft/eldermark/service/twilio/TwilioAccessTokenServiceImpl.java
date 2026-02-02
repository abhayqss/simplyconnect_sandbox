package com.scnsoft.eldermark.service.twilio;

import com.twilio.jwt.accesstoken.AccessToken;
import com.twilio.jwt.accesstoken.ChatGrant;
import com.twilio.jwt.accesstoken.Grant;
import com.twilio.jwt.accesstoken.VideoGrant;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;

@Service
public class TwilioAccessTokenServiceImpl implements TwilioAccessTokenService {

    @Value("${twilio.account.sid}")
    private String twilioAccountSid;

    @Value("${twilio.api.key.sid}")
    private String twilioApiKey;

    @Value("${twilio.api.key.secret}")
    private String twilioApiSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    @Value("${videocall.max.pardicipants.duration}")
    private int maxParticipantDuration;

    @Override
    public String generateChatToken(String identity, String chatServiceSid) {
        var chatGrant = new ChatGrant();
        chatGrant.setServiceSid(chatServiceSid);

        return generateToken(identity, chatGrant, jwtExpirationInMs);
    }

    @Override
    public String generateVideoToken(String identity, String roomSid) {
        var videoGrant = new VideoGrant();
        videoGrant.setRoom(roomSid);

        //using maxParticipantDuration instead of jwtExpirationInMs:
        //https://www.twilio.com/docs/video/reconnection-states-and-events#preventing-reconnection-failure-due-to-expired-accesstoken
        return generateToken(identity, videoGrant, maxParticipantDuration);
    }

    @Override
    public TwilioToken parse(String twilioToken) {
        return new TwilioToken(
                Jwts.parserBuilder()
                        .setSigningKey(new SecretKeySpec(twilioApiSecret.getBytes(), SignatureAlgorithm.HS256.getJcaName()))
                        .requireIssuer(twilioApiKey)
                        .build()
                        .parseClaimsJws(twilioToken)
        );
    }

    private String generateToken(String identity, Grant grant, int ttlMillis) {
        var token = new AccessToken.Builder(twilioAccountSid, twilioApiKey, twilioApiSecret)
                .identity(identity)
                .grant(grant)
                .ttl(ttlMillis / 1000)
                .build();

        return token.toJwt();
    }
}
