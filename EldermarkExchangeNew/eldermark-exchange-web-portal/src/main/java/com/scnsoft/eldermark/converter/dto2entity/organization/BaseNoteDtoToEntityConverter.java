package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dao.AdmittanceHistoryDao;
import com.scnsoft.eldermark.dto.notes.NoteDto;
import com.scnsoft.eldermark.entity.document.facesheet.AdmittanceHistory;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.entity.note.NoteType;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.*;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public abstract class BaseNoteDtoToEntityConverter<T extends Note> implements Converter<NoteDto, T> {

    @Autowired
    private ClientService clientService;

    @Autowired
    private EventService eventService;

    @Autowired
    private NoteSubTypeService noteSubTypeService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AdmittanceHistoryDao admittanceHistoryDao;

    @Autowired
    private LoggedUserService loggedUserService;

    protected void convertBase(NoteDto noteDto, T note) {
        if (noteDto.getId() != null) {
            var noteEntity = Optional.ofNullable(noteService.findById(noteDto.getId())).orElseThrow();
            copyNotEditableData(noteEntity, note);
        } else {
            fillNewNoteNotEditableData(noteDto, note);
        }

        note.setEmployee(loggedUserService.getCurrentEmployee());
        note.setSubjective(noteDto.getSubjective());
        note.setObjective(noteDto.getObjective());
        note.setAssessment(noteDto.getAssessment());
        note.setPlan(noteDto.getPlan());

        if (NoteType.GROUP_NOTE.equals(note.getType())) {
            note.setNoteName(noteDto.getNoteName());
        } else {
            if (noteDto.getAdmitDateId() != null) {
                var noteClient = note.getNoteClients().get(0);
                if (!noteService.isAdmitDateCanBeTaken(noteClient.getId(), note.getSubType().getId(), note.getId(), noteDto.getAdmitDateId())) {
                    throw new BusinessException("\"" + note.getSubType().getDescription() + "\" note has been already created for this admit/intake date.");
                }
                if (CareCoordinationConstants.ADMIT_DATE_FROM_INTAKE_DATE_ID.equals(noteDto.getAdmitDateId())) {
                    note.setIntakeDate(noteClient.getIntakeDate());
                } else {
                    final AdmittanceHistory admittanceHistory = admittanceHistoryDao.getOne(noteDto.getAdmitDateId());
                    if (!admittanceHistory.getClient().getId().equals(noteClient.getId())) {
                        throw new BusinessException("Client with id = " + noteClient.getId() + " doesn't have attendance record with id = " + admittanceHistory.getId());
                    }
                    note.setAdmittanceHistory(admittanceHistory);
                }
            } else if (note.getSubType().getFollowUpCode() != null) {
                throw new BusinessException("No admit/intake date was provided for \"" + note.getSubType().getDescription() + "\" note.");
            }
        }

        if (noteDto.getEncounter().getClinicianId() != null) {
            note.setClinicianCompletingEncounter(employeeService.getEmployeeById(noteDto.getEncounter().getClinicianId()));
        }
        note.setOtherClinicianCompletingEncounter(noteDto.getEncounter().getOtherClinician());
        //encounter date is unnecessary. Set equal to from date for compatibility with old portal
        var fromDate = Optional.ofNullable(noteDto.getEncounter().getFromDate())
                .map(DateTimeUtils::toInstant)
                .orElse(null);
        note.setEncounterDate(fromDate);
        note.setEncounterFromTime(fromDate);
        note.setEncounterToTime(Optional.ofNullable(noteDto.getEncounter().getToDate())
                .map(DateTimeUtils::toInstant)
                .orElse(null));
    }

    protected void fillNewNoteNotEditableData(NoteDto noteDto, T note) {
        var noteSubType = noteSubTypeService.findById(noteDto.getSubTypeId());
        if (noteDto.getEventId() == null) {

            if (CollectionUtils.isNotEmpty(noteDto.getClients())) {
                if (!noteSubType.isAllowedForGroupNote()) {
                    throw new ValidationException(noteSubType.getDescription() + " sub type is not allowed for group note.");
                }
                note.setType(NoteType.GROUP_NOTE);
                note.setNoteClients(clientService.findAllById(noteDto.getClientIds()));
            } else {
                note.setType(NoteType.PATIENT_NOTE);
                var client = clientService.getById(noteDto.getClientId());
                note.setClient(client);
                note.setNoteClients(Collections.singletonList(client));
            }
        } else {
            if (!noteSubType.getAllowedForEventNote()) {
                throw new ValidationException(noteSubType.getDescription() + " sub type is not allowed for event note.");
            }
            final Event event = eventService.findById(noteDto.getEventId());
            note.setEvent(event);
            note.setClient(event.getClient());
            note.setNoteClients(Collections.singletonList(event.getClient()));
            note.setType(NoteType.EVENT_NOTE);
        }
        note.setNoteDate(DateTimeUtils.toInstant(noteDto.getNoteDate()));
        note.setSubType(noteSubType);
    }

    protected void copyNotEditableData(Note noteEntity, T note) {
        note.setId(noteEntity.getId());
        note.setEvent(noteEntity.getEvent());
        note.setType(noteEntity.getType());
        note.setClient(noteEntity.getClient());
        note.setNoteClients(new ArrayList<>(noteEntity.getNoteClients()));
        note.setNoteDate(noteEntity.getNoteDate());
        note.setSubType(noteEntity.getSubType());
    }

}
