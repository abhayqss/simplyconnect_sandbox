package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Event;
import com.scnsoft.eldermark.services.carecoordination.EventService;
import com.scnsoft.eldermark.shared.carecoordination.EmployeeDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.PatientDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventFilterDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventListItemDto;
import com.scnsoft.eldermark.web.entity.EventAuthorDto;
import com.scnsoft.eldermark.web.entity.EventCreateDto;
import com.scnsoft.eldermark.web.entity.HospitalEditDto;
import com.scnsoft.eldermark.web.entity.NameWithAddressEditDto;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author phomal
 * Created on 1/31/2018.
 */
@Service
@Transactional
public class EventsService {

    private final ResidentsService residentsService;
    private final EventService eventService;
    private DozerBeanMapper dozer;

    @Autowired
    public EventsService(ResidentsService residentsService, EventService eventService) {
        this.residentsService = residentsService;
        this.eventService = eventService;
    }

    public Long create(Long residentId, EventCreateDto eventCreateDto) {
        residentsService.checkAccessOrThrow(residentId);

        final EventDto eventDto = convert(eventCreateDto);
        eventDto.setPatient(new PatientDto());
        eventDto.getPatient().setId(residentId);

        final Event event = eventService.processManualEvent(eventDto);
        return event.getId();
    }

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

    @Transactional(readOnly = true)
    public EventDto get(Long residentId, Long eventId) {
        residentsService.checkAccessOrThrow(residentId);

        // TODO implement
        return null;
    }

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
