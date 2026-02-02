package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.mobile.dto.notification.PushNotificationTokenRegistrationDto;

public interface NotificationFacade {

    boolean register(PushNotificationTokenRegistrationDto pushNotificationTokenRegistrationDto);

    boolean sendDebug();
}
