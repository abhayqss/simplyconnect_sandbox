package com.scnsoft.eldermark.web.controller.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.Location;
import com.scnsoft.eldermark.service.palatiumcare.LocationService;
import com.scnsoft.eldermark.services.palatiumcare.location.NotifyLocationSyncService;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.palatiumcare.location.NotifyLocationDto;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.net.HttpURLConnection;
import java.util.List;


@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@RestController
@RequestMapping("/phr/location")
public class LocationController {

    private LocationService locationService;

    private NotifyLocationSyncService notifyLocationSyncService;

    @Autowired
    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }

    @Autowired
    public void setNotifyLocationSyncService(NotifyLocationSyncService notifyLocationSyncService) {
        this.notifyLocationSyncService = notifyLocationSyncService;
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Response<Long> save(@RequestBody NotifyLocationDto location) {

        Long locationId = location.getId();
        NotifyLocationDto existingLocation = null;
        if(locationId != null) {
            existingLocation = locationService.get(locationId);
        }

        Location savedLocation = null;
        try {
            savedLocation = locationService.save(location);
        }
        catch (Exception exc) {
            return Response.errorResponse(exc.getMessage(), 500);
        }

        if(savedLocation != null) {
            if(existingLocation == null) {
                notifyLocationSyncService.addRecordWithStatusCreated(savedLocation);
            } else {
                notifyLocationSyncService.changeRecordStatusToUpdated(savedLocation);
            }
            return Response.successResponse(location.getId());
        }

        return null;
    }

    @RequestMapping(
            value ="/{id}",
            method = DELETE
    )
    public Response remove(@PathVariable("id") Long id) {
        try {
            locationService.remove(id);
            return Response.successResponse();
        }
        catch (Exception exc) {
            return Response.errorResponse(new PhrException(exc.getMessage()), 500);
        }
    }

    @RequestMapping(value ="/{id}", method = GET)
    public NotifyLocationDto get(@PathVariable("id") Long id) {
        return locationService.get(id);
    }

    @RequestMapping(value ="/list", method = GET)
    public List<NotifyLocationDto> getList() {
        return locationService.getList();
    }

}
