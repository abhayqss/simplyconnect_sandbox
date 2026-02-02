package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.CertificateInfoDto;
import com.scnsoft.eldermark.dto.docutrack.BusinessUnitCodeListItemDto;
import com.scnsoft.eldermark.dto.docutrack.DocutrackSupportedFileListItemDto;
import com.scnsoft.eldermark.facade.DocutrackFacade;
import com.scnsoft.eldermark.service.security.DocutrackSecurityService;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/docutrack")
@RestController
public class DocutrackController {

    @Autowired
    private DocutrackFacade docutrackFacade;

    @Autowired
    private DocutrackSecurityService docutrackSecurityService;

    @GetMapping(value = "/business-unit-codes", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<BusinessUnitCodeListItemDto>> getBusinessUnitCodes() {
        return Response.successResponse(docutrackFacade.getBusinessUnitCodes());
    }

    @GetMapping(value = "/supported-file-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<DocutrackSupportedFileListItemDto>> getSupportedFileTypes() {
        return Response.successResponse(docutrackFacade.getSupportedFileTypes());
    }

    @GetMapping(value = "/can-configure", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canAdd(@RequestParam("organizationId") Long organizationId) {
        return Response.successResponse(docutrackSecurityService.canConfigureDocutrackInOrg(organizationId));
    }

    @GetMapping(value = "/load-server-self-signed-cert")
    public Response<CertificateInfoDto> getSelfSignedCert(@RequestParam("serverDomain") String serverDomain) {
        return Response.successResponse(docutrackFacade.getSelfSignedCert(serverDomain));
    }

    @GetMapping(value = "/non-unique-business-unit-codes")
    public Response<List<String>> nonUniqueBusinessUnitCodes(
            @RequestParam("serverDomain") String serverDomain,
            @RequestParam(value = "excludeCommunityId", required = false) Long excludeCommunityId,
            @RequestParam("businessUnitCodes") List<String> businessUnitCodes
    ) {
        return Response.successResponse(docutrackFacade.nonUniqueBusinessUnitCodes(
                serverDomain,
                excludeCommunityId,
                businessUnitCodes)
        );
    }
}
