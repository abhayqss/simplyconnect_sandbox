package com.scnsoft.eldermark.api.external.web.controller;

import com.scnsoft.eldermark.api.external.service.MedicationsService;
import com.scnsoft.eldermark.api.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.api.shared.web.dto.Response;
import com.scnsoft.eldermark.api.shared.web.dto.ResponseErrorDto;
import com.scnsoft.eldermark.api.shared.web.dto.ResponseValidationErrorDto;
import com.scnsoft.eldermark.api.external.web.dto.MedicationInfoDto;
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
import java.util.logging.Logger;

@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T14:21:48.776+03:00")
@Api(value = "Medications", description = "Medications")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "Not Found", response = ResponseErrorDto.class)
})
@Validated
@RestController
@RequestMapping("/residents/{residentId:\\d+}/medications")
public class MedicationsController {

    final Logger logger = Logger.getLogger(MedicationsController.class.getName());

    private final MedicationsService medicationsService;

    @Autowired
    public MedicationsController(MedicationsService medicationsService) {
        this.medicationsService = medicationsService;
    }

    @ApiOperation(value = "List active medications", notes = "<h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ</pre>")
    @GetMapping(value = "/active")
    public Response<List<MedicationInfoDto>> getActiveMedications(
            @Min(1)
            @ApiParam(value = "resident id", required = true) @PathVariable("residentId") Long residentId,
            @Min(1)
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all medications), ≥ 1")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        final Pageable pageable = PaginationUtils.buildPageable(pageSize, page);
        final Page<MedicationInfoDto> medications = medicationsService.getActive(residentId, pageable);
        return Response.pagedResponse(medications);
    }

    @ApiOperation(value = "Get medications history", notes = "<h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ</pre>")
    @GetMapping(value = "/inactive")
    public Response<List<MedicationInfoDto>> getMedicationsHistory(
            @Min(1)
            @ApiParam(value = "resident id", required = true) @PathVariable("residentId") Long residentId,
            @Min(1)
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all medications), ≥ 1")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        final Pageable pageable = PaginationUtils.buildPageable(pageSize, page);
        final Page<MedicationInfoDto> medications = medicationsService.getInactive(residentId, pageable);
        return Response.pagedResponse(medications);
    }

}
