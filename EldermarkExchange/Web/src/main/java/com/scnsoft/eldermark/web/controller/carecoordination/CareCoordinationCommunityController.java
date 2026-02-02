package com.scnsoft.eldermark.web.controller.carecoordination;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.carecoordination.NotificationType;
import com.scnsoft.eldermark.dao.carecoordination.Responsibility;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.services.carecoordination.*;
import com.scnsoft.eldermark.services.externalapi.NucleusInfoService;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.careteam.CareTeamMemberDto;
import com.scnsoft.eldermark.shared.carecoordination.careteam.CareTeamMemberListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.contacts.LinkedContactDto;
import com.scnsoft.eldermark.shared.carecoordination.patients.NotificationPreferencesGroupDto;
import com.scnsoft.eldermark.shared.exceptions.BusinessAccessDeniedException;
import com.scnsoft.eldermark.web.exception.BadRequestException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by pzhurba on 27-Oct-15.
 */
//@Controller
//@RequestMapping(value = "/care-coordination/communities/community/{communityId}")
//@PreAuthorize(SecurityExpressions.IS_CC_USER)
public class CareCoordinationCommunityController {

    @Autowired
    CareTeamService careTeamService;
    @Autowired
    CareTeamRoleService careTeamRoleService;

    @Autowired
    EventTypeService eventTypeService;

    @Autowired
    ContactService contactService;

    @Autowired
    private CommunityCrudService communityCrudService;

    @Autowired
    private NucleusInfoService nucleusService;

    @RequestMapping(value = "/care-team/{careTeamMemberId}/{affiliated}/{roleEditable}", method = RequestMethod.GET)
    public String getEditCareTeamMemberTemplate(Model model, @PathVariable(value = "communityId") Long communityId, @PathVariable(value = "careTeamMemberId") final Long careTeamMemberId,
                                                @PathVariable("affiliated") Boolean affiliated, @PathVariable("roleEditable") Boolean roleEditable) {
        communityCrudService.checkViewAccessToCommunitiesOrThrow(communityId);
        //SecurityUtils.hasAnyRoleOrThrowException(CareTeamRoleCode.ROLES_CAN_EDIT_SELF_CARE_TEAM_MEMBERS);
        return getCareTeamMemberTemplate(model, careTeamMemberId, communityId, affiliated, roleEditable);
    }

    @RequestMapping(value = "/care-team/{affiliated}/{roleEditable}", method = RequestMethod.GET)
    public String getCreateCareTeamMemberTemplate(Model model, @PathVariable(value = "communityId") Long communityId,@PathVariable("affiliated") Boolean affiliated,
                                                  @PathVariable("roleEditable") Boolean roleEditable) {
        communityCrudService.checkViewAccessToCommunitiesOrThrow(communityId);
        return getCareTeamMemberTemplate(model, null, communityId, affiliated, roleEditable);
    }

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public String getCommunity(@PathVariable("communityId") Long communityId, Model model) {
        communityCrudService.checkViewAccessToCommunitiesOrThrow(communityId);
        model.addAttribute("community", communityCrudService.getCommunityDetails(communityId));
        boolean affiliatedView = SecurityUtils.isAffiliatedView();
        boolean comAdminCanEdit = SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR);

