package com.scnsoft.eldermark.consana.sync.client.services.cron.impl;

import com.scnsoft.eldermark.consana.sync.client.TestUtils;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateQueueDto;
import com.scnsoft.eldermark.consana.sync.client.services.converters.ResidentUpdateDatabaseToQueueStreamConverter;
import com.scnsoft.eldermark.consana.sync.client.services.queue.database.ResidentUpdateDatabaseQueueService;
import com.scnsoft.eldermark.consana.sync.client.services.queue.jms.producers.ResidentUpdateJmsQueueProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledDatabaseResidentUpdateQueueImplTest {

    @Mock
    private ResidentUpdateDatabaseQueueService residentUpdateQueueService;

    @Mock
    private ResidentUpdateDatabaseToQueueStreamConverter residentUpdateDatabaseToQueueStreamConverter;

    @Mock
    private ResidentUpdateJmsQueueProducer residentUpdateJmsQueueProducer;

    @InjectMocks
    private ScheduledDatabaseResidentUpdateQueueImpl instance;

    @Test
    void processBatchFromQueue_WhenBatchIsDequed_ShouldConvertAndSend() {
        var residentUpdateDatabaseQueueBodies = Stream.of(TestUtils.buildResidentUpdateDatabaseQueueBody());
        var residentUpdateDtos = Stream.of(new ResidentUpdateQueueDto());

        when(residentUpdateQueueService.dequeueBatch()).thenReturn(residentUpdateDatabaseQueueBodies);
        when(residentUpdateDatabaseToQueueStreamConverter.apply(residentUpdateDatabaseQueueBodies)).thenReturn(residentUpdateDtos);

        instance.processBatchFromQueue();

        verify(residentUpdateJmsQueueProducer).sendAll(residentUpdateDtos);
    }
}