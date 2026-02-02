package com.scnsoft.eldermark.services.consana.impl;

import com.scnsoft.eldermark.services.consana.EventCreatedQueueProducer;
import com.scnsoft.eldermark.services.consana.model.EventCreatedQueueDto;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventCreatedQueueProducerImpl implements EventCreatedQueueProducer {

    private static final Logger logger = LoggerFactory.getLogger(EventCreatedQueueProducerImpl.class);

    private final JmsTemplate jmsTemplate;
    private final String queueDestination;
    private final boolean enabled;

    @Autowired
    public EventCreatedQueueProducerImpl(JmsTemplate jmsTemplate, @Value("${jms.queue.eventCreated}") String queueDestination, @Value("${jms.enabled}") boolean enabled) {
        this.jmsTemplate = jmsTemplate;
        this.queueDestination = queueDestination;
        this.enabled = enabled;
    }

    @Override
    public boolean putToEventCreatedQueue(Long eventId) {
        if (!enabled) {
            logger.info("Called EventUpdateQueueProducerImpl.putToEventCreatedQueue - jms is disabled.");
            return true;
        }
        try {
            jmsTemplate.convertAndSend(queueDestination, new EventCreatedQueueDto(eventId));
            return true;
        } catch (JmsException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }
}
