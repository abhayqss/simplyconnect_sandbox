package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.BaseAttachmentDto;
import com.scnsoft.eldermark.dto.MarketplaceCommunitySummaryDto;
import com.scnsoft.eldermark.entity.BaseAttachment;
import com.scnsoft.eldermark.entity.Marketplace;
import com.scnsoft.eldermark.service.CommunityPictureService;
import com.scnsoft.eldermark.service.SavedMarketplaceService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class MarketplaceCommunitySummaryDtoConverter extends BaseMarketplaceCommunityConverter
        implements ListAndItemConverter<Marketplace, MarketplaceCommunitySummaryDto> {

    @Autowired
    private CommunityPictureService communityPictureService;

    @Autowired
    private ListAndItemConverter<BaseAttachment, BaseAttachmentDto> baseAttachmentDtoConverter;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private SavedMarketplaceService savedMarketplaceService;

    @Override
    public MarketplaceCommunitySummaryDto convert(Marketplace source) {
        MarketplaceCommunitySummaryDto target = new MarketplaceCommunitySummaryDto();
        fillId(source, target);
        fillLocationData(source, target);
        fillBaseDetailsData(source, target);
        target.setPictures(baseAttachmentDtoConverter.convertList(communityPictureService.findAllByCommunityId(source.getCommunityId())));
        target.setIsSaved(savedMarketplaceService.isExists(loggedUserService.getCurrentEmployeeId(), source.getId()));
        return target;
    }

}
