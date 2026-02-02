package com.scnsoft.eldermark.entity.history;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;

import java.time.Instant;

public interface ActivityClientHistoryAware extends IdAware, ClientIdAware, CommunityIdAware {
    Boolean getActive();
    Instant getUpdatedDatetime();
}
