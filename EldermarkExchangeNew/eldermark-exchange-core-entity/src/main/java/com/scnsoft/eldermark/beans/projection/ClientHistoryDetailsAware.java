package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.beans.ClientDeactivationReason;
import com.scnsoft.eldermark.entity.Person;

import java.time.Instant;
import java.time.LocalDate;

public interface ClientHistoryDetailsAware extends ClientIdAware, ClientNamesAware, ClientCommunityIdNameAware, ClientOrganizationIdNameAware {
    ClientDeactivationReason getDeactivationReason();
    Instant getDeactivationDate();
    String getInNetworkInsuranceDisplayName();
    Person getClientPerson();
    LocalDate getClientBirthDate();
    String getClientMedicaidNumber();
    Boolean getClientActive();
}