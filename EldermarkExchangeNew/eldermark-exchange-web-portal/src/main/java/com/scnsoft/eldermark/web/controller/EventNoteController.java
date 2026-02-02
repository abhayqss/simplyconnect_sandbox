package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.annotations.SwaggerDoc;
import com.scnsoft.eldermark.beans.ClientAccessType;
import com.scnsoft.eldermark.beans.EventNoteFilter;
import com.scnsoft.eldermark.dto.ClientNameDto;
import com.scnsoft.eldermark.dto.events.EventDto;
import com.scnsoft.eldermark.dto.events.EventNotificationListItemDto;
import com.scnsoft.eldermark.dto.events.EventOrNoteListItemDto;
import com.scnsoft.eldermark.dto.filter.ClientFilterDto;
import com.scnsoft.eldermark.dto.notes.NoteDto;
import com.scnsoft.eldermark.dto.notes.NoteHistoryListItemDto;
import com.scnsoft.eldermark.dto.notes.RelatedNoteListItemDto;
import com.scnsoft.eldermark.facade.ClientFacade;
import com.scnsoft.eldermark.facade.EventFacade;
import com.scnsoft.eldermark.facade.EventNotificationFacade;
import com.scnsoft.eldermark.facade.NoteFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/organizations/{organizationId}")
public class EventNoteController {

    @Autowired
    private EventFacade eventFacade;

    @Autowired
    private NoteFacade noteFacade;

    @Autowired
    private EventNotificationFacade eventNotificationFacade;

    @Autowired
    private ClientFacade clientFacade;

    @SwaggerDoc
    @GetMapping(value = "/events-&-notes", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<EventOrNoteListItemDto>> find(@ModelAttribute EventNoteFilter filter,
                                                       @PathVariable(value = "organizationId") final Long organizationId,
                                                       Pageable pageRequest) {
        filter.setOrganizationId(organizationId);
        var pageable = eventFacade.findEventsOrNotes(filter, pageRequest);
        return Response.pagedResponse(pageable);
    }
    @SwaggerDoc
    @GetMapping(value = "/events/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<EventDto> findEventById(@PathVariable("eventId") Long eventId) {
        return Response.successResponse(eventFacade.findById(eventId));
    }

    @GetMapping(value = "/events/{eventId}/page-number", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> findEventPageNumber(@PathVariable("eventId") Long eventId,
                                              @PathVariable("organizationId") Long organizationId,
                                              @RequestParam(value = "pageSize") int pageSize) {
        var filter = new EventNoteFilter();

        filter.setOrganizationId(organizationId);
        filter.setExcludeEvents(true);
        filter.setExcludeNotes(false);

        return Response.successResponse(noteFacade.findPageNumber(eventId, filter, pageSize));
    }

    @GetMapping(value = "/notes/{noteId}/page-number", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> findNotePageNumber(@PathVariable("noteId") Long eventId,
                                              @PathVariable("organizationId") Long organizationId,
                                              @RequestParam(value = "pageSize") int pageSize) {
        var filter = new EventNoteFilter();

        filter.setOrganizationId(organizationId);
        filter.setExcludeEvents(false);
        filter.setExcludeNotes(true);

        return Response.successResponse(eventFacade.findPageNumber(eventId, filter, pageSize));
    }

    @GetMapping(value = "/notes/{noteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<NoteDto> findNoteById(@PathVariable("noteId") Long noteId) {
        return Response.successResponse(noteFacade.findById(noteId));
    }

    @GetMapping(value = "/notes/{noteId}/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<NoteHistoryListItemDto>> findNoteHistory(@PathVariable("noteId") Long noteId, Pageable pageRequest) {
        var page = noteFacade.history(noteId, pageRequest);
        return Response.pagedResponse(page);
    }

    @GetMapping(value = "/events/{eventId}/notes", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<RelatedNoteListItemDto>> findEventNotes(@PathVariable("eventId") Long eventId, Pageable pageRequest) {
        var page = noteFacade.findRelatedNotes(eventId, pageRequest);
        return Response.pagedResponse(page);
    }

    @GetMapping(value = "events/{eventId}/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<EventNotificationListItemDto>> findEventNotifications(@PathVariable("eventId") Long eventId, Pageable pageRequest) {
        var pageable = eventNotificationFacade.find(eventId, pageRequest);
        return Response.pagedResponse(pageable.getContent(), pageable.getTotalElements());
    }

    @GetMapping(value = "/events-&-notes/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> count(@ModelAttribute EventNoteFilter filter, @PathVariable(value = "organizationId") final Long organizationId) {
        filter.setOrganizationId(organizationId);
        return Response.successResponse(eventFacade.count(filter));
    }

    @GetMapping(value = "/events-&-notes/oldest/date", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> findOldestDate(@PathVariable(value = "organizationId") final Long organizationId) {
        return Response.successResponse(eventFacade.findOldestDateByOrganization(organizationId));
    }

    @GetMapping(value = "/events-&-notes/newest/date", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> findNewestDate(@PathVariable(value = "organizationId") final Long organizationId) {
        return Response.successResponse(eventFacade.findNewestDateByOrganization(organizationId));
    }

    @SwaggerDoc
    @RequestMapping(value = "/notes", method = {RequestMethod.POST, RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> saveNote(@Valid @RequestBody NoteDto dto) {
        if (dto.getId() == null) {
            return Response.successResponse(noteFacade.add(dto));
        }
        return Response.successResponse(noteFacade.edit(dto));
    }

    @GetMapping(value = "/events/{eventId}/notes/can-add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canAddEventNote(@PathVariable("eventId") Long eventId) {
        return Response.successResponse(noteFacade.canAddEventNote(eventId));
    }

    @GetMapping(value = "/events-&-notes/clients", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ClientNameDto>> getClientNames(@ModelAttribute ClientFilterDto filter) {
        if (filter.getClientAccessType() == null) filter.setClientAccessType(ClientAccessType.DETAILS);
        return Response.successResponse(clientFacade.findNamesWithoutRecordSearchPermissions(filter));
    }

    @GetMapping(value = "/notes/can-view", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canViewClientNote( @PathVariable("organizationId") Long organizationId) {
        return Response.successResponse(noteFacade.canViewList());
    }

    @GetMapping(value = "/notes/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<IdentifiedNamedEntityDto>> getNoteContacts(@PathVariable Long organizationId) {
        return Response.successResponse(noteFacade.findAvailableContactsForGroupNote(organizationId));
    }
}
