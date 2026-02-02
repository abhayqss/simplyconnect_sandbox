package com.scnsoft.eldermark.beans.projection;

import java.time.Instant;

public interface LastUpdatedAware {
    Instant getLastUpdated();
}
