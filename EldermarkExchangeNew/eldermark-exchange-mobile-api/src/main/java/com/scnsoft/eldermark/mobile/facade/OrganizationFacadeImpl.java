package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.web.commons.dto.FileBytesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class OrganizationFacadeImpl implements OrganizationFacade {

    @Autowired
    private OrganizationService organizationService;

    @Override
    @PreAuthorize("@organizationSecurityService.canViewLogo(#organizationId)")
    public FileBytesDto downloadLogo(Long organizationId) {
        Optional<Pair<byte[], MediaType>> bytesWithMediaType = Optional.ofNullable(organizationService.downloadLogo(organizationId));
        return bytesWithMediaType.map(mediaTypePair -> new FileBytesDto(mediaTypePair.getFirst(), mediaTypePair.getSecond())).orElse(new FileBytesDto());
    }
}
