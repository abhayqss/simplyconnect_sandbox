package com.scnsoft.eldermark.entity.client.history;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;

public interface ClientHistoryStatusAware extends ClientIdAware {
    Boolean getActive();
}
