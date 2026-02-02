package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.service.PhysiciansService;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.web.entity.PhysicianDto;
import com.scnsoft.eldermark.web.entity.PhysicianExtendedDto;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;
import java.util.List;

/**
 * @author phomal
 * Created on 5/2/2017.
 */
@Api(value = "PHR - Physicians", description = "Physicians")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class)
})
@RestController
@RequestMapping("/phr/physicians")
public class PhysiciansController {

    @Autowired
    PhysiciansService physiciansService;


    @ApiOperation(value = "Get a specific physician")
    @GetMapping(value = "/{physicianId:\\d+}")
    public Response<PhysicianExtendedDto> getPhysician(
            @ApiParam(value = "physician id", required = true) @PathVariable("physicianId") Long physicianId
    ) {
        final PhysicianExtendedDto dto = physiciansService.getPhysician(physicianId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get a list of physicians")
    @GetMapping
    public Response<List<PhysicianDto>> getPhysicians(
            @RequestHeader(value = "X-App-Ver", required = false) String appVersion
    ) {
        if ("1.0a".equalsIgnoreCase(appVersion)) throw new PhrException(PhrExceptionType.APP_VERSION_NOT_SUPPORTED);
        final List<PhysicianDto> dto = physiciansService.listPhysicians();
        return Response.successResponse(dto);
    }

}
