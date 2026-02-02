package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.OrgDto;

import java.util.List;

public interface OrgsService {

    OrgDto get(Long orgId);

    List<OrgDto> listAllAccessible();
}
