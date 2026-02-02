package com.scnsoft.eldermark.dto.pointclickcare.projection;

import com.scnsoft.eldermark.beans.projection.IdAware;

import java.time.LocalDate;

public interface PccClientMatchProjection extends IdAware, OrganizationPccOrgUuidAware {

    String getFirstName();

    String getLastName();

    LocalDate getBirthDate();

    String getGenderCodeSystem();

    String getGenderCode();

    String getMedicaidNumber();

    String getMedicareNumber();

    Long getPccPatientId();

    Long getCommunityPccFacilityId();

}
