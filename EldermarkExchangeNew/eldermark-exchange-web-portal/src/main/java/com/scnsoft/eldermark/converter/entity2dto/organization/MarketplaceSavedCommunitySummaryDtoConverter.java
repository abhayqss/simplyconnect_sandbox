package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.BaseAttachmentDto;
import com.scnsoft.eldermark.dto.MarketplaceSavedCommunitySummaryDto;
import com.scnsoft.eldermark.entity.BaseAttachment;
import com.scnsoft.eldermark.entity.Marketplace;
import com.scnsoft.eldermark.service.CommunityPictureService;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.PartnerNetworkService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class MarketplaceSavedCommunitySummaryDtoConverter extends BaseMarketplaceCommunityConverter
        implements Converter<Marketplace, MarketplaceSavedCommunitySummaryDto> {

    @Autowired
    private CommunityPictureService communityPictureService;

    @Autowired
    private ListAndItemConverter<BaseAttachment, BaseAttachmentDto> baseAttachmentDtoConverter;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private PartnerNetworkService partnerNetworkService;

    @Autowired
    private CommunityService communityService;

    @Override
    public MarketplaceSavedCommunitySummaryDto convert(Marketplace source) {
        MarketplaceSavedCommunitySummaryDto target = new MarketplaceSavedCommunitySummaryDto();
        fillId(source, target);
        fillLocationData(source, target);
        fillBaseDetailsData(source, target);
        target.setPictures(baseAttachmentDtoConverter.convertList(communityPictureService.findAllByCommunityId(source.getCommunityId())));
        target.setHasReferralEmails(CollectionUtils.isNotEmpty(source.getReferralEmails()));
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        if (source.getCommunity() != null) {
            var networkIds = CareCoordinationUtils.toIdsSet(partnerNetworkService.findNetworksWithAllCommunities(source.getCommunityId()));
            target.setIsReferralEnabled(source.getCommunity().isReceiveNonNetworkReferrals() ||
                    communityService.existAllowedReferralMarketplaceCommunitiesWithinAnyNetworks(permissionFilter, source.getCommunityId(), networkIds));
        }
        if (target.getIsReferralEnabled()) {
            target.setCanAddReferral(communityService.isAccessibleReferralMarketplaceCommunity(permissionFilter, source.getCommunity()));
        } else {
            target.setCanAddReferral(false);
        }
        return target;
    }
}
