package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;

import java.util.List;

public interface FilterFacade {

    List<IdentifiedNamedEntityDto> findOrganizations();

    List<IdentifiedNamedEntityDto> findCommunities(Long organizationId);
}
