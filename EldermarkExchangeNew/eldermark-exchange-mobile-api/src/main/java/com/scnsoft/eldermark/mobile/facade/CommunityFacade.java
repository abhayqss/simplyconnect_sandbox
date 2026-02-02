package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.web.commons.dto.FileBytesDto;

public interface CommunityFacade {
    FileBytesDto downloadLogo(Long communityId);
}
