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
@Api(value = "Employees (Nucleus)", description = "Nucleus-specific data for Employees")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@Validated
@RestController
@RequestMapping("/employees/{employeeId:\\d+}/nucleus")
public class EmployeesNucleusController {

    final Logger logger = Logger.getLogger(EmployeesNucleusController.class.getName());

    private final NucleusService nucleusService;

    @Autowired
    public EmployeesNucleusController(NucleusService nucleusService) {
        this.nucleusService = nucleusService;
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_CONFLICT, message = "Conflict", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Create Nucleus device for Employee",
            notes = "Add Nucleus device for employee. An Employee can't have duplicated Nucleus devices associated (Device ID should be unique), but multiple Employees or Residents can share the same Nucleus device.<h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ, SPECIAL_NUCLEUS</pre>")
    @PostMapping(value = "/devices", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<NucleusDeviceDto> addEmployeeNucleusDevice(
            @Min(1)
            @ApiParam(value = "employee id", required = true)
            @PathVariable("employeeId") Long employeeId,
            @Valid
            @ApiParam(value = "Nucleus device", required = true)
            @RequestBody NucleusDeviceDto body,
            Errors errors
    ) {
        if (errors.hasErrors()) {
            throw ValidationExceptionFactory.fromBindingErrors(errors);
        }
        final NucleusDeviceDto dto = nucleusService.createDeviceForEmployee(employeeId, body);
        return Response.successResponse(dto);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_CONFLICT, message = "Conflict", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Add Nucleus info for Employee",
            notes = "Add Nucleus-specific information for employee. An Employee can't have multiple Nucleus user IDs associated, but multiple Employees (belonging to different organizations) can share the same Nucleus user ID. <h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ, SPECIAL_NUCLEUS</pre>")
    @PostMapping(value = "/info", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<NucleusInfoDto> addEmployeeNucleusInfo(
            @Min(1)
            @ApiParam(value = "employee id", required = true)
            @PathVariable("employeeId") Long employeeId,
            @Valid
            @ApiParam(value = "Nucleus info", required = true)
            @RequestBody NucleusInfoDto body,
            Errors errors
    ) {
        if (errors.hasErrors()) {
            throw ValidationExceptionFactory.fromBindingErrors(errors);
        }
        final NucleusInfoDto dto = nucleusService.createInfoForEmployee(employeeId, body);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Delete Nucleus info for Employee",
            notes = "Remove Nucleus-specific information for employee <h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ, SPECIAL_NUCLEUS</pre>")
    @DeleteMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response deleteEmployeeNucleusInfo(
            @Min(1)
            @ApiParam(value = "employee id", required = true)
            @PathVariable("employeeId") Long employeeId
    ) {
        nucleusService.deleteInfoForEmployee(employeeId);
        return Response.successResponse();
    }

    @ApiOperation(value = "List Nucleus devices for Employee",
            notes = "Nucleus-specific information for employee <h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ, SPECIAL_NUCLEUS</pre>")
    @GetMapping(value = "/devices")
    public Response<List<NucleusDeviceDto>> getEmployeeNucleusDevices(
            @Min(1)
            @ApiParam(value = "employee id", required = true)
            @PathVariable("employeeId") Long employeeId
    ) {
        final List<NucleusDeviceDto> dto = nucleusService.getDevicesByEmployee(employeeId);
        return Response.successResponse(dto);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "Not Found", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Get Nucleus info for Employee",
            notes = "Nucleus-specific information for employee <h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ, SPECIAL_NUCLEUS</pre>")
    @GetMapping(value = "/info")
    public Response<NucleusInfoDto> getEmployeeNucleusInfo(
            @Min(1)
            @ApiParam(value = "employee id", required = true)
            @PathVariable("employeeId") Long employeeId
    ) {
        final NucleusInfoDto dto = nucleusService.getInfoByEmployee(employeeId);
        return Response.successResponse(dto);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "Not Found", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Update Nucleus info for Employee",
            notes = "Update Nucleus-specific information for employee <h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ, SPECIAL_NUCLEUS</pre>")
    @PutMapping(value = "/info", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response updateEmployeeNucleusInfo(
            @Min(1)
            @ApiParam(value = "employee id", required = true)
            @PathVariable("employeeId") Long employeeId,
            @Valid
            @ApiParam(value = "Nucleus info", required = true)
            @RequestBody NucleusInfoDto body,
            Errors errors
    ) {
        if (errors.hasErrors()) {
            throw ValidationExceptionFactory.fromBindingErrors(errors);
        }
        nucleusService.updateInfoForEmployee(employeeId, body);
        return Response.successResponse();
    }

}
