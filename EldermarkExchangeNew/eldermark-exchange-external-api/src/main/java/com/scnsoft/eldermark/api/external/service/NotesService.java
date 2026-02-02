package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.NoteListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotesService {

    Page<NoteListItemDto> getListNotes(Long residentId, Pageable pageRequest);

    Long getListNotesCount(Long residentId);
}
