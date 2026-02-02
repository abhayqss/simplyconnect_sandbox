package com.scnsoft.controller;

import com.scnsoft.dto.incoming.PalCareDeviceTypeDto;
import com.scnsoft.eldermark.entity.palatiumcare.DeviceType;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.service.PalCareDeviceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("api-v2/device-type")
public class DeviceTypeSyncController {

    private PalCareDeviceTypeService deviceTypeService;

    @Autowired
    public void setDeviceTypeService(PalCareDeviceTypeService deviceTypeService) {
        this.deviceTypeService = deviceTypeService;
    }

    @RequestMapping(method = POST)
    public Response<Long> save(@RequestBody PalCareDeviceTypeDto deviceType) {
        DeviceType savedDeviceType = deviceTypeService.save(deviceType);
        if(savedDeviceType != null && savedDeviceType.getId() >= 0) {
            return Response.successResponse(savedDeviceType.getId());
        }
        return null;
    }

    @RequestMapping(value ="/{id}", method = DELETE)
    public Response remove(@PathVariable("id") Long id) {
        try {
            deviceTypeService.remove(id);
            return Response.successResponse();
        }
        catch (Exception exc) {
            return Response.errorResponse(new PhrException(exc.getMessage()), 500);
        }
    }

    @RequestMapping(value ="/{id}", method = GET)
    public PalCareDeviceTypeDto get(@PathVariable("id") Long id) {
        return deviceTypeService.get(id);
    }

    @RequestMapping(value ="/list", method = GET)
    public List<PalCareDeviceTypeDto> getList() {
        return deviceTypeService.getList();
    }

}
