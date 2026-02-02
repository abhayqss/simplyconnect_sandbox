package com.scnsoft.eldermark.beans.security.projection.entity;

import com.scnsoft.eldermark.beans.projection.EventIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.entity.IncidentReportStatus;

public interface IncidentReportSecurityAwareEntity extends IdAware, EventIdAware {
    @Deprecated
    boolean getSubmitted();
    IncidentReportStatus getStatus();
}
