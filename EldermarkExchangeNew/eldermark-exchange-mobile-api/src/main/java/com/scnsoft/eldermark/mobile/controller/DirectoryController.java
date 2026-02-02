package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.mobile.facade.DirectoryFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedTitledEntityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/directory")
@RestController
public class DirectoryController {

    @Autowired
    private DirectoryFacade directoryFacade;

    @GetMapping(value = "/states")
    public Response<List<IdentifiedNamedTitledEntityDto>> getStates() {
        return Response.successResponse(directoryFacade.getStates());
    }
}
