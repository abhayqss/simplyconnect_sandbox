package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.web.commons.dto.FileBytesDto;

public interface AvatarFacade {

    FileBytesDto downloadById(Long id);
}
