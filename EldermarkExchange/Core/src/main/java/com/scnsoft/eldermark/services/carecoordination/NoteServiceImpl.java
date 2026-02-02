package com.scnsoft.eldermark.services.carecoordination;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.scnsoft.eldermark.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.AdmittanceHistoryDao;
import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.dao.carecoordination.CareCoordinationResidentDao;
import com.scnsoft.eldermark.dao.carecoordination.EncounterNoteDao;
import com.scnsoft.eldermark.dao.carecoordination.EventDao;
import com.scnsoft.eldermark.dao.carecoordination.NoteDao;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.services.exceptions.BusinessException;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.shared.carecoordination.notes.NoteDto;
import com.scnsoft.eldermark.shared.carecoordination.notes.NoteListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.notes.NoteResidentAdmittanceHistoryDto;
import com.scnsoft.eldermark.shared.carecoordination.notes.RelatedNoteItemDto;
import com.scnsoft.eldermark.shared.exceptions.BusinessAccessDeniedException;

@Service
@Transactional
public class NoteServiceImpl implements NoteService {
    private static final Logger logger = LoggerFactory.getLogger(NoteServiceImpl.class);

    @Autowired
    private NoteDao noteDao;

    @Autowired
    private EncounterNoteDao encounterNoteDao;

    @Autowired
    private ResidentDao residentDao;

    @Autowired
    private EventDao eventDao;

    @Autowired
    private NoteNotificationService noteNotificationService;

    @Autowired
    private NoteSubTypeService noteSubTypeService;

    @Autowired
    private MPIService mpiService;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private AdmittanceHistoryDao admittanceHistoryDao;

    @Autowired
    private CareCoordinationResidentDao careCoordinationResidentDao;

    @Autowired
    private EncounterNoteTypeService encounterNoteTypeService;

