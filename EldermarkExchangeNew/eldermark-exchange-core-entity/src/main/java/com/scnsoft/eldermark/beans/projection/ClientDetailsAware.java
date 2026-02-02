package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.beans.ClientDeactivationReason;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.client.ClientNameAndCommunityAware;

import java.time.Instant;
import java.time.LocalDate;

public interface ClientDetailsAware extends ClientNameAndCommunityAware, OrganizationIdNameAware {
    String getInNetworkInsuranceDisplayName();
    Person getPerson();
    LocalDate getBirthDate();
    Boolean getActive();
    String getMedicaidNumber();
    ClientDeactivationReason getDeactivationReason();
    Instant getDeactivationDate();
}
