package com.scnsoft.eldermark.entity.event;

import com.scnsoft.eldermark.beans.projection.IdAware;

import java.time.Instant;

public interface EventDashboardItem extends IdAware {
    Instant getEventDateTime();
    String getEventAuthorFirstName();
    String getEventAuthorLastName();
    String getEventTypeDescription();
    String getEventTypeEventGroupName();
    String getEventTypeEventGroupCode();
}

