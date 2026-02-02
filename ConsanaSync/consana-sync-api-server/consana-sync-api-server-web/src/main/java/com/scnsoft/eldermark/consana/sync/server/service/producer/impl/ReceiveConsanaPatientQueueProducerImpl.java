package com.scnsoft.eldermark.consana.sync.server.service.producer.impl;

import com.scnsoft.eldermark.consana.sync.server.common.model.dto.ReceiveConsanaPatientQueueDto;
import com.scnsoft.eldermark.consana.sync.server.service.producer.ReceiveConsanaPatientQueueProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReceiveConsanaPatientQueueProducerImpl implements ReceiveConsanaPatientQueueProducer {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveConsanaPatientQueueProducerImpl.class);

    @Value("${queue.receiveConsanaPatient.destination}")
    private String queueDestination;

    private final JmsTemplate jmsTemplate;

    @Autowired
    public ReceiveConsanaPatientQueueProducerImpl(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void send(ReceiveConsanaPatientQueueDto receiveConsanaPatientQueueDto) {
        logger.info("Sending to {} queue: {}", queueDestination, receiveConsanaPatientQueueDto);
        jmsTemplate.convertAndSend(queueDestination, receiveConsanaPatientQueueDto);
    }
}
