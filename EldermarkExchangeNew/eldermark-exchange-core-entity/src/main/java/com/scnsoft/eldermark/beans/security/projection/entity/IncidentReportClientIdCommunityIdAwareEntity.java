package com.scnsoft.eldermark.beans.security.projection.entity;

import com.scnsoft.eldermark.beans.projection.IdAware;

public interface IncidentReportClientIdCommunityIdAwareEntity extends IdAware {
    Long getEventClientId();
    Long getEventClientCommunityId();
}
