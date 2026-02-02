package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.EventAuthorDto;
import com.scnsoft.eldermark.api.external.web.dto.EventCreateDto;
import com.scnsoft.eldermark.api.external.web.dto.HospitalEditDto;
import com.scnsoft.eldermark.api.external.web.dto.NameWithAddressEditDto;
import com.scnsoft.eldermark.api.shared.dto.EmployeeDto;
import com.scnsoft.eldermark.api.shared.dto.KeyValueDto;
import com.scnsoft.eldermark.api.shared.dto.PatientDto;
import com.scnsoft.eldermark.api.shared.dto.WithAddressDto;
import com.scnsoft.eldermark.api.shared.dto.events.EventDto;
import com.scnsoft.eldermark.api.shared.dto.events.EventFilterDto;
import com.scnsoft.eldermark.api.shared.dto.events.EventListItemDto;
import com.scnsoft.eldermark.dao.CareTeamRoleDao;
import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.entity.event.*;
import com.scnsoft.eldermark.service.CareCoordinationConstants;
import com.scnsoft.eldermark.service.EventService;
import com.scnsoft.eldermark.service.EventTypeService;
import com.scnsoft.eldermark.service.StateService;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@Transactional
public class EventsServiceImpl implements EventsService {

    private final ResidentsService residentsService;
    private final EventService eventService;
    private DozerBeanMapper dozer;

    @Autowired
    public EventsServiceImpl(ResidentsService residentsService, EventService eventService) {
        this.residentsService = residentsService;
        this.eventService = eventService;
    }

    @Override
    public Long create(Long residentId, EventCreateDto eventCreateDto) {
        residentsService.checkAccessOrThrow(residentId);

        final EventDto eventDto = convert(eventCreateDto);
        eventDto.setPatient(new PatientDto());
        eventDto.getPatient().setId(residentId);

        //todo - write direct EventCreateDto -> Event converter and save
        Event event = createEvent(eventDto, true);
        event = eventService.save(event);
        return event.getId();
    }

    ///===================== temporary workaround ===============================
    //todo - write direct EventCreateDto -> Event converter and save
    @Autowired
    private ClientDao clientDao;

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private CareTeamRoleDao careTeamRoleDao;

    @Autowired
    private StateService stateService;

    private Event createEvent(EventDto eventDto, boolean isManual) {
        var eventEntity = new Event();
        eventEntity.setClient(clientDao.getOne(eventDto.getPatient().getId()));
        eventEntity.setEventType(eventTypeService.findById(eventDto.getEventDetails().getEventTypeId()));
        eventEntity.setAssessment(eventDto.getEventDetails().getAssessment());
        eventEntity.setBackground(eventDto.getEventDetails().getBackground());

        if (eventDto.getEventDetails().isFollowUpExpected()) {
            eventEntity.setIsFollowup(true);
            eventEntity.setFollowup(eventDto.getEventDetails().getFollowUpDetails());
        } else {
            eventEntity.setIsFollowup(false);
        }
        eventEntity.setIsInjury(eventDto.getEventDetails().isInjury());
        eventEntity.setIsManual(isManual);
        eventEntity.setLocation(eventDto.getEventDetails().getLocation());
        eventEntity.setSituation(eventDto.getEventDetails().getSituation());
        eventEntity.setIsErVisit(eventDto.getEventDetails().isEmergencyVisit());
        eventEntity.setIsOvernightIn(eventDto.getEventDetails().isOvernightPatient());
        eventEntity.setNotes(Arrays.asList(eventDto.getNote()));
        eventEntity.setEventDateTime(eventDto.getEventDetails().getEventDatetime().toInstant());
        eventEntity.setAuxiliaryInfo(eventDto.getEventDetails().getAuxiliaryInfo());

        if (eventDto.isIncludeManager()) {
            final EventManager eventManager = new EventManager();
            eventManager.setFirstName(eventDto.getManager().getFirstName());
            eventManager.setLastName(eventDto.getManager().getLastName());
            eventManager.setEmail(eventDto.getManager().getEmail());
            eventManager.setPhone(eventDto.getManager().getPhone());
            eventEntity.setEventManager(eventManager);
        }

        final EventAuthor author = new EventAuthor();

        author.setFirstName(eventDto.getEmployee().getFirstName());
        author.setLastName(eventDto.getEmployee().getLastName());
        author.setOrganization(CareCoordinationConstants.RBA_DEFAULT_ORGANIZATION);
        author.setRole(careTeamRoleDao.findById(eventDto.getEmployee().getRoleId()).orElseThrow().getName());
        eventEntity.setEventAuthor(author);

        if (eventDto.isIncludeResponsible()) {
            final EventRN eventRN = new EventRN();
            eventRN.setFirstName(eventDto.getResponsible().getFirstName());
            eventRN.setLastName(eventDto.getResponsible().getLastName());
            if (eventDto.getResponsible().isIncludeAddress()) {
                eventRN.setEventAddress(createEventAddress(eventDto.getResponsible()));
            }
            eventEntity.setEventRn(eventRN);
        }
        if (eventDto.isIncludeTreatingPhysician()) {
            final EventTreatingPhysician eventTreatingPhysician = new EventTreatingPhysician();

            eventTreatingPhysician.setFirstName(eventDto.getTreatingPhysician().getFirstName());
            eventTreatingPhysician.setLastName(eventDto.getTreatingPhysician().getLastName());
            eventTreatingPhysician.setPhone(eventDto.getTreatingPhysician().getPhone());

            if (eventDto.getTreatingPhysician().isIncludeAddress()) {

                eventTreatingPhysician.setEventAddress(createEventAddress(eventDto.getTreatingPhysician()));
            }
            eventEntity.setEventTreatingPhysician(eventTreatingPhysician);
        }

        if (eventDto.isIncludeHospital()) {
            final EventTreatingHospital eventTreatingHospital = new EventTreatingHospital();

            eventTreatingHospital.setName(eventDto.getTreatingHospital().getName());
            eventTreatingHospital.setPhone(eventDto.getTreatingHospital().getPhone());

            if (eventDto.getTreatingHospital().isIncludeAddress()) {
                eventTreatingHospital.setEventAddress(createEventAddress(eventDto.getTreatingHospital()));
            }
            eventEntity.setEventTreatingHospital(eventTreatingHospital);
        }
//        pushContent(createEvents(eventEntity), eventEntity);
//        eventEntity = eventDao.create(eventEntity);
//        logger.info("Create Event DateTime:" + eventEntity.getEventDatetime());
//        eventDao.flush();

        return eventEntity;
    }

