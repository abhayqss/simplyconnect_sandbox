package com.scnsoft.eldermark.web.controller.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.Facility;
import com.scnsoft.eldermark.service.palatiumcare.FacilityService;
import com.scnsoft.eldermark.shared.palatiumcare.FacilityDto;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;
import java.util.List;

@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@RestController
@RequestMapping("/phr/facility")
public class FacilityController {

    private FacilityService facilityService;

    @Autowired
    public void setFacilityService(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Response<Long> saveFacility(@RequestBody FacilityDto facilityDto) {
        Facility facility = facilityService.save(facilityDto);
        Long id = facility.getId();
        if(id != null) {
            return Response.successResponse(id);
        }
        return null;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public FacilityDto getFacilityById(@PathVariable("id") Long id) {
        FacilityDto facilityDto = facilityService.get(id);
        if(facilityDto != null) {
            return facilityDto;
        }
        return null;
    }


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<FacilityDto> getFacilityList() {
        return facilityService.getList();
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Response removeFacilityById(@PathVariable("id") Long id) {
        try {
            facilityService.remove(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.successResponse();
    }

}
