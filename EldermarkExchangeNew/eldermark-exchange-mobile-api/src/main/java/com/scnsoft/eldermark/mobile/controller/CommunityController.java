package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.mobile.facade.CommunityFacade;
import com.scnsoft.eldermark.web.commons.dto.FileBytesDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/organizations/{organizationId}/communities")
public class CommunityController {

    @Autowired
    private CommunityFacade communityFacade;

    @GetMapping(value = "/{communityId}/logo")
    public Response<byte[]> downloadLogo(@PathVariable("communityId") Long communityId) {
        FileBytesDto logo = communityFacade.downloadLogo(communityId);
        return Response.successResponse(new Response.Body<>(logo.getBytes(), logo.getMediaType()));
    }
}
