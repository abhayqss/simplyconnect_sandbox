package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.web.commons.dto.FileBytesDto;
import com.scnsoft.eldermark.service.AvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AvatarFacadeImpl implements AvatarFacade {

    @Autowired
    private AvatarService avatarService;

    @Override
    @PreAuthorize("@avatarSecurityService.canView(#id)")
    public FileBytesDto downloadById(@P("id") Long id) {
        var bytesWithMediaType = avatarService.downloadById(id);
        return new FileBytesDto(bytesWithMediaType.getFirst(), bytesWithMediaType.getSecond());
    }
}
