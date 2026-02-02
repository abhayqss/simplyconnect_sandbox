package com.scnsoft.eldermark.entity.client;

import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;

import java.time.Instant;

public interface ActivityClientAware extends IdAware, CommunityIdAware {
    Boolean getActive();
    Instant getLastUpdated();
}
