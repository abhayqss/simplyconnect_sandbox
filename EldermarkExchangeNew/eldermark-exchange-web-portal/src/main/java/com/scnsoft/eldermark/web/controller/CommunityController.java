package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.annotations.SwaggerDoc;
import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.web.commons.dto.FileBytesDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.facade.CommunityFacade;
import com.scnsoft.eldermark.validation.ValidationGroups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/organizations/{organizationId}/communities")
public class CommunityController {

    @Autowired
    private CommunityFacade communityFacade;

    @SwaggerDoc
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<CommunityListItemDto>> findByOrgId(@PathVariable("organizationId") Long organizationId,
                                                            Pageable pageRequest) {
        var pageable = communityFacade.findByOrgId(organizationId, pageRequest);
        return Response.pagedResponse(pageable);
    }

    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> count(@PathVariable("organizationId") Long organizationId) {
        return Response.successResponse(communityFacade.count(organizationId));
    }

    @SwaggerDoc
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> add(
        @NotNull @PathVariable("organizationId") Long organizationId,
        @Validated(ValidationGroups.Create.class) @ModelAttribute CommunityDto communityDto
    ) {
        communityDto.setOrganizationId(organizationId);
        return Response.successResponse(communityFacade.add(communityDto));
    }

    @SwaggerDoc
	@PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Response<Long> edit(
        @NotNull @PathVariable("organizationId") Long organizationId,
        @Validated(ValidationGroups.Update.class) @ModelAttribute CommunityDto communityDto
    ) {
		communityDto.setOrganizationId(organizationId);
		return Response.successResponse(communityFacade.edit(communityDto));
	}

    @SwaggerDoc
    @GetMapping(value = "/{communityId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<CommunityDto> findById(@PathVariable("communityId") Long communityId,
                                                         @RequestParam(value="marketplaceDataIncluded", required = false) Boolean marketplaceDataIncluded) {
        return Response.successResponse(communityFacade.findById(communityId, marketplaceDataIncluded));
    }

    @Deprecated
    @GetMapping(value = "/{communityId}/exists-affiliated", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> isExistsAffiliated(@PathVariable("communityId") Long communityId) {
        return Response.successResponse(communityFacade.isExistsAffiliated(communityId));
    }

    @GetMapping(value = "/can-add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canAdd(@PathVariable("organizationId") Long organizationId) {
        return Response.successResponse(communityFacade.canAdd(organizationId));
    }

    @GetMapping(value = "/{communityId}/logo")
    public Response<byte[]> downloadLogo(@PathVariable("communityId") Long communityId) {
        FileBytesDto logo = communityFacade.downloadLogo(communityId);
        return Response.successResponse(new Response.Body<>(logo.getBytes(), logo.getMediaType()));
    }

    @GetMapping(value = "/validate-uniq", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<CommunityUniquenessDto> validateUniq(@PathVariable("organizationId") Long organizationId,
                                                         @RequestParam(value="name", required = false) String name,
                                                         @RequestParam(value="oid", required = false) String oid) {
        return Response.successResponse(communityFacade.validateUniqueFields(organizationId, oid, name));
    }

    @ResponseBody
    @GetMapping(value = "/{communityId}/services")
    public Response<List<ServiceTypeListItemDto>> getServices(@PathVariable Long communityId) {
        return Response.successResponse(communityFacade.getServices(communityId));
    }


    @GetMapping(value = "/{communityId}/pictures/{pictureId}")
    public Response<byte[]> downloadPictureById(@PathVariable("pictureId") Long pictureId) {
        var dto = communityFacade.downloadPictureById(pictureId);
        return Response.successResponse(dto.getBytes(), dto.getMediaType());
    }

    @GetMapping("/permissions")
    public Response<CommunityPermissionsDto> getPermissions(@PathVariable Long organizationId) {
        return Response.successResponse(communityFacade.getPermissions(organizationId));
    }

}
