package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.CommunityDto;

import java.util.List;

public interface CommunitiesService {

    List<CommunityDto> listByOrganization(Long orgId);

    List<CommunityDto> listAllAccessible();

    CommunityDto get(Long communityId);
}
