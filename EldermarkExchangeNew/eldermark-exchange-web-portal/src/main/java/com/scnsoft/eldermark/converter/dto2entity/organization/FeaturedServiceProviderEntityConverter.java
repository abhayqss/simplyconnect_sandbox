package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.FeaturedServiceProviderDto;
import com.scnsoft.eldermark.entity.FeaturedServiceProvider;
import com.scnsoft.eldermark.service.MarketplaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class FeaturedServiceProviderEntityConverter implements Converter<FeaturedServiceProviderDto, FeaturedServiceProvider> {

    @Autowired
    private MarketplaceService marketplaceService;

    @Override
    public FeaturedServiceProvider convert(FeaturedServiceProviderDto source) {
        var target = new FeaturedServiceProvider();
        var marketplace = marketplaceService.findById(source.getId());
        var community = marketplace.getCommunity();
        if (community != null) {
            target.setMarketplaceId(source.getId());
            target.setProviderId(marketplace.getCommunityId());
            target.setDisplayOrder(source.getDisplayOrder());
            target.setOrganizationId(marketplace.getOrganizationId());
            target.setProvider(community);
            return target;
        }
        return null;
    }
}
