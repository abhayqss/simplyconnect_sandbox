package com.scnsoft.eldermark.api.external.web.controller;

import com.scnsoft.eldermark.api.external.service.EventsService;
import com.scnsoft.eldermark.api.shared.dto.events.EventDto;
import com.scnsoft.eldermark.api.shared.dto.events.EventFilterDto;
import com.scnsoft.eldermark.api.shared.dto.events.EventListItemDto;
import com.scnsoft.eldermark.api.shared.exception.ValidationExceptionFactory;
import com.scnsoft.eldermark.api.shared.web.dto.Response;
import com.scnsoft.eldermark.api.shared.web.dto.ResponseErrorDto;
import com.scnsoft.eldermark.api.shared.web.dto.ResponseValidationErrorDto;
import com.scnsoft.eldermark.api.external.web.dto.EventCreateDto;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static com.scnsoft.eldermark.api.shared.utils.PaginationUtils.buildPageable;

@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T14:21:48.776+03:00")
@Api(value = "Events", description = "Events Log")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "Not Found", response = ResponseErrorDto.class)
})
@Validated
@RestController
@RequestMapping("/residents/{residentId:\\d+}/events")
public class EventsController {

    final Logger logger = Logger.getLogger(EventsController.class.getName());

    private final EventsService eventsService;

    @Autowired
    public EventsController(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Create a new event", notes = "Accepts `EventCreateDto` in request body, that is similar to `EventDto`. Required sections are marked with asterisk (see Parameters > body > Model). The incoming data is validated and event is not created in case if validation fails (missing mandatory properties, strings exceeding a maximum length limit, ...)  You'll get 400 - Bad Request response in this case and you should pay attention to validation error messages returned in the response. <h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ, EVENT_CREATE</pre>")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> createEvent(
            @Min(1)
            @ApiParam(value = "resident id", required = true)
            @PathVariable("residentId") Long residentId,
            @Valid
            @ApiParam(value = "Event", required = true)
            @RequestBody EventCreateDto body,
            Errors errors
    ) {
        if (errors.hasErrors()) {
            throw ValidationExceptionFactory.fromBindingErrors(errors);
        }
        Long id = eventsService.create(residentId, body);
        return Response.successResponse(id);
    }

    @ApiOperation(value = "Get event description. This action doesn't affect \"unread\" event status.", notes = "<h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ</pre>", tags = {"not-implemented"})
    @GetMapping(value = "/{eventId:\\d+}")
    public Response<EventDto> getEvent(
            @Min(1)
            @ApiParam(value = "resident id", required = true)
            @PathVariable("residentId") Long residentId,
            @Min(1)
            @ApiParam(value = "Event ID", required = true)
            @PathVariable("eventId") Long eventId
    ) {
        final EventDto dto = eventsService.get(residentId, eventId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get a filtered list of events", notes = "<h3>Sorting rules</h3><p>The data is sorted by event date (from the newest to the oldest)</p><h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ</pre>", tags = {"not-implemented"})
    @GetMapping
    public Response<List<EventListItemDto>> getEvents(
            @Min(1)
            @ApiParam(value = "resident id", required = true)
            @PathVariable("residentId") Long residentId,
            @Min(1)
            @ApiParam(value = "Event type id (`GET /info/eventtypes`). If neither event type, nor event type group are specified, events of \"Any\" type are returned")
            @RequestParam(value = "eventTypeId", required = false) Long eventTypeId,
            @Min(1)
            @ApiParam(value = "Event type group id (`GET /info/eventgroups`). If neither event type group, nor event type are specified, events of \"Any\" type are returned")
            @RequestParam(value = "eventGroupId", required = false) Long eventGroupId,
            @ApiParam(value = "From date (for example `1463270400000` or `05/15/2016`)")
            @RequestParam(value = "dateFrom", required = false) String dateFrom,
            @ApiParam(value = "To date (for example `1494806400000` or `05/15/2017`). If not specified, defaults to current date")
            @RequestParam(value = "dateTo", required = false) String dateTo,
            @Min(1)
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return unlimited number of events), ≥ 1")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        final Date dateFrom1 = dateFrom == null ? null : new Date(dateFrom);
        final Date dateTo1 = dateTo == null ? null : new Date(dateTo);
        final EventFilterDto eventFilter = new EventFilterDto(eventTypeId, eventGroupId, residentId, dateFrom1, dateTo1, null);
        final Pageable pageable = buildPageable(pageSize, page);

        final Page<EventListItemDto> events = eventsService.list(eventFilter, pageable);
        return Response.pagedResponse(events);
    }

}
