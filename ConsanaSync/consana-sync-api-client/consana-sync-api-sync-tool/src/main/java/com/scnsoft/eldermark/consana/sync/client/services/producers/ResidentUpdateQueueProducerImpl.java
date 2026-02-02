package com.scnsoft.eldermark.consana.sync.client.services.producers;

import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateQueueDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Set;

@Component
public class ResidentUpdateQueueProducerImpl implements ResidentUpdateQueueProducer {

    private static final Logger logger = LoggerFactory.getLogger(ResidentUpdateQueueProducerImpl.class);

    private final JmsTemplate jmsTemplate;
    private final Clock clock;

    private String queueDestination = "ResidentUpdate";

    @Autowired
    public ResidentUpdateQueueProducerImpl(JmsTemplate jmsTemplate, Clock clock) {
        this.jmsTemplate = jmsTemplate;
        this.clock = clock;
    }

    @Override
    public void putToResidentUpdateQueue(Long residentId, Set<ResidentUpdateType> updateTypes) {
        putToResidentUpdateQueue(new ResidentUpdateQueueDto(residentId, updateTypes, Instant.now(clock).toEpochMilli()));
    }

    private void putToResidentUpdateQueue(ResidentUpdateQueueDto residentUpdateDto) {
        logger.info("Pushing update for resident {}", residentUpdateDto.getResidentId());
        jmsTemplate.convertAndSend(queueDestination, residentUpdateDto);
    }
}
