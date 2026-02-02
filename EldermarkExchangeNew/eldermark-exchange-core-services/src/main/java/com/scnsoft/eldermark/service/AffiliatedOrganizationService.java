package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.AffiliatedOrganization;

import java.util.List;

public interface AffiliatedOrganizationService {

    List<AffiliatedOrganization> update(List<AffiliatedOrganization> affiliatedOrganizations, Long organizationId);

    List<AffiliatedOrganization> getAllByPrimaryOrganizationId(Long organizationId);

    List<AffiliatedOrganization> getAllByAffiliatedOrganizationId(Long organizationId);

    List<AffiliatedOrganization> getAllForPrimaryCommunityId(Long organizationId);

    List<AffiliatedOrganization> getAllForAffiliatedCommunityId(Long organizationId);
}
