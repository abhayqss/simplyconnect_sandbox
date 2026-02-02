package com.scnsoft.eldermark.web.controller.carecoordination;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.services.carecoordination.CommunityCrudService;
import com.scnsoft.eldermark.services.carecoordination.FileService;
import com.scnsoft.eldermark.services.carecoordination.OrganizationService;
import com.scnsoft.eldermark.services.marketplace.MarketplaceService;
import com.scnsoft.eldermark.shared.carecoordination.SelectBoxItemDto;
import com.scnsoft.eldermark.shared.carecoordination.community.*;
import com.scnsoft.eldermark.shared.exceptions.BusinessAccessDeniedException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


//@Controller
//@RequestMapping(value = "/care-coordination/communities")
//@PreAuthorize(SecurityExpressions.IS_CC_USER)
public class CareCoordinationCommunityCrudController {
    @Autowired
    private CommunityCrudService communityCrudService;
    @Autowired
    private MarketplaceService marketplaceService;

    @Autowired
    FileService fileService;

    @Autowired
    private OrganizationService organizationService;


    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public @ResponseBody
    CommunityViewDto getCommunity(@PathVariable Long id) {
        communityCrudService.checkViewAccessToCommunitiesOrThrow(id);
        return communityCrudService.getCommunityDetails( id);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    CommunityViewDto updateCommunity(@PathVariable Long id, @Valid @RequestBody CommunityCreateDto community) {
        if (ObjectUtils.notEqual(id, community.getId())) {
            throw new BusinessAccessDeniedException("ID mismatch");
        }
        checkAccess(community.getId());
        Long userDatabaseId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
        final CommunityViewDto dto = communityCrudService.update(userDatabaseId, id, community);
        dto.setMarketplace(marketplaceService.updateForCommunity(userDatabaseId, id, community.getMarketplace()));
        return dto;
    }

    private void checkAccess (Long id) {
        if (SecurityUtils.hasRole(CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)) {
            boolean hasAccess = false;
            Set<Long> allEmployeeIdsForDatabase = SecurityUtils.getAuthenticatedUser().getEmployeeIdsForCurrentDatabase();
            for (Long employeeId : allEmployeeIdsForDatabase) {
                Set<GrantedAuthority> currentEmployeeAuthorities = SecurityUtils.getAuthenticatedUser().getEmployeeAuthoritiesMap().get(employeeId);
                Long employeeCommunityId = SecurityUtils.getAuthenticatedUser().getLinkedEmployeeById(employeeId).getCommunityId();
                if (SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)) {
                    if (employeeCommunityId.equals(id)) {
                        hasAccess = true;
                        break;
                    }
                }
            }
            if (!hasAccess) {
                throw new BusinessAccessDeniedException("User doesn't not have enough privileges for that operation");
            }
        }
        else {
            SecurityUtils.hasAnyRoleOrThrowException(CareTeamRoleCode.ROLES_CAN_ADD_EDIT_COMMUNITIES);
        }
    }

    @RequestMapping(value = "/{id}/logo", method = RequestMethod.POST)
    public @ResponseBody
    String updateCommunityLogo(@PathVariable Long id, @RequestParam("logo") MultipartFile logo) {
//        SecurityUtils.hasAnyRoleOrThrowException(CareTeamRoleCode.ROLES_CAN_ADD_EDIT_COMMUNITIES);
        checkAccess(id);
        String fileName = fileService.uploadCommunityLogo(id, logo);
        return "{\"result\":\"Ok\", \"name\":\""+fileName+"\"}";
    }


