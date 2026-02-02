package com.scnsoft.eldermark.web.controller.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.NotifyResident;
import com.scnsoft.eldermark.service.palatiumcare.NotifyResidentService;
import com.scnsoft.eldermark.services.palatiumcare.resident.NotifyResidentSyncService;
import com.scnsoft.eldermark.shared.palatiumcare.resident.NotifyResidentDto;
import com.scnsoft.eldermark.shared.palatiumcare.resident.NotifyResidentFilter;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;
import java.util.List;

@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@RestController
@RequestMapping("/phr/{userId:\\d+}/notify-resident")
public class NotifyResidentController {

    private NotifyResidentService notifyResidentService;

    private NotifyResidentSyncService notifyResidentSyncService;

    @Autowired
    public void setNotifyResidentService(NotifyResidentService notifyResidentService) {
        this.notifyResidentService = notifyResidentService;
    }

    @Autowired
    public void setNotifyResidentSyncService(NotifyResidentSyncService notifyResidentSyncService) {
        this.notifyResidentSyncService = notifyResidentSyncService;
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Response<Long> saveNotifyResident(@RequestBody NotifyResidentDto notifyResidentDto, @PathVariable("userId") Long userId)  {

        Long residentId = notifyResidentDto.getId();
        NotifyResidentDto existingNotifyResident = null;

        if(residentId != null) {
            existingNotifyResident = notifyResidentService.get(residentId);
        }

        NotifyResident notifyResident = null;
        try {
            notifyResident = notifyResidentService.save(notifyResidentDto);
        }
        catch (Exception exc) {
            return Response.errorResponse(exc.getMessage(), 500);
        }

        if(notifyResident != null) {
            if(existingNotifyResident == null) {
                notifyResidentSyncService.addRecordWithStatusCreated(notifyResident);
            } else {
                notifyResidentSyncService.changeRecordStatusToUpdated(notifyResident);
            }
            return Response.successResponse(notifyResident.getId());
        }
        return null;
    }

    @RequestMapping(value = "/{residentId}", method = RequestMethod.GET)
    public NotifyResidentDto getNotifyResidentById(@PathVariable("residentId") Long residentId, @PathVariable("userId") Long userId) {
        NotifyResidentDto notifyResidentDto = notifyResidentService.get(residentId);
        if(notifyResidentDto != null) {
            return notifyResidentDto;
        }
        return null;
    }


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<NotifyResidentDto> getNotifyResidentList(@PathVariable("userId") Long userId) {
        return notifyResidentService.getList();
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Response removeNotifyResidentById(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        try {
            notifyResidentService.remove(id);
            notifyResidentSyncService.changeRecordStatusToRemoved(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.successResponse();
    }

    @RequestMapping(value = "care-team-member-resident/{employeeId:\\d+}", method = RequestMethod.GET)
    public List<NotifyResidentDto> getCareTeamMemberResidentList(
            @PathVariable("employeeId") Long employeeId,
            @PathVariable("userId") Long userId,
            NotifyResidentFilter filter,
            Pageable pageable
    ) {
        return notifyResidentService.getCareTeamMemberResidentList(employeeId, filter, pageable);
    }

    @RequestMapping(value = "community-resident/{employeeId:\\d+}", method = RequestMethod.GET)
    public List<NotifyResidentDto> getCommunityResidentList(
            @PathVariable("employeeId")Long employeeId,
            @PathVariable("userId") Long userId,
            NotifyResidentFilter filter,
            Pageable pageable
    ) {
        return notifyResidentService.getCommunityResidentList(employeeId, filter, pageable);
    }

    @RequestMapping(value = "resident-all/{employeeId:\\d+}", method = RequestMethod.GET)
    public List<NotifyResidentDto> getAllResidentsByEmployeeId(
            @PathVariable("employeeId") Long employeeId,
            @PathVariable("userId") Long userId,
            NotifyResidentFilter filter,
            Pageable pageable
    ) {
        return notifyResidentService.getAllResidentsByEmployeeId(employeeId, filter, pageable);
    }


}
