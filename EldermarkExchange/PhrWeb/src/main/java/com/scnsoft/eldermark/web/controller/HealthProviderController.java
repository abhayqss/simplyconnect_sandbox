package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.service.AsyncHealthProviderService;
import com.scnsoft.eldermark.service.HealthProviderService;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.web.entity.HealthProviderDto;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;
import java.util.List;

/**
 * Manage Health Providers of Personal Health Record
 * Created by averazub on 1/12/2017.
 */
@Api(value = "PHR - Health Provider", description = "Health Provider of Personal Health Record")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@RestController
@RequestMapping("/phr/{userId:\\d+}/healthProvider")
public class HealthProviderController {

    @Autowired
    HealthProviderService healthProviderService;

    @Autowired
    AsyncHealthProviderService asyncHealthProviderService;

    @ApiOperation(value = "Get a list of Health Providers",
            notes = "When 'Display Merged Data' mode is active, the `current` attribute is true for all returned health providers.")
    @GetMapping
    public Response<List<HealthProviderDto>> getHealthProviders(@PathVariable("userId") Long userId) {
        // this check should be executed on the main request thread so it's separated from the following asynchronously called method
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        asyncHealthProviderService.updateUserResidentRecords(userId);
        return Response.successResponse(healthProviderService.getHealthProviders(userId));
    }

    @ApiOperation(value = "Change Health Provider",
            notes = "Use special value of `{residentId} = 0` to turn on 'Display Merged Data' mode")
    @PostMapping(value="/{residentId:\\d+}")
    public Response<List<HealthProviderDto>> changeHealthProvider(@PathVariable("userId") Long userId, @PathVariable("residentId") Long residentId) {
        return Response.successResponse(healthProviderService.setCurrentHealthProvider(userId, residentId));
    }

}
