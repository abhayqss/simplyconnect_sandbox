package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.mobile.dto.signature.DocumentSignatureHistoryDto;
import com.scnsoft.eldermark.mobile.dto.signature.DocumentSignatureRequestDto;
import com.scnsoft.eldermark.mobile.dto.signature.DocumentSignatureRequestRenewDto;
import com.scnsoft.eldermark.mobile.dto.signature.DocumentSignatureResendPinResponseDto;
import com.scnsoft.eldermark.mobile.facade.DocumentSignatureRequestFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/documents/e-sign/")
public class ESignController {

    @Autowired
    private DocumentSignatureRequestFacade documentSignatureRequestFacade;

    @GetMapping(value = "/requests/{requestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<DocumentSignatureRequestDto> getRequestById(@PathVariable("requestId") Long requestId) {
        return Response.successResponse(documentSignatureRequestFacade.getById(requestId));
    }

    @PostMapping(value = "/requests/{requestId}/renew", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> renew(
            @PathVariable("requestId") Long requestId,
            @RequestBody @Valid DocumentSignatureRequestRenewDto dto
    ) {
        dto.setRequestId(requestId);
        return Response.successResponse(documentSignatureRequestFacade.renewRequest(dto));
    }

    @PostMapping(value = "/requests/{requestId}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> cancelRequest(@PathVariable("requestId") Long requestId) {
        documentSignatureRequestFacade.cancelRequest(requestId);
        return Response.successResponse();
    }

    @PostMapping(value = "/requests/{requestId}/resend-pin", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<DocumentSignatureResendPinResponseDto> resendPin(@PathVariable("requestId") Long requestId) {
        return Response.successResponse(documentSignatureRequestFacade.resendPin(requestId));
    }

    @GetMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<DocumentSignatureHistoryDto>> getHistory(
            @RequestParam Long documentId,
            Pageable pageable,
            @RequestHeader(value = "TimezoneOffset", required = false) Integer timezoneOffset
    ) {
        return Response.pagedResponse(documentSignatureRequestFacade.findHistoryByDocumentId(documentId, pageable, timezoneOffset));
    }

    @GetMapping(value = "/requests/can-add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canAdd(@RequestParam(value = "clientId") Long clientId) {
        return Response.successResponse(documentSignatureRequestFacade.canAdd(clientId));
    }
}
