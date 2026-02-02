package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.events.EventNotificationListItemDto;
import com.scnsoft.eldermark.entity.event.GroupedEventNotification;
import com.scnsoft.eldermark.service.EventNotificationService;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EventNotificationFacadeImpl implements EventNotificationFacade {

    @Autowired
    private EventNotificationService eventNotificationService;

    @Autowired
    private ListAndItemConverter<GroupedEventNotification, EventNotificationListItemDto> eventNotificationsDtoListAndItemConverter;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@eventSecurityService.canView(#eventId)")
    public Page<EventNotificationListItemDto> find(@P("eventId") Long eventId, Pageable pageable) {
        return eventNotificationService.find(eventId, PaginationUtils.applyEntitySort(pageable, EventNotificationListItemDto.class))
                .map(eventNotificationsDtoListAndItemConverter::convert);
    }

}
