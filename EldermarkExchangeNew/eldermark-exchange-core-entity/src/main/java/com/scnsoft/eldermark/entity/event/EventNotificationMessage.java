package com.scnsoft.eldermark.entity.event;

import com.scnsoft.eldermark.entity.NotificationType;

import java.time.Instant;

public interface EventNotificationMessage {
    NotificationType getNotificationType();
    Instant getSentDatetime();
    String getDestination();
}
