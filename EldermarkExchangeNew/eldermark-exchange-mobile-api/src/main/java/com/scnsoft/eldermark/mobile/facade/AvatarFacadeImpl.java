package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.service.AvatarService;
import com.scnsoft.eldermark.service.report.converter.WriterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@Transactional
public class AvatarFacadeImpl implements AvatarFacade {

    @Autowired
    private AvatarService avatarService;

    @Override
    @PreAuthorize("@avatarSecurityService.canView(#id)")
    public void downloadById(@P("id") Long id, HttpServletResponse response) {
        var bytesWithMediaType = avatarService.downloadById(id);
        WriterUtils.copyDocumentContentAndContentTypeToResponse(bytesWithMediaType.getFirst(), Optional.ofNullable(bytesWithMediaType.getSecond()).map(MediaType::toString).orElse(null), response);
    }
}
