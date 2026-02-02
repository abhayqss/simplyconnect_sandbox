package com.scnsoft.eldermark.facade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.scnsoft.eldermark.dto.events.EventNotificationListItemDto;

public interface EventNotificationFacade {

    Page<EventNotificationListItemDto> find(Long eventId, Pageable pageable);
}
