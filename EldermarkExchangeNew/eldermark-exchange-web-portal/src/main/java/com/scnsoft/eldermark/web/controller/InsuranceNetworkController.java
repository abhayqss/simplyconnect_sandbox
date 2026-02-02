package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.InsuranceNetworkDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.facade.InsuranceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/insurance-networks")
@RestController
public class InsuranceNetworkController {

    @Autowired
    private InsuranceFacade insuranceFacade;

    @GetMapping
    public Response<List<InsuranceNetworkDto>> find(@RequestParam(name = "name", required = false) String title, Pageable pageRequest) {
        var insurances = insuranceFacade.find(title, pageRequest);
        return Response.pagedResponse(insurances);
    }

}
