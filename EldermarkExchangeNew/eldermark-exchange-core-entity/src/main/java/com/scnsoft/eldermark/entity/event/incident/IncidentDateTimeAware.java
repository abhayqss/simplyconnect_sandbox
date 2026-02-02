package com.scnsoft.eldermark.entity.event.incident;

import java.time.Instant;

public interface IncidentDateTimeAware {
    Instant getIncidentDatetime();
}
