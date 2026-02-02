package com.scnsoft.controller;

import com.scnsoft.eldermark.entity.palatiumcare.FacilityLastChange;
import com.scnsoft.eldermark.services.palatiumcare.facility.NotifyExchangeFacilityService;
import com.scnsoft.eldermark.services.palatiumcare.facility.NotifyFacilitySyncService;
import com.scnsoft.eldermark.shared.palatiumcare.facility.PalCareFacilityInDto;
import com.scnsoft.eldermark.shared.palatiumcare.facility.PalCareFacilityOutDto;
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
@Api(value = "Facility API", description = "API for facilities synchronization")
@RestController
@RequestMapping("api-v2/facility")
public class FacilitySyncController {

    private NotifyExchangeFacilityService facilityService;

    private NotifyFacilitySyncService facilitySyncService;

    @Autowired
    public void setFacilityService(NotifyExchangeFacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @Autowired
    public void setFacilitySyncService(NotifyFacilitySyncService facilitySyncService) {
        this.facilitySyncService = facilitySyncService;
    }

    @ApiOperation(value = "Used to get ALL the facilitys from Notify. Should be used as a last resort. " +
            "Only in case of initialization or failure.")
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/sync/all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Response getAllFacilities() {
        List<PalCareFacilityOutDto> notifyFacilityList = facilityService.getAllFacilities();
        if(notifyFacilityList != null) {
            return Response.successResponse(notifyFacilityList);

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
    public Response getChangesForAllFacilities() {
        List<FacilityLastChange> facilityLastChangeList = facilitySyncService.getChanges();
        if(facilityLastChangeList != null) {
            return Response.successResponse(facilityLastChangeList);
        }
        return Response.errorResponse("No data!", 422);
    }

    @ApiOperation(value = "Used to get a single facility from Notify by its ID. " +
            "Takes the id of the facility.")
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/sync/{facilityId:\\d+}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Response<PalCareFacilityOutDto> getChangesForFacility(@PathVariable("facilityId") Long facilityId) {
        PalCareFacilityOutDto facilityOutDto = facilityService.getFacility(facilityId);
        if(facilityOutDto != null) {
            return Response.successResponse(facilityOutDto);

        }
        return Response.errorResponse("Can't find the facility!", 422);
    }


    @ApiOperation(value = "Used to add new facilitys to Notify. Takes a list of facilities.")
    @RequestMapping(
            method = RequestMethod.POST, value = "/sync/list",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Response postChangesForAllFacilities(
            @ApiParam(value = "List of facilitys", required = true)
            @RequestBody List<PalCareFacilityInDto> palCareFacilityInDtoList
    ) {
        try {
            facilityService.addFacilityList(palCareFacilityInDtoList);
        } catch (Exception e) {
            String sqlExc = e.getMessage();
            return Response.errorResponse(sqlExc, 500);
        }
        return Response.successResponse();
    }

    @ApiOperation(value = "Used to add a new facility to Notify. " +
            "If there is more than one entity the endpoint '/sync/list' is preferable." +
            "Takes a single object.")
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/sync",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Response postChangesForFacility(
            @ApiParam(value = "Facility item", required = true)
            @RequestBody PalCareFacilityInDto palCareFacilityInDto
    ) {
        try {
            facilityService.createFacility(palCareFacilityInDto);
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
        facilitySyncService.removeRecordList(recordIds);
        return Response.successResponse();
    }






}
