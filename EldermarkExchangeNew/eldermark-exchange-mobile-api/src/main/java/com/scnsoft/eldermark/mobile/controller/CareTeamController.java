package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.beans.CareTeamFilter;
import com.scnsoft.eldermark.mobile.dto.careteam.CareTeamMemberDto;
import com.scnsoft.eldermark.mobile.dto.careteam.CareTeamMemberListItemDto;
import com.scnsoft.eldermark.mobile.facade.CareTeamMemberFacade;
import com.scnsoft.eldermark.service.security.ClientCareTeamSecurityService;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/care-team-members")
public class CareTeamController {

    @Autowired
    private CareTeamMemberFacade careTeamMemberFacade;

    @Autowired
    private ClientCareTeamSecurityService clientCareTeamSecurityService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<CareTeamMemberListItemDto>> find(CareTeamFilter careTeamFilter, final Pageable pageable) {
        return Response.pageResponse(careTeamMemberFacade.find(careTeamFilter, pageable));
    }

    @GetMapping(value = "/{careTeamMemberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<CareTeamMemberDto> findById(@PathVariable("careTeamMemberId") final Long careTeamMemberId) {
        return Response.successResponse(careTeamMemberFacade.findById(careTeamMemberId));
    }

    @GetMapping(value = "/count")
    public Response<Long> count(CareTeamFilter careTeamFilter) {
        return Response.successResponse(careTeamMemberFacade.count(careTeamFilter));
    }

    @GetMapping(value = "/exists")
    public Response<Boolean> exists(CareTeamFilter careTeamFilter) {
        return Response.successResponse(careTeamMemberFacade.exists(careTeamFilter));
    }

    @GetMapping(value = "/can-view", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canView(@RequestParam(value = "clientId", required = false) final Long clientId) {
        return Response.successResponse(clientCareTeamSecurityService.canViewList(clientId));
    }

    @DeleteMapping(value = "/{careTeamMemberId}")
    public Response<Void> deleteById(@PathVariable("careTeamMemberId") Long careTeamMemberId) {
        careTeamMemberFacade.deleteById(careTeamMemberId);
        return Response.successResponse();
    }
}