        boolean hasAddCtmRole = false;
        boolean hasAddAffiliatedCtm = SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR);
        boolean hasEditRole = false;
        boolean hasChangeCtmRoleRole = false;
        Set<Long> employeeIds = SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds();
        if (CollectionUtils.isNotEmpty(employeeIds)) {
            for (Long employeeId :  employeeIds) {
                List<Long> communityIds = communityCrudService.getUserCommunityIds(false, employeeId, false);
                if ((communityIds == null) || (communityIds != null && communityIds.contains(communityId))) {
                    Set<GrantedAuthority> currentEmployeeAuthorities = SecurityUtils.getAuthenticatedUser().getEmployeeAuthoritiesMap().get(employeeId);
                    hasAddCtmRole = hasAddCtmRole || SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_CAN_ADD_EDIT_COMMUNITY_CARE_TEAM_MEMBERS);
                    hasEditRole = hasEditRole || SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_ADMINISTRATORS);
                    hasChangeCtmRoleRole = hasChangeCtmRoleRole ||
                            !(SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.PRIMARY_PHYSICIAN) || SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.BEHAVIORAL_HEALTH));
                    hasAddAffiliatedCtm = hasAddAffiliatedCtm || SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_CAN_ADD_EDIT_AFF_COMMUNITY_CARE_TEAM_MEMBERS);
                    if (SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)) {
                        LinkedContactDto currentEmployee = SecurityUtils.getAuthenticatedUser().getLinkedEmployeeById(employeeId);
                        hasEditRole = hasEditRole || currentEmployee.getCommunityId().equals(communityId);
                        hasAddCtmRole = hasAddCtmRole || currentEmployee.getCommunityId().equals(communityId);
                    }
                }
            }
        }

        model.addAttribute("affiliatedView", affiliatedView);
        model.addAttribute("editable", !affiliatedView &&
                (hasEditRole || comAdminCanEdit));
        model.addAttribute("canAddCtm", !affiliatedView &&
                (hasAddCtmRole || comAdminCanEdit));
        model.addAttribute("hasAddAffiliatedCtm", hasAddAffiliatedCtm);

        addNucleusAttributes(model);

        return "care.coordination.community.details";
    }

    private void addNucleusAttributes(Model model) {
        if (nucleusService.isNucleusIntegrationEnabled()) {
            final Long employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();
            final String loggedInEmployeeNucleusUserId = nucleusService.findByEmployeeId(employeeId);
            if (StringUtils.isNotBlank(loggedInEmployeeNucleusUserId)) {
                model.addAttribute("loggedInEmployeeNucleusUserId", loggedInEmployeeNucleusUserId);
                model.addAttribute("nucleusAuthToken", nucleusService.getNucleusAuthToken());
                model.addAttribute("nucleusHost", nucleusService.getNucleusHost());
            }
        }
    }

    @RequestMapping(value = "/show-copy-settings", method = RequestMethod.GET)
    @ResponseBody
    public Boolean isShowCopySettings(@PathVariable("communityId") Long communityId) {
        return communityCrudService.isShowCopySettings(communityId,null);
    }

    @RequestMapping(value = "/copy-settings", method = RequestMethod.GET)
    public String getCopySettingsCommunities(@PathVariable("communityId") Long communityId, Model model) {
        //communityCrudService.checkViewAccessToCommunitiesOrThrow(communityId);
        model.addAttribute("communities", communityCrudService.getCopySettingsCommunities(communityId));
        model.addAttribute("communityName", communityCrudService.getCommunityName(communityId));
        return "care.coordination.community.copysettings";
    }

    @RequestMapping(value = "/copy-settings/{copyFromCommunityId}", method = RequestMethod.GET)
    @ResponseBody
    public void getCopySettingsCommunities(@PathVariable("communityId") Long communityId, @PathVariable("copyFromCommunityId") Long copyFromCommunityId) {
        communityCrudService.copySettings(communityId, copyFromCommunityId);
    }

    @RequestMapping(value = "/care-team/{careTeamMemberId}/delete", method = RequestMethod.GET)
    public String getDeleteCareTeamMemberTemplate(@PathVariable("communityId") Long communityId, @PathVariable("careTeamMemberId") Long careTeamMemberId) {
        //I guess no need to check it 2 times
        //communityCrudService.checkAddEditCareTeamAccessToCommunity(communityId, careTeamMemberId);
        return "care.coordination.delete.careteam.member";
    }

    @RequestMapping(value = "/care-team/{careTeamMemberId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteCareTeamMember(@PathVariable("communityId") Long communityId, @PathVariable("careTeamMemberId") Long careTeamMemberId) {
        communityCrudService.checkAddEditCareTeamAccessToCommunity(communityId, careTeamMemberId, null);
        careTeamService.deleteCommunityCareTeamMember(careTeamMemberId);
    }

    @RequestMapping(value = "/care-team/{affiliated}", method = RequestMethod.POST)
    @ResponseBody
    public Page<CareTeamMemberListItemDto> getCommunityCareTeam(@PathVariable("communityId") Long communityId, @PathVariable("affiliated") Boolean affiliated, Pageable pageable) {
            communityCrudService.checkViewAccessToCommunitiesOrThrow(communityId);
        final Page<CareTeamMemberListItemDto> result = careTeamService.getCommunityCareTeam(communityId, affiliated, pageable);
        return result;

    }

