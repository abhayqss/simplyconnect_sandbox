package com.scnsoft.eldermark.web.controller.carecoordination;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.carecoordination.CareCoordinationCommunityDao;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.carecoordination.CareCoordinationResidentService;
import com.scnsoft.eldermark.services.carecoordination.CommunityCrudService;
import com.scnsoft.eldermark.services.carecoordination.OrganizationService;
import com.scnsoft.eldermark.services.externalapi.NucleusInfoService;
import com.scnsoft.eldermark.shared.carecoordination.ChangeOrganizationResultDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author averazub
 * @author knetkachou
 * @author mradzivonenka
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 30-Sep-15.
 */
//@Controller
//@RequestMapping(value = "/care-coordination")
//@PreAuthorize(SecurityExpressions.IS_CC_USER)
public class CareCoordinationController {

    @Autowired
    private StateService stateService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CommunityCrudService communityCrudService;

    @Autowired
    CareCoordinationCommunityDao careCoordinationCommunityDao;

    @Autowired
    CareCoordinationResidentService careCoordinationResidentService;

    @Autowired
    private NucleusInfoService nucleusService;

    private static final String[] EMPTY_ARRAY = new String[0];

    @RequestMapping(method = RequestMethod.GET)
    public String initView(Model model) {
        if (SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_CAN_VIEW_OWN_COMMUNITIES)) {
            model.addAttribute("currentOrgCommunityCount", communityCrudService.getCommunityCountForCurrentUser());
        } else {
            model.addAttribute("currentOrgCommunityCount", 0);
        }
        final boolean showCommunitiesTab = SecurityUtils.hasAnyRole(
                new HashSet<>(SecurityUtils.getAuthenticatedUser().getAuthorities()), CareTeamRoleCode.ROLES_CAN_VIEW_OWN_COMMUNITIES);
        model.addAttribute("showCommunitiesTab", showCommunitiesTab);
        model.addAttribute("currentOrgPatientsCount", careCoordinationResidentService.getResidentsCountForCurrentUserAndOrganization());

        if (nucleusService.isNucleusIntegrationEnabled()) {
            // TODO change to collection of user IDs for linked accounts
            final Long employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();
            final String loggedInEmployeeNucleusUserId = nucleusService.findByEmployeeId(employeeId);
            if (StringUtils.isNotBlank(loggedInEmployeeNucleusUserId)) {
                model.addAttribute("loggedInEmployeeNucleusUserId", loggedInEmployeeNucleusUserId);
                model.addAttribute("nucleusPollingAuthToken", nucleusService.getNucleusPollingAuthToken());
                model.addAttribute("nucleusAuthToken", nucleusService.getNucleusAuthToken());
                model.addAttribute("nucleusHost", nucleusService.getNucleusHost());
            }
        }

        return "careCoordination";
    }

    @ModelAttribute("states")
    public List<KeyValueDto> getStates() {
        return stateService.getStates();
    }


    @RequestMapping(method = RequestMethod.POST, value = "/admin/databaseId")
    public @ResponseBody ChangeOrganizationResultDto setCurrentDatabaseId(@RequestParam Long databaseId) {
        ExchangeUserDetails userDetails = SecurityUtils.getAuthenticatedUser();
        organizationService.setCurrentOrganization(databaseId);

        //in case organization is changed - we change authorities for user related to new organization
        //no need to change for super admin: he has access to everything
        if (!SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(userDetails, auth.getCredentials(), userDetails.getAuthorities());
            newAuth.setDetails(auth.getDetails());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

//        List<Long> communityIds = SecurityUtils.getAuthenticatedUser().getCurrentCommunityIds();

        if (SecurityUtils.isAffiliatedView()) {
            List<Long>communityIds = careCoordinationCommunityDao.getInitialOrganizationIds(userDetails.getCommunityId(), userDetails.getCurrentDatabaseId(), userDetails.getEmployee().getDatabaseId());
            if (!communityIds.contains(null)) {
                userDetails.setCurrentCommunityIdsForUser(communityIds);
            }
        }
//        else if (SecurityUtils.hasRole(CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)) {
//            if (!SecurityUtils.isAffiliatedView()) {
//                List<Long>communityIds = new ArrayList<Long>();
//                communityIds.add(userDetails.getCommunityId());
//                userDetails.setCurrentCommunityIdsForUser(communityIds);
//            }
//        }
        Boolean showCommunitiesTab = SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR);
        showCommunitiesTab = showCommunitiesTab || SecurityUtils.hasAnyRole(userDetails.getAuthoritiesForDatabase(userDetails.getCurrentDatabaseId()), CareTeamRoleCode.ROLES_CAN_VIEW_OWN_COMMUNITIES);
        if (!showCommunitiesTab && CollectionUtils.isEmpty(userDetails.getEmployeeCommunitiesForCurrentDatabase())) {
            Set<Long> affiliatedOrgIds = organizationService.getAffiliatedOrgIds(userDetails.getCurrentDatabaseId());
            if (CollectionUtils.isNotEmpty(affiliatedOrgIds)) {
                for (Long affiliatedOrgId : affiliatedOrgIds) {
                    showCommunitiesTab = showCommunitiesTab ||  SecurityUtils.hasAnyRole(userDetails.getAuthoritiesForDatabase(affiliatedOrgId), CareTeamRoleCode.ROLES_CAN_VIEW_OWN_COMMUNITIES);
                }
            }
        }
        return new ChangeOrganizationResultDto(true, showCommunitiesTab);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/admin/communityId")
    public @ResponseBody Boolean setCurrentCommunityIds(@RequestParam(value = "communityId") String communityIdsStr) {
        List<Long> communityIds = new ArrayList<>();
        String[] communityIdsStrArray = StringUtils.isEmpty(communityIdsStr)? EMPTY_ARRAY : communityIdsStr.split(",");
        for (String communityIdStr: communityIdsStrArray) {
            Long id = "null".equals(communityIdStr)?0:Long.valueOf(communityIdStr);
            if (id==0) {
                communityIds.clear();
                break;
            } else  {
                communityIds.add(id);
            }
        }
        communityCrudService.checkViewAccessToCommunitiesOrThrow(communityIds, true);
        SecurityUtils.getAuthenticatedUser().setCurrentCommunityIdsForUser(communityIds);
        return true;
    }

}
