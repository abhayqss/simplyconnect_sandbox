package com.scnsoft.eldermark.consana.sync.client.services.consumers.impl;

import com.google.common.cache.Cache;
import com.scnsoft.eldermark.consana.sync.client.model.ConsanaSyncApiDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateQueueDto;
import com.scnsoft.eldermark.consana.sync.client.services.converters.ConsanaPatientUpdateQueueToApiConverter;
import com.scnsoft.eldermark.consana.sync.client.services.logging.DispatchLogService;
import com.scnsoft.eldermark.consana.sync.client.services.senders.ConsanaSyncApiSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientDispatchQueueConsumerImplTest {

    @Mock
    private ConsanaSyncApiSender consanaSyncApiSender;

    @Mock
    private ConsanaPatientUpdateQueueToApiConverter consanaPatientUpdateQueueToApiConverter;

    @Mock
    private Cache<String, Instant> processedPatientsCache;

    @Mock
    private DispatchLogService dispatchLogService;

    @InjectMocks
    private PatientDispatchQueueConsumerImpl instance;

    private static final String PATIENT_ID = "xref123";

    @Test
    void consume_DateIsNotInCache_ShouldConvertAndSendAndLogSuccess() {
        var consanaUpdateQueueDto = prepareConsanaPatientUpdateQueueDto(System.currentTimeMillis());
        var consanaUpdateApiDto = new ConsanaSyncApiDto();

        when(processedPatientsCache.getIfPresent(PATIENT_ID)).thenReturn(null);
        when(consanaPatientUpdateQueueToApiConverter.convert(consanaUpdateQueueDto)).thenReturn(consanaUpdateApiDto);

        instance.consume(consanaUpdateQueueDto);

        verify(consanaSyncApiSender).sendSyncNotification(consanaUpdateApiDto);
        verify(dispatchLogService).logSuccess(consanaUpdateQueueDto);
    }

    @Test
    void consume_DateInCacheBeforeUpdated_ShouldConvertAndSendAndLogSuccess() {
        var millis = System.currentTimeMillis();
        var timeInCache = Instant.ofEpochMilli(millis - 1);
        var consanaUpdateQueueDto = prepareConsanaPatientUpdateQueueDto(millis);
        var consanaUpdateApiDto = new ConsanaSyncApiDto();

        when(processedPatientsCache.getIfPresent(PATIENT_ID)).thenReturn(timeInCache);
        when(consanaPatientUpdateQueueToApiConverter.convert(consanaUpdateQueueDto)).thenReturn(consanaUpdateApiDto);

        instance.consume(consanaUpdateQueueDto);

        verify(consanaSyncApiSender).sendSyncNotification(consanaUpdateApiDto);
        verify(dispatchLogService).logSuccess(consanaUpdateQueueDto);
    }

    @Test
    void consume_DateInCacheAfterUpdated_ShouldNotConvertAndSend() {
        var millis = System.currentTimeMillis();
        var timeInCache = Instant.ofEpochMilli(millis + 1);
        var consanaUpdateQueueDto = prepareConsanaPatientUpdateQueueDto(millis);

        when(processedPatientsCache.getIfPresent(PATIENT_ID)).thenReturn(timeInCache);

        instance.consume(consanaUpdateQueueDto);

        verifyNoInteractions(consanaPatientUpdateQueueToApiConverter);
        verifyNoInteractions(consanaSyncApiSender);
        verify(dispatchLogService).logSuccess(consanaUpdateQueueDto, timeInCache);
    }

    @Test
    void consume_SenderThrows_ShouldLogFail() {
        var consanaUpdateQueueDto = prepareConsanaPatientUpdateQueueDto(System.currentTimeMillis());
        var consanaUpdateApiDto = new ConsanaSyncApiDto();

        when(processedPatientsCache.getIfPresent(PATIENT_ID)).thenReturn(null);
        when(consanaPatientUpdateQueueToApiConverter.convert(consanaUpdateQueueDto)).thenReturn(consanaUpdateApiDto);

        var exception = new RuntimeException();
        doThrow(exception).when(consanaSyncApiSender).sendSyncNotification(consanaUpdateApiDto);

        instance.consume(consanaUpdateQueueDto);

        verify(dispatchLogService).logFail(consanaUpdateQueueDto, exception);
    }

    @Test
    void consume_ConverterThrows_ShouldLogFail() {
        var consanaUpdateQueueDto = prepareConsanaPatientUpdateQueueDto(System.currentTimeMillis());

        when(processedPatientsCache.getIfPresent(PATIENT_ID)).thenReturn(null);

        var exception = new RuntimeException();
        when(consanaPatientUpdateQueueToApiConverter.convert(consanaUpdateQueueDto)).thenThrow(exception);

        instance.consume(consanaUpdateQueueDto);

        verify(dispatchLogService).logFail(consanaUpdateQueueDto, exception);
        verifyNoInteractions(consanaSyncApiSender);
    }

    private ConsanaPatientUpdateQueueDto prepareConsanaPatientUpdateQueueDto(Long millis) {
        var consanaUpdateQueueDto = new ConsanaPatientUpdateQueueDto();
        consanaUpdateQueueDto.setPatientId(PATIENT_ID);
        consanaUpdateQueueDto.setUpdateTime(millis);
        return consanaUpdateQueueDto;
    }
}