package com.scnsoft.eldermark.facade.impl;

import com.scnsoft.eldermark.entity.Event;
import com.scnsoft.eldermark.entity.Note;
import com.scnsoft.eldermark.entity.NoteType;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.phr.AccountType;
import com.scnsoft.eldermark.facade.BasePhrFacade;
import com.scnsoft.eldermark.facade.NotesFacade;
import com.scnsoft.eldermark.service.EventsService;
import com.scnsoft.eldermark.service.NoteService;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.web.entity.notes.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Component
public class NoteFacadeImpl extends BasePhrFacade implements NotesFacade {

    @Autowired
    private NoteService noteService;

    @Autowired
    private Converter<Note, NoteDetailsDto> noteDetailsDtoConverter;

    @Autowired
    private Converter<Note, NoteModifiedDto> noteNoteModifiedDtoConverterProvider;

    @Autowired
    private EventsService eventsService;

    @Override
    @Transactional(readOnly = true)
    public Page<NoteListItemDto> getNotesPageForUser(Long userId, AccountType.Type accountType, Pageable pageRequest) {
        noteService.canViewNoteOrThrow();
        final Page<Note> notesPage;
        switch (accountType) {
            case CONSUMER:
                notesPage = getNotesPageForConsumer(userId, pageRequest);
                break;
            case PROVIDER:
                notesPage = getNotesPageForProvider(pageRequest);
                break;
            default:
                notesPage = PaginationUtils.buildEmptyPage();
                break;
        }
        return noteService.convertToListItemPage(notesPage);
    }

    private Page<Note> getNotesPageForConsumer(Long userId, Pageable pageRequest) {
        if (currentUserHasAccessRightToUser(userId, AccessRight.Code.EVENT_NOTIFICATIONS)) {
            return noteService.getNotesPage(getUserResidentIds(userId), pageRequest);
        }
        return noteService.getNotEventNotesPage(getUserResidentIds(userId), pageRequest);
    }

    private Page<Note> getNotesPageForProvider(Pageable pageRequest) {
        final List<Long> patientNoteResidents = getCareTeamSecurityUtils().getCareTeamReceiversAndTheirMergesForCurrentUser(AccessRight.Code.MY_PHR);
        final List<Long> eventNoteResidents = getCareTeamSecurityUtils().getCareTeamReceiversAndTheirMergesForCurrentUser(AccessRight.Code.EVENT_NOTIFICATIONS);
        //event notes will be shown if there is both access to events and personal health record
        eventNoteResidents.retainAll(patientNoteResidents);

        return noteService.getNotesPage(patientNoteResidents, eventNoteResidents, pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotesCountForUser(Long userId) {
        noteService.canViewNoteOrThrow();
        if (currentUserHasAccessRightToUser(userId, AccessRight.Code.EVENT_NOTIFICATIONS)) {
            return noteService.getNotesCount(getUserResidentIds(userId));
        }
        return noteService.getNotEventNotesCount(getUserResidentIds(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoteListItemDto> getNotesPageForReceiver(Long receiverId, Pageable pageRequest) {
        noteService.canViewNoteOrThrow();
        if (currentUserHasAccessRightToReceiver(receiverId, AccessRight.Code.EVENT_NOTIFICATIONS)) {
            return noteService.convertToListItemPage(noteService.getNotesPage(getReceiverResidentIds(receiverId), pageRequest));
        }
        return noteService.convertToListItemPage(noteService.getNotEventNotesPage(getReceiverResidentIds(receiverId), pageRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotesCountForReceiver(Long receiverId) {
        noteService.canViewNoteOrThrow();
        if (currentUserHasAccessRightToReceiver(receiverId, AccessRight.Code.EVENT_NOTIFICATIONS)) {
            return noteService.getNotesCount(getReceiverResidentIds(receiverId));
        }
        return noteService.getNotEventNotesCount(getReceiverResidentIds(receiverId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoteListItemDto> getEventRelatedNotes(Long eventId, Pageable pageRequest) {
        noteService.canViewNoteOrThrow();
        canAccessEventOrThrow(eventId);
        return noteService.convertToListItemPage(noteService.getRelatedEventNotes(eventId, pageRequest));
    }

    @Override
    public NoteModifiedDto createNoteForUser(Long userId, NoteCreateDto noteCreateDto) {
        noteService.canAddNoteOrThrow();
        final Note note;
        if (noteCreateDto.getEventId() != null) {
            note = createNoteForEvent(noteCreateDto.getEventId(), noteCreateDto);
        } else {
            note = noteService.createNote(getUserMainResidentId(userId), noteCreateDto);
        }
        return noteNoteModifiedDtoConverterProvider.convert(note);
    }

    @Override
    public NoteModifiedDto createNoteForReceiver(Long receiverId, NoteCreateDto noteCreateDto) {
        noteService.canAddNoteOrThrow();
        final Note note;
        if (noteCreateDto.getEventId() != null) {
            note = createNoteForEvent(noteCreateDto.getEventId(), noteCreateDto);
        } else {
            note = noteService.createNote(getReceiverMainResidentId(receiverId), noteCreateDto);
        }
        return noteNoteModifiedDtoConverterProvider.convert(note);
    }

    private Note createNoteForEvent(Long eventId, NoteCreateDto noteCreateDto) {
        final Event event = canAccessEventOrThrow(eventId);
        return noteService.createNote(event.getResident().getId(), noteCreateDto);
    }

    private Event canAccessEventOrThrow(Long eventId) {
        final Event event = eventsService.getAvailableEvent(eventId);
        if (event == null || !hasAssociation(event.getResident().getId(), AccessRight.Code.EVENT_NOTIFICATIONS)) {
            throw new PhrException(PhrExceptionType.EVENT_NOT_FOUND);
        }
        return event;
    }

    @Override
    public NoteModifiedDto editNote(NoteEditDto noteEditDto) {
        final Note note = noteService.getNote(noteEditDto.getId());
        noteService.canEditNote(note);
        if (note.getType() == NoteType.EVENT_NOTE) {
            validateAssociation(note.getResident().getId(), AccessRight.Code.EVENT_NOTIFICATIONS);
        }
        final Note editedNote = noteService.editNote(noteEditDto);
        return noteNoteModifiedDtoConverterProvider.convert(editedNote);
    }

    @Override
    @Transactional(readOnly = true)
    public NoteDetailsDto getNoteDetails(Long noteId) {
        final Note note = noteService.getNote(noteId);       
        canViewNoteDetailsOrThrow(note);
        noteService.setWasRead(noteId, getCareTeamSecurityUtils().getCurrentUser().getId());
        return noteDetailsDtoConverter.convert(note);
    }

    private void canViewNoteDetailsOrThrow(Note note) {
        noteService.canViewNoteOrThrow();
        if (note.getType().equals(NoteType.EVENT_NOTE)) {
            validateAssociation(note.getResident().getId(), AccessRight.Code.EVENT_NOTIFICATIONS);
        } else {
            validateAssociation(note.getResident().getId());
        }
    }
}