package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.mobile.dto.notification.PushNotificationTokenRegistrationDto;
import com.scnsoft.eldermark.mobile.facade.NotificationFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/push-notifications")
public class PushNotificationController {

    @Autowired
    private NotificationFacade notificationFacade;

    @PostMapping("/register")
    public Response<Boolean> register(@RequestBody PushNotificationTokenRegistrationDto pushNotificationTokenRegistrationDto) {
        return Response.successResponse(notificationFacade.register(pushNotificationTokenRegistrationDto));
    }

    @PostMapping("/debug")
    public Response<Boolean> sendDebug() {
        return Response.successResponse(notificationFacade.sendDebug());
    }
}
