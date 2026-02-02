package com.scnsoft.eldermark.consana.sync.client.services.consumers.impl;

import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateQueueDto;
import com.scnsoft.eldermark.consana.sync.client.services.consumers.ResidentUpdateQueueConsumer;
import com.scnsoft.eldermark.consana.sync.client.services.converters.ResidentToPatientsConverter;
import com.scnsoft.eldermark.consana.sync.client.services.producers.PatientDispatchQueueProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResidentUpdateQueueConsumerImpl implements ResidentUpdateQueueConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ResidentUpdateQueueConsumerImpl.class);

    private final PatientDispatchQueueProducer patientDispatchQueueProducer;
    private final ResidentToPatientsConverter residentToPatientsConverter;

    @Autowired
    public ResidentUpdateQueueConsumerImpl(PatientDispatchQueueProducer patientDispatchQueueProducer, ResidentToPatientsConverter residentToPatientsConverter) {
        this.patientDispatchQueueProducer = patientDispatchQueueProducer;
        this.residentToPatientsConverter = residentToPatientsConverter;
    }

    @JmsListener(
            destination = "${queue.residentUpdate.destination}",
            concurrency = "${queue.residentUpdate.concurrency}",
            containerFactory = "residentUpdateJmsListenerContainerFactory"
    )
    @Transactional(readOnly = true)
    @Override
    public void consume(ResidentUpdateQueueDto residentUpdateDto) {
        logger.debug("Received {}", residentUpdateDto);
        patientDispatchQueueProducer.sendAll(residentToPatientsConverter.convert(residentUpdateDto));
        logger.debug("{} processed", residentUpdateDto);
    }

}
