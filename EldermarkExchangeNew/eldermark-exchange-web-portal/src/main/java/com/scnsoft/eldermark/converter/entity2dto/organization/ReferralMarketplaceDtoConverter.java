package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.referral.ReferralMarketplaceDto;
import com.scnsoft.eldermark.entity.referral.ReferralRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ReferralMarketplaceDtoConverter implements Converter<ReferralRequest, ReferralMarketplaceDto> {

    @Override
    public ReferralMarketplaceDto convert(ReferralRequest source) {
        var target = new ReferralMarketplaceDto();
        target.setCommunityId(source.getCommunityId());
        target.setCommunityTitle(source.getCommunity().getName());
        target.setOrganizationTitle(source.getCommunity().getOrganization().getName());
        target.setCommunityEmail(source.getCommunity().getEmail());
        target.setSharedChannel(source.getSharedChannel());
        target.setSharedFax(source.getSharedFax());
        target.setSharedPhone(source.getSharedPhone());
        target.setSharedFaxComment(source.getSharedFaxComment());
        return target;
    }
}
