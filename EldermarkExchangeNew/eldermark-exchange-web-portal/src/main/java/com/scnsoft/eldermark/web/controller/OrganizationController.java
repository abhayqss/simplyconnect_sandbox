package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.web.commons.dto.FileBytesDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.facade.OrganizationFacade;
import com.scnsoft.eldermark.validation.ValidationGroups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    @Autowired
    private OrganizationFacade organizationFacade;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<OrganizationListItemDto>> find(@RequestParam(value = "name", required = false) String name,
                                                        Pageable pageRequest) {
        var page = organizationFacade.find(pageRequest, name);
        return Response.pagedResponse(page.getContent(), page.getTotalElements());
    }

    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> count() {
        return Response.successResponse(organizationFacade.count());
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> add(@ModelAttribute @Validated(ValidationGroups.Create.class) OrganizationDto organizationDto) {
        return Response.successResponse(organizationFacade.add(organizationDto));
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> edit(@ModelAttribute @Validated(ValidationGroups.Update.class) OrganizationDto organizationDto) {
        return Response.successResponse(organizationFacade.edit(organizationDto));
    }

    @GetMapping(value = "/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<? extends OrganizationBaseDto> findById(@PathVariable("organizationId") Long organizationId, @RequestParam(value = "marketplaceDataIncluded", required = false) Boolean marketplaceDataIncluded) {
        return Response.successResponse(organizationFacade.findById(organizationId, marketplaceDataIncluded));
    }

    @GetMapping(value = "/can-add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canAdd() {
        return Response.successResponse(organizationFacade.canAdd());
    }

    @GetMapping(value = "/{organizationId}/logo")
    public Response<byte[]> downloadLogo(@PathVariable("organizationId") Long organizationId) {
        FileBytesDto logo = organizationFacade.downloadLogo(organizationId);
        return Response.successResponse(new Response.Body<>(logo.getBytes(), logo.getMediaType()));
    }

    @GetMapping(value = "/validate-uniq", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<OrganizationUniquenessDto> validateUniq(@RequestParam(value = "name", required = false) String name,
                                                            @RequestParam(value = "oid", required = false) String oid,
                                                            @RequestParam(value = "companyId", required = false) String companyId,
                                                            @RequestParam(value = "organizationId", required = false) Long organizationId) {
        return Response.successResponse(organizationFacade.validateUniqueFields(oid, name, companyId, organizationId));
    }

    @GetMapping("/permissions")
    public Response<OrganizationPermissionsDto> getPermissions() {
        return Response.successResponse(organizationFacade.getPermissions());
    }
}
