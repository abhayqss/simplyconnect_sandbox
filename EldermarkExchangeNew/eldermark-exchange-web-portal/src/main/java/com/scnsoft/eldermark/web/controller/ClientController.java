package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.beans.ClientAccessType;
import com.scnsoft.eldermark.beans.ClientRecordSearchFilter;
import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.dto.assessment.AssessmentDefaultsDto;
import com.scnsoft.eldermark.dto.client.*;
import com.scnsoft.eldermark.dto.clientactivation.ClientActivationDto;
import com.scnsoft.eldermark.dto.clientactivation.ClientDeactivationDto;
import com.scnsoft.eldermark.dto.filter.ClientFilterDto;
import com.scnsoft.eldermark.facade.BillingFacade;
import com.scnsoft.eldermark.facade.ClientFacade;
import com.scnsoft.eldermark.facade.DeviceFacade;
import com.scnsoft.eldermark.service.security.ClientSecurityService;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientFacade clientFacade;

    @Autowired
    private DeviceFacade deviceFacade;

    @Autowired
    private BillingFacade billingFacade;

    @Autowired
    private ClientSecurityService clientSecurityService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ClientListItemDto>> find(@ModelAttribute ClientFilterDto filter,
                                                  Pageable pageable) {

        if (filter.getClientAccessType() == null) filter.setClientAccessType(ClientAccessType.IN_LIST);
        var page = clientFacade.find(filter, pageable);
        return Response.pagedResponse(page);
    }

    @GetMapping(value = "/records",produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ClientListItemDto>> findRecords(@ModelAttribute @Valid ClientRecordSearchFilter filter, Pageable pageable) {
        var page = clientFacade.findRecords(filter, pageable);
        return Response.pagedResponse(page);
    }

    @GetMapping(value = "/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ClientDto> findById(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientFacade.findById(clientId));
    }

    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> save(@Valid @ModelAttribute ClientDto clientDto) {
        if (clientDto.getId() == null) {
            return Response.successResponse(clientFacade.add(clientDto));
        }
        return Response.successResponse(clientFacade.edit(clientDto));
    }

    @PostMapping(value = "/edit-essentials", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> editEssentials(@Valid @ModelAttribute ClientEssentialsDto clientDto) {
        return Response.successResponse(clientFacade.editEssentials(clientDto));
    }

    @GetMapping(value = "/{clientId}/billing-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<BillingInfoDto> findBillingInfo(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(billingFacade.findByClientId(clientId));
    }

    @GetMapping(value = "/{clientId}/emergency-contacts", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<EmergencyContactListItemDto>> findEmergencyContacts(@PathVariable("clientId") Long clientId,
                                                                             Pageable pageRequest) {
        var pageable = clientFacade.findEmergencyContacts(clientId, pageRequest);
        return Response.successResponse(pageable);
    }

    @GetMapping(value = "/{clientId}/medical-contacts", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<MedicalContactDto>> findMedicalContacts(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientFacade.findMedicalContacts(clientId));
    }

    @GetMapping(value = "/{clientId}/household-members", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<HouseHoldMemberListItemDto>> findHouseHoldMembers(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientFacade.findHouseHoldMembers(clientId));
    }

    @RequestMapping(value = "/devices", method = {RequestMethod.POST,
            RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> save(@RequestBody DeviceDto clientDeviceDto) {
        return Response.successResponse(deviceFacade.save(clientDeviceDto));
    }

    @GetMapping(value = "/{clientId}/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<DeviceDetailDto>> find(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(deviceFacade.find(clientId));
    }

    @DeleteMapping(value = "/devices/{deviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> deleteById(@PathVariable("deviceId") Long deviceId) {
        return Response.successResponse(deviceFacade.deleteById(deviceId));
    }

    @GetMapping(value = "/can-add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canAdd(@RequestParam("organizationId") Long organizationId) {
        return Response.successResponse(clientFacade.canAdd(organizationId));
    }

    @GetMapping(value = "/validate-uniq-in-community", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ClientCommunityUniquenessDto> validateUniqInCommunity(@RequestParam(value = "clientId", required = false) Long clientId,
                                                                          @RequestParam(value = "communityId") Long communityId,
                                                                          @RequestParam(value = "ssn", required = false) String ssn,
                                                                          @RequestParam(value = "medicareNumber", required = false) String medicareNumber,
                                                                          @RequestParam(value = "medicaidNumber", required = false) String medicaidNumber,
                                                                          @RequestParam(value = "memberNumber", required = false) String memberNumber) {
        return Response.successResponse(clientFacade.validateUniqueInCommunity(clientId, communityId, ssn, medicareNumber, medicaidNumber, memberNumber));
    }

    @GetMapping(value = "/validate-uniq-in-organization", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ClientOrganizationUniquenessDto> validateUniqInOrganization(@RequestParam(value = "clientId", required = false) Long clientId,
                                                                                @RequestParam(value = "organizationId") Long organizationId,
                                                                                @RequestParam(value = "email", required = false) String email) {
        return Response.successResponse(clientFacade.validateUniqueInOrganization(clientId, organizationId, email));
    }

    @GetMapping(value = "/{clientId}/can-edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canEdit(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientSecurityService.canEdit(clientId));
    }

    @GetMapping(value = "/{clientId}/not-viewable-event-type-ids", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<Long>> findNotViewableEventTypeIds(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientFacade.findNotViewableEventTypeIds(clientId));
    }

    @PostMapping(value = "/{clientId}/toggle-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> toggleStatus(@PathVariable("clientId") Long clientId) {
        clientFacade.toggleStatus(clientId);
        return Response.successResponse();
    }

    @GetMapping(value = "/{clientId}/exists-affiliated-communities", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> isExistsAffiliated(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientFacade.isExistsAffiliatedCommunities(clientId));
    }

    @GetMapping(value = "/unassociated",produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ClientNameCommunityIdListItemDto>> findUnassociated(@RequestParam("organizationId") Long organizationId) {
        var page = clientFacade.findUnassociated(organizationId);
        return Response.successResponse(page);
    }

    @PostMapping(value = "/{clientId}/activate", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> activateClient(
        @PathVariable("clientId") Long clientId, @RequestBody ClientActivationDto activationDto
    ) {
        clientFacade.activateClient(clientId, activationDto);
        return Response.successResponse();
    }


    @PostMapping(value = "/{clientId}/deactivate", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> deactivateClient(
        @PathVariable("clientId") Long clientId, @RequestBody ClientDeactivationDto deactivationDto
    ) {
        clientFacade.deactivateClient(clientId, deactivationDto);
        return Response.successResponse();
    }

    @GetMapping(value = "/{clientId}/assessments/default", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<AssessmentDefaultsDto> assessmentDefaults(
        @PathVariable("clientId") Long clientId,
        @RequestParam("assessmentTypeId") Long assessmentTypeId,
        @RequestParam(value = "assessmentId", required = false) Long parentAssessmentResultId) {
        return Response.successResponse(clientFacade.assessmentDefaults(clientId, assessmentTypeId, parentAssessmentResultId));
    }

    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> count(
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) Boolean canRequestSignature
    ) {
        return Response.successResponse(clientFacade.count(organizationId, canRequestSignature));
    }

    @GetMapping(value = "/{clientId}/prospective-primary-contacts", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ProspectivePrimaryContactDto>> getProspectivePrimaryContacts(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientFacade.getProspectivePrimaryContacts(clientId));
    }

    @GetMapping(value = "/{clientId}/telecom", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ClientTelecomDto> findClientTelecom(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientFacade.findTelecom(clientId));
    }
}