    @RequestMapping(value = "/{id}/logo", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteCommunityLogo(@PathVariable Long id) {
//        SecurityUtils.hasAnyRoleOrThrowException(CareTeamRoleCode.ROLES_CAN_ADD_EDIT_COMMUNITIES);
        checkAccess(id);
        fileService.deleteCommunityLogo(id);
        return "{\"result\":\"Ok\"}";
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteCommunity(@PathVariable Long id) {
        SecurityUtils.hasAnyRoleOrThrowException(CareTeamRoleCode.ROLES_CAN_ADD_EDIT_COMMUNITIES);
        marketplaceService.deleteForCommunity(id);
        communityCrudService.deleteCommunity(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody public CommunityViewDto createCommunity(@Valid @RequestBody CommunityCreateDto community) {
        SecurityUtils.hasAnyRoleOrThrowException(CareTeamRoleCode.ROLES_CAN_ADD_EDIT_COMMUNITIES);
        Long userDatabaseId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
        final CommunityViewDto dto = communityCrudService.create(userDatabaseId, community, false);
        dto.setMarketplace(marketplaceService.updateForCommunity(userDatabaseId, dto.getId(), community.getMarketplace()));
        return dto;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Page<CommunityListItemDto> getCommunityPage(@ModelAttribute("communityFilter") CommunityFilterDto communityFilterDto, Pageable pageRequest) {
        if (!canViewCommunities()) {
            return new PageImpl<CommunityListItemDto>(new ArrayList<CommunityListItemDto>(), null, 0);
        }
        return communityCrudService.listDto(pageRequest, communityFilterDto);
    }

    @RequestMapping(value = "/byorg/{databaseId}", method = RequestMethod.GET)
    @ResponseBody
    public Page<CommunityListItemDto> getCommunityPage(@PathVariable Long databaseId, Pageable pageRequest) {
        if (!canViewCommunities()) {
            return new PageImpl<CommunityListItemDto>(new ArrayList<CommunityListItemDto>(), null, 0);
        }
        return communityCrudService.listDto(pageRequest, databaseId, false);
    }

    @RequestMapping(method=RequestMethod.GET, value="/isUnique")
    public @ResponseBody Boolean checkUniqueness(
            @ModelAttribute CommunityCreateDto data
    ) {
        return communityCrudService.checkIfUnique(data);
    }

    @RequestMapping(method=RequestMethod.GET, value="/selectList")
    public @ResponseBody List<SelectBoxItemDto> getCommunityList() {
        List<Long> currentCommunityIds = SecurityUtils.getAuthenticatedUser().getCurrentCommunityIds();
        if (currentCommunityIds==null) currentCommunityIds = new ArrayList<Long>();
        List<CommunityListItemDto> communitiesSource = communityCrudService.filterListDto().getContent();
        List<SelectBoxItemDto> communities = new ArrayList<SelectBoxItemDto>();
        communities.add(new SelectBoxItemDto(0L, "All", currentCommunityIds.isEmpty()));
        for (CommunityListItemDto dto: communitiesSource) {
            communities.add(new SelectBoxItemDto(dto.getId(), dto.getName(), currentCommunityIds.contains(dto.getId())));
        }
        return communities;

    }

    @RequestMapping(method=RequestMethod.GET, value="/count")
    public @ResponseBody Integer getCommunityCount() {
//        if (SecurityUtils.hasRole(CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)) {
//            return 1;
//        }
//        else {
            if (!canViewCommunities()) {
                return 0;
            }
            return communityCrudService.getCommunityCountForCurrentUser();
//        }
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody public CommunityViewDto updateCommunityData(@PathVariable Long id, @Valid @RequestBody CommunityCreateDto community) {
        SecurityUtils.hasAnyRoleOrThrowException(CareTeamRoleCode.ROLES_CAN_ADD_EDIT_COMMUNITIES);
        Long userDatabaseId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
        final CommunityViewDto dto = communityCrudService.updateData(userDatabaseId, id, community);
        dto.setMarketplace(community.getMarketplace());
        return dto;
    }

    private boolean canViewCommunities() {
        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            return true;
        }
        ExchangeUserDetails userDetails = SecurityUtils.getAuthenticatedUser();
        if (!SecurityUtils.isAffiliatedView()) {
            if (!SecurityUtils.hasAnyRole(userDetails.getAuthoritiesForDatabase(userDetails.getCurrentDatabaseId()),CareTeamRoleCode.ROLES_CAN_VIEW_OWN_COMMUNITIES)) {
                return false;
            }
        } else {
            boolean canViewCommunities = SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR);
            Set<Long> affiliatedOrgIds = organizationService.getAffiliatedOrgIds(userDetails.getCurrentDatabaseId());
            if (CollectionUtils.isNotEmpty(affiliatedOrgIds)) {
                for (Long affiliatedOrgId : affiliatedOrgIds) {
                    canViewCommunities = canViewCommunities ||  SecurityUtils.hasAnyRole(userDetails.getAuthoritiesForDatabase(affiliatedOrgId), CareTeamRoleCode.ROLES_CAN_VIEW_OWN_COMMUNITIES);
                }
            }
            if (!canViewCommunities) {
                return false;
            }
        }
        return true;
    }

}
