package com.scnsoft.eldermark.event.xml.converter;

import com.scnsoft.eldermark.entity.event.*;
import com.scnsoft.eldermark.event.xml.schema.*;
import com.scnsoft.eldermark.service.EventTypeService;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.core.convert.converter.Converter;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Instant;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

public abstract class AbstractEventEntityConverter {

    private final EventTypeService eventTypeService;

    private final Converter<Address, EventAddress> eventAddressEntityConverter;

    protected AbstractEventEntityConverter(EventTypeService eventTypeService, Converter<Address, EventAddress> eventAddressEntityConverter) {
        this.eventTypeService = eventTypeService;
        this.eventAddressEntityConverter = eventAddressEntityConverter;
    }

    protected com.scnsoft.eldermark.entity.event.Event createAndFillEvent(EventDetails eventDetails) {
        var target = new com.scnsoft.eldermark.entity.event.Event();
        target.setEventType(eventTypeService.findByCode(eventDetails.getType()));
        target.setAssessment(eventDetails.getAssessmentNarrative());
        target.setBackground(eventDetails.getBackgroundNarrative());

        var followUp = eventDetails.getFollowUp();
        if (followUp != null && followUp.isIsExpected()) {
            target.setIsFollowup(true);
            target.setFollowup(followUp.getDetails());
        } else {
            target.setIsFollowup(false);
        }

        target.setIsInjury(BooleanUtils.isTrue(eventDetails.isResultedInInjury()));
        target.setIsManual(false);
        target.setLocation(eventDetails.getLocation());
        target.setSituation(eventDetails.getSituationNarrative());
        target.setIsErVisit(eventDetails.isERVisit());
        target.setIsOvernightIn(eventDetails.isOvernightInPatient());
        target.setAuxiliaryInfo(eventDetails.getAuxiliaryInfo());
        target.setEventDateTime(calculateDatetime(eventDetails.getDate(), eventDetails.getTime()));
        target.setEventTreatingPhysician(Optional.ofNullable(eventDetails.getTreatingPhysician()).map(this::convertEventTreatingPhysician).orElse(null));
        target.setEventTreatingHospital(Optional.ofNullable(eventDetails.getTreatingHospital()).map(this::convertEventTreatingHospital).orElse(null));
        return target;
    }

    protected Instant calculateDatetime(XMLGregorianCalendar date, XMLGregorianCalendar time) {
        var calendar = time == null ? new GregorianCalendar() : time.toGregorianCalendar();
        if (date != null) {
            calendar.set(Calendar.DAY_OF_MONTH, date.getDay());
            calendar.set(Calendar.MONTH, date.getMonth() - 1);
            calendar.set(Calendar.YEAR, date.getYear());
        }
        return calendar.toInstant();
    }

    protected EventManager convertEventManager(Manager manager) {
        var eventManager = new EventManager();
        eventManager.setFirstName(manager.getName().getFirstName());
        eventManager.setLastName(manager.getName().getLastName());
        eventManager.setEmail(manager.getEmail());
        eventManager.setPhone(manager.getPhone());
        return eventManager;
    }

    protected EventAuthor convertEventAuthor(FormAuthor formAuthor) {
        var author = new EventAuthor();
        author.setFirstName(formAuthor.getName().getFirstName());
        author.setLastName(formAuthor.getName().getLastName());
        author.setOrganization(formAuthor.getOrganization());
        author.setRole(formAuthor.getRole());
        return author;
    }

    protected EventRN convertEventRN(RN rn) {
        var eventRN = new EventRN();
        eventRN.setFirstName(rn.getName().getFirstName());
        eventRN.setLastName(rn.getName().getLastName());
        eventRN.setEventAddress(eventAddressEntityConverter.convert(rn.getAddress()));
        return eventRN;
    }

    protected EventTreatingPhysician convertEventTreatingPhysician(TreatingPhysician treatingPhysician) {
        var eventTreatingPhysician = new EventTreatingPhysician();
        eventTreatingPhysician.setFirstName(treatingPhysician.getName().getFirstName());
        eventTreatingPhysician.setLastName(treatingPhysician.getName().getLastName());
        eventTreatingPhysician.setPhone(treatingPhysician.getPhone());
        eventTreatingPhysician.setEventAddress(eventAddressEntityConverter.convert(treatingPhysician.getAddress()));
        return eventTreatingPhysician;
    }

    protected EventTreatingHospital convertEventTreatingHospital(TreatingHospital treatingHospital) {
        var eventTreatingHospital = new EventTreatingHospital();
        eventTreatingHospital.setName(treatingHospital.getName());
        eventTreatingHospital.setPhone(treatingHospital.getPhone());
        eventTreatingHospital.setEventAddress(eventAddressEntityConverter.convert(treatingHospital.getAddress()));
        return eventTreatingHospital;
    }

    protected abstract void pushContent(Object event, com.scnsoft.eldermark.entity.event.Event eventEntity);
}
