package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.hiepolicy.HieConsentPolicyDto;
import com.scnsoft.eldermark.facade.HieConsentPolicyFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients/{clientId}/hie-consent-policy")
public class ClientHieConsentPolicyController {

    @Autowired
    private HieConsentPolicyFacade facade;

    @PostMapping
    public Response<Void> update(@PathVariable Long clientId, @RequestBody HieConsentPolicyDto dto) {
        dto.setClientId(clientId);
        facade.update(dto);
        return Response.successResponse();
    }
}
