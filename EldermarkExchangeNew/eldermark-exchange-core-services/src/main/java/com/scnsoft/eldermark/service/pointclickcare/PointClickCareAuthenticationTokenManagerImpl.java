package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dto.pointclickcare.PointClickCareApiException;
import com.scnsoft.eldermark.dto.pointclickcare.model.auth.PCCAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
@Conditional(PccIntegrationOrPatientMatchEnabledCondition.class)
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
class PointClickCareAuthenticationTokenManagerImpl implements PointClickCareAuthenticationTokenManager {
    private static final Logger logger = LoggerFactory.getLogger(PointClickCareAuthenticationTokenManagerImpl.class);

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final RestTemplate restTemplate;
    private final String authEndpoint;
    private volatile String currentToken;
    private volatile Instant currentTokenExpiresAt;

    public PointClickCareAuthenticationTokenManagerImpl(@Value("${pcc.auth.clientId}") String clientId,
                                                        @Value("${pcc.auth.clientSecret}") String clientSecret,
                                                        @Value("${pcc.auth.endpoint}") String authEndpoint,
                                                        @Qualifier("pccRestTemplateBuilder") RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .basicAuthentication(clientId, clientSecret, StandardCharsets.UTF_8)
                .build();
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        this.authEndpoint = authEndpoint;
    }

    @Override
    public String getBearerToken() {
        var readLocked = false;
        lock.readLock().lock();
        try {
            readLocked = true;
            if (!canUseCurrentToken()) {
                readLocked = false;
                updateCurrentToken();
                readLocked = true;
            } else {
                logger.info("Using cached Point Click Care auth token");
            }
            return currentToken;
        } finally {
            if (readLocked) {
                lock.readLock().unlock();
            }
        }
    }

    private boolean canUseCurrentToken() {
        return currentToken != null && currentTokenExpiresAt.isAfter(Instant.now().plus(1, ChronoUnit.MINUTES));
    }

    private void updateCurrentToken() {
        lock.readLock().unlock();
        lock.writeLock().lock();
        try {
            logger.info("Updating Point Click Care auth token");
            if (!canUseCurrentToken()) {
                var now = Instant.now();
                var response = getPccAuthApiCallResponse();
                currentToken = response.getAccessToken();
                currentTokenExpiresAt = now.plusSeconds(response.getExpiresIn());
                logger.info("Updated Point Click Care token, exires at {}", currentTokenExpiresAt);
            } else {
                logger.info("No need to update Point Click Care token");
            }
            lock.readLock().lock();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private PCCAuthResponse getPccAuthApiCallResponse() {
        logger.info("Fetching new auth token from Point Click Care");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        var map = new LinkedMultiValueMap<String, String>();
        map.add("grant_type", "client_credentials");

        var request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<PCCAuthResponse> response;
        try {
            response = restTemplate.postForEntity(authEndpoint, request, PCCAuthResponse.class);
        } catch (Exception e) {
            logger.error("Failed fetched auth token from Point Click Care", e);
            throw new PointClickCareApiException("Failed fetched auth token from Point Click Care", e);
        }
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            logger.info("Successfully fetched auth token from Point Click Care");
            return response.getBody();
        } else {
            logger.error("Failed fetched auth token from Point Click Care, statusCode {}", response.getStatusCodeValue());
            throw new PointClickCareApiException("Failed fetched auth token from Point Click Care, statusCode " + response.getStatusCode());
        }
    }

    RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
