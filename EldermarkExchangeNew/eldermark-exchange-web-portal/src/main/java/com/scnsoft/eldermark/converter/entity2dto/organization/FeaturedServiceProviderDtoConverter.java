package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.FeaturedServiceProviderDto;
import com.scnsoft.eldermark.entity.FeaturedServiceProvider;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.web.commons.utils.ConverterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class FeaturedServiceProviderDtoConverter implements Converter<FeaturedServiceProvider, FeaturedServiceProviderDto> {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    public FeaturedServiceProviderDto convert(FeaturedServiceProvider source) {
        var provider = source.getProvider();
        if (provider != null) {
            var target = new FeaturedServiceProviderDto();

            target.setId(source.getMarketplaceId());

            var marketplace = provider.getMarketplace();

            target.setCommunityId(source.getProviderId());
            target.setCommunityName(provider.getName());

            target.setWebsiteUrl(provider.getWebsiteUrl());

            target.setOrganizationId(source.getOrganizationId());
            var organization = provider.getOrganization();
            target.setOrganizationName(organization.getName());

            target.setConfirmVisibility(marketplace.getDiscoverable());
            target.setAllowExternalInboundReferrals(provider.isReceiveNonNetworkReferrals());
            target.setDisplayOrder(source.getDisplayOrder());

            target.setServiceCategories(ConverterUtils.getDisplayNames(marketplace.getServiceCategories()));
            target.setCanAddReferral(communityService.isAccessibleReferralMarketplaceCommunity(
                    permissionFilterService.createPermissionFilterForCurrentUser(),
                    marketplace.getCommunity()
            ));
            return target;
        }
        return null;
    }

}
