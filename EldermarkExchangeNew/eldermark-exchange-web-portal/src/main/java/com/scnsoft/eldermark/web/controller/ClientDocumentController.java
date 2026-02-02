package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.EditDocumentDto;
import com.scnsoft.eldermark.dto.UploadClientDocumentDto;
import com.scnsoft.eldermark.dto.document.DocumentDto;
import com.scnsoft.eldermark.dto.document.ClientDocumentListItemDto;
import com.scnsoft.eldermark.dto.filter.ClientDocumentFilter;
import com.scnsoft.eldermark.facade.ClientDocumentFacade;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping("/clients/{clientId}/documents")
public class ClientDocumentController {

    @Autowired
    private ClientDocumentFacade documentFacade;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ClientDocumentListItemDto>> find(@PathVariable(value = "clientId") Long clientId,
                                                    @ModelAttribute @Valid ClientDocumentFilter documentFilter, Pageable pageRequest) {
        documentFilter.setClientId(clientId);
        var pageable = documentFacade.find(documentFilter, pageRequest);
        return Response.pagedResponse(pageable);
    }

    @GetMapping(value = "/{documentId}")
    public Response<DocumentDto> findById(@PathVariable(value = "documentId") Long documentId) {
        return Response.successResponse(documentFacade.findById(documentId));
    }

    @GetMapping(value = "/{documentId}/download")
    public void download(@PathVariable(value = "documentId") Long documentId, HttpServletResponse response) {
        documentFacade.download(documentId, response, false);
    }

    @GetMapping(value = "/download", params = "documentIds")
    public void downloadMultiple(@RequestParam(value = "documentIds") @NotEmpty List<Long> documentIds, HttpServletResponse response) {
        documentFacade.downloadMultiple(documentIds, response);
    }

    @GetMapping(value = "/download")
    public void downloadMultiple(@PathVariable(value = "clientId") Long clientId, @ModelAttribute ClientDocumentFilter filter, HttpServletResponse response) {
        filter.setClientId(clientId);
        documentFacade.downloadMultiple(filter, response);
    }

    @GetMapping(value = "/{documentId}/cda-view")
    public Response<String> findCcdHtmlById(@PathVariable(value = "documentId") Long documentId) {
        return Response.successResponse(documentFacade.cdaToHtml(documentId));
    }

    @GetMapping(value = "/ccd/download")
    public void downloadCcd(@PathVariable(value = "clientId") Long clientId,
                            @RequestParam(value = "aggregated", required = false) Boolean aggregated, HttpServletResponse response) {
        documentFacade.downloadCcd(clientId, response, false, aggregated);
    }

    @GetMapping(value = "/ccd/cda-view")
    public Response<String> findCcdHtmlByClientId(@PathVariable(value = "clientId") Long clientId,
                                                  @RequestParam(value = "aggregated", required = false) Boolean aggregated) {
        return Response.successResponse(documentFacade.clientCcdToHtml(clientId, aggregated));
    }

    @GetMapping(value = "/facesheet/download")
    public void downloadFacesheet(@PathVariable(value = "clientId") Long clientId,
                                  @RequestParam(value = "aggregated", required = false) Boolean aggregated, HttpServletResponse response,
                                  @RequestHeader(value = "TimezoneOffset", required = false) Integer timeZoneOffset) {
        documentFacade.downloadFacesheet(clientId, response, false, aggregated, DateTimeUtils.generateZoneOffset(timeZoneOffset));
    }

    @PostMapping
    public Response<Long> upload(@PathVariable("clientId") Long clientId, @ModelAttribute UploadClientDocumentDto uploadDto) {
        uploadDto.setClientId(clientId);
        return Response.successResponse(documentFacade.save(uploadDto));
    }

    @DeleteMapping(value = "/{documentId}")
    public Response<Void> deleteById(@PathVariable(value = "documentId") Long documentId, @RequestParam(value = "isTemporaryDeletion", required = false) boolean isTemporary) {
        documentFacade.deleteById(documentId, isTemporary);
        return Response.successResponse();
    }

    @PostMapping(value = "/{documentId}/restore")
    public Response<Void> restoreById(@PathVariable(value = "documentId") Long documentId) {
        documentFacade.restoreById(documentId);
        return Response.successResponse();
    }

    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> count(@PathVariable(value = "clientId") Long clientId,
                                @ModelAttribute @Valid ClientDocumentFilter documentFilter) {
        documentFilter.setClientId(clientId);
        var count = documentFacade.count(documentFilter);
        return Response.successResponse(count);
    }

    @GetMapping(value = "/can-add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canAdd(@PathVariable(value = "clientId") Long clientId) {
        return Response.successResponse(documentFacade.canAdd(clientId));
    }

    @GetMapping(value = "/shared-service-plan/download")
    public void downloadServicePlan(@PathVariable(value = "clientId") Long clientId,
                                    @RequestHeader(value = "TimezoneOffset", required = false) Integer timeZoneOffset, HttpServletResponse response) {
        documentFacade.downloadServicePlanPdf(clientId, response, DateTimeUtils.generateZoneOffset(timeZoneOffset));
    }

    @PutMapping
    public Response<Long> edit(@ModelAttribute @Valid EditDocumentDto editDocumentDto) {
        return Response.successResponse(documentFacade.edit(editDocumentDto));
    }

    @GetMapping(value = "/oldest/date", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> findOldestDate(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(documentFacade.findOldestDateByClient(clientId));
    }

}
