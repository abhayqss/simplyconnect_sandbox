package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.beans.ReferralFilter;
import com.scnsoft.eldermark.beans.ReferralType;
import com.scnsoft.eldermark.config.StringCrlfToLfFormatter;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.dto.referral.*;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.facade.ReferralFacade;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/referrals", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReferralController {

    @Autowired
    public ReferralFacade referralFacade;

    @InitBinder
    private void initBinder(WebDataBinder dataBinder) {
        dataBinder.addCustomFormatter(new StringCrlfToLfFormatter(), String.class);
    }

    @GetMapping
    public Response<List<ReferralListItemDto>> find(@ModelAttribute ReferralFilter filter,
                                                    @RequestParam("type") ReferralType type,
                                                    Pageable pageable) {
        switch (type) {
            case OUTBOUND:
                return Response.pagedResponse(referralFacade.findOutbounds(filter, pageable));
            case INBOUND:
                return Response.pagedResponse(referralFacade.findInbounds(filter, pageable));
            default:
                throw new ValidationException("unsupported 'type' param value");
        }
    }

    @GetMapping(path = "/{id}")
    public Response<ReferralDto> findOutboundById(@PathVariable Long id) {
        return Response.successResponse(referralFacade.findOutboundById(id));
    }

    @GetMapping(path = "/referral-requests/{id}")
    public Response<ReferralDto> findInboundById(@PathVariable Long id) {
        return Response.successResponse(referralFacade.findInboundById(id));
    }

    @PostMapping
    public Response<Long> save(@ModelAttribute @Valid ReferralDto referralDto, @RequestHeader("timeZoneOffset") Integer timeZoneOffset) {
        if (referralDto.getId() == null) {
            referralDto.setZoneId(DateTimeUtils.generateZoneOffset(timeZoneOffset));
            return Response.successResponse(referralFacade.add(referralDto));
        } else {
            throw new NotImplementedException("edit is not currently implemented");
        }
    }

    @GetMapping(path = "/can-add")
    public Response<Boolean> canAdd(@RequestParam("communityId") Long communityId) {
        return Response.successResponse(referralFacade.canAddToCommunity(communityId));
    }

    @GetMapping(path = "/default")
    public Response<ReferralDto> findDefault(@RequestParam("clientId") Long clientId) {
        return Response.successResponse(referralFacade.findDefault(clientId));
    }

    @GetMapping(path = "/{referralId}/info-requests")
    public Response<List<ReferralCommunicationListItemDto>> findOutboundInfoRequests(@PathVariable("referralId") Long referralId, Pageable pageable) {
        return Response.pageResponse(referralFacade.findOutboundInfoRequests(referralId, pageable));
    }

    @GetMapping(path = "/referral-requests/{requestId}/info-requests")
    public Response<List<ReferralCommunicationListItemDto>> findInboundInfoRequests(@PathVariable("requestId") Long requestId, Pageable pageable) {
        return Response.pageResponse(referralFacade.findInboundInfoRequests(requestId, pageable));
    }

    @GetMapping(path = "{id}/referral-requests")
    public Response<List<ReferralSharedWithListItemDto>> findSharedWith(@PathVariable("id") Long id, Pageable pageable) {
        return Response.pageResponse(referralFacade.findOutboundReferralRequests(id, pageable));
    }

    @GetMapping(path = "{id}/referral-requests/{requestId}")
    public Response<ReferralSharedWithDetailsDto> findSharedWithDetails(@PathVariable Long requestId) {
        return Response.successResponse(referralFacade.findOutboundRequestById(requestId));
    }

    @GetMapping(path = "/referral-requests/{requestId}/contacts")
    public Response<List<IdentifiedTitledEntityDto>> findPossibleAssignees(@PathVariable("requestId") Long requestId) {
        return Response.successResponse(referralFacade.findPossibleAssignees(requestId));
    }

    @PutMapping(path = "/referral-requests/{requestId}/contacts/{contactId}/assign")
    public Response<Void> assign(@PathVariable("requestId") Long requestId,
                                 @PathVariable("contactId") Long contactId) {
        referralFacade.assign(requestId, contactId);
        return Response.successResponse();
    }

    @PutMapping(path = "/referral-requests/{requestId}/contacts/unassign")
    public Response<Void> unassign(@PathVariable("requestId") Long requestId) {
        referralFacade.assign(requestId, null);
        return Response.successResponse();
    }

    @PostMapping(path = "/referral-requests/{requestId}/info-requests", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> sendInfoRequest(@PathVariable("requestId") Long requestId,
                                          @RequestBody @Validated(ReferralCommunicationDto.SendValidation.class)
                                                  ReferralCommunicationDto referralCommunicationDto) {
        referralCommunicationDto.setReferralRequestId(requestId);
        return Response.successResponse(referralFacade.sendInfoRequest(referralCommunicationDto));
    }

    @GetMapping(path = "/referral-requests/{requestId}/info-requests/{infoRequestId}")
    public Response<ReferralCommunicationDto> getInboundInfoRequest(@PathVariable("infoRequestId") Long infoRequestId) {
        return Response.successResponse(referralFacade.getInboundInfoRequest(infoRequestId));
    }

    @GetMapping(path = "/{referralId}/info-requests/{infoRequestId}")
    public Response<ReferralCommunicationDto> getOutboundInfoRequest(@PathVariable("infoRequestId") Long infoRequestId) {
        return Response.successResponse(referralFacade.getOutboundInfoRequest(infoRequestId));
    }

    @PutMapping(path = "/{referralId}/info-requests/{infoRequestId}/respond", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> respondToInfoRequest(@PathVariable("infoRequestId") Long infoRequestId,
                                               @RequestBody @Validated(ReferralCommunicationDto.RespondValidation.class)
                                                       ReferralCommunicationDto referralCommunicationDto) {
        referralCommunicationDto.setId(infoRequestId);
        referralFacade.respondToInfoRequest(referralCommunicationDto);
        return Response.successResponse();
    }

    @GetMapping(path = "/recipients")
    public Response<List<IdentifiedTitledEntityDto>> findRecipients(@ModelAttribute ReferralFilter filter) {
        return Response.successResponse(referralFacade.findRecipients(filter));
    }

    @GetMapping(path = "/referral-requests/senders")
    public Response<List<IdentifiedTitledEntityDto>> findSenders(@ModelAttribute ReferralFilter filter) {
        return Response.successResponse(referralFacade.findSenders(filter));
    }

    @PostMapping(path = "/referral-requests/{requestId}/preadmit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> preadmit(@PathVariable("requestId") Long requestId) {
        referralFacade.preadmit(requestId);
        return Response.successResponse();
    }

    @PostMapping(path = "/referral-requests/{requestId}/accept", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> accept(@PathVariable("requestId") Long requestId, @RequestBody ReferralAcceptDto dto) {
        referralFacade.accept(requestId, dto);
        return Response.successResponse();
    }

    @PostMapping(path = "/referral-requests/{requestId}/decline", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> decline(@PathVariable("requestId") Long requestId, @RequestBody @Valid ReferralDeclineDto referralDeclineDto) {
        referralFacade.decline(requestId, referralDeclineDto);
        return Response.successResponse();
    }

    @PostMapping(path = "/{id}/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> cancel(@PathVariable("id") Long id) {
        referralFacade.cancel(id);
        return Response.successResponse();
    }

    @GetMapping(path = "/organizations")
    public Response<List<IdentifiedTitledEntityDto>> getReferralOrganizations(@RequestParam("targetCommunityId") Long targetCommunityId) {
        return Response.successResponse(referralFacade.getReferralOrganizations(targetCommunityId));
    }

    @GetMapping(path = "/communities")
    public Response<List<IdentifiedTitledEntityDto>> getReferralCommunities(@RequestParam("targetCommunityId") Long targetCommunityId,
                                                                            @RequestParam("organizationId") Long organizationId) {
        return Response.successResponse(referralFacade.getReferralCommunities(targetCommunityId, organizationId));
    }

    @GetMapping(value = "/{referralId}/attachments/{attachmentId}")
    public Response<byte[]> downloadOutboundReferralAttachmentById(@PathVariable("attachmentId") Long attachmentId) {
        var dto = referralFacade.downloadOutboundReferralAttachmentById(attachmentId);
        return Response.successResponse(dto.getBytes(), dto.getMediaType());
    }

    @GetMapping(value = "/referral-requests/{requestId}/attachments/{attachmentId}")
    public Response<byte[]> downloadInboundReferralAttachmentById(@PathVariable("requestId") Long requestId, @PathVariable("attachmentId") Long attachmentId) {
        var dto = referralFacade.downloadInboundReferralAttachmentById(requestId, attachmentId);
        return Response.successResponse(dto.getBytes(), dto.getMediaType());
    }
}
