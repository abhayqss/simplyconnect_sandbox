package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.beans.ClientDeactivationReason;
import com.scnsoft.eldermark.entity.Person;

import java.time.Instant;

public interface ClientHistoryOutreachReportDetailsAware extends ClientIdNamesAware, ClientCommunityIdNameAware, ClientOrganizationIdAware {
    ClientDeactivationReason getDeactivationReason();
    Instant getExitDate();
    String getClientMedicareNumber();
    Instant getClientIntakeDate();
}
