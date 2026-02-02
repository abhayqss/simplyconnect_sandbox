package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.PhilipsTestDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.service.inbound.philips.PhilipsTestFileGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "/philips", produces = MediaType.APPLICATION_JSON_VALUE)
public class PhilipsController {

    // ============================================= Testing ==============================================
    @Autowired
    private PhilipsTestFileGenerator philipsTestFileGenerator;

    @PostMapping(value = "/testing", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMINISTRATOR')")
    public Response<Boolean> submitTestDtos(@RequestBody List<PhilipsTestDto> dtos) {
        return Response.successResponse(philipsTestFileGenerator.generateCsvFile(dtos));
    }

    @PostMapping(value = "/testing/csv", consumes = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMINISTRATOR')")
    public Response<Boolean> submitTestCSV(@RequestBody String csv) {
        return Response.successResponse(philipsTestFileGenerator.generateCsvFile(csv));
    }
}
