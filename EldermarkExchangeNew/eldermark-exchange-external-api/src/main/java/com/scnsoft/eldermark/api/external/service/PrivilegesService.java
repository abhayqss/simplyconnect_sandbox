package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;

import java.util.List;

public interface PrivilegesService {

    Boolean canReadOrganization(Long organizationId);

    Boolean canReadCommunity(Long communityId);

    List<Organization> listOrganizationsWithReadAccess();

    List<Long> listOrganizationIdsWithReadAccess();

    List<Community> listCommunitiesWithReadAccess();

    boolean hasConsanaAccess();
}
