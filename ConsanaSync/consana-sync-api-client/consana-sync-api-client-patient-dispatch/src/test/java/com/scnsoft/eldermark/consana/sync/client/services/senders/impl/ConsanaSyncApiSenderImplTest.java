package com.scnsoft.eldermark.consana.sync.client.services.senders.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.consana.sync.client.exceptions.ConsanaDispatchException;
import com.scnsoft.eldermark.consana.sync.client.model.ConsanaEventCreatedApiDto;
import com.scnsoft.eldermark.consana.sync.client.model.ConsanaSyncApiDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateType;
import com.scnsoft.eldermark.consana.sync.client.services.senders.template.ConsanaRestTemplateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConsanaSyncApiSenderImplTest {

    @Mock
    ConsanaRestTemplateService restTemplateService;

    @Mock
    RestTemplate restTemplate;

    HttpHeaders httpHeaders = new HttpHeaders();

    @Mock
    ObjectMapper objectMapper;

    @Mock
    ResponseEntity<String> responseEntity;

    private RetryTemplate createRetryTemplate() {
        RetryTemplate r = new RetryTemplate();
        r.setRetryPolicy(new SimpleRetryPolicy(1));
        return r;
    }

    private ConsanaSyncApiDto createPatientUpdateApiDto() {
        return new ConsanaSyncApiDto(
                "patient_id",
                "org_oid",
                "comm_oid",
                ConsanaPatientUpdateType.PATIENT_UPDATE
        );
    }

    private HttpEntity<String> httpEntityMatcher(String body, HttpHeaders headers) {
        return argThat(stringHttpEntity ->
                body.equals(stringHttpEntity.getBody()) && headers.equals(stringHttpEntity.getHeaders()));
    }

    @Test
    void sendSyncNotification_WhenSuccess_willReturn() throws IOException {
        var instance = new ConsanaSyncApiSenderImpl(createRetryTemplate(), restTemplateService, objectMapper);

        var dto = createPatientUpdateApiDto();
        var body = "test_body";

        var endpoint = "endpoint";
        ReflectionTestUtils.setField(instance, "consanaSyncEndpointURL", endpoint);

        when(restTemplateService.getConsanaRestTemplate()).thenReturn(restTemplate);
        when(restTemplateService.createConsanaHttpHeaders()).thenReturn(httpHeaders);

        when(objectMapper.writeValueAsString(dto)).thenReturn(body);

        when(restTemplate.postForEntity(eq(endpoint), httpEntityMatcher(body, httpHeaders), eq(String.class)))
                .thenReturn(responseEntity);

        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);

        instance.sendSyncNotification(dto);
    }

    @Test
    void sendSyncNotification_WhenFailed_willThrow() throws IOException {
        var instance = new ConsanaSyncApiSenderImpl(createRetryTemplate(), restTemplateService, objectMapper);

        var dto = createPatientUpdateApiDto();
        var body = "test_body";

        var endpoint = "endpoint";
        ReflectionTestUtils.setField(instance, "consanaSyncEndpointURL", endpoint);

        when(restTemplateService.getConsanaRestTemplate()).thenReturn(restTemplate);
        when(restTemplateService.createConsanaHttpHeaders()).thenReturn(httpHeaders);

        when(objectMapper.writeValueAsString(dto)).thenReturn(body);

        when(restTemplate.postForEntity(eq(endpoint), httpEntityMatcher(body, httpHeaders), eq(String.class)))
                .thenReturn(responseEntity);

        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

        assertThrows(ConsanaDispatchException.class, () -> instance.sendSyncNotification(dto));
    }

    private ConsanaEventCreatedApiDto createEventCreatedApiDto() {
        return new ConsanaEventCreatedApiDto();
    }

    @Test
    void sendEvent_WhenSuccess_willReturn() throws IOException {
        var instance = new ConsanaSyncApiSenderImpl(createRetryTemplate(), restTemplateService, objectMapper);

        var dto = createEventCreatedApiDto();
        var body = "test_body";

        var endpoint = "endpoint";
        ReflectionTestUtils.setField(instance, "consanaEventEndpointURL", endpoint);

        when(restTemplateService.getConsanaRestTemplate()).thenReturn(restTemplate);
        when(restTemplateService.createConsanaHttpHeaders()).thenReturn(httpHeaders);

        when(objectMapper.writeValueAsString(dto)).thenReturn(body);

        when(restTemplate.postForEntity(eq(endpoint), httpEntityMatcher(body, httpHeaders), eq(String.class)))
                .thenReturn(responseEntity);

        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);

        instance.sendEvent(dto);
    }

    @Test
    void sendEvent_WhenFailed_willThrow() throws IOException {
        var instance = new ConsanaSyncApiSenderImpl(createRetryTemplate(), restTemplateService, objectMapper);

        var dto = createEventCreatedApiDto();
        var body = "test_body";

        var endpoint = "endpoint";
        ReflectionTestUtils.setField(instance, "consanaEventEndpointURL", endpoint);

        when(restTemplateService.getConsanaRestTemplate()).thenReturn(restTemplate);
        when(restTemplateService.createConsanaHttpHeaders()).thenReturn(httpHeaders);

        when(objectMapper.writeValueAsString(dto)).thenReturn(body);

        when(restTemplate.postForEntity(eq(endpoint), httpEntityMatcher(body, httpHeaders), eq(String.class)))
                .thenReturn(responseEntity);

        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

        assertThrows(ConsanaDispatchException.class, () -> instance.sendEvent(dto));
    }

}
