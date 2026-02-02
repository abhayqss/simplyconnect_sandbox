package com.scnsoft.eldermark.web.controller.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.Handset;
import com.scnsoft.eldermark.service.palatiumcare.HandsetService;
import com.scnsoft.eldermark.shared.palatiumcare.HandsetDto;
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
@RequestMapping("/phr/handset")
public class HandsetController {

    private HandsetService handsetService;

    @Autowired
    public void setHandsetService(HandsetService handsetService) {
        this.handsetService = handsetService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Response<Long> saveHandset(@RequestBody HandsetDto handsetDto) {
        Handset handset = handsetService.save(handsetDto);
        Long handsetId = handset.getId();
        if(handsetId != null) {
            return Response.successResponse(handsetId);
        }
        return null;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public HandsetDto getHandsetById(@PathVariable("id") Long id) {
        HandsetDto handsetDto = handsetService.get(id);
        if(handsetDto != null) {
            return handsetDto;
        }
        return null;
    }


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<HandsetDto> getHandsetList() {
        return handsetService.getList();
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Response removeHandsetById(@PathVariable("id") Long id) {
        try {
            handsetService.remove(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.successResponse();
    }
}
