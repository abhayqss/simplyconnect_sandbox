package com.scnsoft.controller;


import com.scnsoft.eldermark.entity.palatiumcare.LocationLastChange;
import com.scnsoft.eldermark.services.palatiumcare.location.NotifyExchangeLocationService;
import com.scnsoft.eldermark.services.palatiumcare.location.NotifyLocationSyncService;
import com.scnsoft.eldermark.shared.palatiumcare.location.PalCareLocationInDto;
import com.scnsoft.eldermark.shared.palatiumcare.location.PalCareLocationOutDto;
import com.scnsoft.eldermark.shared.web.entity.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Generated;
import java.util.List;

@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-09-28T14:14:24.000+03:00")
@Api(value = "Location API", description = "API for location synchronization")
@RestController
@RequestMapping("api-v2/location")
public class LocationSyncController {

    private NotifyExchangeLocationService locationService;

    private NotifyLocationSyncService locationSyncService;

    @Autowired
    public void setLocationService(NotifyExchangeLocationService locationService) {
        this.locationService = locationService;
    }

    @Autowired
    public void setLocationSyncService(NotifyLocationSyncService locationSyncService) {
        this.locationSyncService = locationSyncService;
    }

    @ApiOperation(value = "Used to get ALL the locations from Notify. Should be used as a last resort. " +
            "Only in case of initialization or failure.")
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/sync/all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Response getAllLocations() {
        List<PalCareLocationOutDto> locationList = locationService.getAllLocations();
        if(locationList != null) {
            return Response.successResponse(locationList);

        }
        return Response.errorResponse("No data!", 422);
    }

    @ApiOperation(value = "Used to get updated entities. " +
            "Only in case of initialization or failure.")
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/sync/list",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Response getChangesForAllLocations() {
        List<LocationLastChange> locationLastChangeList = locationSyncService.getChanges();
        if(locationLastChangeList != null) {
            return Response.successResponse(locationLastChangeList);
        }
        return Response.errorResponse("No data!", 422);
    }

    @ApiOperation(value = "Used to get a single location from Notify by its ID. " +
            "Takes the id of the location.")
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/sync/{locationId:\\d+}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Response<PalCareLocationOutDto> getChangesForLocation(@PathVariable("locationId") Long locationId) {
        PalCareLocationOutDto locationOutDto = locationService.getLocation(locationId);
        if(locationOutDto != null) {
            return Response.successResponse(locationOutDto);

        }
        return Response.errorResponse("Can't find the location!", 422);
    }


    @ApiOperation(value = "Used to add new locations to Notify. Takes a list of locations.")
    @RequestMapping(
            method = RequestMethod.POST, value = "/sync/list",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Response postChangesForAllLocations(
            @ApiParam(value = "List of locations", required = true)
            @RequestBody List<PalCareLocationInDto> palCareLocationInDtoList
    ) {
        try {
            locationService.addLocationList(palCareLocationInDtoList);
        } catch (Exception e) {
            String sqlExc = e.getMessage();
            return Response.errorResponse(sqlExc, 500);
        }
        return Response.successResponse();
    }

    @ApiOperation(value = "Used to add a new location to Notify. " +
            "If there is more than one entity the endpoint '/sync/list' is preferable." +
            "Takes a single object.")
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/sync",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Response postChangesForLocation(
            @ApiParam(value = "Location item", required = true)
            @RequestBody PalCareLocationInDto palCareLocationInDto
    ) {
        try {
            locationService.createLocation(palCareLocationInDto);
        } catch (Exception e) {
            String sqlExc = e.getMessage();
            return Response.errorResponse(sqlExc, 500);
        }
        return Response.successResponse();
    }

    @ApiOperation(value = "Used to confirm that requested items were received. Take a 'changeId' list which were previously received with requested items.")
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/sync-confirmation",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Response confirmReception(
            @ApiParam(value = "IDs which were previously received with requested items in the field 'changeId'", required = true)
            @RequestBody List<Long> recordIds
    ) {
        locationSyncService.removeRecordList(recordIds);
        return Response.successResponse();
    }


}
