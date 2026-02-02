package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.signature.pdcflow.PdcFlowCallbackDto;
import com.scnsoft.eldermark.facade.signature.PdcFlowCallbackFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/pdcflow/postback")
public class PdcFlowCallbackController {

    @Autowired
    private PdcFlowCallbackFacade pdcFlowCallbackFacade;

    @PostMapping
    public Response<Void> pdcFlowPostback(
            @RequestHeader("Authorization") String postbackAuth,
            @RequestBody PdcFlowCallbackDto callbackDto
    ) {
        pdcFlowCallbackFacade.processCallback(callbackDto, postbackAuth);
        return Response.successResponse();
    }
}
