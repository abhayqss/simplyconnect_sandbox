package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.beans.ClientDeactivationReason;
import com.scnsoft.eldermark.entity.client.ClientNameAndCommunityAware;

import java.time.Instant;

public interface ClientOutreachReportDetailsAware extends ClientNameAndCommunityAware, OrganizationIdAware {
    String getMedicareNumber();
    ClientDeactivationReason getDeactivationReason();
    Instant getIntakeDate();
    Instant getExitDate();
}
