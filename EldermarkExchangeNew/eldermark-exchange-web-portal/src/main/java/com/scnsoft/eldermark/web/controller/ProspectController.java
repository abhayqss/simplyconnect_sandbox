package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.prospect.ProspectCommunityUniquenessDto;
import com.scnsoft.eldermark.dto.prospect.ProspectActivationDto;
import com.scnsoft.eldermark.dto.prospect.ProspectDeactivationDto;
import com.scnsoft.eldermark.dto.prospect.ProspectDto;
import com.scnsoft.eldermark.dto.prospect.ProspectFilterDto;
import com.scnsoft.eldermark.dto.prospect.ProspectListItemDto;
import com.scnsoft.eldermark.dto.prospect.ProspectOrganizationUniquenessDto;
import com.scnsoft.eldermark.facade.prospect.ProspectFacade;
import com.scnsoft.eldermark.validation.ValidationGroups;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/prospects")
public class ProspectController {

    @Autowired
    private ProspectFacade prospectFacade;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ProspectListItemDto>> find(@ModelAttribute ProspectFilterDto filter, Pageable pageable) {
        return Response.pagedResponse(prospectFacade.find(filter, pageable));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> create(@ModelAttribute @Validated(ValidationGroups.Create.class) ProspectDto prospectDto) {
        return Response.successResponse(prospectFacade.add(prospectDto));
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> update(@ModelAttribute @Validated(ValidationGroups.Update.class) ProspectDto prospectDto) {
        return Response.successResponse(prospectFacade.edit(prospectDto));
    }

    @GetMapping(value = "/{prospectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ProspectDto> findById(@PathVariable("prospectId") Long prospectId) {
        return Response.successResponse(prospectFacade.findById(prospectId));
    }

    @PostMapping(value = "/{prospectId}/activate", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> activateProspect(
            @PathVariable("prospectId") Long prospectId,
            @RequestBody ProspectActivationDto activationDto
    ) {
        prospectFacade.activate(prospectId, activationDto);
        return Response.successResponse();
    }

    @PostMapping(value = "/{prospectId}/deactivate", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> deactivateProspect(
            @PathVariable("prospectId") Long prospectId,
            @RequestBody ProspectDeactivationDto deactivationDto
    ) {
        prospectFacade.deactivate(prospectId, deactivationDto);
        return Response.successResponse();
    }

    @GetMapping(path = "/can-view")
    public Response<Boolean> canView() {
        return Response.successResponse(prospectFacade.canView());
    }

    @GetMapping(value = "/can-add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canAdd(@RequestParam Long organizationId) {
        return Response.successResponse(prospectFacade.canAdd(organizationId));
    }

    @GetMapping(value = "/{prospectId}/can-edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canEdit(@PathVariable("prospectId") Long prospectId) {
        return Response.successResponse(prospectFacade.canEdit(prospectId));
    }

    @GetMapping(value = "/validate-uniq-in-community", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ProspectCommunityUniquenessDto> validateUniqInCommunity(@RequestParam(value = "prospectId", required = false) Long prospectId,
                                                                            @RequestParam(value = "communityId") Long communityId,
                                                                            @RequestParam(value = "ssn", required = false) String ssn
    ) {
        return Response.successResponse(prospectFacade.validateUniqueInCommunity(prospectId, communityId, ssn));
    }

    @GetMapping(value = "/validate-uniq-in-organization", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ProspectOrganizationUniquenessDto> validateUniqInOrganization(@RequestParam(value = "prospectId", required = false) Long prospectId,
                                                                                  @RequestParam(value = "organizationId") Long organizationId,
                                                                                  @RequestParam(value = "email", required = false) String email) {
        return Response.successResponse(prospectFacade.validateUniqueInOrganization(prospectId, organizationId, email));
    }
}
