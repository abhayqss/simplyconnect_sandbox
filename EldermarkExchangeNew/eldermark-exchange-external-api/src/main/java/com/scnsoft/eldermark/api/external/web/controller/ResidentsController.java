package com.scnsoft.eldermark.api.external.web.controller;

import com.scnsoft.eldermark.api.external.facade.ExternalApiDocumentFacade;
import com.scnsoft.eldermark.api.external.service.ResidentsService;
import com.scnsoft.eldermark.api.shared.web.dto.Response;
import com.scnsoft.eldermark.api.shared.web.dto.ResponseErrorDto;
import com.scnsoft.eldermark.api.shared.web.dto.ResponseValidationErrorDto;
import com.scnsoft.eldermark.api.external.web.dto.ResidentDto;
import com.scnsoft.eldermark.api.external.web.dto.ResolveItiPatientIdentifierRequestDto;
import com.scnsoft.eldermark.entity.document.DocumentReport;
import io.swagger.annotations.*;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Generated;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Logger;

@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T14:21:48.776+03:00")
@Api(value = "Residents", description = "Residents")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "Not Found", response = ResponseErrorDto.class)
})
@Validated
@RestController
@RequestMapping("/residents")
public class ResidentsController {

    final Logger logger = Logger.getLogger(ResidentsController.class.getName());

    private final ResidentsService residentsService;
    private final ExternalApiDocumentFacade documentFacade;

    @Autowired
    public ResidentsController(ResidentsService residentsService, ExternalApiDocumentFacade documentFacade) {
        this.residentsService = residentsService;
        this.documentFacade = documentFacade;
    }

    @ApiOperation(value = "Get resident details", notes = "General information about resident, without partner-specific info. <h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ</pre>")
    @GetMapping(value = "/{residentId:\\d+}")
    public Response<ResidentDto> getResident(
            @Min(1)
            @ApiParam(value = "resident id", required = true)
            @PathVariable("residentId") Long residentId
    ) {
        final ResidentDto dto = residentsService.get(residentId);
        return Response.successResponse(dto);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "Not Found", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Get residentId by ITI patient identifier", notes = "Get residentId by ITI patient identifier. <h3>Required privileges</h3> <pre>ORGANIZATION_READ</pre>")
    @PostMapping(value = "/iti/resolve", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> resolveITIResidentUsingPOST(
            @ApiParam(value = "resident id" , required = true ) @RequestBody ResolveItiPatientIdentifierRequestDto itIPatientIdentifier
    ) {
        return Response.successResponse(residentsService.resolveItiPatientIdentifier(itIPatientIdentifier));
    }

    @ApiOperation(value = "Get resident's ccd document", notes = "Get resident's ccd document. <h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ</pre>")
    @GetMapping(value = "/{residentId:\\d+}/documents/ccd")
    public ResponseEntity<byte[]> getResidentCcd(
            @Min(1) @ApiParam(value = "resident id", required = true) @PathVariable("residentId") Long residentId,
            @ApiParam(value = "A flag which indicates whether ccd document should contain aggregated data.", defaultValue = "true")
            @RequestParam(value = "aggregated", required = false, defaultValue="true") Boolean aggregated
    ) throws IOException {
        var ccdReport = documentFacade.generateContinuityOfCareDocument(residentId, Boolean.TRUE.equals(aggregated));
        return buildCcdResponseEntity(ccdReport);
    }

    private ResponseEntity<byte[]> buildCcdResponseEntity(DocumentReport report) throws IOException {
        final byte[] reportBytes = IOUtils.toByteArray(report.getInputStream());
        return ResponseEntity.ok()
                .headers(buildCcdResponseHeaders(report))
                .contentType(MediaType.valueOf(report.getMimeType()))
                .contentLength(reportBytes.length)
                .body(reportBytes);
    }

    private HttpHeaders buildCcdResponseHeaders(DocumentReport report) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + report.getDocumentTitle());
        return headers;
    }

}
