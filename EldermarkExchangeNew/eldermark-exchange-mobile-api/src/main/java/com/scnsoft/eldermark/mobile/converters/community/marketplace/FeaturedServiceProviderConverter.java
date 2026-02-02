package com.scnsoft.eldermark.mobile.converters.community.marketplace;

import com.scnsoft.eldermark.entity.FeaturedServiceProvider;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.mobile.dto.community.marketplace.FeaturedServiceProviderDto;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.UrlService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FeaturedServiceProviderConverter implements Converter<FeaturedServiceProvider, FeaturedServiceProviderDto> {

    @Autowired
    private UrlService urlService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    public FeaturedServiceProviderDto convert(FeaturedServiceProvider source) {

        var target = new FeaturedServiceProviderDto();

        var providerCommunity = source.getProvider();
        var organization = providerCommunity.getOrganization();

        target.setOrganizationId(organization.getId());
        target.setOrganizationLogoName(organization.getMainLogoPath());

        target.setCommunityId(providerCommunity.getId());
        target.setCommunityName(providerCommunity.getName());
        target.setCommunityLogoName(providerCommunity.getMainLogoPath());

        if (StringUtils.isNotBlank(providerCommunity.getWebsiteUrl())) {
            target.setWebsiteUrl(providerCommunity.getWebsiteUrl());
        }
        target.setDescription(providerCommunity.getMarketplace().getSummary());

        if (canAddReferral(providerCommunity)) {
            target.setCreateExternalInboundReferralUrl(urlService.createReferralRequestExternalUrl(
                    source.getCommunity().getOrganizationId(),
                    source.getCommunity().getId(),
                    providerCommunity.getId()
            ));
        }

        target.setHasReferralEmails(CollectionUtils.isNotEmpty(providerCommunity.getMarketplace().getReferralEmails()));

        return target;
    }

    private boolean canAddReferral(Community provider) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return communityService.isAccessibleReferralMarketplaceCommunity(permissionFilter, provider);
    }
}
