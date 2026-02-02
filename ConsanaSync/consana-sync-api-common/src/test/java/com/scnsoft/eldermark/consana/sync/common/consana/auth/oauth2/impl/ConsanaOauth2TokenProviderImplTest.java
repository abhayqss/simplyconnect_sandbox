package com.scnsoft.eldermark.consana.sync.common.consana.auth.oauth2.impl;

import com.scnsoft.eldermark.consana.sync.common.consana.auth.oauth2.ConsanaOauth2Response;
import com.scnsoft.eldermark.consana.sync.common.consana.auth.oauth2.ConsanaOauth2RestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsanaOauth2TokenProviderImplTest {

    private static Clock clock = Clock.fixed(Instant.ofEpochSecond(10000), ZoneId.systemDefault());

    private ConsanaOauth2TokenProviderImpl instance;

    @Mock
    private ConsanaOauth2RestTemplate restTemplate;

    @BeforeEach
    void createInstance() {
        instance = new ConsanaOauth2TokenProviderImpl(clock, restTemplate);
    }

    private void setInstanceState(Instant currentTokenExpiresIn, String currentActiveToken) {
        ReflectionTestUtils.setField(instance, "currentTokenExpiresIn", currentTokenExpiresIn);
        ReflectionTestUtils.setField(instance, "currentActiveToken", currentActiveToken);
    }

    @Test
    void getActiveToken_WhenTokenIsNotExpired_ShouldReturnToken() {
        var token = "token";
        setInstanceState(Instant.now(clock).plus(10, ChronoUnit.MINUTES), token);

        var result = instance.getActiveToken();
        assertEquals(token, result);
    }

    @Test
    void getActiveToken_WhenTokenIsAboutToExpire_ShouldRequestAndReturnNewToken() {
        testExpireAndRequest(Instant.now(clock).plus(59, ChronoUnit.SECONDS));
    }

    @Test
    void getActiveToken_WhenTokenIsExpired_ShouldRequestAndReturnNewToken() {
        testExpireAndRequest(Instant.now(clock).minus(59, ChronoUnit.SECONDS));
    }

    private void testExpireAndRequest(Instant tokenExpiresIn) {
        var oldToken = "token";
        var newToken = "token1";
        ConsanaOauth2Response response = new ConsanaOauth2Response();
        response.setAccessToken(newToken);
        response.setExpiresIn(3600);

        setInstanceState(tokenExpiresIn, oldToken);
        when(restTemplate.sendPostForToken()).thenReturn(response);

        var result = instance.getActiveToken();

        assertEquals(newToken, result);
        assertEquals(newToken, ReflectionTestUtils.getField(instance, "currentActiveToken"));
        assertEquals(Instant.now(clock).plus(3600, ChronoUnit.SECONDS), ReflectionTestUtils.getField(instance, "currentTokenExpiresIn"));
    }


}