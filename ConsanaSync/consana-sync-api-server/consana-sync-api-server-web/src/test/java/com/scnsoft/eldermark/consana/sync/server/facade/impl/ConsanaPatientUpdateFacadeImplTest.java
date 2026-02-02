package com.scnsoft.eldermark.consana.sync.server.facade.impl;

import com.scnsoft.eldermark.consana.sync.server.common.model.dto.ReceiveConsanaPatientQueueDto;
import com.scnsoft.eldermark.consana.sync.server.model.ConsanaSyncDto;
import com.scnsoft.eldermark.consana.sync.server.service.converter.ConsanaSyncWebToQueueDtoConverter;
import com.scnsoft.eldermark.consana.sync.server.service.producer.impl.ReceiveConsanaPatientQueueProducerImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsanaPatientUpdateFacadeImplTest {

    @Mock
    private ConsanaSyncWebToQueueDtoConverter converter;

    @Mock
    private ReceiveConsanaPatientQueueProducerImpl producer;

    @InjectMocks
    private ConsanaPatientUpdateFacadeImpl instance;

    @Test
    void convertAndSendToQueue() {
        var input = new ConsanaSyncDto();
        var converted = new ReceiveConsanaPatientQueueDto();

        when(converter.convert(input)).thenReturn(converted);

        instance.convertAndSendToQueue(input);

        verify(producer).send(converted);
    }
}