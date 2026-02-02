package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.entity.FeaturedServiceProvider;
import com.scnsoft.eldermark.mobile.dto.community.marketplace.FeaturedServiceProviderDto;
import com.scnsoft.eldermark.service.FeaturedServiceProviderService;
import com.scnsoft.eldermark.service.security.MarketplaceCommunitySecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MarketplaceCommunityFacadeImpl implements MarketplaceCommunityFacade {

    @Autowired
    private FeaturedServiceProviderService featuredServiceProviderService;

    @Autowired
    private Converter<FeaturedServiceProvider, FeaturedServiceProviderDto> featuredServiceProviderDtoConverter;

    @Autowired
    private MarketplaceCommunitySecurityService marketplaceCommunitySecurityService;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@marketplaceCommunitySecurityService.canViewFeaturedPartnerProviders(#communityId)")
    public List<FeaturedServiceProviderDto> fetchFeaturedServiceProviders(Long communityId) {
        return featuredServiceProviderService.findAllDiscoverableByCommunityId(communityId).stream()
                .sorted(Comparator.comparing(FeaturedServiceProvider::getDisplayOrder))
                .map(featuredServiceProviderDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean canViewList() {
        return marketplaceCommunitySecurityService.canViewList();
    }
}
