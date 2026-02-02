package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.AdmittanceHistoryDao;
import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.dao.carecoordination.EventDao;
import com.scnsoft.eldermark.dao.carecoordination.NoteDao;
import com.scnsoft.eldermark.dao.phr.NoteReadStatusDao;
import com.scnsoft.eldermark.dao.projections.NoteAndReadBoolean;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.NoteReadStatus;
import com.scnsoft.eldermark.service.transformer.BiConverter;
import com.scnsoft.eldermark.service.transformer.NoteIdConverter;
import com.scnsoft.eldermark.service.transformer.NoteListItemDtoConverter;
import com.scnsoft.eldermark.services.carecoordination.NoteNotificationService;
import com.scnsoft.eldermark.services.carecoordination.NoteSubTypeService;
import com.scnsoft.eldermark.shared.carecoordination.contacts.LinkedContactDto;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.exception.ValidationException;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.web.entity.AdmitDateDto;
import com.scnsoft.eldermark.web.entity.notes.*;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Provider;
import java.util.*;
import java.util.logging.Logger;

@Service
@Transactional
public class NoteService extends BasePhrService {
    Logger logger = Logger.getLogger(NoteService.class.getName());

    @Autowired
    private CareTeamSecurityUtils careTeamSecurityUtils;

    @Autowired
    private ResidentDao residentDao;

    @Autowired
    private NoteDao noteDao;

    @Autowired
    private NoteNotificationService noteNotificationService;

    @Autowired
    private EventDao eventDao;

    @Autowired
    private EventsService eventsService;

    @Autowired
    private com.scnsoft.eldermark.services.carecoordination.ContactService contactService;

    @Autowired
    private PrivilegesService privilegesService;

    @Autowired
    private NoteSubTypeService noteSubTypeService;

    @Autowired
    private AdmittanceHistoryDao admittanceHistoryDao;

    @Autowired
    private Provider<NoteListItemDtoConverter> noteNoteListItemDtoConverterProvider;

    @Autowired
    private BiConverter<NoteCreateDto, Long, Note> noteCreateDtoLongNoteBiConverter;

    @Autowired
    private NoteIdConverter noteIdConverter;

    @Autowired
    private NoteReadStatusDao noteReadStatusDao;

    //=========================== [deprecated] since next after 2.34.23 release version of frontend ======================
    @Deprecated
    @Transactional(readOnly = true)
    public Page<NoteListItemDto> getUserListNotes(Long userId, Pageable pageRequest) {
        canViewNoteOrThrow();
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId);

