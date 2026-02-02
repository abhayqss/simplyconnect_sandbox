package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.mobile.dto.community.marketplace.FeaturedServiceProviderDto;
import com.scnsoft.eldermark.mobile.facade.MarketplaceCommunityFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/marketplace/communities")
public class MarketplaceCommunityController {

    @Autowired
    private MarketplaceCommunityFacade marketplaceCommunityFacade;

    @GetMapping("/{communityId}/providers")
    public Response<List<FeaturedServiceProviderDto>> fetchFeaturedServiceProviders(
            @PathVariable("communityId") Long communityId
    ) {
        return Response.successResponse(marketplaceCommunityFacade.fetchFeaturedServiceProviders(communityId));
    }

    @GetMapping("/can-view")
    public Response<Boolean> canViewList() {
        return Response.successResponse(marketplaceCommunityFacade.canViewList());
    }
}
