package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.EventCreateDto;
import com.scnsoft.eldermark.api.shared.dto.events.EventDto;
import com.scnsoft.eldermark.api.shared.dto.events.EventFilterDto;
import com.scnsoft.eldermark.api.shared.dto.events.EventListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventsService {

    Long create(Long residentId, EventCreateDto eventCreateDto);

    EventDto get(Long residentId, Long eventId);

    Page<EventListItemDto> list(EventFilterDto eventFilter, Pageable pageable);
}
