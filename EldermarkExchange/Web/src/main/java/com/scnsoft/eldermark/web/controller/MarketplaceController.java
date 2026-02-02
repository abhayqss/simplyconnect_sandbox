package com.scnsoft.eldermark.web.controller;


import com.scnsoft.eldermark.services.marketplace.MarketplaceService;
import com.scnsoft.eldermark.shared.administration.SearchFilter;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.PrimaryFocusKeyValueDto;
import com.scnsoft.eldermark.shared.marketplace.BasicMarketplaceInfoDto;
import com.scnsoft.eldermark.shared.marketplace.MarketplaceDetailsDto;
import com.scnsoft.eldermark.shared.marketplace.MarketplaceFilterDto;
import com.scnsoft.eldermark.shared.marketplace.MarketplaceInfoDto;
import com.scnsoft.eldermark.util.PaginationUtils;
import com.scnsoft.eldermark.web.controller.helper.MarketPlaceControllerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


//@Controller
//@RequestMapping(value = "/marketplace")
public class MarketplaceController {

    private static final String ALL_MARKET_PLACE_ITEMS_ATTR = "ALL_MARKET_PLACE_ITEMS";
    private static final String FILTER_ATTR = "FILTER_ATTR";

    private static final int INIT_PAGE_SIZE = 30;

	@Autowired
    MarketplaceService marketplaceService;

    @Autowired
    private MarketPlaceControllerHelper marketPlaceControllerHelper;

    @RequestMapping(method = RequestMethod.GET)
    public String initView(Model model) {

//        MarketplaceFilterDto marketplaceFilter = new MarketplaceFilterDto();
        model.addAttribute("initLocationWasSet", false);
        return initMarketplaceView(model,  new MarketplaceFilterDto());

//        model.addAttribute("marketplaceFilter",marketplaceFilter);
//
//        model.addAttribute("communityTypes", marketplaceService.getCommunityTypes());
//        model.addAttribute("primaryFocuses", marketplaceService.getPrimaryFocuses());
//        model.addAttribute("services", marketplaceService.getServicesTreatmentApproaches());
//        model.addAttribute("searchFilter",new SearchFilter());
//
//        final Pageable pageable = PaginationUtils.buildPageable(100, 0);
//        final Page<BasicMarketplaceInfoDto> dto = marketplaceService.getDiscoverableMarketplaces(null, null, null,
//                null, null, null, null, null, null, null, null, pageable);
//
//        model.addAttribute("communityList",dto);
//
//        return "marketplace";
    }

    @RequestMapping(method = RequestMethod.POST)  //TODO check if can be removed
    public String initView(Model model,  @ModelAttribute("marketplaceFilter") MarketplaceFilterDto marketplaceFilter) {

//        if (marketplaceFilter==null) {
//            marketplaceFilter = new MarketplaceFilterDto();
//        }
        return initMarketplaceView(model,  marketplaceFilter);
    }

    private Page<BasicMarketplaceInfoDto> initMarketplaceDto(MarketplaceFilterDto marketplaceFilter, Integer pageNum) {

        final int pageNumber = pageNum==null?0:pageNum;
        final Pageable pageable = PaginationUtils.buildPageable(INIT_PAGE_SIZE, pageNumber);

        if (marketplaceFilter.getInitLatitude() == null || marketplaceFilter.getInitLongitude() == null) {
            return new PageImpl<BasicMarketplaceInfoDto>(Collections.<BasicMarketplaceInfoDto>emptyList(),
                    pageable, 0L);
        }
        List<Long> filteredCommunityTypeIdsWithSameDisplayName =marketplaceService.getCommunityTypeIdsWithSameDisplayName(marketplaceFilter.getCommunityTypeIds());
        List<Long> filteredServiceIdsWithSameDisplayName =marketplaceService.getServiceTreatmentApproachIdsWithSameDisplayName(marketplaceFilter.getServiceIds());

        final Page<BasicMarketplaceInfoDto> pageDto = marketplaceService.getDiscoverableMarketplaces(marketplaceFilter.getPrimaryFocusIds(), filteredCommunityTypeIdsWithSameDisplayName, filteredServiceIdsWithSameDisplayName,
                marketplaceFilter.getInNetworkInsuranceId() == null ? null : Collections.singletonList(marketplaceFilter.getInNetworkInsuranceId()),
                marketplaceFilter.getInsurancePlanId() == null ? null : Collections.singletonList(marketplaceFilter.getInsurancePlanId()),
                null, null, null, marketplaceFilter.getSearchText(),
                marketplaceFilter.getInitLatitude(), marketplaceFilter.getInitLongitude(), pageable);

        for (BasicMarketplaceInfoDto marketplaceInfoDto1: pageDto.getContent()) {
            marketplaceInfoDto1.initSameAddrIds();
            if (marketplaceInfoDto1.isAddMarker()) {
                for (BasicMarketplaceInfoDto marketplaceInfoDto2 : pageDto.getContent()) {
                    if (!marketplaceInfoDto1.getId().equals(marketplaceInfoDto2.getId())
                            && marketplaceInfoDto1.getLocation() != null && marketplaceInfoDto2.getLocation() != null
                            && marketplaceInfoDto1.getLocation().getLatitude() != null && marketplaceInfoDto1.getLocation().getLongitude() != null
                            && marketplaceInfoDto1.getLocation().getLatitude().equals(marketplaceInfoDto2.getLocation().getLatitude())
                            && marketplaceInfoDto2.getLocation().getLongitude().equals(marketplaceInfoDto2.getLocation().getLongitude())) {
//                        marketplaceInfoDto1.incMarker();
//                        marketplaceInfoDto1.getSameAddrIds().add(marketplaceInfoDto2.getId());
                        marketplaceInfoDto1.addSameAddrId(marketplaceInfoDto2.getId());
                        marketplaceInfoDto2.setAddMarker(false);
                    }
                }
            }
        }
        getMarketPlaceControllerHelper().prepareMarketPlaceList(pageDto.getContent());
       return pageDto;
    }

