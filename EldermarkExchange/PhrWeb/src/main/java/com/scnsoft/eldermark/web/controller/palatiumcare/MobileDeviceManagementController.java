package com.scnsoft.eldermark.web.controller.palatiumcare;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.entity.palatiumcare.MobileDevice;
import com.scnsoft.eldermark.service.palatiumcare.MobileDeviceService;
import com.scnsoft.eldermark.shared.palatiumcare.MobileDeviceDto;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;

@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@RestController
@RequestMapping("/phr/mobile-device-management")
public class MobileDeviceManagementController {

    private MobileDeviceService mobileDeviceService;

    @Autowired
    public void setMobileDeviceService(MobileDeviceService mobileDeviceService) {
        this.mobileDeviceService = mobileDeviceService;
    }

    @RequestMapping(value = "/request-device-register", method = RequestMethod.POST)
    public String registerDevice(@RequestBody MobileDeviceDto mobileDeviceDto) {
        return mobileDeviceService.requestDeviceRegister(mobileDeviceDto);
    }

    @RequestMapping(value = "/device-status/{uniqueDeviceId}", method = RequestMethod.GET)
    public Response getDeviceStatusByDeviceUID(@PathVariable("uniqueDeviceId") String deviceUID) {
        String status = mobileDeviceService.getMobileDeviceStatusByUID(deviceUID);
        return Response.successResponse(status);
    }

    @RequestMapping(value = "userId/{userId}/activate-device/{deviceId}", method = RequestMethod.GET)
    public void activateDevice(@PathVariable("userId") Long userId, @PathVariable("deviceId") Long deviceId) {
        //@todo: security checks here!
        mobileDeviceService.activateDevice(deviceId);
    }

    @RequestMapping(value = "userId/{userId}/deactivate-device/{deviceId}", method = RequestMethod.GET)
    public void deactivateDevice(@PathVariable("userId") Long userId, @PathVariable("deviceId") Long deviceId) {
        //@todo: security checks here!
        mobileDeviceService.deactivateDevice(deviceId);
    }

    @RequestMapping(value = "userId/{userId}/list", method = RequestMethod.GET)
    public Iterable<MobileDevice> getMobileDeviceList(@PathVariable("userId") Long userId) {
        //@todo: security checks here!
         return mobileDeviceService.getMobileDeviceList();
    }


}
