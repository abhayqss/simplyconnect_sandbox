package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.entity.phr.VitalSignType;
import com.scnsoft.eldermark.service.VitalSignsService;
import com.scnsoft.eldermark.shared.exception.ValidationExceptionFactory;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.shared.web.entity.ResponseValidationErrorDto;
import com.scnsoft.eldermark.web.entity.VitalSignObservationDetailsDto;
import com.scnsoft.eldermark.web.entity.VitalSignObservationReport;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;
import java.util.logging.Logger;

@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T14:21:48.776+03:00")
@Api(value = "Vital Signs", description = "Biometric data")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "Not Found", response = ResponseErrorDto.class)
})
@Validated
@RestController
@RequestMapping("/residents/{residentId:\\d+}/vitalSigns")
public class VitalSignsController {

    final Logger logger = Logger.getLogger(VitalSignsController.class.getName());

    private final VitalSignsService vitalSignsService;

    @Autowired
    public VitalSignsController(VitalSignsService vitalSignsService) {
        this.vitalSignsService = vitalSignsService;
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Create Vital Sign observation",
            notes = "Submit biometric data for a patient. The incoming data is validated and observation is not created in case if validation fails.<br/>" +
                    "Please note that<ul>" +
                    "<li><p>Batch creation is not supported. Only one vital sign observation per request.</p></li> <li><p>`\"type\"` and `\"loinc\"` properties are mutually exclusive - if `\"type\"` is specified then `\"loinc\"` should be `null` and vice versa. The below example value contains an error. Remove either `\"type\"` or `\"loinc\"` property before sending a test request.</p></li>" +
                    "<li><p>`\"loinc\".\"displayName\"` should be a correct fully-specified name of vital sign in English language.</p></li>" +
                    "<li><p>`\"loinc\".\"code\"`, `\"loinc\".\"displayName\"`, and `\"unit\"` are persisted \"as is\". This means that Simply Connect application doesn't validate these properties against the <a href=\"https://loinc.org/get-started/what-loinc-is/\">LOINC</a> and <a href=\"http://unitsofmeasure.org/trac\">UCUM</a> vocabularies. It's a responsibility of request sender to send only valid measurement units, vital sign codes, and display names.</p></li>" +
                    "<li><p>`\"value\"` is of type Number. So vital signs requiring string representation (e.g. <a href=\"https://r.details.loinc.org/LOINC/8352-7.html?sections=Comprehensive\">\"8352-7\" - \"Clothing worn during measure\"</a> or <a href=\"https://r.details.loinc.org/LOINC/8327-9.html?sections=Comprehensive\">\"8327-9\" - \"Body temperature measurement site\"</a>) are not supported.</p></li>" +
                    "<li><p>Trailing and leading spaces in `\"loinc\".\"displayName\"` and `\"unit\"` are trimmed.</p></li></ul>" +
                    "<h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ, VITALSIGN_CREATE</pre>")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<VitalSignObservationDetailsDto> createVitalSign(
            @Min(1)
            @ApiParam(value = "resident id", required = true)
            @PathVariable("residentId") Long residentId,
            @Valid
            @ApiParam(value = "Vital Sign observation", required = true)
            @RequestBody VitalSignObservationDetailsDto body,
            Errors errors
    ) {
        if (errors.hasErrors()) {
            throw ValidationExceptionFactory.fromBindingErrors(errors);
        }
        final VitalSignObservationDetailsDto dto = vitalSignsService.create(residentId, body);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get Vital Sign observation", notes = "<h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ</pre>", tags = {"not-implemented"})
    @GetMapping(value = "/{vitalSignId:\\d+}")
    public Response<VitalSignObservationDetailsDto> getVitalSign(
            @Min(1)
            @ApiParam(value = "resident id", required = true)
            @PathVariable("residentId") Long residentId,
            @Min(1)
            @ApiParam(value = "vital sign id", required = true)
            @PathVariable("vitalSignId") Long vitalSignId
    ) {
        final VitalSignObservationDetailsDto dto = vitalSignsService.get(residentId, vitalSignId);
        return Response.successResponse(dto);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "List Vital Signs", notes = "<h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ</pre>", tags = {"not-implemented"})
    @GetMapping
    public Response<VitalSignObservationReport> getVitalSigns(
            @Min(1)
            @ApiParam(value = "resident id", required = true)
            @PathVariable("residentId") Long residentId,
            @NotNull
            @ApiParam(value = "Type of Vital Sign", required = true,
                    allowableValues = "RESP, HEART_BEAT, O2_SAT, INTR_SYSTOLIC, INTR_DIASTOLIC, TEMP, HEIGHT, HEIGHT_LYING, CIRCUMFERENCE, WEIGHT")
            @RequestParam(value = "type", required = true) VitalSignType type,
            @ApiParam(value = "From date (for example `1463270400000` or `05/15/2016`)")
            @RequestParam(value = "dateFrom", required = false) String dateFrom,
            @ApiParam(value = "To date (for example `1494806400000` or `05/15/2017`)")
            @RequestParam(value = "dateTo", required = false) String dateTo,
            @ApiParam(value = "Maximum results to appear in report (if not specified, system will return last 7 vital sign observations, in case period is omitted; and 100 vital sign observations otherwise)")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Max(0)
            @ApiParam(value = "Report page. 0 will return report for last Report period. -1 will return report for preceding Report Period, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        logger.info("Get Vital Sign Details : " + type);
        final Pageable pageable = PaginationUtils.buildPageable(pageSize, page);
        final VitalSignObservationReport dto = vitalSignsService.report(residentId, type, dateFrom, dateTo, pageable);
        return Response.successResponse(dto);
    }

}
