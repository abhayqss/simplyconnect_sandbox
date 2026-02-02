package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlanStatus;

import java.time.Instant;

public interface ServicePlanDetailsAware extends ClientIdAware {
    Instant getDateCompleted();
    Instant getLastModifiedDate();
    ServicePlanStatus getServicePlanStatus();
}