package com.scnsoft.controller;


import com.scnsoft.eldermark.entity.palatiumcare.ResidentLastChange;
import com.scnsoft.eldermark.services.palatiumcare.resident.NotifyExchangeResidentService;
import com.scnsoft.eldermark.services.palatiumcare.resident.NotifyResidentSyncService;
import com.scnsoft.eldermark.shared.palatiumcare.resident.PalCareResidentInDto;
import com.scnsoft.eldermark.shared.palatiumcare.resident.PalCareResidentOutDto;
import com.scnsoft.eldermark.shared.web.entity.Response;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Generated;
import java.util.List;

@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-09-28T14:14:24.000+03:00")
@Api(value = "Resident API", description = "API for residents synchronization")
@RestController
@RequestMapping("api-v2/resident")
public class ResidentSyncController {

    private NotifyExchangeResidentService residentService;

    private NotifyResidentSyncService residentSyncService;

    @Autowired
    public void setResidentService(NotifyExchangeResidentService residentService) {
        this.residentService = residentService;
    }

    @Autowired
    public void setResidentSyncService(NotifyResidentSyncService residentSyncService) {
        this.residentSyncService = residentSyncService;
    }

    @ApiOperation(value = "Used to get ALL the residents from Notify. Should be used as a last resort. " +
            "Only in case of initialization or failure.")
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/sync/all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Response getAllResidents() {
        List<PalCareResidentOutDto> notifyResidentList = residentService.getAllNotifyResidents();
        if(notifyResidentList != null) {
            return Response.successResponse(notifyResidentList);

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
    public Response getChangesForAllResidents() {
        List<ResidentLastChange> residentLastChangeList = residentSyncService.getChanges();
        if(residentLastChangeList != null) {
            return Response.successResponse(residentLastChangeList);
        }
        return Response.errorResponse("No data!", 422);
    }

    @ApiOperation(value = "Used to get a single resident from Notify by its ID. " +
            "Takes the id of the resident.")
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/sync/{residentId:\\d+}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Response<PalCareResidentOutDto> getChangesForResident(@PathVariable("residentId") Long residentId) {
        PalCareResidentOutDto residentOutDto = residentService.getNotifyResident(residentId);
        if(residentOutDto != null) {
            return Response.successResponse(residentOutDto);

        }
        return Response.errorResponse("Can't find the resident!", 422);
    }


    @ApiOperation(value = "Used to add new residents to Notify. Takes a list of residents.")
    @RequestMapping(
            method = RequestMethod.POST, value = "/sync/list",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Response postChangesForAllResidents(
            @ApiParam(value = "List of residents", required = true)
            @RequestBody List<PalCareResidentInDto> palCareResidentInDtoList
    ) {
        try {
            residentService.addNotifyResidentList(palCareResidentInDtoList);
        } catch (Exception e) {
            String sqlExc = e.getMessage();
            return Response.errorResponse(sqlExc, 500);
        }
        return Response.successResponse();
    }

    @ApiOperation(value = "Used to add a new resident to Notify. " +
            "If there is more than one entity the endpoint '/sync/list' is preferable." +
            "Takes a single object.")
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/sync",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Response postChangesForResident(
            @ApiParam(value = "Resident item", required = true)
            @RequestBody PalCareResidentInDto palCareResidentInDto
    ) {
        try {
            residentService.createNotifyResident(palCareResidentInDto);
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
        residentSyncService.removeRecordList(recordIds);
        return Response.successResponse();
    }


}
