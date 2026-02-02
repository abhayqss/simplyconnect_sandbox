package com.scnsoft.eldermark.services.carecoordination;

import com.google.common.collect.ImmutableMap;
import com.scnsoft.eldermark.entity.Note;
import com.scnsoft.eldermark.facades.carecoordination.PatientFacade;
import com.scnsoft.eldermark.shared.carecoordination.EmployeeDto;
import com.scnsoft.eldermark.shared.carecoordination.EventDetailsDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class NoteNotificationServiceImpl implements NoteNotificationService {

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private EventService eventService;

    @Autowired
    private PatientFacade patientFacade;

    @Override
    public void sendNoteNotifications(Note note) {
        eventService.processAutomaticEvent(createEventForNotification(note));
    }

    private EventDto createEventForNotification(Note note) {
        final EventDto eventDto = new EventDto();
        eventDto.setPatient(patientFacade.getPatientDto(note.getResident().getId(), false, false));
        eventDto.setEmployee(new EmployeeDto());
        eventDto.getEmployee().setRoleId(note.getEmployee().getCareTeamRole().getId());
        eventDto.getEmployee().setFirstName(note.getEmployee().getFirstName());
        eventDto.getEmployee().setLastName(note.getEmployee().getLastName());
        final EventDetailsDto eventDetailsDto = new EventDetailsDto();
        eventDetailsDto.setEventDatetime(new Date());
        eventDetailsDto.setSituation(JSONObject.toJSONString(ImmutableMap.of("noteId", note.getId(), "patientId", note.getResident().getId(), "noteType", note.getType().name())));
        eventDetailsDto.setEventTypeId(generateEventTypeId(note));
        eventDto.setEventDetails(eventDetailsDto);
        eventDto.setNote(note);
        return eventDto;
    }

    private Long generateEventTypeId(Note note) {
        switch (note.getStatus()) {
            case CREATED:
                return eventTypeService.getByCode("NOTEADD").getId();
            case UPDATED:
                return eventTypeService.getByCode("NOTEEDIT").getId();
        }
        return null;
    }
}
