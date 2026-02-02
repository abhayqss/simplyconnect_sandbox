package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.service.PhysiciansService;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.web.entity.PhysicianExtendedDto;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Generated;
import javax.validation.constraints.Min;
import java.net.HttpURLConnection;
import java.util.logging.Logger;

@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-15T10:23:24.023+03:00")
@Api(value = "Physicians", description = "Physicians")
@ApiResponses({
    @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
    @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@Validated
@RestController
@RequestMapping("/physicians")
public class PhysiciansController {

    final Logger logger = Logger.getLogger(PhysiciansController.class.getName());

    private final PhysiciansService physiciansService;

    @Autowired
    public PhysiciansController(PhysiciansService physiciansService) {
        this.physiciansService = physiciansService;
    }

    @ApiOperation(value = "Get a specific physician")
    @GetMapping(value = "/{physicianId:\\d+}")
    public Response<PhysicianExtendedDto> getPhysician(
         @Min(1)
         @ApiParam(value = "physician id", required = true)
         @PathVariable("physicianId") Long physicianId
    ) {
        final PhysicianExtendedDto dto = physiciansService.get(physicianId);
        return Response.successResponse(dto);
    }

}
