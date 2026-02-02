package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.beans.ClientDeactivationReason;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.client.ClientNameAndCommunityAware;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.history.PersonAddressHistory;

import java.time.Instant;
import java.time.LocalDate;

public interface ClientIntakeDetailsAware extends ClientNameAndCommunityAware, OrganizationIdNameAware {

    Instant getIntakeDate();

    Instant getLastUpdated();

    LocalDate getBirthDate();

    Boolean getActive();

    Instant getCreatedDate();

    String getInsurancePlan();

    Instant getExitDate();

    Instant getActivationDate();

    Instant getDeactivationDate();

    String getComment();

    String getExitComment();

    ClientDeactivationReason getDeactivationReason();

    CcdCode getGender();

    CcdCode getRace();

    Person getPerson();
    
    String getInNetworkInsuranceDisplayName();

    String getRaceDisplayName();

    String getGenderDisplayName();


}
