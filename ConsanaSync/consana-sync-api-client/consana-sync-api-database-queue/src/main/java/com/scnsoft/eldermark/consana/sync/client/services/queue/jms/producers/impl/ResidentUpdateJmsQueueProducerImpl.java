package com.scnsoft.eldermark.consana.sync.client.services.queue.jms.producers.impl;

import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateQueueDto;
import com.scnsoft.eldermark.consana.sync.client.services.queue.jms.producers.ResidentUpdateJmsQueueProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Service
@Transactional
public class ResidentUpdateJmsQueueProducerImpl implements ResidentUpdateJmsQueueProducer {

    private static final Logger logger = LoggerFactory.getLogger(ResidentUpdateJmsQueueProducerImpl.class);

    private final JmsTemplate jmsTemplate;

    @Value("${queue.residentUpdate.destination}")
    private String residentUpdateAddressDestination;

    @Autowired
    public ResidentUpdateJmsQueueProducerImpl(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }


    @Override
    public void sendAll(Stream<ResidentUpdateQueueDto> residentUpdateDtoStream) {
        residentUpdateDtoStream.forEach(this::send);
    }

    private void send(ResidentUpdateQueueDto residentUpdateDto) {
        logger.debug("sending: {}", residentUpdateDto);
        jmsTemplate.convertAndSend(residentUpdateAddressDestination, residentUpdateDto);
    }
}
