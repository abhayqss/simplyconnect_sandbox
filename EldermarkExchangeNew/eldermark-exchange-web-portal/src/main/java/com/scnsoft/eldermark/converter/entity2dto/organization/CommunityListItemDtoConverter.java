package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.CommunityListItemDto;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.service.security.CommunitySecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommunityListItemDtoConverter implements ListAndItemConverter<Community, CommunityListItemDto> {

    @Autowired
    private CommunitySecurityService communitySecurityService;

    @Override
    public CommunityListItemDto convert(Community source) {
        var target = new CommunityListItemDto();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setOid(source.getOid());
        target.setCreatedAutomatically(source.getCreatedAutomatically());
        target.setLastModified(DateTimeUtils.toEpochMilli(source.getLastModified()));
        target.setCanView(communitySecurityService.canView(source.getId()));
        target.setCanEdit(communitySecurityService.canEdit(source.getId()));
        return target;
    }
}
