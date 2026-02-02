package com.scnsoft.eldermark.consana.sync.client.services.queue.jms.producers.impl;

import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateQueueDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ResidentUpdateJmsQueueProducerImplTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private ResidentUpdateJmsQueueProducerImpl instance;

    private static final String QUEUE_DESTINATION = "dest";

    @BeforeEach
    void setQueueDestination() {
        ReflectionTestUtils.setField(instance, "residentUpdateAddressDestination", QUEUE_DESTINATION);
    }

    @Test
    void sendAll_IfStreamIsNotEmpty_ShouldSendDtosOneByOne() {
        final ResidentUpdateQueueDto dto1 = new ResidentUpdateQueueDto(1L, null, null);
        final ResidentUpdateQueueDto dto2 = new ResidentUpdateQueueDto(2L, null, null);

        instance.sendAll(Stream.of(dto1, dto2));

        verify(jmsTemplate).convertAndSend(argThat((String arg) -> arg.equals(QUEUE_DESTINATION)), argThat((ResidentUpdateQueueDto arg) -> arg.equals(dto1)));
        verify(jmsTemplate).convertAndSend(argThat((String arg) -> arg.equals(QUEUE_DESTINATION)), argThat((ResidentUpdateQueueDto arg) -> arg.equals(dto2)));
    }

    @Test
    void sendAll_IfStreamIsEmpty_ShouldSendNothing() {
        instance.sendAll(Stream.empty());

        verifyNoInteractions(jmsTemplate);
    }
}