package com.scnsoft.eldermark.consana.sync.client.services.consumers.impl;

import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateQueueDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateQueueDto;
import com.scnsoft.eldermark.consana.sync.client.services.converters.ResidentToPatientsConverter;
import com.scnsoft.eldermark.consana.sync.client.services.producers.PatientDispatchQueueProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResidentUpdateQueueConsumerImplTest {

    @Mock
    private PatientDispatchQueueProducer patientDispatchQueueProducer;

    @Mock
    private ResidentToPatientsConverter residentToPatientsConverter;

    @InjectMocks
    private ResidentUpdateQueueConsumerImpl instance;

    @Test
    void consume_ShouldPassConvertedToProducer() {
        final ResidentUpdateQueueDto residentUpdateDto = new ResidentUpdateQueueDto();
        final List<ConsanaPatientUpdateQueueDto> converted = Collections.emptyList();

        when(residentToPatientsConverter.convert(residentUpdateDto)).thenReturn(converted);

        instance.consume(residentUpdateDto);

        verify(patientDispatchQueueProducer).sendAll(converted);
    }
}