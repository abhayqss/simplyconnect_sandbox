package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.beans.EventNoteFilter;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.dto.events.EventDto;
import com.scnsoft.eldermark.dto.events.EventNotificationListItemDto;
import com.scnsoft.eldermark.dto.events.EventOrNoteListItemDto;
import com.scnsoft.eldermark.dto.notes.AdmitDateDto;
import com.scnsoft.eldermark.dto.notes.NoteDto;
import com.scnsoft.eldermark.dto.notes.NoteHistoryListItemDto;
import com.scnsoft.eldermark.dto.notes.RelatedNoteListItemDto;
import com.scnsoft.eldermark.facade.EventFacade;
import com.scnsoft.eldermark.facade.EventNotificationFacade;
import com.scnsoft.eldermark.facade.NoteFacade;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/clients/{clientId}")
public class ClientEventNoteController {

    @Autowired
    private EventFacade eventFacade;

    @Autowired
    private NoteFacade noteFacade;

    @Autowired
    private EventNotificationFacade eventNotificationFacade;

    @GetMapping(value = "/events-&-notes", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<EventOrNoteListItemDto>> find(@ModelAttribute EventNoteFilter filter,
                                                       @PathVariable(value = "clientId") final Long clientId,
                                                       Pageable pageRequest) {
        filter.setClientId(clientId);
        var pageable = eventFacade.findEventsOrNotes(filter, pageRequest);
        return Response.pagedResponse(pageable.getContent(), pageable.getTotalElements());
    }

    @GetMapping(value = "/events/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<EventDto> findEventById(@PathVariable("eventId") Long eventId) {
        return Response.successResponse(eventFacade.findById(eventId));
    }

    @GetMapping(value = "/events/{eventId}/page-number", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> findEventPageNumber(@PathVariable("eventId") Long eventId,
                                              @PathVariable("clientId") Long clientId,
                                              @RequestParam(value = "pageSize") int pageSize) {
        var filter = new EventNoteFilter();

        filter.setClientId(clientId);
        filter.setExcludeEvents(false);
        filter.setExcludeNotes(true);

        return Response.successResponse(eventFacade.findPageNumber(eventId, filter, pageSize));
    }

    @GetMapping(value = "/notes/{noteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<NoteDto> findNoteById(@PathVariable("noteId") Long noteId) {
        return Response.successResponse(noteFacade.findById(noteId));
    }

    @GetMapping(value = "/notes/{noteId}/page-number", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> findNotePageNumber(@PathVariable("noteId") Long noteId,
                                             @PathVariable("clientId") Long clientId,
                                             @RequestParam(value = "pageSize") int pageSize) {
        var filter = new EventNoteFilter();

        filter.setClientId(clientId);
        filter.setExcludeEvents(true);
        filter.setExcludeNotes(false);

        return Response.successResponse(noteFacade.findPageNumber(noteId, filter, pageSize));
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

    @GetMapping(value = "/events/{eventId}/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<EventNotificationListItemDto>> findEventNotifications(@PathVariable("eventId") Long eventId, Pageable pageRequest) {
        var pageable = eventNotificationFacade.find(eventId, pageRequest);
        return Response.pagedResponse(pageable.getContent(), pageable.getTotalElements());
    }

    @RequestMapping(value = "/notes", method = {RequestMethod.POST, RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> saveNote(@PathVariable(value = "clientId") Long clientId, @Valid @RequestBody NoteDto dto) {
        dto.setClientId(clientId);
        if (dto.getId() == null) {
            return Response.successResponse(noteFacade.add(dto));
        }
        return Response.successResponse(noteFacade.edit(dto));
    }

    @PostMapping(value = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> saveEvent(@PathVariable(value = "clientId") Long clientId, @Valid @RequestBody EventDto eventDto) {
        eventDto.getClient().setId(clientId);
        return Response.successResponse(eventFacade.add(eventDto));
    }

    @GetMapping(value = "/events/can-add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canAdd(@PathVariable(value = "clientId") Long clientId) {
        return Response.successResponse(eventFacade.canAdd(clientId));
    }

    @GetMapping(value = "/notes/can-add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canAddClientNote(@PathVariable(value = "clientId") Long clientId) {
        return Response.successResponse(noteFacade.canAddClientNote(clientId));
    }

    @GetMapping(value = "/notes/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<IdentifiedNamedEntityDto>> getNoteContacts(@PathVariable Long clientId) {
        return Response.successResponse(noteFacade.findAvailableContacts(clientId));
    }

    @GetMapping(value = "/events/{eventId}/notes/can-add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canAddEventNote(@PathVariable(value = "clientId") Long clientId, @PathVariable("eventId") Long eventId) {
        return Response.successResponse(noteFacade.canAddEventNote(eventId));
    }

    @GetMapping(value = "/events-&-notes/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> count(@ModelAttribute EventNoteFilter filter, @PathVariable(value = "clientId") final Long clientId) {
        filter.setClientId(clientId);
        return Response.successResponse(eventFacade.count(filter));
    }

    @GetMapping(value = "/notes/admit-dates", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<AdmitDateDto>> findAdmitDates(@PathVariable(value = "clientId") Long clientId) {
        return Response.successResponse(noteFacade.findAdmitDates(clientId));
    }

    @GetMapping(value = "/events-&-notes/oldest/date", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> findOldestDate(@PathVariable(value = "clientId") final Long clientId) {
        return Response.successResponse(eventFacade.findOldestDateByClient(clientId));
    }

    @GetMapping(value = "/events-&-notes/newest/date", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> findNewestDate(@PathVariable(value = "clientId") final Long clientId) {
        return Response.successResponse(eventFacade.findNewestDateByClient(clientId));
    }

    @GetMapping(value = "/notes/can-view", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canViewClientNote(@PathVariable(value = "clientId") Long clientId) {
        return Response.successResponse(noteFacade.canViewList());
    }
}
