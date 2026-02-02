package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.hiepolicy.HieConsentChangeCareTeamInvitationListItemDto;
import com.scnsoft.eldermark.facade.ClientCareTeamInvitationFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/care-team-invitations")
public class CareTeamInvitationController {

    @Autowired
    private ClientCareTeamInvitationFacade clientCareTeamInvitationFacade;


    @GetMapping("/email/create-account")
    public void createAccountFromEmail(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        var redirectUrl = clientCareTeamInvitationFacade.resolveRedirectCreateAccountFromEmail(token);
        response.sendRedirect(redirectUrl);
    }

    @GetMapping(value = "/hie-consent-change/exists-incoming", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> existsIncomingForHieConsentChange(@RequestParam(value = "clientId") Long clientId) {
        return Response.successResponse(clientCareTeamInvitationFacade.existsIncomingForHieConsentChange(clientId));
    }

    @GetMapping(value = "/hie-consent-change/find-incoming", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<HieConsentChangeCareTeamInvitationListItemDto>> findIncomingForHieConsentChange(@RequestParam(value = "communityId") Long communityId) {
        return Response.successResponse(clientCareTeamInvitationFacade.findIncomingForHieConsentChangeInCommunity(communityId));
    }

}
