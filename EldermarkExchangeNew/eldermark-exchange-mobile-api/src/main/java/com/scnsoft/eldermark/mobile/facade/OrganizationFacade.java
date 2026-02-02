package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.web.commons.dto.FileBytesDto;

public interface OrganizationFacade {
    FileBytesDto downloadLogo(Long organizationId);
}