//    @RequestMapping(value = "/affiliated-care-team", method = RequestMethod.POST)
//    @ResponseBody
//    public Page<CareTeamMemberListItemDto> getAffiliatedCommunityCareTeam(@PathVariable("communityId") Long communityId, Pageable pageable) {
//        communityCrudService.checkViewAccessToCommunitiesOrThrow(communityId);
//        final Page<CareTeamMemberListItemDto> result = careTeamService.getCommunityCareTeam(communityId, affiliated, pageable);
//        return result;
//
//    }

    @RequestMapping(value = "/care-team/notification-preferences", method = RequestMethod.GET)
    @ResponseBody
    public List<NotificationPreferencesGroupDto> getNotificationPreferences(
            @PathVariable("communityId") Long communityId,
            @RequestParam(value = "careTeamRoleId", required = false) Long careTeamRoleId,
            @RequestParam(value = "careTeamMemberId", required = false) Long careTeamMemberId,
            @RequestParam(value = "employeeId", required = false) Long employeeId
    ) {
        communityCrudService.checkViewAccessToCommunitiesOrThrow(communityId);
        return careTeamService.getAvailableNotificationPreferences(careTeamRoleId, careTeamMemberId, employeeId);
    }


    @RequestMapping(value = "/care-team", method = RequestMethod.PUT, headers = "Accept=application/json")
    @ResponseBody
    public void saveComunityCareTeamMember(@PathVariable("communityId") final Long communityId, @RequestBody final CareTeamMemberDto careTeamMemberDto) {
        boolean canEditSelf = communityCrudService.checkAddEditCareTeamAccessToCommunity(communityId, careTeamMemberDto.getCareTeamMemberId(),careTeamMemberDto.getCareTeamEmployeeSelect());
        if (careTeamMemberDto.getCareTeamMemberId() == null) {
            if (careTeamMemberDto.getCareTeamRoleSelect() == null) {
                throw new BadRequestException("Please Select Role");
            }
            if (careTeamMemberDto.getCareTeamEmployeeSelect() == null) {
                throw new BadRequestException("Please Select Employee");
            }
        }
        if (CollectionUtils.isEmpty(careTeamMemberDto.getNotificationPreferences())) {
            throw new BadRequestException("Notification preferences is Mandatory");
        }

        careTeamService.createOrUpdateCommunityCareTeamMember(communityId, careTeamMemberDto, canEditSelf,false);
    }

    @ModelAttribute("notificationTypes")
    public NotificationType[] getNotificationTypes() {
        return NotificationType.values();
    }

    @ModelAttribute("responsibilities")
    public Responsibility[] getResponsibilities() {
        return Responsibility.values();
    }

    @ModelAttribute("careTeamRoles")
    public List<KeyValueDto> getCareTeamRoles() {
        final List<KeyValueDto> result = new ArrayList<KeyValueDto>();
        result.add(new KeyValueDto(null, "-- Select Role --"));
        result.addAll(careTeamRoleService.getAllCareTeamRoles());
        return result;
    }

    @ModelAttribute("roles")
    public List<KeyValueDto> getNonAdminCareTeamRoles() {
        final List<KeyValueDto> result = new ArrayList<KeyValueDto>();
        result.add(new KeyValueDto(null, "-- Select Role --"));
        result.addAll(careTeamRoleService.getNonAdminCareTeamRoles());
        return result;
    }

    @ModelAttribute("eventTypes")
    public List<KeyValueDto> getEventServices() {
        return eventTypeService.getAllEventTypes();
    }

    private String getCareTeamMemberTemplate(Model model, Long careTeamMemberId, Long communityId,  Boolean affiliated, Boolean canChangeCtmRole) {
        final CareTeamMemberDto careTeamMemberDto = new CareTeamMemberDto();
        model.addAttribute("careTeamMemberDto", careTeamMemberDto);

        model.addAttribute("notificationTypeList", Arrays.asList(NotificationType.SMS));
        model.addAttribute("responsibility", Responsibility.A);

        final List<KeyValueDto> employees = new ArrayList<KeyValueDto>();
        if (careTeamMemberId == null) {
            employees.add(new KeyValueDto(null, "-- Select Contact --"));
            employees.addAll(contactService.getEmployeeSelectList(communityId, null, affiliated, null, null));
        }
        else {
            employees.add(careTeamService.getEmployeeForCareTeamMember(careTeamMemberId));
        }

        model.addAttribute("employees",employees);

        if (careTeamMemberId != null) {
            careTeamMemberDto.setCanChangeEmployee(false);
            careTeamMemberDto.setCanChangeRole(canChangeCtmRole);
        } else {
            careTeamMemberDto.setCanChangeEmployee(true);
            careTeamMemberDto.setCanChangeRole(true);
        }

        return "care.coordination.create.careteam.member";
    }

    @RequestMapping(value = "/has-view-access", method = RequestMethod.GET)
    @ResponseBody
    public Boolean hasViewAccess(@PathVariable("communityId") final Long communityId) {
        try {
            communityCrudService.checkViewAccessToCommunitiesOrThrow(communityId);
            return true;
        } catch (BusinessAccessDeniedException e) {
            return false;
        }
    }
}
