package com.scnsoft.eldermark.dto.pointclickcare.projection;

import com.scnsoft.eldermark.beans.projection.IdAware;

public interface IdAndOrganizationPccFacUuidAndPccFacilityIdAware extends CommunityPccFieldsAware, OrganizationPccOrgUuidAware, IdAware {

    Long getPccFacilityId();

}
