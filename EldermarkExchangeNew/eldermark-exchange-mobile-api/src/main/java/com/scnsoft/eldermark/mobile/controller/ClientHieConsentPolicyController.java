package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.mobile.dto.ClientHieConsentPolicyDto;
import com.scnsoft.eldermark.mobile.facade.HieConsentPolicyFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients/{clientId}/hie-consent-policy")
public class ClientHieConsentPolicyController {

    @Autowired
    private HieConsentPolicyFacade facade;

    @PostMapping
    public Response<Void> update(@PathVariable("clientId") Long clientId, @RequestBody ClientHieConsentPolicyDto dto) {
        dto.setClientId(clientId);
        facade.update(dto);
        return Response.successResponse();
    }
}
