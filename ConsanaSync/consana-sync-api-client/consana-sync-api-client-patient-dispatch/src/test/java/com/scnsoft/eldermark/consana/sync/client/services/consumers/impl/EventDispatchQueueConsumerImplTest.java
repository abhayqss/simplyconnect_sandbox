package com.scnsoft.eldermark.consana.sync.client.services.consumers.impl;

import com.scnsoft.eldermark.consana.sync.client.consana.ConsanaGateway;
import com.scnsoft.eldermark.consana.sync.client.model.ConsanaEntryDto;
import com.scnsoft.eldermark.consana.sync.client.model.ConsanaEventCreatedApiDto;
import com.scnsoft.eldermark.consana.sync.client.model.ConsanaPatientDto;
import com.scnsoft.eldermark.consana.sync.client.model.ConsanaResourceDto;
import com.scnsoft.eldermark.consana.sync.client.model.entities.Database;
import com.scnsoft.eldermark.consana.sync.client.model.entities.Event;
import com.scnsoft.eldermark.consana.sync.client.model.entities.Resident;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaEventCreatedQueueDto;
import com.scnsoft.eldermark.consana.sync.client.services.EventService;
import com.scnsoft.eldermark.consana.sync.client.services.ResidentService;
import com.scnsoft.eldermark.consana.sync.client.services.logging.DispatchLogService;
import com.scnsoft.eldermark.consana.sync.client.services.senders.ConsanaSyncApiSender;
import com.scnsoft.eldermark.consana.sync.common.services.db.SqlServerService;
import org.hl7.fhir.instance.model.Patient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventDispatchQueueConsumerImplTest {

    @Mock
    private EventService eventService;

    @Mock
    private ResidentService residentService;

    @Mock
    private ConsanaGateway consanaGateway;

    @Mock
    private Converter<Pair<Event, Pair<Resident, Patient>>, ConsanaEventCreatedApiDto> eventConverter;

    @Mock
    private ConsanaSyncApiSender consanaSyncApiSender;

    @Mock
    private SqlServerService sqlServerService;

    @Mock
    private DispatchLogService dispatchLogService;

    @InjectMocks
    private EventDispatchQueueConsumerImpl instance;

    @Test
    void consume_ConvertedAdnSentSuccessfully_ShouldLogSuccess() {
        var consanaEventQueueDto = prepareEventCreatedQueueDto();
        var consanaEventApiDto = prepareConsanaEventCreatedApiDto();
        var optionalEvent = Optional.of(new Event());
        var resident = prepareResident();
        var patient = new Patient();

        setEventsPushEnabled(true);
        when(eventService.findById(consanaEventQueueDto.getEventId())).thenReturn(optionalEvent);
        when(residentService.findById(consanaEventQueueDto.getResidentId())).thenReturn(Optional.of(resident));
        when(consanaGateway.getPatient(resident.getConsanaXrefId(), resident.getDatabase().getConsanaXOwningId())).thenReturn(patient);
        when(eventConverter.convert(Pair.of(optionalEvent.get(), Pair.of(resident, patient)))).thenReturn(consanaEventApiDto);

        instance.consume(consanaEventQueueDto);

        verify(sqlServerService).openKey();
        verify(consanaSyncApiSender).sendEvent(consanaEventApiDto);
        verify(dispatchLogService).logSuccess(consanaEventQueueDto, resident);
    }

    @Test
    void consume_SenderThrows_ShouldLogFail() {
        var consanaEventQueueDto = prepareEventCreatedQueueDto();
        var consanaEventApiDto = prepareConsanaEventCreatedApiDto();
        var optionalEvent = Optional.of(new Event());
        var resident = prepareResident();
        var patient = new Patient();

        setEventsPushEnabled(true);
        when(eventService.findById(consanaEventQueueDto.getEventId())).thenReturn(optionalEvent);
        when(residentService.findById(consanaEventQueueDto.getResidentId())).thenReturn(Optional.of(resident));
        when(consanaGateway.getPatient(resident.getConsanaXrefId(), resident.getDatabase().getConsanaXOwningId())).thenReturn(patient);
        when(eventConverter.convert(Pair.of(optionalEvent.get(), Pair.of(resident, patient)))).thenReturn(consanaEventApiDto);

        var exception = new RuntimeException();
        doThrow(exception).when(consanaSyncApiSender).sendEvent(consanaEventApiDto);

        instance.consume(consanaEventQueueDto);

        verify(sqlServerService).openKey();
        verify(dispatchLogService).logFail(consanaEventQueueDto, resident, exception);
    }

    @Test
    void consume_ConverterThrows_ShouldLogFail() {
        var consanaEventQueueDto = prepareEventCreatedQueueDto();
        var exception = new RuntimeException();
        var optionalEvent = Optional.of(new Event());
        var resident = prepareResident();
        var patient = new Patient();

        setEventsPushEnabled(true);
        when(eventService.findById(consanaEventQueueDto.getEventId())).thenReturn(optionalEvent);
        when(residentService.findById(consanaEventQueueDto.getResidentId())).thenReturn(Optional.of(resident));
        when(consanaGateway.getPatient(resident.getConsanaXrefId(), resident.getDatabase().getConsanaXOwningId())).thenReturn(patient);
        when(eventConverter.convert(Pair.of(optionalEvent.get(), Pair.of(resident, patient)))).thenThrow(exception);

        instance.consume(consanaEventQueueDto);

        verify(sqlServerService).openKey();
        verifyNoInteractions(consanaSyncApiSender);
        verify(dispatchLogService).logFail(consanaEventQueueDto, resident, exception);
    }

    @Test
    void consume_EventsPushDisabled_ShouldDoNothing() {
        var consanaEventQueueDto = prepareEventCreatedQueueDto();

        setEventsPushEnabled(false);
        instance.consume(consanaEventQueueDto);

        verifyNoInteractions(sqlServerService);
        verifyNoInteractions(eventService);
        verifyNoInteractions(residentService);
        verifyNoInteractions(consanaGateway);
        verifyNoInteractions(eventConverter);
        verifyNoInteractions(consanaSyncApiSender);
        verifyNoInteractions(dispatchLogService);
    }

    private void setEventsPushEnabled(boolean value) {
        ReflectionTestUtils.setField(instance, "eventsPushEnabled", value);
    }

    private ConsanaEventCreatedQueueDto prepareEventCreatedQueueDto() {
        return new ConsanaEventCreatedQueueDto(1L, 2L);
    }

    private ConsanaEventCreatedApiDto prepareConsanaEventCreatedApiDto() {
        var consanaEventCreatedApiDto = new ConsanaEventCreatedApiDto();
        var consanaEntryDto = new ConsanaEntryDto();
        var consanaResourceDto = new ConsanaResourceDto();
        consanaResourceDto.setPatient(new ConsanaPatientDto());
        consanaEntryDto.setResource(consanaResourceDto);
        consanaEventCreatedApiDto.setEntry(List.of(consanaEntryDto));
        return consanaEventCreatedApiDto;
    }

    private Resident prepareResident() {
        var resident = new Resident(2L, "xref");
        var database = new Database(3L, "orgOid");
        resident.setDatabase(database);
        return resident;
    }
}