package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.mobile.dto.document.DocumentDto;
import com.scnsoft.eldermark.mobile.dto.document.DocumentListItemDto;
import com.scnsoft.eldermark.mobile.facade.ClientDocumentFacade;
import com.scnsoft.eldermark.mobile.filter.MobileDocumentFilter;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledValueEntityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/clients/{clientId}/documents")
public class ClientDocumentController {

    @Autowired
    private ClientDocumentFacade clientDocumentFacade;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<DocumentListItemDto>> find(@PathVariable(value = "clientId") Long clientId,
                                                    @ModelAttribute @Valid MobileDocumentFilter documentFilter,
                                                    Pageable pageRequest) {
        documentFilter.setClientId(clientId);
        var pageable = clientDocumentFacade.find(documentFilter, pageRequest);
        return Response.pagedResponse(pageable);
    }

    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> count(@PathVariable(value = "clientId") Long clientId,
                                @ModelAttribute @Valid MobileDocumentFilter documentFilter) {
        documentFilter.setClientId(clientId);
        var count = clientDocumentFacade.count(documentFilter);
        return Response.successResponse(count);
    }

    @GetMapping(value = "/count-grouped", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<NamedTitledValueEntityDto<Long>>> countGroupedBySignatureStatus(
            @PathVariable(value = "clientId") Long clientId,
            @ModelAttribute @Valid MobileDocumentFilter documentFilter
    ) {
        documentFilter.setClientId(clientId);
        return Response.successResponse(clientDocumentFacade.countGroupedBySignatureStatus(documentFilter));
    }

    @GetMapping(value = "/{documentId}")
    public Response<DocumentDto> findById(@PathVariable(value = "documentId") Long documentId) {
        return Response.successResponse(clientDocumentFacade.findById(documentId));
    }

    @GetMapping(value = "/{documentId}/download")
    public void download(@PathVariable(value = "documentId") Long documentId, HttpServletResponse response) {
        clientDocumentFacade.download(documentId, response);
    }

    @GetMapping(value = "/ccd/download")
    public void downloadCcd(@PathVariable(value = "clientId") Long clientId,
                            HttpServletResponse response) {
        clientDocumentFacade.downloadCcd(clientId, response);
    }

    @GetMapping(value = "/{documentId}/cda-html-view")
    public Response<String> findCcdHtmlById(@PathVariable(value = "documentId") Long documentId) {
        return Response.successResponse(clientDocumentFacade.cdaToHtml(documentId));
    }
    @GetMapping(value = "/ccd/cda-html-view")
    public Response<String> findCcdHtmlByClientId(@PathVariable(value = "clientId") Long clientId) {
        return Response.successResponse(clientDocumentFacade.clientCcdToHtml(clientId));
    }

    @GetMapping(value = "/facesheet/download")
    public void downloadFacesheet(@PathVariable(value = "clientId") Long clientId,
                                  @RequestHeader(value = "TimezoneOffset", required = false) Integer timeZoneOffset,
                                  HttpServletResponse response) {
        clientDocumentFacade.downloadFacesheet(clientId, response, DateTimeUtils.generateZoneOffset(timeZoneOffset));
    }
}
