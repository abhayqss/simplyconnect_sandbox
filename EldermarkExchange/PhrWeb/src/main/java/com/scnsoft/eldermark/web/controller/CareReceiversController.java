package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.service.CareReceiverService;
import com.scnsoft.eldermark.service.NotificationPreferencesService;
import com.scnsoft.eldermark.service.VideoCallNucleusService;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.shared.web.entity.ResponseValidationErrorDto;
import com.scnsoft.eldermark.web.entity.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.concurrent.Callable;

import static com.scnsoft.eldermark.shared.utils.PaginationUtils.buildPageable;
import static com.scnsoft.eldermark.shared.utils.PaginationUtils.lazyTotalCount;


/**
 * @author phomal
 * Created on 6/2/2017.
 */
@Api(value = "PHR - Care Receivers", description = "Care Receivers information")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@Validated
@RestController
@RequestMapping("/phr/{userId:\\d+}/careReceivers")
public class CareReceiversController {

    final CareReceiverService careReceiverService;
    private final NotificationPreferencesService notificationPreferencesService;
    private final VideoCallNucleusService videoCallNucleusService;

    @Autowired
    public CareReceiversController(CareReceiverService careReceiverService,
                                   NotificationPreferencesService notificationPreferencesService,
                                   VideoCallNucleusService videoCallNucleusService) {
        this.careReceiverService = careReceiverService;
        this.notificationPreferencesService = notificationPreferencesService;
        this.videoCallNucleusService = videoCallNucleusService;
    }

    @ApiOperation(value = "Get care receiver")
    @GetMapping(value = "/{receiverId:\\d+}")
    public Response<CareReceiverDto> getCareReceiver(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "care receiver id", required = true) @PathVariable("receiverId") Long receiverId
    ) {
        final CareReceiverDto dto = careReceiverService.getCareReceiver(userId, receiverId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get care receiver by resident id and employee id")
    @GetMapping(value = "/resident/{residentId:\\d+}")
    public Response<CareReceiverDto> getCareReceiverByResidentId(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "resident id", required = true) @PathVariable("residentId") Long residentId
    ) {
        final CareReceiverDto dto = careReceiverService.getCareReceiverByResidentId(userId, residentId);
        return Response.successResponse(dto);
    }


    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Get care receivers", notes = "###Sorting rules\n" +
            " The data is sorted alphabetically by patient's name (from A to Z).")
    @GetMapping
    public Response<List<CareReceiverDto>> getCareReceivers(
            @ApiParam(value = "chat token", required = false) @RequestParam(value ="chatToken", required = false) String chatToken,
            @ApiParam(value = "user id", required = true) @PathVariable("userId") final Long userId,
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all care receivers), ≥ 1")
            @Min(1)
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        Pageable pageable = buildPageable(pageSize, page);
        final List<CareReceiverDto> dto = careReceiverService.getCareReceivers(userId, pageable, chatToken);
        final Long totalCount = lazyTotalCount(dto.size(), page, pageSize, new Callable<Long>() {
            @Override
            public Long call() {
                return careReceiverService.countCareReceivers(userId);
            }
        });
        return Response.pagedResponse(dto, totalCount);
    }

    @ApiOperation(value = "Get notification settings",
            notes = "Returns a map of notification channels to status (enabled/disabled) + a list of event types with their status (enabled/disabled). The response represents event notification settings for a specific care receiver.\n" +
                    "###Sorting rules\n" +
                    " * The event types (see `eventTypes` list) are sorted alphabetically by the description (from A to Z).\n" +
                    " * The notification channels (see `notificationChannels` map) are sorted alphabetically by the name (from A to Z).\n\n\n" +
                    " Notification settings = Notification preferences = notification channels + event types")
    @GetMapping(value = "/{receiverId:\\d+}/notificationSettings")
    public Response<NotificationSettingsDto> getCareReceiverNotificationSettings(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "care receiver id", required = true) @PathVariable("receiverId") Long receiverId
    ) {
        final NotificationSettingsDto dto = notificationPreferencesService.getNotificationSettings(userId, receiverId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Change notification settings", notes = "NOTE!  \n" +
            " * Notification channels missing in the request are treated as disabled.\n * Event types missing in the request are treated as disabled.")
    @PostMapping(value = "/{receiverId:\\d+}/notificationSettings", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<NotificationSettingsDto> editCareReceiverNotificationSettings(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "care receiver id", required = true) @PathVariable("receiverId") Long receiverId,
            @ApiParam(required = true) @RequestBody NotificationSettingsDto body
    ) {
        final NotificationSettingsDto dto = notificationPreferencesService.setNotificationSettings(userId, receiverId, body);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get Nucleus user IDs", notes = "Get Nucleus user IDs (if known) that can be used to make calls between PHR app and Nucleus device. Please note, that a single user may have multiple Nucleus user IDs, in general, one user ID per health provider.")
    @GetMapping(value = "/{receiverId:\\d+}/nucleus/info")
    public Response<List<NucleusInfoDto>> getCareReceiverNucleusInfo(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "care receiver id", required = true) @PathVariable("receiverId") Long receiverId
    ) {
        final List<NucleusInfoDto> dto = videoCallNucleusService.listNucleusInfoForCareReceiver(userId, receiverId);
        return Response.successResponse(dto);
    }

}
