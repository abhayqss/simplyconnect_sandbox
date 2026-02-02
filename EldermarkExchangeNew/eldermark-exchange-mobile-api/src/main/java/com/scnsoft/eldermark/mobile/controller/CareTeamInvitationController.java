package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.beans.ClientCareTeamInvitationFilter;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.mobile.dto.careteam.invitation.ClientCareTeamInboundInvitationListItemDto;
import com.scnsoft.eldermark.mobile.dto.careteam.invitation.ClientCareTeamInvitationDto;
import com.scnsoft.eldermark.mobile.dto.careteam.invitation.ClientCareTeamInvitationListItemDto;
import com.scnsoft.eldermark.mobile.dto.careteam.invitation.ClientCareTeamInvitationValidationDto;
import com.scnsoft.eldermark.mobile.facade.CareTeamInvitationFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.web.commons.dto.careteam.CareTeamInvitationConfirmDto;
import com.scnsoft.eldermark.web.commons.dto.careteam.CareTeamInvitationDto;
import com.scnsoft.eldermark.web.commons.dto.careteam.CareTeamInvitationResendDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/care-team-invitations")
public class CareTeamInvitationController {

    @Autowired
    private CareTeamInvitationFacade careTeamInvitationFacade;

    @GetMapping
    public Response<List<ClientCareTeamInvitationListItemDto>> findInvitations(ClientCareTeamInvitationFilter filter,
                                                                               Pageable pageable) {
        return Response.pagedResponse(careTeamInvitationFacade.findInvitations(filter, pageable));
    }

    @GetMapping("/inbound")
    public Response<List<ClientCareTeamInboundInvitationListItemDto>> findInboundInvitations(Pageable pageable) {
        return Response.pageResponse(careTeamInvitationFacade.findInboundInvitations(pageable));
    }

    @GetMapping("/count")
    public Response<Long> countInvitations(ClientCareTeamInvitationFilter filter) {
        return Response.successResponse(careTeamInvitationFacade.countInvitations(filter));
    }

    @GetMapping("/{invitationId}")
    public Response<ClientCareTeamInvitationDto> findById(@PathVariable("invitationId") Long id) {
        return Response.successResponse(careTeamInvitationFacade.findById(id));
    }

    @PostMapping
    public Response<Long> invite(@Valid @RequestBody CareTeamInvitationDto inviteDto) {
        return Response.successResponse(careTeamInvitationFacade.invite(inviteDto));
    }

    @PutMapping("/{invitationId}")
    public Response<Long> resendInvitation(
            @PathVariable("invitationId") Long id,
            @Valid @RequestBody CareTeamInvitationResendDto dto
    ) {
        dto.setId(id);
        return Response.successResponse(careTeamInvitationFacade.resendInvitation(dto));
    }

    @PostMapping("/confirm-registration")
    public Response<Void> confirmRegistration(@Valid @RequestBody CareTeamInvitationConfirmDto inviteDto) {
        careTeamInvitationFacade.confirmRegistration(inviteDto);
        return Response.successResponse();
    }

    @PostMapping("/{invitationId}/cancel")
    public Response<Void> cancelInvitation(@PathVariable("invitationId") Long id) {
        careTeamInvitationFacade.cancelInvitation(id);
        return Response.successResponse();
    }

    @PostMapping("/{invitationId}/accept")
    public Response<Void> accept(@PathVariable("invitationId") Long id) {
        careTeamInvitationFacade.acceptInvitation(id);
        return Response.successResponse();
    }

    @PostMapping("/{invitationId}/decline")
    public Response<Void> decline(@PathVariable("invitationId") Long id) {
        careTeamInvitationFacade.declineInvitation(id);
        return Response.successResponse();
    }

    @GetMapping("/can-invite")
    public Response<Boolean> canInvite(@RequestParam Long clientId) {
        return Response.successResponse(careTeamInvitationFacade.canInvite(clientId));
    }

    @GetMapping(value = "/hie-consent-change/exists-incoming", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> existsIncomingForHieConsentChange(@RequestParam(value = "clientId") Long clientId) {
        return Response.successResponse(careTeamInvitationFacade.existsIncomingForHieConsentChange(clientId));
    }

    @GetMapping(value = "/hie-consent/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ClientCareTeamInvitationValidationDto> validateHieConsent(@RequestParam("clientId") Long clientId) {
        return Response.successResponse(careTeamInvitationFacade.validateHieConsent(clientId));
    }
}
