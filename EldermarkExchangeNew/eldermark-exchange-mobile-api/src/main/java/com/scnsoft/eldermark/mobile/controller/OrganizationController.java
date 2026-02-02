package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.mobile.facade.OrganizationFacade;
import com.scnsoft.eldermark.web.commons.dto.FileBytesDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    @Autowired
    private OrganizationFacade organizationFacade;

    @GetMapping(value = "/{organizationId}/logo")
    public Response<byte[]> downloadLogo(@PathVariable("organizationId") Long organizationId) {
        FileBytesDto logo = organizationFacade.downloadLogo(organizationId);
        return Response.successResponse(new Response.Body<>(logo.getBytes(), logo.getMediaType()));
    }
}
