package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.dao.carecoordination.NoteDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.facades.carecoordination.PatientFacade;
import com.scnsoft.eldermark.shared.carecoordination.EmployeeDto;
import com.scnsoft.eldermark.shared.carecoordination.EventDetailsDto;
import com.scnsoft.eldermark.shared.carecoordination.assessments.ResidentAssessmentScoringDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

@Service
@Transactional
public class ResidentAssessmentResultNotificationServiceImpl implements ResidentAssessmentResultNotificationService {

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private EventService eventService;

    @Autowired
    private PatientFacade patientFacade;

    @Autowired
    private NoteDao noteDao;

    @Autowired
    private NoteSubTypeService noteSubTypeService;

    @Autowired
    private NoteNotificationService noteNotificationService;

    @Override
    public Event sendAssessmentNotifications(ResidentAssessmentResult residentAssessmentResult, Long score) {
        return eventService.processManualEvent(createEventForAssessment(residentAssessmentResult, score));
    }

    @Override
    public void createNoteForResidentAssessmentResultEvent(Date lastModifiedDate, ResidentAssessmentResult residentAssessmentResult, ResidentAssessmentScoringDto dto) {
        Note note = createNote(lastModifiedDate, residentAssessmentResult, dto);
        noteDao.saveAndFlush(note);
        noteNotificationService.sendNoteNotifications(note);
    }

    private Note createNote(Date lastModifiedDate, ResidentAssessmentResult residentAssessmentResult, ResidentAssessmentScoringDto dto) {
        Note note = new Note();
        note.setStatus(NoteStatus.CREATED);
        note.setLastModifiedDate(lastModifiedDate);
        note.setArchived(Boolean.FALSE);
        note.setResident(residentAssessmentResult.getResident());
        note.setNoteResidents(Collections.singletonList(residentAssessmentResult.getResident()));
        note.setEmployee(residentAssessmentResult.getEmployee());
        /*note.setAssessment("<p class=\"assessment-field-paragraph\">The assessment results have been updated</p>" +
                "<p class=\"assessment-field-paragraph\">The updates are:</p>" +
                "<p class=\"assessment-field-paragraph\">Date completed: " + new SimpleDateFormat("MM/dd/yyyy hh:mm a zzz").format(residentAssessmentResult.getLastModifiedDate()) + "</p>" +
                "<p class=\"assessment-field-paragraph\">Scoring results: " + dto.getAssessmentScore() + " point(s); " + dto.getSeverity() + "</p>");*/
        
        note.setAssessment("The assessment results have been updated; \n" +
                "  The updates are:; \n" +
                " Date completed: " + new SimpleDateFormat("MM/dd/yyyy hh:mm a zzz").format(residentAssessmentResult.getLastModifiedDate()) + ";\n" +
                " Scoring results: " + dto.getAssessmentScore() + " point(s) " + dto.getSeverity() + ";");
        
        note.setEvent(residentAssessmentResult.getEvent());
        note.setSubType(noteSubTypeService.getAssessmentSubType());
        note.setType(NoteType.EVENT_NOTE);
        return note;
    }

    private EventDto createEventForAssessment(ResidentAssessmentResult residentAssessmentResult, Long score) {
        final EventDto eventDto = new EventDto();
        eventDto.setPatient(patientFacade.getPatientDto(residentAssessmentResult.getResident().getId(), false,false));
        eventDto.setEmployee(new EmployeeDto());
        eventDto.getEmployee().setRoleId(residentAssessmentResult.getEmployee().getCareTeamRole().getId());
        eventDto.getEmployee().setFirstName(residentAssessmentResult.getEmployee().getFirstName());
        eventDto.getEmployee().setLastName(residentAssessmentResult.getEmployee().getLastName());
        final EventDetailsDto eventDetailsDto = new EventDetailsDto();
        eventDetailsDto.setEventDatetime(residentAssessmentResult.getLastModifiedDate());
        eventDetailsDto.setAssessment(buildAssessmentField(residentAssessmentResult, score));
        eventDetailsDto.setAssessmentCompletedDate(residentAssessmentResult.getDateCompleted());
        EventType eventType = eventTypeService.getByCode("ARD");
        if (eventType != null) {
            eventDetailsDto.setEventTypeId(eventType.getId());
        }
        eventDto.setEventDetails(eventDetailsDto);
        return eventDto;
    }

    private String buildAssessmentField(ResidentAssessmentResult residentAssessmentResult, Long score) {
        StringBuilder assessmentSb = new StringBuilder();
        assessmentSb.append("Assessment: ");
        assessmentSb.append(residentAssessmentResult.getAssessment().getName());
        assessmentSb.append("; ");
       // assessmentSb.append("<p class=\"assessment-field-paragraph\">Date Completed: ");
        assessmentSb.append("\n Date Completed: ");
        assessmentSb.append(new SimpleDateFormat("MM/dd/yyyy hh:mm a zzz").format(residentAssessmentResult.getDateCompleted()));
        assessmentSb.append(";");
       // assessmentSb.append("<p class=\"assessment-field-paragraph\">Scoring: ");
        assessmentSb.append("\n Scoring: ");
        assessmentSb.append(score);
        assessmentSb.append(" point(s);");
        return assessmentSb.toString();
    }

}
