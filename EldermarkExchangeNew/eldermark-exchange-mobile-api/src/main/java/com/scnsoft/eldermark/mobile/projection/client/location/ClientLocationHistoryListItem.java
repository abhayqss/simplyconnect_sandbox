package com.scnsoft.eldermark.mobile.projection.client.location;


import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;

import java.math.BigDecimal;
import java.time.Instant;

public interface ClientLocationHistoryListItem extends IdAware, ClientIdAware {

    Instant getSeenDatetime();

    BigDecimal getLongitude();

    BigDecimal getLatitude();
}
