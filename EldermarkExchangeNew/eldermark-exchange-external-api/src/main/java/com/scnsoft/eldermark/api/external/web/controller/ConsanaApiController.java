package com.scnsoft.eldermark.api.external.web.controller;

import com.scnsoft.eldermark.api.external.service.ConsanaService;
import com.scnsoft.eldermark.api.external.web.dto.ConsanaResidentDto;
import com.scnsoft.eldermark.api.external.web.dto.ConsanaXrefPatientIdDto;
import com.scnsoft.eldermark.api.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.api.shared.web.dto.Response;
import com.scnsoft.eldermark.api.shared.web.dto.ResponseErrorDto;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Generated;
import javax.validation.constraints.Min;
import java.net.HttpURLConnection;
import java.util.Optional;

@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-11-13T18:49:56.390+03:00")
@Api(value = "consana", description = "Consana-specific data")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "Not Found", response = ResponseErrorDto.class)
})
@Validated
@RestController
@RequestMapping("/consana")
public class ConsanaApiController {

    private final ConsanaService consanaService;

    @Autowired
    public ConsanaApiController(ConsanaService consanaService) {
        this.consanaService = consanaService;
    }

    @ApiOperation(value = "Get residentId by Consana xref patinent ID", notes = "Get residentId by Consana xref ID. <h3>Required privileges</h3> <pre>SPECIAL_CONSANA</pre>")
    @PostMapping(value = "/residents/resolve", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> resolveConsanaXrefId(
            @ApiParam(value = "Xref id", required = true) @RequestBody ConsanaXrefPatientIdDto consanaXrefPatientIdDto
    ) {
        final Optional<Long> residentIdOpt = consanaService.getResidentIdByXref(consanaXrefPatientIdDto);
        return residentIdOpt
                .map(Response::successResponse)
                .orElseGet(() -> Response.errorResponse(PhrExceptionType.NOT_FOUND));
    }

    @ApiOperation(value = "Get resident details", notes = "Consana-specific information about resident. <h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ</pre>")
    @GetMapping(value = "/residents/{residentId:\\d+}")
    public Response<ConsanaResidentDto> getResident(
            @Min(1)
            @ApiParam(value = "resident id", required = true)
            @PathVariable("residentId") Long residentId
    ) {
        final ConsanaResidentDto dto = consanaService.getResident(residentId);
        return Response.successResponse(dto);
    }
}
