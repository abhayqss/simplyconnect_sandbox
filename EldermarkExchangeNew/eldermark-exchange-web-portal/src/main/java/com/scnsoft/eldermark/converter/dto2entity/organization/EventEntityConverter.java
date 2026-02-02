package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.events.EventDescriptionDto;
import com.scnsoft.eldermark.dto.events.EventDto;
import com.scnsoft.eldermark.dto.events.EventEssentialsDto;
import com.scnsoft.eldermark.dto.events.TreatmentDto;
import com.scnsoft.eldermark.entity.event.*;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.EventTypeService;
import com.scnsoft.eldermark.service.StateService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class EventEntityConverter implements Converter<EventDto, Event> {

    private static final String RBA_DEFAULT_ORGANIZATION = "Altair ACH";

    @Autowired
    private ClientService clientService;

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private StateService stateService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Override
    public Event convert(EventDto eventDto) {
        var event = new Event();
        event.setClient(clientService.getById(eventDto.getClient().getId()));
        event.setIsManual(true);
        fillEventEssentials(event, eventDto.getEssentials());
        fillEventAuthor(event, eventDto);
        fillEventDescription(event, eventDto.getDescription());
        if (eventDto.getTreatment()!=null) {
            fillTreatmentDetails(event, eventDto.getTreatment());
        }
        fillResponsibleManager(event, eventDto);
        fillRegisteredNurse(event, eventDto);
        return event;
    }

    private void fillEventDescription(Event event, EventDescriptionDto eventDescription) {
        event.setLocation(eventDescription.getLocation());
        event.setSituation(eventDescription.getSituation());
        event.setBackground(eventDescription.getBackground());
        event.setAssessment(eventDescription.getAssessment());
        if (BooleanUtils.isTrue(eventDescription.getIsFollowUpExpected())) {
            event.setIsFollowup(true);
            event.setFollowup(eventDescription.getFollowUpDetails());
        } else {
            event.setIsFollowup(false);
        }
        event.setIsInjury(eventDescription.getHasInjury());
    }

    private void fillEventEssentials(Event event, EventEssentialsDto eventEssentials) {
        event.setEventType(eventTypeService.findById(eventEssentials.getTypeId()));
        event.setIsErVisit(eventEssentials.getIsEmergencyDepartmentVisit());
        event.setIsOvernightIn(eventEssentials.getIsOvernightInpatient());
        event.setEventDateTime(DateTimeUtils.toInstant(eventEssentials.getDate()));
    }

    private void fillResponsibleManager(Event event, EventDto eventDto) {
        if (eventDto.getHasResponsibleManager()) {
            EventManager eventManager = new EventManager();
            eventManager.setFirstName(eventDto.getResponsibleManager().getFirstName());
            eventManager.setLastName(eventDto.getResponsibleManager().getLastName());
            eventManager.setEmail(eventDto.getResponsibleManager().getEmail());
            eventManager.setPhone(eventDto.getResponsibleManager().getPhone());
            event.setEventManager(eventManager);
        }
    }

    private void fillEventAuthor(Event event, EventDto eventDto) {
        var author = new EventAuthor();
        var employee = loggedUserService.getCurrentEmployee();
        author.setFirstName(employee.getFirstName());
        author.setLastName(employee.getLastName());
        author.setOrganization(RBA_DEFAULT_ORGANIZATION); //todo why RBA_DEFAULT_ORGANIZATION?
        author.setRole(eventDto.getEssentials().getAuthorRole());
        event.setEventAuthor(author);
    }

    private void fillRegisteredNurse(Event event, EventDto eventDto) {
        if (eventDto.getHasRegisteredNurse()) {
            var eventRN = new EventRN();
            eventRN.setFirstName(eventDto.getRegisteredNurse().getFirstName());
            eventRN.setLastName(eventDto.getRegisteredNurse().getLastName());
            if (eventDto.getRegisteredNurse().isHasAddress()) {
                eventRN.setEventAddress(convertEventAddress(eventDto.getRegisteredNurse().getAddress()));
            }
            event.setEventRn(eventRN);
        }
    }

    private void fillTreatmentDetails(Event event, TreatmentDto treatmentDetails) {
        fillTreatingPhysician(event, treatmentDetails);
        fillTreatingHospital(event, treatmentDetails);
    }

    private void fillTreatingPhysician(Event event, TreatmentDto treatmentDetails) {
        if (treatmentDetails.getHasPhysician()) {
            var eventTreatingPhysician = new EventTreatingPhysician();

            eventTreatingPhysician.setFirstName(treatmentDetails.getPhysician().getFirstName());
            eventTreatingPhysician.setLastName(treatmentDetails.getPhysician().getLastName());
            eventTreatingPhysician.setPhone(treatmentDetails.getPhysician().getPhone());

            if (treatmentDetails.getPhysician().getHasAddress()) {
                eventTreatingPhysician.setEventAddress(convertEventAddress(treatmentDetails.getPhysician().getAddress()));
            }

            event.setEventTreatingPhysician(eventTreatingPhysician);
        }
    }

    private void fillTreatingHospital(Event event, TreatmentDto treatmentDetails) {
        if (treatmentDetails.getHasHospital()) {
            final EventTreatingHospital eventTreatingHospital = new EventTreatingHospital();

            eventTreatingHospital.setName(treatmentDetails.getHospital().getName());
            eventTreatingHospital.setPhone(treatmentDetails.getHospital().getPhone());

            if (treatmentDetails.getHospital().getHasAddress()) {
                eventTreatingHospital.setEventAddress(convertEventAddress(treatmentDetails.getHospital().getAddress()));
            }

            event.setEventTreatingHospital(eventTreatingHospital);
        }
    }

    private EventAddress convertEventAddress(AddressDto addressDto) {
        EventAddress eventAddress = new EventAddress();
        eventAddress.setCity(addressDto.getCity());
        eventAddress.setStreet(addressDto.getStreet());
        eventAddress.setZip(addressDto.getZip());
        eventAddress.setState(stateService.findById(addressDto.getStateId()).orElseThrow());
        return eventAddress;
    }
}
