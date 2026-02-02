package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.EventNoteFilter;
import com.scnsoft.eldermark.dto.events.EventDashboardListItemDto;
import com.scnsoft.eldermark.dto.events.EventDto;
import com.scnsoft.eldermark.dto.events.EventOrNoteListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventFacade {

    Long add(EventDto eventDto);

    Page<EventOrNoteListItemDto> findEventsOrNotes(EventNoteFilter eventNoteFilter, Pageable pageable);

    List<EventDashboardListItemDto> findEventsForDashboard(Long clientId, Integer limit);

    EventDto findById(Long eventId);

    Long findPageNumber(Long eventId, EventNoteFilter eventNoteFilter, int pageSize);

    Long count(EventNoteFilter eventNoteFilter);

    boolean canAdd(Long clientId);

    Long findOldestDateByClient(Long clientId);

    Long findOldestDateByOrganization(Long organizationId);

    Long findNewestDateByClient(Long clientId);

    Long findNewestDateByOrganization(Long organizationId);
}
