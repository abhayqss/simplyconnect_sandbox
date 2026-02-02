package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.entity.phr.AccountType;
import com.scnsoft.eldermark.service.EventsService;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventFilterDto;
import com.scnsoft.eldermark.shared.exception.ValidationExceptionFactory;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.shared.web.entity.ResponseValidationErrorDto;
import com.scnsoft.eldermark.web.entity.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import static com.scnsoft.eldermark.shared.utils.PaginationUtils.buildPageable;
import static com.scnsoft.eldermark.shared.utils.PaginationUtils.lazyTotalCount;

/**
 * @author phomal
 * Created on 5/2/2017.
 */
@Api(value = "PHR - Events", description = "Events log")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@Validated
@RestController
@RequestMapping("/phr/{userId:\\d+}/events")
public class EventsController {

    Logger logger = Logger.getLogger(EventsController.class.getName());

    @Autowired
    EventsService eventsService;

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Get a filtered list of events", notes = "###Sorting rules\nThe data is sorted by event date (from the newest to the oldest)")
    @GetMapping
    public Response<List<EventListItemDto>> getEvents(
            @Min(1)
            @ApiParam(value = "User ID", required = true) @PathVariable("userId") final Long userId,
            @Min(1)
            @ApiParam(value = "Event type id (`GET /info/eventtypes`). If neither event type, nor event type group are specified, events of \"Any\" type are returned",
                    required = false)
            @RequestParam(value = "eventTypeId", required = false) Long eventTypeId,
            @Min(1)
            @ApiParam(value = "Event type group id (`GET /info/eventgroups`). If neither event type group, nor event type are specified, events of \"Any\" type are returned",
                    required = false)
            @RequestParam(value = "eventGroupId", required = false) Long eventGroupId,
            @DateTimeFormat(pattern = "MM/dd/yyyy")
            @ApiParam(value = "From date (for example `1463270400000` or `05/15/2016`)", required = false)
            @RequestParam(value = "dateFrom", required = false) Long dateFrom,
            @DateTimeFormat(pattern = "MM/dd/yyyy")
            @ApiParam(value = "To date (for example `1494806400000` or `05/15/2017`). If not specified, defaults to current date", required = false)
            @RequestParam(value = "dateTo", required = false) Long dateTo,
            @Min(1)
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return unlimited number of events), ≥ 1")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page. The first page is 0, the second page is 1, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @ApiParam(value = "Current account type. This parameter is used when `userId` = current user id. It might be either `provider` or `consumer` at \"CTM view\" events screen or at \"More options for patient\" events screen accordingly.", required = false,
                    allowableValues = "PROVIDER, CONSUMER, NOTIFY, provider, consumer, notify", defaultValue = "consumer")
            @RequestParam(value = "accountType", required = false, defaultValue = "consumer") String type,
            @ApiParam(value = "", required = false)
            @RequestParam(value = "hasIr", required = false) Boolean hasIr
    ) {
        final AccountType.Type accountType = AccountType.Type.fromValue(type);
        final Date dateFrom1 = dateFrom == null ? null : new Date(dateFrom);
        final Date dateTo1 = dateTo == null ? null : new Date(dateTo);
        final EventFilterDto eventFilter = new EventFilterDto(eventTypeId, eventGroupId, null, dateFrom1, dateTo1, hasIr);
        final Pageable pageable = buildPageable(pageSize, page);

        final List<EventListItemDto> dto = eventsService.getEvents(userId, eventFilter, accountType, pageable);
        final Date minimumDate = eventsService.getEventsMinimumDate(userId, accountType);
        final Long totalCount = lazyTotalCount(dto.size(), page, pageSize, new Callable<Long>() {
            @Override
            public Long call() {
                return eventsService.countEvents(userId, eventFilter, accountType);
            }
        });
        return Response.pagedResponse(dto, totalCount, minimumDate);
    }

    @ApiResponses({
    	@ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
	})
	@ApiOperation(value = "Get count of events", notes = "Get count of events")
	@GetMapping("/count")
	public Response<Long> getEventCount(
        @Min(1)
        @ApiParam(value = "User ID", required = true) @PathVariable("userId") final Long userId,
        @Min(1)
        @ApiParam(value = "Event type id (`GET /info/eventtypes`). If neither event type, nor event type group are specified, events of \"Any\" type are returned",
                required = false)
        @RequestParam(value = "eventTypeId", required = false) Long eventTypeId,
        @Min(1)
        @ApiParam(value = "Event type group id (`GET /info/eventgroups`). If neither event type group, nor event type are specified, events of \"Any\" type are returned",
                required = false)
        @RequestParam(value = "eventGroupId", required = false) Long eventGroupId,
        @DateTimeFormat(pattern = "MM/dd/yyyy")
        @ApiParam(value = "From date (for example `1463270400000` or `05/15/2016`)", required = false)
        @RequestParam(value = "dateFrom", required = false) Long dateFrom,
        @DateTimeFormat(pattern = "MM/dd/yyyy")
        @ApiParam(value = "To date (for example `1494806400000` or `05/15/2017`). If not specified, defaults to current date", required = false)
        @RequestParam(value = "dateTo", required = false) Long dateTo,
        @ApiParam(value = "Current account type. This parameter is used when `userId` = current user id. It might be either `provider` or `consumer` at \"CTM view\" events screen or at \"More options for patient\" events screen accordingly.", required = false,
                allowableValues = "PROVIDER, CONSUMER, provider, consumer", defaultValue = "consumer")
        @RequestParam(value = "accountType", required = false, defaultValue = "consumer") String type,
        @ApiParam(value = "", required = false)
            @RequestParam(value = "irRelatedEvent", required = false) Boolean irRelatedEvent) {

        final AccountType.Type accountType = AccountType.Type.fromValue(type);
        final Date dateFrom1 = dateFrom == null ? null : new Date(dateFrom);
        final Date dateTo1 = dateTo == null ? null : new Date(dateTo);
        final EventFilterDto eventFilter = new EventFilterDto(eventTypeId, eventGroupId, null, dateFrom1, dateTo1,
                irRelatedEvent);

        Long totalCount = eventsService.countEvents(userId, eventFilter, accountType);

        return Response.successResponse(totalCount);

    }
    
    
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Create a new event",
            notes = "EventCreateDto is similar to EventDto, except that it contains no excess information, and required sections are marked with asterisk")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response createEvent(
            @ApiParam(value = "User ID", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "Event", required = true) @Valid @RequestBody EventCreateDto body,
            Errors errors
    ) {
        if (errors.hasErrors()) {
            throw ValidationExceptionFactory.fromBindingErrors(errors);
        }
        eventsService.createEvent(userId, body);
        return Response.successResponse();
    }

    @ApiOperation(value = "Get event description")
    @GetMapping(value = "/{eventId:\\d+}")
    public Response<EventDto> getEvent(
            @ApiParam(value = "User ID. Either ID of the current user-provider or ID of a user-consumer associated with the patient in event.", required = true)
            @PathVariable("userId") Long userId,
            @ApiParam(value = "Event ID", required = true)
            @PathVariable("eventId") Long eventId
    ) {
        final EventDto dto = eventsService.getEvent(userId, eventId);
        return Response.successResponse(dto);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Get a list of notifications for a specific event",
            notes = "###Sorting rules\nThe data is sorted alphabetically by the care team member name (from A to Z).")
    @GetMapping(value = "/{eventId:\\d+}/sentNotifications")
    public Response<List<EventNotificationDto>> getEventNotifications(
            @ApiParam(value = "User ID", required = true) @PathVariable("userId") final Long userId,
            @Min(1)
            @ApiParam(value = "Event ID", required = true) @PathVariable("eventId") final Long eventId,
            @Min(1)
            @ApiParam(value = "Maximum results to appear in event notifications list (if not specified, system will return all event notifications), ≥ 1")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Event notifications page. The first page is 0, the second page is 1, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        Sort sort = new Sort(Sort.Direction.ASC, "contactName");
        final Pageable pageable = new PageRequest(page, pageSize == null ? Integer.MAX_VALUE : pageSize, sort);
        final List<EventNotificationDto> dtos = eventsService.getEventNotifications(userId, eventId, pageable);
        final Long count = lazyTotalCount(dtos.size(), page, pageSize, new Callable<Long>() {
            @Override
            public Long call() {
                return eventsService.getEventNotificationsCount(userId, eventId);
            }
        });
        return Response.pagedResponse(dtos, count);
    }

}
