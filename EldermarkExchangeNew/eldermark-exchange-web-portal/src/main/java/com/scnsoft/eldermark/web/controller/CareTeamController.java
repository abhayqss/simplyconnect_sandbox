package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.beans.CareTeamFilter;
import com.scnsoft.eldermark.dto.CareTeamMemberDto;
import com.scnsoft.eldermark.dto.CareTeamMemberListItemDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.facade.CareTeamMemberFacade;
import com.scnsoft.eldermark.facade.CareTeamMemberUIButtonsService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/care-team-members")
public class CareTeamController {

    @Autowired
    private CareTeamMemberFacade careTeamMemberFacade;

    @Autowired
    private CareTeamMemberUIButtonsService careTeamMemberUIButtonsService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<CareTeamMemberListItemDto>> find(CareTeamFilter careTeamFilter, final Pageable pageable) {
        return Response.pageResponse(careTeamMemberFacade.find(careTeamFilter, pageable));
    }

    @GetMapping(value = "/{careTeamMemberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<CareTeamMemberDto> findById(@PathVariable("careTeamMemberId") final Long careTeamMemberId) {
        return Response.successResponse(careTeamMemberFacade.findById(careTeamMemberId));
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> save(@Valid @RequestBody CareTeamMemberDto careTeamMemberDto) {
        if (careTeamMemberDto.getId() == null) {
            return Response.successResponse(careTeamMemberFacade.add(careTeamMemberDto));
        }
        return Response.successResponse(careTeamMemberFacade.edit(careTeamMemberDto));
    }

    @DeleteMapping(value = "/{careTeamMemberId}")
    public Response<Void> deleteById(@PathVariable("careTeamMemberId") Long careTeamMemberId) {
        careTeamMemberFacade.deleteById(careTeamMemberId);
        return Response.successResponse();
    }

    //todo changed may be needed once linked accounts are fully implemented
    @GetMapping(value = "/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<IdentifiedNamedEntityDto>> findContacts(@RequestParam(value = "organizationId") final Long organizationId,
                                                                 @RequestParam(value = "clientId", required = false) final Long clientId,
                                                                 @RequestParam(value = "communityId", required = false) final Long communityId) {
        return Response.successResponse(careTeamMemberFacade.getContacts(organizationId, clientId, communityId));
    }

    @GetMapping(value = "/contacts/organizations", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<IdentifiedNamedEntityDto>> findContactsOrganizations(@RequestParam(value = "clientId", required = false) final Long clientId,
                                                                              @RequestParam(value = "communityId", required = false) final Long communityId,
                                                                              @RequestParam(value = "affiliation", required = false, defaultValue = CareTeamFilter.DEFAULT_AFFILIATION_VALUE)
                                                                                      CareTeamFilter.Affiliation affiliation) {
        return Response.successResponse(careTeamMemberFacade.getContactsOrganizations(clientId, communityId, affiliation));
    }

    @GetMapping(value = "/count")
    public Response<Long> count(CareTeamFilter careTeamFilter) {
        return Response.successResponse(careTeamMemberFacade.count(careTeamFilter));
    }

    @GetMapping(value = "/can-add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canAdd(@RequestParam(value = "clientId", required = false) final Long clientId,
                                    @RequestParam(value = "communityId", required = false) final Long communityId,
                                    @RequestParam(value = "affiliation", required = false, defaultValue = CareTeamFilter.DEFAULT_AFFILIATION_VALUE)
                                            CareTeamFilter.Affiliation affiliation) {
        if (!CareCoordinationUtils.isOnlyOneIsPresent(clientId, communityId)) {
            throw new ValidationException("Only one of ['clientId', 'communityId'] should be provided");
        }
        return Response.successResponse(careTeamMemberUIButtonsService.canAdd(clientId, communityId, affiliation));
    }


    @GetMapping(value = "/can-view", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canView(@RequestParam(value = "clientId", required = false) final Long clientId,
                                     @RequestParam(value = "communityId", required = false) final Long communityId) {
        return Response.successResponse(careTeamMemberUIButtonsService.canViewList(clientId, communityId));
    }

    @GetMapping(value = "/contacts/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> contactsCount(@RequestParam(value = "organizationId") final Long organizationId,
                                                                 @RequestParam(value = "clientId", required = false) final Long clientId,
                                                                 @RequestParam(value = "communityId", required = false) final Long communityId) {
        return Response.successResponse(careTeamMemberFacade.getContactsCount(organizationId, clientId, communityId));
    }

}