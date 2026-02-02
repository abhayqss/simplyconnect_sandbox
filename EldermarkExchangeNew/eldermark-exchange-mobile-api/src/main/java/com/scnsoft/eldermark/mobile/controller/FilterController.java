package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.mobile.facade.FilterFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/filters")
public class FilterController {

    @Autowired
    private FilterFacade filterFacade;

    @GetMapping("/organizations")
    public Response<List<IdentifiedNamedEntityDto>> findOrganizations(){
        return Response.successResponse(filterFacade.findOrganizations());
    }

    @GetMapping("/communities")
    public Response<List<IdentifiedNamedEntityDto>> findCommunities(@RequestParam Long organizationId){
        return Response.successResponse(filterFacade.findCommunities(organizationId));
    }
}
