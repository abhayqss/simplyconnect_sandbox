package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.healthpartners.HealthPartnersTestOutcome;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;
import com.scnsoft.eldermark.facade.HealthPartnersFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/health-partners", produces = MediaType.APPLICATION_JSON_VALUE)
public class HealthPartnersController {

    // ============================================= Testing ==============================================
    @Autowired
    private HealthPartnersFacade healthPartnersFacade;

    @PostMapping(value = "/testing/rx-claims/csv", consumes = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMINISTRATOR')")
    public Response<HealthPartnersTestOutcome> submitRxClaimTestCSV(@RequestBody String csv) {
        return Response.successResponse(healthPartnersFacade.submitTestCSV(csv, HpFileType.CONSANA_RX));
    }

    @PostMapping(value = "/testing/termed-members/csv", consumes = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMINISTRATOR')")
    public Response<HealthPartnersTestOutcome> submitTermedMembersTestCSV(@RequestBody String csv) {
        return Response.successResponse(healthPartnersFacade.submitTestCSV(csv, HpFileType.CONSANA_TERMED_MEMBERS));
    }
}
