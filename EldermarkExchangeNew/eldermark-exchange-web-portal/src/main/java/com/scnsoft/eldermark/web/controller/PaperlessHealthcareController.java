package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.RequestDemoDto;
import com.scnsoft.eldermark.facade.PaperlessHealthcareFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paperless-healthcare")
public class PaperlessHealthcareController {

    @Autowired
    private PaperlessHealthcareFacade facade;

    @GetMapping(path = "/can-view")
    public Response<Boolean> canView() {
        return Response.successResponse(facade.canView());
    }

    @PostMapping(path = "/demo-request")
    public Response<Long> createDemoRequest(@RequestBody RequestDemoDto dto) {
        return Response.successResponse(facade.createDemoRequest(dto));
    }

}
