package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.mobile.dto.ClientHieConsentPolicyDto;
import com.scnsoft.eldermark.mobile.facade.HieConsentPolicyFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hie-consent-policies")
public class HieConsentPolicyController {

    @Autowired
    private HieConsentPolicyFacade facade;

    @GetMapping
    public Response<List<ClientHieConsentPolicyDto>> findAll() {
        return Response.successResponse(facade.findAll());
    }

    @GetMapping("/can-view")
    public Response<Boolean> getCanView() {
        return Response.successResponse(facade.canView());
    }
}
