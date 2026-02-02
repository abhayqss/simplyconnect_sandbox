package com.scnsoft.eldermark.consana.sync.client.services.producers.impl;

import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateQueueDto;
import com.scnsoft.eldermark.consana.sync.client.services.producers.PatientDispatchQueueProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class PatientDispatchQueueProducerImpl implements PatientDispatchQueueProducer {

    private static final Logger logger = LoggerFactory.getLogger(PatientDispatchQueueProducerImpl.class);

    @Value("${queue.dispatchConsanaPatient.destination}")
    private String patientDispatchAddressDestination;

    private final JmsTemplate jmsTemplate;

    @Autowired
    public PatientDispatchQueueProducerImpl(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    private void send(ConsanaPatientUpdateQueueDto consanaPatientUpdateDto) {
        logger.debug("Sending to Patient Dispatch {}", consanaPatientUpdateDto);
        jmsTemplate.convertAndSend(patientDispatchAddressDestination, consanaPatientUpdateDto);
    }

    @Override
    public void sendAll(Collection<ConsanaPatientUpdateQueueDto> consanaPatientUpdateDtoStream) {
        consanaPatientUpdateDtoStream.forEach(this::send);
    }
}