    private Set<Long> getMatchedResidentIds(Long patientId) {
        final Set<Long> mergedFilterResidentsIds = new HashSet<Long>();
        mergedFilterResidentsIds.add(patientId);
        mergedFilterResidentsIds.addAll(mpiService.listMergedResidents(patientId));
        return mergedFilterResidentsIds;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoteListItemDto> listPatientNotes(Long patientId, Pageable pageRequest) {
        Page<Note> notes = noteDao.getAllByResident_IdInAndArchivedIsFalseOrderByLastModifiedDateDescIdDesc(
                getMatchedResidentIds(patientId), pageRequest);
        return new PageImpl<>(toNoteListDto(notes.getContent()), pageRequest, notes.getTotalElements());
    }

    @Override
    public List<RelatedNoteItemDto> getRelatedEventNotes(Long eventId) {
        return NoteUtil.toRelatedNoteDtoList(
                noteDao.getAllByEventIdAndArchivedIsFalseOrderByLastModifiedDateDescIdDesc(eventId));
    }

    @Override
    public Long count(Long patientId) {
        return noteDao.countByResident_IdInAndArchivedIsFalse(getMatchedResidentIds(patientId));
    }

    @Override
    public Integer getPageNumber(Long noteId, Long patientId) {
        return noteDao.getPageNumber(getMatchedResidentIds(patientId), noteId).intValue();
    }

    @Override
    public Long createPatientNote(NoteDto noteDto) {
        return createNote(noteDto, NoteType.PATIENT_NOTE);
    }

    @Override
    public Long createEventNote(NoteDto noteDto) {
        return createNote(noteDto, NoteType.EVENT_NOTE);
    }

    private Long createNote(NoteDto noteDto, NoteType noteType) {
        final Date current = new Date();
        if (current.before(noteDto.getLastModifiedDate())) {
            logger.warn(String.format("Adding note with date greater than current. Current time: %s, note's time: %s. "
                    + "Substituting current time.", current, noteDto.getLastModifiedDate()));
            noteDto.setLastModifiedDate(current);
        }

        Note note = fromNoteDto(noteDto, noteType, NoteStatus.CREATED);
        if (note instanceof EncounterNote) {
            encounterNoteDao.saveAndFlush((EncounterNote) note);
        } else {
            noteDao.saveAndFlush(note);
        }
        logger.info("Create note DateTime: " + note.getLastModifiedDate());

        noteNotificationService.sendNoteNotifications(note);
        return note.getId();
    }

    @Override
    public Long editNote(NoteDto noteDto) {
        final Note parentNote = noteDao.getOne(noteDto.getId());
        if (parentNote.getArchived()) {
            throw new BusinessException("Modified already archived note with id = " + parentNote.getId());
        }

        Note note = null;

        if (parentNote.getSubType().getEncounterCode() != null) {
            EncounterNote eNote = new EncounterNote();
            eNote.setEncounterNoteType(encounterNoteTypeService.getById(noteDto.getEncouterNoteTypeId()));
            eNote.setClinicianCompletingEncounter(noteDto.getClinicianCompletingEncounter());
            
            Date fromTime = getServerTime(noteDto.getEncounterDate(), noteDto.getFrom(), noteDto.getTimeZoneOffset());
            Date toTime = getServerTime(noteDto.getEncounterDate(), noteDto.getTo(), noteDto.getTimeZoneOffset());
            
            eNote.setEncounterDate(fromTime);
            eNote.setFromTime(fromTime);
            eNote.setToTime(toTime);

            note = eNote;
        } else {
            note = new Note();
        }
        parentNote.setArchived(true);
        noteDao.save(parentNote);
        note.setChainId(parentNote.getChainId() == null ? parentNote.getId() : parentNote.getChainId());
        note.setType(parentNote.getType());
        note.setStatus(NoteStatus.UPDATED);
        note.setLastModifiedDate(new Date());
        note.setEmployee(parentNote.getEmployee());
        note.setResident(parentNote.getResident());
        note.setNoteResidents(Collections.singletonList(parentNote.getResident()));
        note.setEvent(parentNote.getEvent());
        note.setArchived(false);
        note.setSubType(parentNote.getSubType());
        note.setAdmittanceHistory(parentNote.getAdmittanceHistory());
        note.setIntakeDate(parentNote.getIntakeDate());

        note.setSubjective(noteDto.getSubjective());
        note.setObjective(noteDto.getObjective());
        note.setAssessment(noteDto.getAssessment());
        note.setPlan(noteDto.getPlan());

        note = noteDao.saveAndFlush(note);
        logger.info("Note with id=" + parentNote.getId() + " was updated at dateTime:" + note.getLastModifiedDate());

        noteNotificationService.sendNoteNotifications(note);
        return note.getId();
    }

    @Override
    public void checkAddedBySelfOrThrow(Long noteId) {
        if (!isAddedBySelf(noteId)) {
            throw new BusinessAccessDeniedException(
                    "You have no access to specified note, please contact your Administrator for more details.");
        }
    }

    @Override
    public boolean isAddedBySelf(Long noteId) {
        final Note note = noteDao.getOne(noteId);
        return SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds()
                .contains(note.getEmployee().getId())
                && !note.getSubType().getId().equals(noteSubTypeService.getAssessmentSubType().getId());
    }

    private Note fromNoteDto(NoteDto noteDto, NoteType noteType, NoteStatus noteStatus) {
        final Note note ;

        NoteSubType noteSubType = noteSubTypeService.getById(noteDto.getSubType().getId());

        if (noteSubType.getEncounterCode() != null) {

            EncounterNote eNote = new EncounterNote();
            eNote.setEncounterNoteType(encounterNoteTypeService.getById(noteDto.getEncouterNoteTypeId()));
            eNote.setClinicianCompletingEncounter(noteDto.getClinicianCompletingEncounter());
            
            
            Date fromTime = getServerTime(noteDto.getEncounterDate(), noteDto.getFrom(), noteDto.getTimeZoneOffset());
            Date toTime = getServerTime(noteDto.getEncounterDate(), noteDto.getTo(), noteDto.getTimeZoneOffset());
            
            eNote.setEncounterDate(fromTime);
            eNote.setFromTime(fromTime);
            eNote.setToTime(toTime);

            note = eNote;

        } else {
            note = new Note();
        }
        CareCoordinationResident careCoordinationResident = null;
        note.setType(noteType);
        note.setStatus(noteStatus);
        note.setLastModifiedDate(noteDto.getLastModifiedDate());
        note.setEmployee(SecurityUtils.getAuthenticatedUser().getEmployee());
        if (noteDto.getEvent() == null || noteDto.getEvent().getId() == null) {
            Resident resident = residentDao.getResident(noteDto.getPatientId());
            note.setResident(resident);
            note.setNoteResidents(Collections.singletonList(resident));
            careCoordinationResident = careCoordinationResidentDao.get(noteDto.getPatientId());
        } else {
            final Event event = eventDao.get(noteDto.getEvent().getId());
            note.setEvent(event);
            Resident resident = residentDao.getResident(event.getResident().getId());
            note.setResident(resident);
            note.setNoteResidents(Collections.singletonList(resident));
            careCoordinationResident = careCoordinationResidentDao.get(event.getResident().getId());
        }
        note.setArchived(false);
        note.setSubjective(noteDto.getSubjective());
        note.setObjective(noteDto.getObjective());
        note.setAssessment(noteDto.getAssessment());
        note.setPlan(noteDto.getPlan());
        note.setSubType(noteSubType);

        if (noteDto.getNoteResidentAdmittanceHistoryDto() != null
                && noteDto.getNoteResidentAdmittanceHistoryDto().getId() != null) {
            if (note.getSubType().getFollowUpCode() != null && noteSubTypeService
                    .getTakenAdmitIntakeHistoryIdForSubType(note.getResident().getId(),
                            note.getSubType().getFollowUpCode())
                    .contains(noteDto.getNoteResidentAdmittanceHistoryDto().getId())) {
                throw new BusinessException("\"" + note.getSubType().getDescription()
                        + "\" note has been already created for this admit/intake date.");
            }
            if (noteDto.getNoteResidentAdmittanceHistoryDto().getId() == 0L) {
                note.setIntakeDate(careCoordinationResident.getIntakeDate());
            } else {
                final AdmittanceHistory admittanceHistory = admittanceHistoryDao
                        .get(noteDto.getNoteResidentAdmittanceHistoryDto().getId());

                if (!admittanceHistory.getResident().getId().equals(note.getResident().getId())) {
                    throw new BusinessException("Resident with id = " + note.getResident().getId()
                            + " doesn't have attendance record with id = " + admittanceHistory.getId());
                }
                note.setAdmittanceHistory(admittanceHistory);
            }
        } else if (note.getSubType().getFollowUpCode() != null) {
            throw new BusinessException(
                    "No admit/intake date was provided for \"" + note.getSubType().getDescription() + "\" note.");
        }
        return note;
    }

    private static Date getServerTime(Date date, Date time, int timeZoneOffsetInMinutes) {
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);
        cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        cal.add(Calendar.MINUTE, timeZoneOffsetInMinutes);
        return cal.getTime();
    }

