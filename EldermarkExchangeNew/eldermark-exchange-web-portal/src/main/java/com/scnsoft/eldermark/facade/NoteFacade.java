package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.EventNoteFilter;
import com.scnsoft.eldermark.beans.NoteStatisticsFilterDto;
import com.scnsoft.eldermark.dto.notes.*;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoteFacade {

    NoteDto findById(Long id);

    Long findPageNumber(Long eventId, EventNoteFilter eventNoteFilter, int pageSize);

    List<NoteDashboardListItemDto> findNotesForDashboard(Long clientId, Integer limit);

    Long add(NoteDto noteDto);

    Long edit(NoteDto noteDto);

    Page<NoteHistoryListItemDto> history(Long noteId, Pageable pageRequest);

    Page<RelatedNoteListItemDto> findRelatedNotes(Long eventId, Pageable pageRequest);

    List<EntityStatisticsDto> getEncounterNoteCount(NoteStatisticsFilterDto filter);

    List<AdmitDateDto> findAdmitDates(Long clientId);

    List<IdentifiedNamedEntityDto> findAvailableContacts(Long clientId);

    List<IdentifiedNamedEntityDto> findAvailableContactsForGroupNote(Long organizationId);

    boolean canAddClientNote(Long clientId);

    boolean canAddEventNote(Long eventId);

    boolean canViewList();
}
