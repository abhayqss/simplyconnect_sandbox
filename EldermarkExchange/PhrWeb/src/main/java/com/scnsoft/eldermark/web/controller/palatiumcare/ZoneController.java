package com.scnsoft.eldermark.web.controller.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.Zone;
import com.scnsoft.eldermark.service.palatiumcare.ZoneService;
import com.scnsoft.eldermark.mapper.ZoneDto;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.HttpURLConnection;
import java.util.List;

@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@Controller
@RequestMapping("/phr/zone")
public class ZoneController {

    private ZoneService zoneService;

    @Autowired
    public void setZoneService(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Response<Long> saveZone(@RequestBody ZoneDto zoneDto) {
        Zone zone = zoneService.save(zoneDto);
        Long id = zone.getId();
        if(id != null) {
            return Response.successResponse(id);
        }
        return null;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ZoneDto getZoneById(@PathVariable("id") Long id) {
        ZoneDto zoneDto = zoneService.get(id);
        if(zoneDto != null) {
            return zoneDto;
        }
        return null;
    }


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<ZoneDto> getZoneList() {
        return zoneService.getList();
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Response removeZoneById(@PathVariable("id") Long id) {
        try {
            zoneService.remove(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.successResponse();
    }

    
}
