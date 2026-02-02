package com.scnsoft.eldermark.consana.sync.client.services.producers.impl;

import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateQueueDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PatientDispatchQueueProducerImplTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private PatientDispatchQueueProducerImpl instance;

    private static final String QUEUE_DESTINATION = "dest";

    @BeforeEach
    void setQueueDestination() {
        ReflectionTestUtils.setField(instance, "patientDispatchAddressDestination", QUEUE_DESTINATION);
    }

    @Test
    void sendAll_IfListIsNotEmpty_ShouldSendDtosOneByOne() {
        final ConsanaPatientUpdateQueueDto dto1 = new ConsanaPatientUpdateQueueDto();
        dto1.setPatientId("1");
        final ConsanaPatientUpdateQueueDto dto2 = new ConsanaPatientUpdateQueueDto();
        dto2.setPatientId("2");
        instance.sendAll(Arrays.asList(dto1, dto2));

        verify(jmsTemplate).convertAndSend(argThat((String arg) -> arg.equals(QUEUE_DESTINATION)), argThat((ConsanaPatientUpdateQueueDto arg) -> arg.equals(dto1)));
        verify(jmsTemplate).convertAndSend(argThat((String arg) -> arg.equals(QUEUE_DESTINATION)), argThat((ConsanaPatientUpdateQueueDto arg) -> arg.equals(dto2)));
    }

    @Test
    void sendAll_IfListIsEmpty_ShouldSendNothing() {
        instance.sendAll(Collections.emptyList());

        verifyNoInteractions(jmsTemplate);
    }
}