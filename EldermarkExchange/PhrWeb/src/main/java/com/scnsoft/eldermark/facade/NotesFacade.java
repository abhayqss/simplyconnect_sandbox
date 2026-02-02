package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.entity.phr.AccountType;
import com.scnsoft.eldermark.web.entity.notes.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotesFacade {

    Page<NoteListItemDto> getNotesPageForUser(Long userId, AccountType.Type accountType, Pageable pageRequest);

    Long getNotesCountForUser(Long userId);

    Page<NoteListItemDto> getNotesPageForReceiver(Long receiverId, Pageable pageRequest);

    Long getNotesCountForReceiver(Long receiverId);

    Page<NoteListItemDto> getEventRelatedNotes(Long eventId, Pageable pageRequest);

    NoteModifiedDto createNoteForUser(Long userId, NoteCreateDto noteCreateDto);

    NoteModifiedDto createNoteForReceiver(Long receiverId, NoteCreateDto noteCreateDto);

    NoteModifiedDto editNote(NoteEditDto noteEditDto);

    NoteDetailsDto getNoteDetails(Long noteId);
}
