package com.scnsoft.eldermark.consana.sync.server.service.producer.impl;

import com.scnsoft.eldermark.consana.sync.server.common.model.dto.ReceiveConsanaPatientQueueDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReceiveConsanaPatientQueueProducerImplTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private ReceiveConsanaPatientQueueProducerImpl instance;

    private static String DESTINATION = "destination";

    @BeforeEach
    void injectDestinationQueue(){
        ReflectionTestUtils.setField(instance, "queueDestination", DESTINATION);
    }


    @Test
    void send_WhenCalled_ShouldSendToQueue() {
        var dto = new ReceiveConsanaPatientQueueDto();

        instance.send(dto);

        verify(jmsTemplate).convertAndSend(DESTINATION, dto);
    }
}