        Page<Note> notes = noteDao.getAllByResident_IdInAndArchivedIsFalseOrderByLastModifiedDateDescIdDesc(getResidentIdsOrThrow(userId), pageRequest);
        return convertToListItemPage(notes);
    }

    @Deprecated
    @Transactional(readOnly = true)
    public Long getUserListNotesCount(Long userId) {
        canViewNoteOrThrow();
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        return noteDao.countByResident_IdInAndArchivedIsFalse(getResidentIdsOrThrow(userId));
    }

    @Deprecated
    @Transactional(readOnly = true)
    public Page<NoteListItemDto> getRelatedUserEventNotes(Long userId, Long eventId, Pageable pageRequest) {
        canViewNoteOrThrow();
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        eventsService.validateAssociation(userId, eventDao.get(eventId));

        Page<Note> notes = noteDao.getAllByEventIdAndArchivedIsFalseOrderByLastModifiedDateDescIdDesc(eventId, pageRequest);
        return convertToListItemPage(notes);
    }

    public Page<NoteListItemDto> convertToListItemPage(Page<Note> notes) {
        final List<Long> noteIds = notes.map(noteIdConverter).getContent();
        final Map<Long, Boolean> readMap = getReadMapForUserAndNotes(
                careTeamSecurityUtils.getCurrentUser().getId(), noteIds);

        return notes.map(noteNoteListItemDtoConverterProvider.get().addToReadMap(readMap));
    }


    @Deprecated
    public NoteModifiedDto createNote(NoteCreateDto noteCreateDto) {
        canAddNoteOrThrow();
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(noteCreateDto.getCareReceiverId());

        final Date current = new Date();
        if (current.before(noteCreateDto.getLastModifiedDate())) {
            logger.warning(String.format("Adding note with date greater than current. Current time: %s, note's time: %s. " +
                    "Substituting current time.", current, noteCreateDto.getLastModifiedDate()));
            noteCreateDto.setLastModifiedDate(current);
        }

        if (StringUtils.isEmpty(noteCreateDto.getSubjective()) && StringUtils.isEmpty(noteCreateDto.getObjective()) &&
                StringUtils.isEmpty(noteCreateDto.getAssessment()) && StringUtils.isEmpty(noteCreateDto.getPlan())) {
            throw new ValidationException("At least one of [subjective, objective, assessment, plan] should be populated.");
        }

        final Note note = noteDao.saveAndFlush(toNote(noteCreateDto));
        logger.info("Create note DateTime: " + note.getLastModifiedDate());
        noteNotificationService.sendNoteNotifications(note);

        return toNoteModifiedDto(note);

    }

    @Deprecated
    private NoteModifiedDto toNoteModifiedDto(Note note) {
        final NoteModifiedDto noteModifiedDto = new NoteModifiedDto();
        noteModifiedDto.setId(note.getId());
        noteModifiedDto.setType(note.getType());
        noteModifiedDto.setStatus(note.getStatus());
        return noteModifiedDto;
    }

    @Deprecated
    private void canViewNoteDetailsOrThrow(Note note) {
        canViewNoteOrThrow();
        if (note.getType().equals(NoteType.EVENT_NOTE)) {
            //check resident and event association
            eventsService.validateAssociation(careTeamSecurityUtils.getCurrentUser().getId(), note.getEvent());
        } else {
            //patient note - just check resident association
            if (!careTeamSecurityUtils.isAssociated(careTeamSecurityUtils.getCurrentUser().getId(), note.getResident().getId())) {
                throw new PhrException(PhrExceptionType.NOTE_NOT_ASSOCIATED);
            }
        }
    }


    private boolean checkAddedBySelf(Note note) {
        final Long employeeId = getEmployeeOrThrow(careTeamSecurityUtils.getCurrentUser()).getId();

        //1. check if note was added by current employee
        if (note.getEmployee().getId().equals(employeeId)) {
            return true;
        }

        //2. check whether note was added by linked employee
        for (LinkedContactDto lco : contactService.getLinkedEmployees(employeeId)) {
            if (lco.getId().equals(note.getEmployee().getId())) {
                return true;
            }
        }
        return false;
    }

    @Deprecated
    @Transactional(readOnly = true)
    public NoteDetailsDto getNoteDetailsDto(Long noteId) {
        final Note note = noteDao.getOne(noteId);

        canViewNoteDetailsOrThrow(note);

        final NoteDetailsDto noteDetailsDto = new NoteDetailsDto();

        noteDetailsDto.setId(note.getId());
        noteDetailsDto.setResidentId(note.getResident().getId());
        noteDetailsDto.setResidentName(note.getResident().getFullName());
        noteDetailsDto.setType(note.getType());
        if (noteDetailsDto.getType().equals(NoteType.EVENT_NOTE)) {
            noteDetailsDto.setEventId(note.getEvent().getId());
            noteDetailsDto.setEventType(note.getEvent().getEventType().getDescription());
            noteDetailsDto.setEventDate(note.getEvent().getEventDatetime());
            noteDetailsDto.setEventResidentName(note.getEvent().getResident().getFullName());
        }
        noteDetailsDto.setSubType(new NoteSubTypeDto(note.getSubType().getId(), note.getSubType().getDescription(),
                note.getSubType().getFollowUpCode()));
        noteDetailsDto.setStatus(note.getStatus());
        noteDetailsDto.setLastModifiedDate(note.getLastModifiedDate());
        noteDetailsDto.setCreator(createNoteEmployeeDto(note));
        noteDetailsDto.setDataSource(DataSourceService.transform(note.getEmployee().getDatabase(), note.getResident().getId()));
        noteDetailsDto.setSubjective(note.getSubjective());
        noteDetailsDto.setObjective(note.getObjective());
        noteDetailsDto.setAssessment(note.getAssessment());
        noteDetailsDto.setPlan(note.getPlan());
        noteDetailsDto.setIsArchived(note.getArchived());
        noteDetailsDto.setIsEditable(canEditNote(note));

        if (note.getStatus().equals(NoteStatus.UPDATED) && !note.getArchived()) {
            final List<Note> history = getHistoryNotes(note);
            if (!CollectionUtils.isEmpty(history)) {
                List<HistoryNoteItemDto> transformedHistory = new ArrayList<>(history.size());
                for (Note n : history) {
                    transformedHistory.add(toHistoryNoteItemDto(n));
                }
                noteDetailsDto.setChangeHistory(transformedHistory);
            }
        }

        if (note.getAdmittanceHistory() != null) {
            final AdmittanceHistory noteAdmittanceHistory = note.getAdmittanceHistory();
            noteDetailsDto.setAdmitDate(new AdmitDateDto(noteAdmittanceHistory.getId(), noteAdmittanceHistory.getAdmitDate()));
        } else if (note.getIntakeDate() != null) {
            noteDetailsDto.setAdmitDate(new AdmitDateDto(0l, note.getIntakeDate()));
        }
        return noteDetailsDto;
    }

    @Deprecated
    private HistoryNoteItemDto toHistoryNoteItemDto(Note note) {
        final HistoryNoteItemDto dto = new HistoryNoteItemDto();
        dto.setId(note.getId());
        dto.setStatus(note.getStatus());
        dto.setLastModifiedDate(note.getLastModifiedDate());
        dto.setCreator(createNoteEmployeeDto(note));
        return dto;
    }

    @Deprecated
    private NoteEmployeeDto createNoteEmployeeDto(Note note) {
        final NoteEmployeeDto noteEmployeeDto = new NoteEmployeeDto();
        noteEmployeeDto.setId(getUserId(note.getEmployee()));
        noteEmployeeDto.setFirstName(note.getEmployee().getFirstName());
        noteEmployeeDto.setMiddleName(note.getEmployee().getMiddleName());
        noteEmployeeDto.setLastName(note.getEmployee().getLastName());
        noteEmployeeDto.setRole(note.getEmployee().getCareTeamRole().getName());
        noteEmployeeDto.setRoleId(note.getEmployee().getCareTeamRole().getId());
        return noteEmployeeDto;
    }

    public List<Note> getHistoryNotes(Note note) {
        if (note == null || note.getStatus().equals(NoteStatus.CREATED)) {
            return Collections.emptyList();
        }
        return noteDao.getAllByChainIdOrIdAndArchivedIsTrueOrderByLastModifiedDateDescIdDesc(note.getChainId(), note.getChainId());
    }

    @Deprecated
    private Note toNote(NoteCreateDto noteCreateDto) {
        final Note note = new Note();
        note.setStatus(NoteStatus.CREATED);
        note.setLastModifiedDate(noteCreateDto.getLastModifiedDate());

        note.setEmployee(getEmployeeOrThrow(careTeamSecurityUtils.getCurrentUser()));

        if (noteCreateDto.getEventId() == null) {
            note.setType(NoteType.PATIENT_NOTE);
            Resident resident = residentDao.getResident(getResidentIdOrThrow(noteCreateDto.getCareReceiverId()));
            note.setResident(resident);
            note.setNoteResidents(Collections.singletonList(resident));
        } else {
            note.setType(NoteType.EVENT_NOTE);
            final Event event = eventDao.get(noteCreateDto.getEventId());
            note.setEvent(event);
            Resident resident = residentDao.getResident(event.getResident().getId());
            note.setResident(resident);
            note.setNoteResidents(Collections.singletonList(resident));
        }
        note.setArchived(false);
        note.setSubjective(noteCreateDto.getSubjective());
        note.setObjective(noteCreateDto.getObjective());
        note.setAssessment(noteCreateDto.getAssessment());
        note.setPlan(noteCreateDto.getPlan());

        note.setSubType(noteSubTypeService.getById(noteCreateDto.getSubTypeId()));

        if (note.getSubType().getFollowUpCode() != null) {
            if (noteCreateDto.getAdmitDateId() == null) {
                throw new PhrException(PhrExceptionType.NO_ADMIT_DATE_FOR_FOLLOW_UP_NOTE);
            }
            final AdmittanceHistory admittanceHistory = admittanceHistoryDao.get(noteCreateDto.getAdmitDateId());
            if (!admittanceHistory.getResident().getId().equals(getResidentIdOrThrow(noteCreateDto.getCareReceiverId()))) {
                logger.warning("Resident with id = " + note.getResident().getId() + " doesn't have attendance record with id = " + admittanceHistory.getId());
                throw new PhrException(PhrExceptionType.NOT_ASSOCIATED_ADMIT_RECORD);
            }
            note.setAdmittanceHistory(admittanceHistory);
        }
        return note;
    }

    //============================================= [deprecated] ============================================================



    @Transactional(readOnly = true)
    public boolean canAddNote() {
        return privilegesService.canAddNote();
    }

    @Transactional(readOnly = true)
    public void canAddNoteOrThrow() {
        if (!canAddNote()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
    }

    private void canEditNoteOrThrow(Note note) {
        if (!canEditNote(note)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
    }

    @Transactional(readOnly = true)
    public boolean canEditNote(Note note) {
        if (note == null) {
            throw new PhrException(PhrExceptionType.NOT_FOUND);
        }
        return privilegesService.canEditNote() && checkAddedBySelf(note);
    }

    @Transactional(readOnly = true)
    public void canViewNoteOrThrow() {
        if (!privilegesService.canViewNote()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
    }

    @Transactional(readOnly = true)
    public Page<Note> getNotesPage(Collection<Long> residentIds, Pageable pageRequest) {
        return noteDao.getAllByResident_IdInAndArchivedIsFalseOrderByLastModifiedDateDescIdDesc(residentIds, pageRequest);
    }

    @Transactional(readOnly = true)
    public Page<Note> getNotesPage(Collection<Long> patientNotesResidentIds, Collection<Long> eventNotesResidentIds, Pageable pageRequest) {
        Pageable pageWithSort = PaginationUtils.setSort(pageRequest, noteDao.ORDER_BY_LAST_MODIFIED_DATE_DESC, noteDao.ORDER_BY_LAST_ID_DESC);
        return noteDao.getAllByResidentIdInAndArchivedIsFalseAndTypeOrResidentIdInAndArchivedIsFalseAndType(
                patientNotesResidentIds, NoteType.PATIENT_NOTE,
                eventNotesResidentIds, NoteType.EVENT_NOTE,
                pageWithSort
        );
    }

    @Transactional(readOnly = true)
    public Long getNotesCount(Collection<Long> residentIds) {
        return noteDao.countByResident_IdInAndArchivedIsFalse(residentIds);
    }

    @Transactional(readOnly = true)
    public Page<Note> getNotEventNotesPage(Collection<Long> residentIds, Pageable pageRequest) {
        return noteDao.getAllByResident_IdInAndArchivedIsFalseAndTypeNotOrderByLastModifiedDateDescIdDesc(residentIds,
                NoteType.EVENT_NOTE, pageRequest);
    }

    @Transactional(readOnly = true)
    public Page<Note> getRelatedEventNotes(Long eventId, Pageable pageRequest) {
        return noteDao.getAllByEventIdAndArchivedIsFalseOrderByLastModifiedDateDescIdDesc(eventId, pageRequest);
    }

    @Transactional(readOnly = true)
    public Long getNotEventNotesCount(Collection<Long> residentIds) {
        return noteDao.countByResident_IdInAndArchivedIsFalseAndTypeNot(residentIds, NoteType.EVENT_NOTE);
    }

    public Note createNote(Long residentId, NoteCreateDto noteCreateDto) {
        final Date current = new Date();
        if (current.before(noteCreateDto.getLastModifiedDate())) {
            logger.warning(String.format("Adding note with date greater than current. Current time: %s, note's time: %s. " +
                    "Substituting current time.", current, noteCreateDto.getLastModifiedDate()));
            noteCreateDto.setLastModifiedDate(current);
        }

        if (StringUtils.isEmpty(noteCreateDto.getSubjective()) && StringUtils.isEmpty(noteCreateDto.getObjective()) &&
                StringUtils.isEmpty(noteCreateDto.getAssessment()) && StringUtils.isEmpty(noteCreateDto.getPlan())) {
            throw new ValidationException("At least one of [subjective, objective, assessment, plan] should be populated.");
        }

        final Note note = noteDao.saveAndFlush(noteCreateDtoLongNoteBiConverter.convert(noteCreateDto, residentId));
        logger.info("Create note DateTime: " + note.getLastModifiedDate());
        noteNotificationService.sendNoteNotifications(note);

        return note;
    }

    public Note editNote(NoteEditDto noteEditDto) {
        if (StringUtils.isEmpty(noteEditDto.getSubjective()) && StringUtils.isEmpty(noteEditDto.getObjective()) &&
                StringUtils.isEmpty(noteEditDto.getAssessment()) && StringUtils.isEmpty(noteEditDto.getPlan())) {
            throw new ValidationException("At least one of [subjective, objective, assessment, plan] should be populated.");
        }

        final Note parentNote = noteDao.getOne(noteEditDto.getId());

        if (parentNote.getArchived()) {
            throw new PhrException(PhrExceptionType.ARCHIVED_NOTE_MODIFICATION);
        }
        parentNote.setArchived(true);
        noteDao.save(parentNote);

        final Note childNote = new Note();
        childNote.setChainId(parentNote.getChainId() == null ? parentNote.getId() : parentNote.getChainId());
        childNote.setType(parentNote.getType());
        childNote.setSubType(parentNote.getSubType());
        childNote.setStatus(NoteStatus.UPDATED);
        childNote.setLastModifiedDate(new Date());
        childNote.setEmployee(parentNote.getEmployee());
        childNote.setResident(parentNote.getResident());
        childNote.setNoteResidents(Collections.singletonList(parentNote.getResident()));
        childNote.setEvent(parentNote.getEvent());
        childNote.setArchived(false);
        childNote.setAdmittanceHistory(parentNote.getAdmittanceHistory());
        childNote.setIntakeDate(parentNote.getIntakeDate());

        childNote.setSubjective(noteEditDto.getSubjective());
        childNote.setObjective(noteEditDto.getObjective());
        childNote.setAssessment(noteEditDto.getAssessment());
        childNote.setPlan(noteEditDto.getPlan());

        final Note note = noteDao.saveAndFlush(childNote);
        noteNotificationService.sendNoteNotifications(note);

        return note;
    }

    @Transactional(readOnly = true)
    public Note getNote(Long id) {
        if (id == null) {
            return null;
        }
        return noteDao.getOne(id);
    }

    public void setWasRead(Long noteId, Long userId) {
        if (!noteReadStatusDao.existsByUserIdAndNoteId(userId, noteId)) {
            NoteReadStatus noteReadStatus = new NoteReadStatus();
            noteReadStatus.setNoteId(noteId);
            noteReadStatus.setUserId(userId);
            noteReadStatusDao.save(noteReadStatus);
        }
    }

    public Map<Long, Boolean> getReadMapForUserAndNotes(Long userId, Collection<Long> noteIds) {
        if (CollectionUtils.isEmpty(noteIds)) {
            return Collections.emptyMap();
        }
        final Map<Long, Boolean> readMap = new HashMap<>(noteIds.size());
        for (Long noteId: noteIds) {
            readMap.put(noteId, false);
        }
        final List<NoteAndReadBoolean> noteAndReadBooleans = noteReadStatusDao.getWasReadByUserIdAndNoteIds(userId, noteIds);
        for(NoteAndReadBoolean noteAndReadBoolean: noteAndReadBooleans) {
            readMap.put(noteAndReadBoolean.getNoteId(), noteAndReadBoolean.getRead());
        }
        return readMap;
    }
}
