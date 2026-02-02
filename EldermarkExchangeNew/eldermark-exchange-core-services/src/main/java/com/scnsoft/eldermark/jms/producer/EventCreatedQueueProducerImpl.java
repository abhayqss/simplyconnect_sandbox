package com.scnsoft.eldermark.jms.producer;

import com.scnsoft.eldermark.jms.dto.EventCreatedQueueDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventCreatedQueueProducerImpl implements EventCreatedQueueProducer {

    private static final Logger logger = LoggerFactory.getLogger(EventCreatedQueueProducerImpl.class);

    private final JmsTemplate jmsTemplate;

    @Value("${jms.queue.eventCreated.destination}")
    private String eventCreatedDestination;

    @Value("${jms.sending.enabled}")
    private boolean jmsSendingEnabled;

    @Autowired
    public EventCreatedQueueProducerImpl(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void putToEventCreatedQueue(Long eventId) {
        if (!jmsSendingEnabled) {
            logger.warn("Attempt to putToEventCreatedQueue: jms sending is disabled");
            return;
        }
        logger.debug("Sending to EventCreated {}", eventId);
        jmsTemplate.convertAndSend(eventCreatedDestination, new EventCreatedQueueDto(eventId));
    }
}
