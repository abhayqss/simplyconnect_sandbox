package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.service.CommunityService;
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
public class CommunityFacadeImpl implements CommunityFacade {

    @Autowired
    private CommunityService communityService;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@communitySecurityService.canViewLogo(#communityId)")
    public FileBytesDto downloadLogo(Long communityId) {
        Optional<Pair<byte[], MediaType>> bytesWithMediaType = Optional.ofNullable(communityService.downloadLogo(communityId));
        return bytesWithMediaType.map(mediaTypePair -> new FileBytesDto(mediaTypePair.getFirst(), mediaTypePair.getSecond()))
                .orElse(new FileBytesDto());
    }

}
