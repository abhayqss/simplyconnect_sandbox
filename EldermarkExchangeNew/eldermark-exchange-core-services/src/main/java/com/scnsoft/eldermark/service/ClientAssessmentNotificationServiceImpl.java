package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.entity.basic.AuditableEntityStatus;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.EventAuthor;
import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.entity.note.NoteType;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientAssessmentNotificationServiceImpl implements ClientAssessmentNotificationService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a z")
            .withZone(TimeZone.getTimeZone("CST6CDT").toZoneId());

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private EventService eventService;

    @Autowired
    private NoteSubTypeService noteSubTypeService;

    @Autowired
    private NoteService noteService;

    @Override
    public Event createAssessmentEvent(ClientAssessmentResult clientAssessmentResult, Pair<Long, Map<String,String>> scoreWithNegativeAnswers) {
        var event = createEventForAssessment(clientAssessmentResult, scoreWithNegativeAnswers);
        return  eventService.save(event);
    }

    @Override
    public Long createAssessmentEventNote(ClientAssessmentResult previousAssessmentResult, ClientAssessmentResult updatedAssessmentResult, Long updatedScore, String updatedSeverity) {
        var note = createNote(previousAssessmentResult, updatedAssessmentResult, updatedScore, updatedSeverity);
        return noteService.createAuditableEntity(note);
    }

    private Event createEventForAssessment(ClientAssessmentResult clientAssessmentResult, Pair<Long, Map<String,String>> scoreWithNegativeAnswers) {
        final Event event = new Event();
        var author = new EventAuthor();
        author.setFirstName(clientAssessmentResult.getEmployee().getFirstName());
        author.setLastName(clientAssessmentResult.getEmployee().getLastName());
        author.setOrganization(clientAssessmentResult.getEmployee().getOrganization().getName());
        author.setRole(clientAssessmentResult.getEmployee().getCareTeamRole().getName());
        event.setEventAuthor(author);
        event.setEventDateTime(clientAssessmentResult.getDateCompleted());
        event.setClient(clientAssessmentResult.getClient());
        event.setAssessment(buildAssessmentField(clientAssessmentResult, scoreWithNegativeAnswers));
        event.setAssessmentResult(clientAssessmentResult);
        clientAssessmentResult.setEvent(event);
        event.setEventType(eventTypeService.findByCode("ARD"));
        return event;
    }

    private String buildAssessmentField(ClientAssessmentResult clientAssessmentResult, Pair<Long, Map<String,String>> scoreWithNegativeAnswers) {
        var assessment = clientAssessmentResult.getAssessment();
        StringBuilder assessmentSb = new StringBuilder();
        assessmentSb.append("Assessment: ");
        assessmentSb.append(clientAssessmentResult.getAssessment().getName());
        assessmentSb.append("; ");
        assessmentSb.append("\n Date Completed: ");
        assessmentSb.append(DATE_TIME_FORMATTER.format(clientAssessmentResult.getDateCompleted()));
        assessmentSb.append(";");
        if (BooleanUtils.isTrue(assessment.getScoringEnabled())) {
            assessmentSb.append("\n Scoring: ");
            assessmentSb.append(scoreWithNegativeAnswers.getFirst());
            assessmentSb.append(" point(s);");
        }
        if (assessment.getEventsPreferences().getIncludeDetailedAnswers()) {
            assessmentSb.append("\n Triggers: ");
            var questionsWithAnswers = scoreWithNegativeAnswers.getSecond().entrySet().stream()
                    .map(entry -> "\n" +  entry.getKey() + ": " + entry.getValue()).collect(Collectors.joining(". "));
            assessmentSb.append(questionsWithAnswers);
        }
        return assessmentSb.toString();
    }

    private Note createNote(ClientAssessmentResult previousAssessmentResult, ClientAssessmentResult updatedAssessmentResult, Long updatedScore, String updatedSeverity) {
        Note note = new Note();
        note.setAuditableStatus(AuditableEntityStatus.CREATED);
        note.setLastModifiedDate(Instant.now());
        note.setArchived(Boolean.FALSE);
        note.setClient(updatedAssessmentResult.getClient());
        note.setNoteClients(Collections.singletonList(updatedAssessmentResult.getClient()));
        note.setEmployee(updatedAssessmentResult.getEmployee());
        note.setAssessment("The assessment results have been updated \n" +
                "  The updates are: \n" +
                " Date completed: " + DATE_TIME_FORMATTER.format(updatedAssessmentResult.getDateCompleted()) + "\n" +
                " Scoring results: " + updatedScore + " point(s) " + updatedSeverity + "");
        note.setEvent(previousAssessmentResult.getEvent());
        note.setSubType(noteSubTypeService.findByCode("ASSESSMENT_NOTE"));
        note.setType(NoteType.EVENT_NOTE);
        return note;
    }
}
