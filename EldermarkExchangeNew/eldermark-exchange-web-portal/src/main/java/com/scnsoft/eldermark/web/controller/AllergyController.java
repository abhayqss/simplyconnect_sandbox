package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.beans.ClientAllergyFilter;
import com.scnsoft.eldermark.dto.AllergyDto;
import com.scnsoft.eldermark.dto.AllergyListItemDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.entity.document.ccd.ClientAllergyStatus;
import com.scnsoft.eldermark.facade.ClientAllergyFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping(value = "/clients/{clientId}/allergies")
public class AllergyController {

    @Autowired
    private ClientAllergyFacade clientAllergyFacade;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response<AllergyDto> byId(@PathVariable("id") Long id) {
        return Response.successResponse(clientAllergyFacade.findById(id));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response<List<AllergyListItemDto>> find(@PathVariable("clientId") Long clientId, Pageable pageRequest) {
        var filter = new ClientAllergyFilter();
        filter.setClientId(clientId);
        filter.setStatuses(EnumSet.of(ClientAllergyStatus.ACTIVE));
        var pageable = clientAllergyFacade.find(filter, pageRequest);
        return Response.pageResponse(pageable);
    }

    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response<Long> count(@PathVariable("clientId") Long clientId) {
        var filter = new ClientAllergyFilter();
        filter.setClientId(clientId);
        filter.setStatuses(EnumSet.of(ClientAllergyStatus.ACTIVE));
        return Response.successResponse(clientAllergyFacade.count(filter));
    }
}
