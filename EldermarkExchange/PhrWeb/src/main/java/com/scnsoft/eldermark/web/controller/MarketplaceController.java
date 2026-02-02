package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.service.MarketplacesService;
import com.scnsoft.eldermark.service.marketplace.*;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.web.entity.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import static com.scnsoft.eldermark.shared.utils.PaginationUtils.buildPageable;

@Api(value = "PHR - Marketplace", description = "Marketplace")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class)
})
@RestController
@RequestMapping("/phr/{userId:\\d+}/marketplaces")
public class MarketplaceController {

    @Autowired
    private CareTypeService careTypeService;

    @Autowired
    private MarketplacesService marketplacesService;

    @Autowired
    private CommunityTypeService communityTypeService;

    @Autowired
    private ServicesTreatmentApproachService servicesTreatmentApproachService;

    @Autowired
    private InNetworkInsuranceService inNetworkInsuranceService;

    @Autowired
    private InsurancePlanService insurancePlanService;

    @ApiOperation(value = "Get list of care types", notes = "Returns a list of care types")
    @RequestMapping(value = "/careTypes", method = RequestMethod.GET)
    public @ResponseBody
    Response<List<CareTypeInfoDto>> getCareTypes(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId
    ) {
        final List<CareTypeInfoDto> dto = careTypeService.listCareTypes();
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get list marketplaces by care type", notes = "Returns a list of marketplaces for given care type")
    @GetMapping
    public @ResponseBody
    Response<List<MarketplaceListInfoDto>> getMarketplacesByCareType(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "location search string") @RequestParam(value = "location", required = false) String location,
            @ApiParam(value = "location search string type (STATE, CITY, ZIP_CODE)") @RequestParam(value = "locationType", required = false) String locationType,
            @ApiParam(value = "marketplace search string to search by address, community name, organization name, community type") @RequestParam(value = "searchText", required = false) String searchText,
            @ApiParam(value = "current care type id") @RequestParam(value = "careTypeId", required = false) Long careTypeId,
            @ApiParam(value = "community type ids") @RequestParam(value = "communityTypeIds", required = false) List<Long> communityTypeIds,
            @ApiParam(value = "flag to check if marketplace has services") @RequestParam(value = "isNotHavingServicesIncluded", required = false) Boolean isNotHavingServicesIncluded,
            @ApiParam(value = "services treatment approaches ids") @RequestParam(value = "servicesIds", required = false) List<Long> servicesIds,
            @ApiParam(value = "inNetwork insurances id") @RequestParam(value = "insuranceId", required = false) Long insuranceId,
            @ApiParam(value = "insurance plans ids") @RequestParam(value = "insurancePlanId", required = false) Long insurancePlanId,
            @ApiParam(value = "flag to check if community provides emergency services") @RequestParam(value = "hasEmergencyServices", required = false) Boolean hasEmergencyServices,
            @ApiParam(value = "user coordinates (latitude)") @RequestParam(value = "latitude", required = false) Double latitude,
            @ApiParam(value = "user coordinates (longitude)") @RequestParam(value = "longitude", required = false) Double longitude,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return unlimited number of communities), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        List<Long> inNetworkInsurancesIds = new ArrayList<>();
        List<Long> insurancePlansIds = new ArrayList<>();
        if (insuranceId != null) {
            inNetworkInsurancesIds.add(insuranceId);
        }
        if (insurancePlanId != null) {
            insurancePlansIds.add(insurancePlanId);
        }
        final Page<MarketplaceListInfoDto> dto = marketplacesService.getDiscoverableMarketplaces(careTypeId, communityTypeIds, isNotHavingServicesIncluded, servicesIds,
                inNetworkInsurancesIds, insurancePlansIds, hasEmergencyServices, location, locationType, searchText, latitude, longitude, pageable);
        return Response.pagedResponse(dto);
    }

    @ApiOperation(value = "Get count of marketplaces by given search criteria", notes = "Returns count of marketplaces by given search criteria")
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public @ResponseBody
    Response<CountDto> getMarketplacesCount(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "location search string") @RequestParam(value = "location", required = false) String location,
            @ApiParam(value = "location search string type (STATE, CITY, ZIP_CODE)") @RequestParam(value = "locationType", required = false) String locationType,
            @ApiParam(value = "marketplace search string to search by address, community name, organization name, community type") @RequestParam(value = "searchText", required = false) String searchText,
            @ApiParam(value = "current care type id") @RequestParam(value = "careTypeId", required = false) Long careTypeId,
            @ApiParam(value = "community type ids") @RequestParam(value = "communityTypeIds", required = false) List<Long> communityTypeIds,
            @ApiParam(value = "flag to check if marketplace has services") @RequestParam(value = "isNotHavingServicesIncluded", required = false) Boolean isNotHavingServicesIncluded,
            @ApiParam(value = "services treatment approaches ids") @RequestParam(value = "servicesIds", required = false) List<Long> servicesIds,
            @ApiParam(value = "inNetwork insurances id") @RequestParam(value = "insuranceId", required = false) Long insuranceId,
            @ApiParam(value = "insurance plans ids") @RequestParam(value = "insurancePlanId", required = false) Long insurancePlanId,
            @ApiParam(value = "flag to check if community provides emergency services") @RequestParam(value = "hasEmergencyServices", required = false) Boolean hasEmergencyServices
    ) {
        List<Long> inNetworkInsurancesIds = new ArrayList<>();
        List<Long> insurancePlansIds = new ArrayList<>();
        if (insuranceId != null) {
            inNetworkInsurancesIds.add(insuranceId);
        }
        if (insurancePlanId != null) {
            insurancePlansIds.add(insurancePlanId);
        }
        final CountDto dto = marketplacesService.getDiscoverableMarketplacesCount(careTypeId, communityTypeIds, isNotHavingServicesIncluded, servicesIds,
                inNetworkInsurancesIds, insurancePlansIds, hasEmergencyServices, location, locationType, searchText);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get marketplace by id", notes = "Returns a single marketplace for given marketplace id")
    @RequestMapping(value = "/{marketplaceId}", method = RequestMethod.GET)
    public @ResponseBody
    Response<MarketplaceInfoDto> getMarketplace(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "current marketplace id", required = true) @PathVariable("marketplaceId") Long marketplaceId,
            @ApiParam(value = "user coordinates (latitude)") @RequestParam(value = "latitude", required = false) Double latitude,
            @ApiParam(value = "user coordinates (longitude)") @RequestParam(value = "longitude", required = false) Double longitude
    ) {
        final MarketplaceInfoDto dto = marketplacesService.getMarketplace(marketplaceId, latitude, longitude);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get marketplace services by id", notes = "Returns marketplace services for given marketplace id")
    @GetMapping(value = "/{marketplaceId}/services")
    public @ResponseBody
    Response<MarketplaceServicesDto> getMarketplaceServices(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "current marketplace id", required = true) @PathVariable("marketplaceId") Long marketplaceId
    ) {
        final MarketplaceServicesDto dto = marketplacesService.getMarketplacesServices(marketplaceId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get list of community types", notes = "Returns a list of community types")
    @RequestMapping(value = "/communityTypes", method = RequestMethod.GET)
    public @ResponseBody
    Response<List<CommunityTypeInfoDto>> getCommunityTypes(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId
    ) {
        final List<CommunityTypeInfoDto> dto = communityTypeService.listCommunityTypes();
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get list of services treatment approaches", notes = "Returns a list of services treatment approaches")
    @RequestMapping(value = "/services", method = RequestMethod.GET)
    public @ResponseBody
    Response<List<ServicesTreatmentApproachInfoDto>> getServices(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId
    ) {
        final List<ServicesTreatmentApproachInfoDto> dto = servicesTreatmentApproachService.listServicesTreatmentApproaches();
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get list of innetwork insurances", notes = "Returns a list of innetwork insurances")
    @RequestMapping(value = "/insurances", method = RequestMethod.GET)
    public @ResponseBody
    Response<List<InNetworkInsuranceInfoDto>> getInsurances(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "search text") @RequestParam(value = "searchText", required = false) String searchText,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return unlimited number of entities), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        final Page<InNetworkInsuranceInfoDto> dto = inNetworkInsuranceService.listInNetworkInsurances(searchText, pageSize, page);
        return Response.pagedResponse(dto);
    }

    @ApiOperation(value = "Get list of insurance plans for specified innetwork insurance", notes = "Returns a list of insurance plans for specified innetwork insurance")
    @RequestMapping(value = "/insurancePlans", method = RequestMethod.GET)
    public @ResponseBody
    Response<List<InsurancePlanInfoDto>> getInsurancePlans(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "current inNetwork Insurance id") @RequestParam(value = "insuranceId", required = false) Long insuranceId,
            @ApiParam(value = "search text") @RequestParam(value = "searchText", required = false) String searchText,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return unlimited number of entities), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        final Page<InsurancePlanInfoDto> dto = insurancePlanService.listInsurancePlans(insuranceId, searchText, pageSize, page);
        return Response.pagedResponse(dto);
    }

    @ApiOperation(value = "Get marketplace by id", notes = "Returns a single marketplace for given marketplace id")
    @PostMapping(value = "{marketplaceId}/appointments", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response<Response> createMarketplaceAppointment(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "current marketplace id", required = true) @PathVariable("marketplaceId") Long marketplaceId,
            @ApiParam(value = "Appointment data", required = true) @RequestBody AppointmentCreateDto body
    ) {
        marketplacesService.processNewAppointment(marketplaceId, userId, body);
        return Response.successResponse();
    }

    @ApiOperation(value = "Get time for processing appointment")
    @RequestMapping(value = "/{marketplaceId}/appointments/confirmContactWaitTime", method = RequestMethod.GET)
    public @ResponseBody
    Response<Integer> getMaxDaysToContact(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "current marketplace id", required = true) @PathVariable("marketplaceId") Long marketplaceId,
            @ApiParam(value = "measure unit") @RequestParam(value = "unit", required = false) String unit
    ) {
        Integer maxDays = marketplacesService.getMaxDaysToProcessAppointment(marketplaceId);
        return Response.successResponse(maxDays);
    }

    @ApiOperation(value = "Get page number of insurance plan entity")
    @RequestMapping(value = "/insurancePlans/page", method = RequestMethod.GET)
    public @ResponseBody
    Response<Integer> getPageNumberInsurancePlan(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @NotNull @ApiParam(value = "current insurance plan id", required = true) @RequestParam(value = "insurancePlanId", required = true) Long insurancePlanId,
            @ApiParam(value = "current insurance id") @RequestParam(value = "insuranceId", required = false) Long insuranceId,
            @Min(1) @ApiParam(value = "Page Size", required = true) @RequestParam(value = "pageSize", required = true) Integer pageSize
    ) {
        Integer pageNumber = insurancePlanService.getPageNumber(insurancePlanId, insuranceId, pageSize);
        return Response.successResponse(pageNumber);
    }

    @ApiOperation(value = "Get page number of insurance entity")
    @RequestMapping(value = "/insurances/page", method = RequestMethod.GET)
    public @ResponseBody
    Response<InNetworkInsurancePageInfoDto> getPageNumberInsurance(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @NotNull @Min(1) @ApiParam(value = "Page Size", required = true) @RequestParam(value = "pageSize", required = true) Integer pageSize,
            @ApiParam(value = "current insurance id") @RequestParam(value = "insuranceId", required = false) Long insuranceId,
            @ApiParam(value = "current insurance plan id") @RequestParam(value = "insurancePlanId", required = false) Long insurancePlanId
    ) {
        InNetworkInsurancePageInfoDto pageInfo = inNetworkInsuranceService.getPageInfo(insuranceId, insurancePlanId, pageSize);
        return Response.successResponse(pageInfo);
    }

}