    private String initMarketplaceView(Model model, MarketplaceFilterDto marketplaceFilter) {

//        model.addAttribute("marketplaceFilter",marketplaceFilter);
//
//        model.addAttribute("communityTypes", marketplaceService.getCommunityTypes());
//        model.addAttribute("primaryFocuses", marketplaceService.getPrimaryFocuses());
//        model.addAttribute("services", marketplaceService.getServicesTreatmentApproaches());
//        model.addAttribute("searchFilter",new SearchFilter());
//
//        final Pageable pageable = PaginationUtils.buildPageable(100, 0);
//        final Page<BasicMarketplaceInfoDto> dto = marketplaceService.getDiscoverableMarketplaces(marketplaceFilter.getPrimaryFocusIds(), marketplaceFilter.getCommunityTypeIds(), marketplaceFilter.getServiceIds(),
//                null, null, null, null, null, marketplaceFilter.getSearchText(), null, null, pageable);
        getMarketPlaceControllerHelper().clearFilter(marketplaceFilter);

        List<KeyValueDto> filteredCommunityTypes=marketplaceService.removeDuplicatesKeyValueDtos(marketplaceService.getCommunityTypes());
        List<KeyValueDto> filteredServicesTreatmentApproaches=marketplaceService.removeDuplicatesKeyValueDtos(marketplaceService.getServicesTreatmentApproaches());

        model.addAttribute("marketplaceFilter",marketplaceFilter);

        model.addAttribute("communityTypes", filteredCommunityTypes);
        model.addAttribute("primaryFocuses", marketplaceService.getPrimaryFocuses());
        model.addAttribute("services", filteredServicesTreatmentApproaches);
        model.addAttribute("allInNetworkInsurances", marketplaceService.getInNetworkInsurances());
        model.addAttribute("inNetworkInsurancesExceptSection0", marketplaceService.getInNetworkInsurancesExceptSection0());
        model.addAttribute("inNetworkInsurancesSection1", marketplaceService.getNetworkInsurancesGroupWithoutName());
        model.addAttribute("popularInNetworkInsurances", marketplaceService.getPopularInNetworkInsurances());
        model.addAttribute("insurancePlans", marketplaceService.getInsurancePlans());
        model.addAttribute("popularInsurancePlans", marketplaceService.getPopularInsurancePlans());
        model.addAttribute("searchFilter",new SearchFilter());
        model.addAttribute("marketplaceList",initMarketplaceDto(marketplaceFilter,null));

        return "marketplace";

    }

    @RequestMapping(value = "/scroll", method = RequestMethod.POST)
    @ResponseBody
    public Page<BasicMarketplaceInfoDto> getDiscoverableMarketplaces(@ModelAttribute("marketplaceFilter") MarketplaceFilterDto marketplaceFilter, Integer pageNumber) {
        getMarketPlaceControllerHelper().clearFilter(marketplaceFilter);
//        if (marketplaceFilter==null) {
//            marketplaceFilter = new MarketplaceFilterDto();
//        }
//
//        final Page<BasicMarketplaceInfoDto> dto = marketplaceService.getDiscoverableMarketplaces(null, null, null,
//                null, null, null, null, null, null, null, null, pageable);
//        return dto;
//        marketplaceFilter.incrementPageNumber();
        return initMarketplaceDto(marketplaceFilter,marketplaceFilter.getPageNumber());
    }

    @RequestMapping(value = "/popup", method = RequestMethod.POST)
    public String popup(@RequestParam(value="ids[]") ArrayList<Long> ids, Model model) {
        List<MarketplaceInfoDto> marketplaces = marketplaceService.getMarketplacesByIds(ids);
        model.addAttribute("marketplaces",marketplaces);
        return "marketplace.popup";
    }

    @RequestMapping(value = "/{id}/details", method = RequestMethod.GET)
    public String details(@PathVariable("id") Long id, Model model) {
        MarketplaceDetailsDto marketplaceDto = marketplaceService.getMarketplaceDetails(id);
        model.addAttribute("community",marketplaceDto);
        return "marketplace.details";
    }

    public MarketPlaceControllerHelper getMarketPlaceControllerHelper() {
        return marketPlaceControllerHelper;
    }

    public void setMarketPlaceControllerHelper(final MarketPlaceControllerHelper marketPlaceControllerHelper) {
        this.marketPlaceControllerHelper = marketPlaceControllerHelper;
    }

    @RequestMapping(value="/communityTypes", method=RequestMethod.POST)
    @ResponseBody
    public List<List<PrimaryFocusKeyValueDto>> getCommunityTypes(@RequestBody List<Long> primaryFocusIds){
        if (primaryFocusIds.size()==0) {
            return null;
        }
        return marketplaceService.getFilteredCommunityTypesListofLists(primaryFocusIds);
    }

    @RequestMapping(value="/servicesTreatmentApproach", method=RequestMethod.POST)
    @ResponseBody
    public List<List<PrimaryFocusKeyValueDto>> getServicesTreatmentApproaches(@RequestBody List<Long> primaryFocusIds){
    	if (primaryFocusIds.size()==0) {
    		return null;
    	}
    	return marketplaceService.getFilteredServiceTreatmentApproachListofLists(primaryFocusIds);
    }
}
