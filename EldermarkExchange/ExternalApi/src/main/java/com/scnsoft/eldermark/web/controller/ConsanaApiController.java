package com.scnsoft.eldermark.web.controller;

import com.google.common.base.Optional;
import com.scnsoft.eldermark.service.ConsanaService;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.shared.ConsanaXrefPatientIdDto;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Generated;
import java.net.HttpURLConnection;

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
        if (residentIdOpt.isPresent()) {
            return Response.successResponse(residentIdOpt.get());
        }
        return Response.errorResponse(PhrExceptionType.NOT_FOUND);
    }

}
