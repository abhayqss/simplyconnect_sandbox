package com.scnsoft.eldermark.converter.event;

import com.scnsoft.eldermark.converter.hl7.entity2dto.segment.PatientVisitViewDataConverter;
import com.scnsoft.eldermark.converter.event.base.*;
import com.scnsoft.eldermark.dto.adt.segment.PatientVisitNotificationDto;
import com.scnsoft.eldermark.dto.notification.event.*;
import com.scnsoft.eldermark.entity.event.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class EventDetailsMailDtoConverter extends EventViewDataConverter<EventDetailsNotificationDto> implements Converter<Event, EventDetailsNotificationDto> {

    @Autowired
    private ClientSummaryViewDataConverter<ClientInfoNotificationDto> clientSummaryConverter;

    @Autowired
    private EventEssentialsViewDataConverter<EventEssentialsNotificationDto> essentialsConverter;

    @Autowired
    private EventDescriptionViewDataConverter<EventDescriptionNotificationDto> descriptionConverter;

    @Autowired
    private TreatmentViewDataConverter<TreatingPhysicianMailDto,
            TreatingHospitalMailDto, TreatmentDetailsNotificationDto> treatmentConverter;

    @Autowired
    private ResponsibleManagerConverter<PersonNotificationDto> responsibleManagerConverter;

    @Autowired
    private RegisteredNurseConverter<PersonNotificationDto> registeredNurseConverter;

    @Autowired
    private PatientVisitViewDataConverter<PatientVisitNotificationDto> patientVisitConverter;

    @Override
    protected EventDetailsNotificationDto create() {
        return new EventDetailsNotificationDto();
    }

    @Override
    protected ClientSummaryViewDataConverter getClientSummaryViewDataConverter() {
        return clientSummaryConverter;
    }

    @Override
    protected EventEssentialsViewDataConverter getEventEssentialsViewDataConverter() {
        return essentialsConverter;
    }

    @Override
    protected EventDescriptionViewDataConverter getEventDescriptionViewDataConverter() {
        return descriptionConverter;
    }

    @Override
    protected TreatmentViewDataConverter getTreatmentViewDataConverter() {
        return treatmentConverter;
    }

    @Override
    protected ResponsibleManagerConverter getResponsibleManagerConverter() {
        return responsibleManagerConverter;
    }

    @Override
    protected RegisteredNurseConverter getRegisteredNurseConverter() {
        return registeredNurseConverter;
    }

    @Override
    protected PatientVisitViewDataConverter getPatientVisitViewDataConverter() {
        return patientVisitConverter;
    }
}
