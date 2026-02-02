package com.scnsoft.eldermark.consana.sync.client.services.producer.impl;

import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaEventCreatedQueueDto;
import com.scnsoft.eldermark.consana.sync.client.services.producer.EventDispatchQueueProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventDispatchQueueProducerImpl implements EventDispatchQueueProducer {

    private static final Logger logger = LoggerFactory.getLogger(EventDispatchQueueProducerImpl.class);

    @Value("${queue.consanaEventDispatch.destination}")
    private String consanaEventDispatchDestination;

    private final JmsTemplate jmsTemplate;

    @Autowired
    public EventDispatchQueueProducerImpl(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void send(ConsanaEventCreatedQueueDto consanaEventCreatedQueueDto) {
        logger.debug("Sending to Event Dispatch {}", consanaEventCreatedQueueDto);
        jmsTemplate.convertAndSend(consanaEventDispatchDestination, consanaEventCreatedQueueDto);
    }
}
