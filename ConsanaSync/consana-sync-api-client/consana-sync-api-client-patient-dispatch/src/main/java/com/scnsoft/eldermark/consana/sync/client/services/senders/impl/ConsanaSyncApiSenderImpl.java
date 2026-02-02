package com.scnsoft.eldermark.consana.sync.client.services.senders.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.consana.sync.client.exceptions.ConsanaDispatchException;
import com.scnsoft.eldermark.consana.sync.client.model.ConsanaEventCreatedApiDto;
import com.scnsoft.eldermark.consana.sync.client.model.ConsanaSyncApiDto;
import com.scnsoft.eldermark.consana.sync.client.services.senders.ConsanaSyncApiSender;
import com.scnsoft.eldermark.consana.sync.client.services.senders.template.ConsanaRestTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

@Service
public class ConsanaSyncApiSenderImpl implements ConsanaSyncApiSender {

    private static final Logger logger = LoggerFactory.getLogger(ConsanaSyncApiSenderImpl.class);

    @Value("${consana.endpoint.url}")
    private String consanaSyncEndpointURL;

    @Value("${consana.event.endpoint.url}")
    private String consanaEventEndpointURL;

    private final RetryTemplate retryTemplate;
    private final ConsanaRestTemplateService consanaRestTemplateService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ConsanaSyncApiSenderImpl(RetryTemplate retryTemplate, ConsanaRestTemplateService consanaRestTemplateService, ObjectMapper objectMapper) {
        this.retryTemplate = retryTemplate;
        this.consanaRestTemplateService = consanaRestTemplateService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendSyncNotification(ConsanaSyncApiDto consanaPatientUpdateApiDto) {
        send(consanaPatientUpdateApiDto, consanaSyncEndpointURL);
    }

    @Override
    public void sendEvent(ConsanaEventCreatedApiDto consanaEventCreatedApiDto) {
        send(consanaEventCreatedApiDto, consanaEventEndpointURL);
    }

    private void send(Object dto, String endpoint) {
        retryTemplate.execute((RetryCallback<Void, RuntimeException>) retryContext -> {

            var restTemplate = consanaRestTemplateService.getConsanaRestTemplate();
            HttpEntity<String> entity;
            try {
                var dtoAsString = objectMapper.writeValueAsString(dto);
                logger.debug("Event content: {}", dtoAsString);
                entity = new HttpEntity<>(dtoAsString, consanaRestTemplateService.createConsanaHttpHeaders());
            } catch (JsonProcessingException e) {
                throw new ConsanaDispatchException(e);
            }
            var response = restTemplate.postForEntity(endpoint, entity, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ConsanaDispatchException("Response code: " + response.getStatusCodeValue() + ", Body " + response.getBody());
            }
            return null;
        });
    }

}
