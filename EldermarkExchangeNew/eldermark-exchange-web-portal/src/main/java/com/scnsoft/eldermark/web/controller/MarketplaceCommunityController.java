package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.beans.FeaturedServiceProviderFilter;
import com.scnsoft.eldermark.beans.MarketplaceFilter;
import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.facade.MarketplaceCommunityFacade;
import com.scnsoft.eldermark.service.security.MarketplaceCommunitySecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/marketplace/communities")
public class MarketplaceCommunityController {

    @Autowired
    private MarketplaceCommunityFacade marketplaceCommunityFacade;

    @Autowired
    private MarketplaceCommunitySecurityService marketplaceCommunitySecurityService;

    @GetMapping
    public Response<List<MarketplaceCommunitySummaryDto>> find(@ModelAttribute MarketplaceFilter filter,
                                                               Pageable pageRequest) {
        var pageable = marketplaceCommunityFacade.find(filter, pageRequest);
        return Response.pagedResponse(pageable);
    }

    @GetMapping("/locations")
    public Response<List<MarketplaceCommunityLocationListItemDto>> findLocations(@ModelAttribute MarketplaceFilter filter) {
        var result = marketplaceCommunityFacade.findLocations(filter);
        return Response.successResponse(result);
    }

    @GetMapping("/{communityId}/location-details")
    public Response<MarketplaceCommunityLocationDetailsDto> findLocationDetails(@PathVariable("communityId") Long communityId) {
        var result = marketplaceCommunityFacade.findLocationDetails(communityId);
        return Response.successResponse(result);
    }

    @GetMapping("/{communityId}/partners")
    public Response<List<MarketplaceCommunitySummaryDto>> findPartners(
            @PathVariable("communityId") final Long communityId,
            MarketplaceFilter filter,
            Pageable pageRequest
    ) {
        var pageable = marketplaceCommunityFacade.findPartners(communityId, filter, pageRequest);
        return Response.pagedResponse(pageable);
    }

    @GetMapping("/{communityId}/providers")
    public Response<List<FeaturedServiceProviderDto>> fetchFeaturedServiceProviders(
            @PathVariable("communityId") Long communityId,
            @RequestParam(value = "isFeatured", required = false) Boolean isFeatured,
            Pageable pageRequest
    ) {
        var filter = new FeaturedServiceProviderFilter();
        filter.setCommunityId(communityId);
        filter.setFeatured(isFeatured);
        var pageable = marketplaceCommunityFacade.fetchFeaturedServiceProviders(
                filter, pageRequest
        );
        return Response.pagedResponse(pageable);
    }

    @GetMapping("/{communityId}")
    public Response<CommunityWithAddressDetailsDto> findById(@PathVariable("communityId") Long communityId,
                                           @RequestParam(value = "referralClientId", required = false) Long referralClientId) {
        return Response.successResponse(marketplaceCommunityFacade.findByCommunityId(communityId, referralClientId));
    }

    @GetMapping(path = "/can-view")
    public Response<Boolean> canView() {
        return Response.successResponse(marketplaceCommunitySecurityService.canViewList());
    }

    @GetMapping("/saved")
    public Response<List<MarketplaceSavedCommunitySummaryDto>> findSaved(Pageable pageRequest) {
        return Response.pagedResponse(marketplaceCommunityFacade.findSavedMarketplaces(pageRequest));
    }

    @PostMapping("/{communityId}/save")
    public Response<Void> saveById(@PathVariable("communityId") Long communityId) {
        marketplaceCommunityFacade.addSavedMarketplaceByCommunityId(communityId);
        return Response.successResponse();
    }

    @PostMapping("/{communityId}/remove")
    public Response<Void> removeById(@PathVariable("communityId") Long communityId) {
        marketplaceCommunityFacade.removeSavedMarketplaceByCommunityId(communityId);
        return Response.successResponse();
    }

    @GetMapping(path = "/can-edit-partner-providers")
    public Response<Boolean> canEditPartnerProviders(
            @RequestParam("communityId") Long communityId,
            @RequestParam("organizationId") Long organizationId
    ) {
        return Response.successResponse(marketplaceCommunitySecurityService.canEditFeaturedPartnerProviders(communityId, organizationId));
    }

    @GetMapping(path = "/in-network/exists")
    public Response<Boolean> existsInNetworkCommunities() {
        return Response.successResponse(marketplaceCommunityFacade.existsInNetworkMarketplaceAccessibleCommunities());
    }
}
