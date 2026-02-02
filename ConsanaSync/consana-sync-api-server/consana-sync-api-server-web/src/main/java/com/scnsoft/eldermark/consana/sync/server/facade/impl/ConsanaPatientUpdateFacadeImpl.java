package com.scnsoft.eldermark.consana.sync.server.facade.impl;

import com.scnsoft.eldermark.consana.sync.server.facade.ConsanaPatientUpdateFacade;
import com.scnsoft.eldermark.consana.sync.server.model.ConsanaSyncDto;
import com.scnsoft.eldermark.consana.sync.server.service.converter.ConsanaSyncWebToQueueDtoConverter;
import com.scnsoft.eldermark.consana.sync.server.service.producer.impl.ReceiveConsanaPatientQueueProducerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConsanaPatientUpdateFacadeImpl implements ConsanaPatientUpdateFacade {

    private static final Logger logger = LoggerFactory.getLogger(ConsanaPatientUpdateFacadeImpl.class);

    private final ConsanaSyncWebToQueueDtoConverter converter;
    private final ReceiveConsanaPatientQueueProducerImpl receiveConsanaPatientQueueProducer;

    @Autowired
    public ConsanaPatientUpdateFacadeImpl(ConsanaSyncWebToQueueDtoConverter converter, ReceiveConsanaPatientQueueProducerImpl receiveConsanaPatientQueueProducer) {
        this.converter = converter;
        this.receiveConsanaPatientQueueProducer = receiveConsanaPatientQueueProducer;
    }

    @Override
    public void convertAndSendToQueue(ConsanaSyncDto consanaSyncDto) {
        logger.info("Received {}", consanaSyncDto);
        var converted = converter.convert(consanaSyncDto);
        receiveConsanaPatientQueueProducer.send(converted);
    }
}
