package com.scnsoft.eldermark.service.notification.sender;

import com.scnsoft.eldermark.entity.NotificationType;

public interface EventNotificationSender {

    void send(Long eventNotificationId);

    NotificationType supportedNotificationType();
}
