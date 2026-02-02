package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.signature.DocumentSignatureBulkRequestInfoDto;
import com.scnsoft.eldermark.dto.signature.DocumentSignatureBulkRequestRenewDto;
import com.scnsoft.eldermark.dto.singature.SubmitTemplateSignatureBulkRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestStatus;
import com.scnsoft.eldermark.facade.signature.DocumentSignatureBulkRequestFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/documents/e-sign/bulk-requests")
public class ESignBulkRequestController {

    @Autowired
    private DocumentSignatureBulkRequestFacade documentSignatureBulkRequestFacade;

    @PostMapping
    public Response<Long> submitBulkRequest(
            @RequestBody SubmitTemplateSignatureBulkRequest dto,
            @RequestHeader(value = "TimezoneOffset", required = false) Integer timeZoneOffset
    ) {
        dto.setTimezoneOffset(timeZoneOffset);
        return Response.successResponse(documentSignatureBulkRequestFacade.submitBulkRequest(dto));
    }

    @PostMapping(value = "/{id}/renew", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> renew(
            @PathVariable("id") Long id,
            @RequestBody @Valid DocumentSignatureBulkRequestRenewDto dto
    ) {
        dto.setBulkRequestId(id);
        return Response.successResponse(documentSignatureBulkRequestFacade.renewBulkRequest(dto));
    }

    @GetMapping(value = "/{id}/requests")
    public Response<List<DocumentSignatureBulkRequestInfoDto>> fetchRequests(
            @PathVariable("id") Long id,
            @RequestParam("status") List<DocumentSignatureRequestStatus> statuses
    ) {
        return Response.successResponse(documentSignatureBulkRequestFacade.fetchRequests(id, statuses));
    }

    @PostMapping(value = "/{id}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> cancelBulkRequest(@PathVariable("id") Long id, @RequestParam Long templateId) {
        documentSignatureBulkRequestFacade.cancelRequest(id, templateId);
        return Response.successResponse();
    }
}