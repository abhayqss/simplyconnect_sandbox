package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.service.NucleusService;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.shared.validation.Uuid;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.shared.web.entity.ResponseValidationErrorDto;
import com.scnsoft.eldermark.web.entity.NucleusDeviceDto;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Generated;
import javax.validation.constraints.Min;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-12T14:09:35.338+03:00")
@Api(value = "Nucleus Devices", description = "Nucleus-specific data (devices)")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
})
@Validated
@RestController
@RequestMapping("/nucleus/devices")
public class NucleusController {

    final Logger logger = Logger.getLogger(NucleusController.class.getName());

    private final NucleusService nucleusService;

    @Autowired
    public NucleusController(NucleusService nucleusService) {
        this.nucleusService = nucleusService;
    }

    @ApiOperation(value = "Delete Nucleus device",
            notes = "Complete and immediate deletion of a specific Nucleus device from Simply Connect system.<h3>Required privileges</h3> <pre>SPECIAL_NUCLEUS</pre>")
    @DeleteMapping(value = "/{deviceId}")
    public Response deleteNucleusDevice(
            @Uuid
            @ApiParam(value = "nucleus device id (36-char UUID, e.g. \"1a8a0c8d-7e16-41be-a73d-c6cc5ed68f07\")", required = true)
            @PathVariable("deviceId") String deviceId
    ) {
        nucleusService.deleteDevice(UUID.fromString(deviceId));
        return Response.successResponse();
    }

    @ApiOperation(value = "Get Nucleus device details", notes = "<h3>Required privileges</h3> <pre>SPECIAL_NUCLEUS</pre>", tags={"not-implemented"})
    @GetMapping(value = "/{deviceId}")
    public Response<NucleusDeviceDto> getNucleusDevice(
            @Uuid
            @ApiParam(value = "nucleus device id (36-char UUID, e.g. \"1a8a0c8d-7e16-41be-a73d-c6cc5ed68f07\")", required = true)
            @PathVariable("deviceId") String deviceId
    ) {
        final NucleusDeviceDto dto = nucleusService.getDevice(UUID.fromString(deviceId));
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "List all Nucleus devices",
            notes = "A paginated list of all known Nucleus devices. Some devices in the list may appear as duplicates if they're associated with multiple Employees / Residents. <h3>Required privileges</h3> <pre>SPECIAL_NUCLEUS</pre>")
    @GetMapping
    public Response<List<NucleusDeviceDto>> getNucleusDevices(
            @Min(1)
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all devices), ≥ 1")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        final Pageable pageable = PaginationUtils.buildPageable(pageSize, page);
        final Page<NucleusDeviceDto> devices = nucleusService.getAllDevices(pageable);
        return Response.pagedResponse(devices);
    }

}
