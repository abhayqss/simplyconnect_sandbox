package com.scnsoft.eldermark.service.twilio;

import com.twilio.jwt.accesstoken.VideoGrant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.util.Map;
import java.util.Optional;

public class TwilioToken {
    private final Jws<Claims> jwt;

    public TwilioToken(Jws<Claims> jwt) {
        this.jwt = jwt;
    }

    public String getIdentity() {
        return (String) getGrants().getOrDefault("identity", null);
    }

    public Optional<VideoGrant> getVideoGrant() {
        var grant = new VideoGrant();
        var videoData = (Map<String, Object>) getGrants().getOrDefault(grant.getGrantKey(), null);

        if (videoData == null) {
            return Optional.empty();
        }


        grant.setRoom((String) videoData.getOrDefault("room", null));
        return Optional.of(grant);
    }

    private Map<String, Object> getGrants() {
        return (Map<String, Object>) jwt.getBody().get("grants", Map.class);
    }
}
