package com.scnsoft.eldermark.consana.sync.common.consana.auth.oauth2.impl;

import com.scnsoft.eldermark.consana.sync.common.consana.auth.oauth2.ConsanaOauth2RestTemplate;
import com.scnsoft.eldermark.consana.sync.common.consana.auth.oauth2.ConsanaOauth2TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class ConsanaOauth2TokenProviderImpl implements ConsanaOauth2TokenProvider {

    private final Clock clock;
    private final  ConsanaOauth2RestTemplate consanaOauth2RestTemplate;

    private Instant currentTokenExpiresIn;
    private String currentActiveToken;


    @Autowired
    public ConsanaOauth2TokenProviderImpl(Clock clock, ConsanaOauth2RestTemplate consanaOauth2RestTemplate) {
        this.clock = clock;
        this.consanaOauth2RestTemplate = consanaOauth2RestTemplate;
        this.currentTokenExpiresIn = Instant.now(clock).minusSeconds(5); // in order to request token on first call
    }


    @Override
    public synchronized String getActiveToken() {
        if (currentTokenIsAboutToExpire()) {
            requestNewToken();
        }
        return currentActiveToken;
    }

    private boolean currentTokenIsAboutToExpire() {
        return Instant.now(clock).isAfter(currentTokenExpiresIn.minus(1, ChronoUnit.MINUTES));
    }

    private void requestNewToken() {
        var response = consanaOauth2RestTemplate.sendPostForToken();
        this.currentActiveToken = response.getAccessToken();
        this.currentTokenExpiresIn = Instant.now(clock).plus(response.getExpiresIn(), ChronoUnit.SECONDS);
    }
}
