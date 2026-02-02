package com.scnsoft.eldermark.beans.projection;

import java.time.Instant;

public interface ClientInsuranceAuthorizationDetailsAware extends ClientIdAware {
    Instant getStartDate();

    Instant getEndDate();

    String getNumber();
}