    private EventAddress createEventAddress(final WithAddressDto withAddressDto) {
        final EventAddress eventAddress = new EventAddress();
        eventAddress.setCity(withAddressDto.getAddress().getCity());
        eventAddress.setStreet(withAddressDto.getAddress().getStreet());
        eventAddress.setZip(withAddressDto.getAddress().getZip());
        eventAddress.setState(stateService.findById(withAddressDto.getAddress().getState().getId()).orElse(null));
        return eventAddress;
    }


    ///===================== temporary workaround end ===============================



    private EmployeeDto convert(EventAuthorDto author) {
        final EmployeeDto dto = dozer.map(author, EmployeeDto.class);
        return dto;
    }

    private EventDto convert(EventCreateDto eventCreateDto) {
        EventDto dto = dozer.map(eventCreateDto, EventDto.class);

        HospitalEditDto treatingHospital = eventCreateDto.getTreatingHospital();
        if (treatingHospital != null && treatingHospital.getAddress() != null) {
            KeyValueDto stateDto = treatingHospital.getAddress().getState();
            dto.getTreatingHospital().getAddress().setState(stateDto);
            dto.getTreatingHospital().setIncludeAddress(true);
        }

        NameWithAddressEditDto responsible = eventCreateDto.getResponsible();
        if (responsible != null && responsible.getAddress() != null) {
            KeyValueDto stateDto = responsible.getAddress().getState();
            dto.getResponsible().getAddress().setState(stateDto);
            dto.getResponsible().setIncludeAddress(true);
        }

        NameWithAddressEditDto treatingPhysician = eventCreateDto.getTreatingPhysician();
        if (treatingPhysician != null && treatingPhysician.getAddress() != null) {
            KeyValueDto stateDto = treatingPhysician.getAddress().getState();
            dto.getTreatingPhysician().getAddress().setState(stateDto);
            dto.getTreatingPhysician().setIncludeAddress(true);
        }

        dto.setIncludeHospital(treatingHospital != null);
        dto.setIncludeResponsible(responsible != null);
        dto.setIncludeTreatingPhysician(treatingPhysician != null);
        dto.setIncludeManager(eventCreateDto.getManager() != null);

        final EmployeeDto employeeDto = convert(eventCreateDto.getAuthor());
        dto.setEmployee(employeeDto);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto get(Long residentId, Long eventId) {
        residentsService.checkAccessOrThrow(residentId);

        // TODO implement
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventListItemDto> list(EventFilterDto eventFilter, Pageable pageable) {
        residentsService.checkAccessOrThrow(eventFilter.getPatientId());

        // TODO implement
        return null;
    }

    @Autowired
    public void setDozer(DozerBeanMapper dozer) {
        this.dozer = dozer;
    }

}
