package com.scnsoft.eldermark.consana.sync.client.services.consumers.impl;

import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaEventCreatedQueueDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.EventCreatedQueueDto;
import com.scnsoft.eldermark.consana.sync.client.services.consumers.EventCreatedQueueConsumer;
import com.scnsoft.eldermark.consana.sync.client.services.producers.EventDispatchQueueProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventCreatedQueueConsumerImpl implements EventCreatedQueueConsumer {

    private static final Logger logger = LoggerFactory.getLogger(EventCreatedQueueConsumerImpl.class);

    private final EventDispatchQueueProducer eventDispatchQueueProducer;
    private final Converter<EventCreatedQueueDto, List<ConsanaEventCreatedQueueDto>> converter;

    @Autowired
    public EventCreatedQueueConsumerImpl(EventDispatchQueueProducer eventDispatchQueueProducer, Converter<EventCreatedQueueDto, List<ConsanaEventCreatedQueueDto>> converter) {
        this.eventDispatchQueueProducer = eventDispatchQueueProducer;
        this.converter = converter;
    }

    @JmsListener(
            destination = "${queue.eventCreated.destination}",
            concurrency = "${queue.eventCreated.concurrency}",
            containerFactory = "eventCreatedJmsListenerContainerFactory"
    )
    @Transactional(readOnly = true)
    @Override
    public void consume(EventCreatedQueueDto eventUpdateDto) {
        logger.debug("Received {}", eventUpdateDto);
        eventDispatchQueueProducer.sendAll(converter.convert(eventUpdateDto));
        logger.debug("{} processed", eventUpdateDto);
    }
}
