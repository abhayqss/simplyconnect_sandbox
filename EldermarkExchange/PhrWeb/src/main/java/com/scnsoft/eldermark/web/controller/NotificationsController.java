package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.entity.phr.PushNotificationRegistration;
import com.scnsoft.eldermark.service.NotificationPreferencesService;
import com.scnsoft.eldermark.service.PushNotificationsService;
import com.scnsoft.eldermark.services.pushnotifications.PushNotificationType;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.web.entity.NotificationSettingsDto;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;

/**
 * @author phomal
 * Created on 6/2/2017.
 */
@Api(value = "PHR - Notifications", description = "Notification settings and Push Notifications")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@RestController
@RequestMapping("/phr/{userId:\\d+}/notifications")
public class NotificationsController {

    @Autowired
    PushNotificationsService pushNotificationsService;

    @Autowired
    NotificationPreferencesService notificationPreferencesService;

    @ApiOperation(value = "Register for push notifications",
            notes = "Submit a push notifications token after registration in Android GCM or iOS APNS.")
    @PostMapping(value = "/registerpush")
    public Response<Boolean> registerPushNotifications(
            @ApiParam(value = "user id", required = true)
            @PathVariable("userId") Long userId,
            @ApiParam(value = "Push notification token", required = true)
            @RequestParam(value = "token", required = true) String token,
            @ApiParam(value = "A service used for registration", required = true, allowableValues = "FCM, GCM, APNS, APNS_VOIP")
            @RequestParam(value = "service", required = true) PushNotificationRegistration.ServiceProvider service
    ) {
        final Boolean dto = pushNotificationsService.register(userId, token, service);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Send a push notification", notes = "Submit a push notification for debug purposes. Debug notification id = 0.")
    @PostMapping(value = "/sendpush")
    public Response sendPushNotifications(
            @ApiParam(value = "user id", required = true)
            @PathVariable("userId") Long userId,
            @ApiParam(value = "Title", required = true)
            @RequestParam(value = "title", required = true) String title,
            @ApiParam(value = "Text", required = true)
            @RequestParam(value = "text", required = true) String text,
            @ApiParam(value = "Data (payload item name)", required = false)
            @RequestParam(value = "dataName", required = false) String dataName,
            @ApiParam(value = "Data (payload item value)", required = false)
            @RequestParam(value = "dataValue", required = false) String dataValue            
    ) {
        pushNotificationsService.send(userId, null,title, text, dataName, dataValue, null, PushNotificationType.NEW_EVENT);
        return Response.successResponse();
    }
    
    @ApiOperation(value = "Send a push notification", notes = "Send push notification for Chat API.")
    @PostMapping(value = "/sendpushtochat/{chatUserId:\\d+}")
    public Response sendPushNotificationsToChat(
            @ApiParam(value = "user id", required = true)
            @PathVariable("userId") Long userId,
            @ApiParam(value = "chat user id", required = true)
            @PathVariable("chatUserId") Long chatUserId,
            @ApiParam(value = "Title", required = false)
            @RequestParam(value = "title", required = false) String title,
            @ApiParam(value = "Text", required = false)
            @RequestParam(value = "text", required = false) String text,            
            @ApiParam(value = "Data (payload in string json)", required = false)
            @RequestParam(value = "chatPayload", required = false) String chatPayload
    ) {
        pushNotificationsService.send(userId, chatUserId, title, text, null, null, chatPayload, PushNotificationType.CHAT_NOTIFICATION);
        return Response.successResponse();
    }

    @ApiOperation(value = "Get notification settings",
            notes = "Returns a map of notification channels to status (enabled/disabled) + a list of event types with their status (enabled/disabled).\n" +
                    "### Sorting rules\n" +
                    " * The event types (see `eventTypes` list) are sorted alphabetically by the description (from A to Z).\n" +
                    " * The notification channels (see `notificationChannels` map) are sorted alphabetically by the name (from A to Z).\n\n\n" +
                    " Notification settings = Notification preferences = notification channels + event types")
    @GetMapping(value = "/settings")
    public Response<NotificationSettingsDto> getNotificationSettings(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId
    ) {
        final NotificationSettingsDto dto = notificationPreferencesService.getNotificationSettings(userId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Change notification settings")
    @PostMapping(value = "/settings", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<NotificationSettingsDto> editNotificationSettings(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(required = true) @RequestBody NotificationSettingsDto body
    ) {
        final NotificationSettingsDto dto = notificationPreferencesService.setNotificationSettings(userId, body);
        return Response.successResponse(dto);
    }

}
