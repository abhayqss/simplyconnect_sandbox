package com.scnsoft.eldermark.service.notification.factory;

import com.scnsoft.eldermark.entity.EventNotification;
import com.scnsoft.eldermark.entity.NotificationPreferences;
import com.scnsoft.eldermark.entity.NotificationType;
import com.scnsoft.eldermark.entity.event.Event;

import java.util.Optional;

public interface EventNotificationFactory {

    Optional<EventNotification> createNotification(Event event, NotificationPreferences np);

    NotificationType supportedNotificationType();

}
