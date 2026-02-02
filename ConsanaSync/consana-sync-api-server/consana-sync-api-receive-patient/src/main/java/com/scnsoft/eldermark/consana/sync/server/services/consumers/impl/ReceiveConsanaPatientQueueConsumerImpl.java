package com.scnsoft.eldermark.consana.sync.server.services.consumers.impl;

import com.scnsoft.eldermark.consana.sync.server.common.model.dto.ReceiveConsanaPatientQueueDto;
import com.scnsoft.eldermark.consana.sync.server.services.consumers.ReceivePatientService;
import com.scnsoft.eldermark.consana.sync.server.services.consumers.ReceiveConsanaPatientQueueConsumer;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class ReceiveConsanaPatientQueueConsumerImpl implements ReceiveConsanaPatientQueueConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveConsanaPatientQueueConsumerImpl.class);

    @Autowired
    private ReceivePatientService receivePatientService;

    @JmsListener(
            destination = "${queue.receiveConsanaPatient.destination}",
            concurrency = "${queue.receiveConsanaPatient.concurrency}",
            containerFactory = "patientReceiveJmsListenerContainerFactory"
    )
    @Override
    public void consume(ReceiveConsanaPatientQueueDto patientDto) {
        logger.info("Received " + patientDto);
        try {
            receivePatientService.receive(patientDto);
        } catch (Exception ex) {
            logger.error("Unexpected exception {} when execute process with {}", ExceptionUtils.getMessage(ex), patientDto);
        }
    }
}
