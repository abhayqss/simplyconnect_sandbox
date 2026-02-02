package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.facade.LabResearchOrderFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/clients/{clientId}/lab-research/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientLabResearchOrderController {

    @Autowired
    private LabResearchOrderFacade labResearchOrderFacade;

    @GetMapping(value = "/can-add")
    public Response<Boolean> canAdd(@PathVariable(value = "clientId") Long clientId) {
        return Response.successResponse(labResearchOrderFacade.canAddToClient(clientId));
    }
}
