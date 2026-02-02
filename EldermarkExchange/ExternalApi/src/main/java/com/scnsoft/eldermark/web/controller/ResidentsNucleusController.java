package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.service.NucleusService;
import com.scnsoft.eldermark.shared.exception.ValidationExceptionFactory;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.shared.web.entity.ResponseValidationErrorDto;
import com.scnsoft.eldermark.web.entity.NucleusDeviceDto;
import com.scnsoft.eldermark.web.entity.NucleusInfoDto;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.logging.Logger;

@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T14:21:48.776+03:00")
@Api(value = "Residents (Nucleus)", description = "Nucleus-specific data for Residents")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "Not Found", response = ResponseErrorDto.class)
})
@Validated
@RestController
@RequestMapping("/residents/{residentId:\\d+}/nucleus")
public class ResidentsNucleusController {

    final Logger logger = Logger.getLogger(ResidentsNucleusController.class.getName());

    private final NucleusService nucleusService;

    @Autowired
    public ResidentsNucleusController(NucleusService nucleusService) {
        this.nucleusService = nucleusService;
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_CONFLICT, message = "Conflict", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Create Nucleus device for Resident",
            notes = "Add Nucleus device for resident. A Resident can't have duplicated Nucleus devices associated (Device ID should be unique), but multiple Residents or Employees can share the same Nucleus device.<h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ, SPECIAL_NUCLEUS</pre>")
    @PostMapping(value = "/devices", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<NucleusDeviceDto> addResidentNucleusDevice(
            @Min(1)
            @ApiParam(value = "resident id", required = true)
            @PathVariable("residentId") Long residentId,
            @Valid
            @ApiParam(value = "Nucleus device", required = true)
            @RequestBody NucleusDeviceDto body,
            Errors errors
    ) {
        if (errors.hasErrors()) {
            throw ValidationExceptionFactory.fromBindingErrors(errors);
        }
        final NucleusDeviceDto dto = nucleusService.createDeviceForResident(residentId, body);
        return Response.successResponse(dto);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_CONFLICT, message = "Conflict", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Add Nucleus info for Resident",
            notes = "Add Nucleus-specific information for resident. A Resident can't have multiple Nucleus user IDs associated, but multiple Residents can share the same Nucleus user ID. <h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ, SPECIAL_NUCLEUS</pre>")
    @PostMapping(value = "/info", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<NucleusInfoDto> addResidentNucleusInfo(
            @Min(1)
            @ApiParam(value = "resident id", required = true)
            @PathVariable("residentId") Long residentId,
            @Valid
            @ApiParam(value = "Nucleus info", required = true)
            @RequestBody NucleusInfoDto body,
            Errors errors
    ) {
        if (errors.hasErrors()) {
            throw ValidationExceptionFactory.fromBindingErrors(errors);
        }
        final NucleusInfoDto dto = nucleusService.createInfoForResident(residentId, body);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Delete Nucleus info for Resident",
            notes = "Remove Nucleus-specific information for resident <h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ, SPECIAL_NUCLEUS</pre>")
    @DeleteMapping(value = "/info")
    public Response deleteResidentNucleusInfo(
            @Min(1)
            @ApiParam(value = "resident id", required = true)
            @PathVariable("residentId") Long residentId
    ) {
        nucleusService.deleteInfoForResident(residentId);
        return Response.successResponse();
    }

    @ApiOperation(value = "List Nucleus devices for Resident",
            notes = "<h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ, SPECIAL_NUCLEUS</pre>")
    @GetMapping(value = "/devices")
    public Response<List<NucleusDeviceDto>> getResidentNucleusDevices(
            @Min(1)
            @ApiParam(value = "resident id", required = true)
            @PathVariable("residentId") Long residentId
    ) {
        final List<NucleusDeviceDto> dto = nucleusService.getDevicesByResident(residentId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get Nucleus info for Resident",
            notes = "Nucleus-specific information for resident <h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ, SPECIAL_NUCLEUS</pre>")
    @GetMapping(value = "/info")
    public Response<NucleusInfoDto> getResidentNucleusInfo(
            @Min(1)
            @ApiParam(value = "resident id", required = true)
            @PathVariable("residentId") Long residentId
    ) {
        final NucleusInfoDto dto = nucleusService.getInfoByResident(residentId);
        return Response.successResponse(dto);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Update Nucleus info for Resident",
            notes = "Update Nucleus-specific information for resident <h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ, SPECIAL_NUCLEUS</pre>")
    @PutMapping(value = "/info", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response updateResidentNucleusInfo(
            @Min(1)
            @ApiParam(value = "resident id", required = true)
            @PathVariable("residentId") Long residentId,
            @Valid
            @ApiParam(value = "Nucleus info", required = true)
            @RequestBody NucleusInfoDto body,
            Errors errors
    ) {
        if (errors.hasErrors()) {
            throw ValidationExceptionFactory.fromBindingErrors(errors);
        }
        nucleusService.updateInfoForResident(residentId, body);
        return Response.successResponse();
    }

}