    private static List<NoteListItemDto> toNoteListDto(List<Note> sourceList) {
        final List<NoteListItemDto> dtos = new ArrayList<NoteListItemDto>();
        for (Note source : sourceList) {
            dtos.add(transformListItem(source));
        }
        return dtos;
    }

    private static NoteListItemDto transformListItem(Note source) {
        NoteListItemDto target = new NoteListItemDto();
        target.setNoteId(source.getId());
        target.setLastModifiedDate(source.getLastModifiedDate());
        target.setStatus(source.getStatus().getDisplayName());
        if (source.getType().equals(NoteType.PATIENT_NOTE)) {
            target.setType(source.getSubType().getDescription());
        } else {
            target.setType(source.getType().getDisplayName());
        }
        return target;
    }

    @Override
    public Long getLatestForNote(Long noteId) {
        return noteDao.getLatestForNote(noteId);
    }

    @Override
    public List<NoteResidentAdmittanceHistoryDto> getNoteAdmittanceHistoryForResidentWithIntakeDate(Long residentId) {
        final List<AdmitIntakeResidentDate> admittanceHistories = residentService
                .getAdmitIntakeHistoryFiltered(residentId);
        return toNoteResidentAdmittanceHistoryDtoList(admittanceHistories);
    }

    @Override
    public List<NoteResidentAdmittanceHistoryDto> getNoteAdmittanceHistoryForEventWithIntakeDate(Long eventId) {
        final Long residentId = eventDao.get(eventId).getResident().getId();
        return getNoteAdmittanceHistoryForResidentWithIntakeDate(residentId);
    }

    private List<NoteResidentAdmittanceHistoryDto> toNoteResidentAdmittanceHistoryDtoList(
            List<AdmitIntakeResidentDate> admitIntakeHistories) {
        if (CollectionUtils.isEmpty(admitIntakeHistories)) {
            return Collections.emptyList();
        }
        final List<NoteResidentAdmittanceHistoryDto> result = new ArrayList<>(admitIntakeHistories.size());
        for (AdmitIntakeResidentDate admitIntakeHistory : admitIntakeHistories) {
            result.add(new NoteResidentAdmittanceHistoryDto(admitIntakeHistory.getId(),
                    admitIntakeHistory.getAdmitIntakeDate()));
        }
        return result;
    }

    @Override
    public List<Long> getTakenAdmitIntakeHistoryIdForSubTypeForEvent(Long eventId,
            NoteSubType.FollowUpCode followUpCode) {
        return noteSubTypeService.getTakenAdmitIntakeHistoryIdForSubType(eventDao.get(eventId).getResident().getId(),
                followUpCode);
    }
}
