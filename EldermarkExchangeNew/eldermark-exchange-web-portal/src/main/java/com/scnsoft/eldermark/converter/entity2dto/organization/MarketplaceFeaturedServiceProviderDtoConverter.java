package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.FeaturedServiceProviderDto;
import com.scnsoft.eldermark.entity.Marketplace;
import com.scnsoft.eldermark.web.commons.utils.ConverterUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class MarketplaceFeaturedServiceProviderDtoConverter implements Converter<Marketplace, FeaturedServiceProviderDto> {

    @Override
    public FeaturedServiceProviderDto convert(Marketplace source) {
        var community = source.getCommunity();
        if (community != null) {
            var target = new FeaturedServiceProviderDto();

            target.setId(source.getId());

            target.setCommunityId(source.getCommunityId());
            target.setCommunityName(community.getName());

            target.setWebsiteUrl(community.getWebsiteUrl());

            target.setOrganizationId(source.getOrganizationId());
            var organization = source.getOrganization();
            target.setOrganizationName(organization.getName());

            target.setConfirmVisibility(source.getDiscoverable());
            target.setAllowExternalInboundReferrals(community.isReceiveNonNetworkReferrals());

            target.setServiceCategories(ConverterUtils.getDisplayNames(source.getServiceCategories()));
            return target;
        }
        return null;
    }

}
