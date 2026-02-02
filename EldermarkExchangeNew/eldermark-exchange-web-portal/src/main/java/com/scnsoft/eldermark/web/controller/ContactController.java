package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.beans.ContactFilter;
import com.scnsoft.eldermark.dto.ContactDto;
import com.scnsoft.eldermark.dto.ContactListItemDto;
import com.scnsoft.eldermark.dto.LocationDto;
import com.scnsoft.eldermark.dto.RoleDto;
import com.scnsoft.eldermark.facade.ContactFacade;
import com.scnsoft.eldermark.validation.ValidationGroups;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    @Autowired
    private ContactFacade contactFacade;

    @GetMapping
    public Response<List<ContactListItemDto>> find(@ModelAttribute("contactFilter") ContactFilter contactFilter,
                                                   Pageable pageRequest) {
        return Response.pagedResponse(contactFacade.find(contactFilter, pageRequest));
    }

    @GetMapping(value = "/{contactId}")
    public Response<ContactDto> findById(@PathVariable("contactId") Long contactId) {
        return Response.successResponse(contactFacade.findById(contactId));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> create(@Validated(ValidationGroups.Create.class) @ModelAttribute ContactDto contact) {
        return Response.successResponse(contactFacade.add(contact));
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> update(@Validated(ValidationGroups.Update.class) @ModelAttribute ContactDto contact) {
        return Response.successResponse(contactFacade.edit(contact));
    }

    @PostMapping(value = "/{contactId}/invite")
    public @ResponseBody
    Response<Void> invite(@PathVariable("contactId") Long contactId) {
        contactFacade.invite(contactId);
        return Response.successResponse();
    }

    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> count(@ModelAttribute("contactFilter") ContactFilter contactFilter) {
        return Response.successResponse(contactFacade.count(contactFilter));
    }

    @GetMapping(value = "/validate-uniq", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> validateUniq(@RequestParam(value = "organizationId") Long organizationId, @RequestParam(value = "login") String login) {
        return Response.successResponse(contactFacade.validateUnique(login, organizationId));
    }

    @GetMapping(value = "/{contactId}/location")
    public Response<LocationDto> findLocationById(@PathVariable("contactId") Long contactId) {
        return Response.successResponse(contactFacade.findAddressLocationById(contactId));
    }

    @GetMapping("/qa-unavailable-roles")
    public Response<List<RoleDto>> getQaUnavailableRoles() {
        return Response.successResponse(contactFacade.getQaUnavailableRoles());
    }
}
