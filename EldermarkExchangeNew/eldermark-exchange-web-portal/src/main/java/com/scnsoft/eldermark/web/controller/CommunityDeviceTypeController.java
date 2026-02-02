package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.CommunityDeviceTypeDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.facade.CommunityFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping("/organizations/{organizationId}/communities/{communityId}/device-types")

//notify project scope
public class CommunityDeviceTypeController {

    @Autowired
    private CommunityFacade communityFacade;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<CommunityDeviceTypeDto>> findDeviceTypeByCommunityId(
            @PathVariable("communityId") Long communityId, Pageable pageRequest) {
        var pageable = communityFacade.findDeviceTypeByCommunityId(communityId, pageRequest);
        return Response.pagedResponse(pageable.getContent(), pageable.getTotalElements());
    }

    @RequestMapping(method = { RequestMethod.PUT,
            RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> saveDeviceType(@PathVariable("communityId") Long communityId,
                                         @RequestBody CommunityDeviceTypeDto communityDeviceTypeDto) {
        return Response.successResponse(communityFacade.saveDeviceType(communityId, communityDeviceTypeDto));
    }

    @GetMapping(value = "/{deviceTypeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<CommunityDeviceTypeDto> findDeviceTypeById(@PathVariable("deviceTypeId") Long deviceTypeId) {
        return Response.successResponse(communityFacade.findDeviceTypeById(deviceTypeId));
    }

}
