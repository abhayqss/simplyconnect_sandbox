package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.EventNoteFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.events.EventDashboardListItemDto;
import com.scnsoft.eldermark.dto.events.EventDto;
import com.scnsoft.eldermark.dto.events.EventOrNoteListItemDto;
import com.scnsoft.eldermark.entity.EventNote;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.EventDashboardItem;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.EventNoteService;
import com.scnsoft.eldermark.service.EventService;
import com.scnsoft.eldermark.service.security.EventSecurityService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.util.PermissionFilterUtils;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class EventFacadeImpl implements EventFacade {

    @Autowired
    private EventService eventService;

    @Autowired
    private Converter<Event, EventDto> eventDtoConverter;

    @Autowired
    private ListAndItemConverter<EventNote, EventOrNoteListItemDto> eventNoteConverter;

    @Autowired
    private ListAndItemConverter<EventDashboardItem, EventDashboardListItemDto> eventDashboardListItemDtoConverter;

    @Autowired
    private EventNoteService eventNoteService;

    @Autowired
    private Converter<EventDto, Event> eventEntityConverter;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private EventSecurityService eventSecurityService;

    @Autowired
    private ClientService clientService;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@eventSecurityService.canView(#eventId)")
    public EventDto findById(@P("eventId") Long eventId) {
        var eventEntity = eventService.findById(eventId);
        return eventDtoConverter.convert(eventEntity);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@eventSecurityService.canViewList()")
    public Long findPageNumber(Long eventId, EventNoteFilter eventNoteFilter, int pageSize) {
        var sort = PaginationUtils.findDefaultEntitySort(EventOrNoteListItemDto.class).orElseThrow();
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        return eventNoteService.findEventPageNumber(eventId, eventNoteFilter, permissionFilter, pageSize, sort);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@noteSecurityService.canViewList() or @eventSecurityService.canViewList()")
    public Page<EventOrNoteListItemDto> findEventsOrNotes(EventNoteFilter eventNoteFilter, Pageable pageable) {
        var permissionFilter = removeRecordSearchPermissionsIfNeeded(eventNoteFilter, permissionFilterService.createPermissionFilterForCurrentUser());
        return eventNoteService.find(eventNoteFilter, permissionFilter, PaginationUtils.applyEntitySort(pageable, EventOrNoteListItemDto.class)).map(eventNoteConverter::convert);
    }

    private PermissionFilter removeRecordSearchPermissionsIfNeeded(EventNoteFilter eventNoteFilter, PermissionFilter permissionFilter) {
        if (eventNoteFilter.getClientId() == null) {
            return PermissionFilterUtils
                    .excludePermissions(permissionFilter,
                            Permission.EVENT_VIEW_MERGED_IF_CLIENT_FOUND_IN_RECORD_SEARCH,
                            Permission.NOTE_VIEW_MERGED_IF_CLIENT_FOUND_IN_RECORD_SEARCH,
                            Permission.CLIENT_VIEW_IF_CLIENT_FOUND_IN_RECORD_SEARCH);
        }
        return permissionFilter;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@eventSecurityService.canViewList()")
    public List<EventDashboardListItemDto> findEventsForDashboard(Long clientId, Integer limit) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var sort = PaginationUtils.findDefaultEntitySort(EventDashboardListItemDto.class).orElse(null);
        var events = eventService.find(clientId, permissionFilter, limit, sort);
        return eventDashboardListItemDtoConverter.convertList(events);
    }

    @Override
    @Transactional
    @PreAuthorize("@eventSecurityService.canAdd(#eventDto)")
    public Long add(@P("eventDto") EventDto eventDto) {
        if (eventDto.getId() != null) {
            throw new InternalServerException(InternalServerExceptionType.EVENT_NOT_EDITABLE);
        }
        clientService.validateActive(eventDto.getClientId());
        return eventService.save(eventEntityConverter.convert(eventDto)).getId();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@noteSecurityService.canViewList() or @eventSecurityService.canViewList()")
    public Long count(EventNoteFilter eventNoteFilter) {
        var permissionFilter = removeRecordSearchPermissionsIfNeeded(eventNoteFilter, permissionFilterService.createPermissionFilterForCurrentUser());
        return eventNoteService.count(eventNoteFilter, permissionFilter);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAdd(Long clientId) {
        return eventSecurityService.canAddToClient(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@noteSecurityService.canViewList() or @eventSecurityService.canViewList()")
    public Long findOldestDateByClient(@P("clientId") Long clientId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var eventNoteFilter = new EventNoteFilter();
        eventNoteFilter.setClientId(clientId);

        return eventNoteService.findOldestDate(eventNoteFilter, permissionFilter).map(Instant::toEpochMilli).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@noteSecurityService.canViewList() or @eventSecurityService.canViewList()")
    public Long findOldestDateByOrganization(@P("organizationId") Long organizationId) {
        var eventNoteFilter = new EventNoteFilter();
        eventNoteFilter.setOrganizationId(organizationId);
        var permissionFilter = removeRecordSearchPermissionsIfNeeded(eventNoteFilter, permissionFilterService.createPermissionFilterForCurrentUser());
        return eventNoteService.findOldestDate(eventNoteFilter, permissionFilter).map(Instant::toEpochMilli).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@eventSecurityService.canViewList()")
    public Long findNewestDateByClient(@P("clientId") Long clientId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var eventNoteFilter = new EventNoteFilter();
        eventNoteFilter.setClientId(clientId);

        return eventNoteService.findNewestDate(eventNoteFilter, permissionFilter).map(Instant::toEpochMilli).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@eventSecurityService.canViewList()")
    public Long findNewestDateByOrganization(@P("organizationId") Long organizationId) {
        var eventNoteFilter = new EventNoteFilter();
        eventNoteFilter.setOrganizationId(organizationId);
        var permissionFilter = removeRecordSearchPermissionsIfNeeded(eventNoteFilter, permissionFilterService.createPermissionFilterForCurrentUser());
        return eventNoteService.findNewestDate(eventNoteFilter, permissionFilter).map(Instant::toEpochMilli).orElse(null);
    }
}
