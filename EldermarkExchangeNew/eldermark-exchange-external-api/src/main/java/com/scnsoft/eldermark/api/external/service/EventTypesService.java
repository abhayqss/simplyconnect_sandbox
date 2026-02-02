package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.shared.web.dto.EventTypeDto;
import com.scnsoft.eldermark.api.shared.web.dto.EventTypeGroupDto;
import com.scnsoft.eldermark.entity.event.EventType;

import java.util.List;

public interface EventTypesService {

    List<EventTypeDto> getEventTypes();

    List<EventTypeGroupDto> getEventGroups();

    EventType findById(Long eventTypeId);
}
