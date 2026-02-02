package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.entity.event.EventType;

import java.time.Instant;

public interface ClientEventDateAware extends ClientIdNamesAware {
    Instant getEventDateTime();
    EventType getEventType();
    Boolean getIsManual();
}