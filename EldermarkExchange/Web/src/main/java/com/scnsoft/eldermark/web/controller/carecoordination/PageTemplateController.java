package com.scnsoft.eldermark.web.controller.carecoordination;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.carecoordination.CommunityCrudService;
import com.scnsoft.eldermark.services.carecoordination.InNetworkInsuranceService;
import com.scnsoft.eldermark.services.carecoordination.OrganizationService;
import com.scnsoft.eldermark.services.marketplace.MarketplaceService;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.OrganizationAffiliatedDetailsDto;
import com.scnsoft.eldermark.shared.carecoordination.OrganizationDto;
import com.scnsoft.eldermark.shared.carecoordination.OrganizationFilterDto;
import com.scnsoft.eldermark.shared.carecoordination.OrganizationListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.community.CommunityCreateDto;
import com.scnsoft.eldermark.shared.carecoordination.community.CommunityFilterDto;
import com.scnsoft.eldermark.shared.carecoordination.community.CommunityListItemDto;
import com.scnsoft.eldermark.shared.marketplace.InNetworkInsuranceDto;
import com.scnsoft.eldermark.shared.marketplace.MarketplaceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author averazub
 * @author knetkachou
 * @author mradzivonenka
 * @author phomal
 * Created by averazub on 3/29/2016.
 */
//@Controller
//@RequestMapping(value = "/care-coordination/templates")
//@PreAuthorize(SecurityExpressions.IS_CC_USER)
public class PageTemplateController {

    @Autowired
    OrganizationService organizationService;

    @Autowired
    CommunityCrudService communityCrudService;

    @Autowired
    StateService stateService;

    @Autowired
    MarketplaceService marketplaceService;

    @Autowired
    private InNetworkInsuranceService inNetworkInsuranceService;

    @Value("${unaffiliated.community.oid}")
    private String unaffiliatedCommunityOid;

    @Value("${unaffiliated.organization.oid}")
    private String unaffiliatedOrganizationOid;

    @Secured(value = {CareTeamRoleCode.CASE_MANAGER
            , CareTeamRoleCode.CARE_COORDINATOR
            , CareTeamRoleCode.SERVICE_PROVIDER
            , CareTeamRoleCode.PRIMARY_PHYSICIAN
            , CareTeamRoleCode.BEHAVIORAL_HEALTH
            , CareTeamRoleCode.COMMUNITY_MEMBERS
    })
    @RequestMapping(value = "/organizations", method = RequestMethod.GET)
    public String getOrganizationsView(Model model) {
        model.addAttribute("orgFilter", new OrganizationFilterDto());
        return "care.coordination.organizations";
    }

    @Secured(value = {CareTeamRoleCode.CASE_MANAGER
            , CareTeamRoleCode.CARE_COORDINATOR
            , CareTeamRoleCode.SERVICE_PROVIDER
            , CareTeamRoleCode.PRIMARY_PHYSICIAN
            , CareTeamRoleCode.BEHAVIORAL_HEALTH
            , CareTeamRoleCode.COMMUNITY_MEMBERS
    })
    @RequestMapping(value = "/organizations/{orgId}", method = RequestMethod.GET)
    public String initOrgNewView(@PathVariable("orgId") Long orgId, Model model) {
        OrganizationDto organizationDto;
        if (orgId == null || orgId == 0L) {
            organizationDto = new OrganizationDto();
        } else {
            organizationDto = organizationService.getOrganizationWithAffiliatedDetails(orgId);
            organizationDto.setMarketplace(marketplaceService.getMarketplaceByOrganizationId(orgId));
        }
        model.addAttribute("orgDto", organizationDto);
        model.addAttribute("unaffiliated", organizationDto.getOid() != null && organizationDto.getOid().equals(unaffiliatedOrganizationOid));

        model.addAttribute("states", stateService.getStates());

        model.addAttribute("primaryFocuses", marketplaceService.getPrimaryFocuses());
        model.addAttribute("communityTypes", marketplaceService.getFilteredCommunityTypesListofLists(organizationDto.getMarketplace().getPrimaryFocusIds()));
        model.addAttribute("levelsOfCare", marketplaceService.getLevelsOfCare());
        model.addAttribute("ageGroups", marketplaceService.getAgeGroups());
        model.addAttribute("servicesTreatmentApproaches", marketplaceService.getFilteredServiceTreatmentApproachListofLists(organizationDto.getMarketplace().getPrimaryFocusIds()));
        model.addAttribute("emergencyServices", marketplaceService.getEmergencyServices());
        model.addAttribute("languageServices", marketplaceService.getLanguageServices());
        model.addAttribute("ancillaryServices", marketplaceService.getAncillaryServices());
        model.addAttribute("insuranceCarriers", marketplaceService.getInNetworkInsurances());

        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            CommunityListItemDto allCommunitiesLabel = new CommunityListItemDto(0L, "All Communities", null);
            List<OrganizationListItemDto> orgsSource = organizationService.list(new OrganizationFilterDto(), new PageRequest(0, 1000)).getContent();
            List<KeyValueDto> orgs = new ArrayList<KeyValueDto>();
            for (OrganizationListItemDto dto : orgsSource) {
                if (!dto.getId().equals(orgId)) {
                    orgs.add(new KeyValueDto(dto.getId(), dto.getName()));
                }
            }
            model.addAttribute("affOrganizations", orgs);

            List<CommunityListItemDto> communityListItemDtos = communityCrudService.listDto(orgId);
            communityListItemDtos.add(0, allCommunitiesLabel);
            model.addAttribute("communities", communityListItemDtos);

            if (!CollectionUtils.isEmpty(organizationDto.getAffiliatedDetails())) {
                List<List<CommunityListItemDto>> affCommunitiesList = new ArrayList<List<CommunityListItemDto>>();
                for (OrganizationAffiliatedDetailsDto detailsDto : organizationDto.getAffiliatedDetails()) {
                    List<CommunityListItemDto> affCommunityListItemDtos = communityCrudService.listDto(detailsDto.getAffOrgId());
                    affCommunityListItemDtos.add(0, allCommunitiesLabel);
                    affCommunitiesList.add(affCommunityListItemDtos);
                }
                model.addAttribute("affCommunities", affCommunitiesList);
            }
        }

        return "care.coordination.organizations.edit";
    }


    @Secured(value = {CareTeamRoleCode.CASE_MANAGER
            , CareTeamRoleCode.CARE_COORDINATOR
            , CareTeamRoleCode.SERVICE_PROVIDER
            , CareTeamRoleCode.PRIMARY_PHYSICIAN
            , CareTeamRoleCode.BEHAVIORAL_HEALTH
            , CareTeamRoleCode.COMMUNITY_MEMBERS
    })
    @RequestMapping(value = "/communities", method = RequestMethod.GET)
    public String getCommunitiesView(Model model) {
        String databaseName = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseName();
        model.addAttribute("organizationName", databaseName);
        model.addAttribute("affiliatedView", SecurityUtils.isAffiliatedView());
        model.addAttribute("canAddCommunity", !SecurityUtils.isUnAffiliatedOrg() && SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_CAN_ADD_EDIT_COMMUNITIES));
        model.addAttribute("communityFilter", new CommunityFilterDto());
        return "care.coordination.communities";
    }

    @Secured(value = {CareTeamRoleCode.CASE_MANAGER
            , CareTeamRoleCode.CARE_COORDINATOR
            , CareTeamRoleCode.SERVICE_PROVIDER
            , CareTeamRoleCode.PRIMARY_PHYSICIAN
            , CareTeamRoleCode.BEHAVIORAL_HEALTH
            , CareTeamRoleCode.COMMUNITY_MEMBERS
    })
    @RequestMapping(value = "/communities/edit/{communityId}", method = RequestMethod.GET)
    public String initCommunityNewView(@PathVariable("communityId") Long communityId, Model model) {
        CommunityCreateDto communityCreateDto;
        if (communityId == null || communityId == 0L) {
            communityCreateDto = new CommunityCreateDto();
        } else {
            communityCreateDto = communityCrudService.getCommunityCrudDetails(communityId);
            communityCreateDto.setMarketplace(marketplaceService.getMarketplaceByCommunityId(communityId));
            if (communityCreateDto.getOid() != null) {
                model.addAttribute("unaffiliated", communityCreateDto.getOid().equals(unaffiliatedCommunityOid));
            }
        }
        if (communityCreateDto.getMarketplace().isEmpty()) {
            // copy marketplace settings from organization
            Long databaseId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
            communityCreateDto.setMarketplace(marketplaceService.getMarketplaceByOrganizationId(databaseId));
        }
        model.addAttribute("communityDto", communityCreateDto);
        model.addAttribute("primaryFocuses", marketplaceService.getPrimaryFocuses());
        model.addAttribute("communityTypes", marketplaceService.getFilteredCommunityTypesListofLists(communityCreateDto.getMarketplace().getPrimaryFocusIds()));
        model.addAttribute("levelsOfCare", marketplaceService.getLevelsOfCare());
        model.addAttribute("ageGroups", marketplaceService.getAgeGroups());
        model.addAttribute("servicesTreatmentApproaches", marketplaceService.getFilteredServiceTreatmentApproachListofLists(communityCreateDto.getMarketplace().getPrimaryFocusIds()));
        model.addAttribute("emergencyServices", marketplaceService.getEmergencyServices());
        model.addAttribute("languageServices", marketplaceService.getLanguageServices());
        model.addAttribute("ancillaryServices", marketplaceService.getAncillaryServices());
        model.addAttribute("insuranceCarriers", marketplaceService.getInNetworkInsurances());
        String databaseName = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseName();
        model.addAttribute("states", stateService.getStates());
        model.addAttribute("organizationName", databaseName);
        return "care.coordination.communities.edit";
    }

    @RequestMapping(value="/communities/{communityId}/networks/{networkIds}", method = RequestMethod.GET)
    public String getCommunityNetworkByIds(
            @PathVariable(value="communityId") Long communityId,
            @PathVariable(value="networkIds") List<Long> networkIds,
            Model model) {
        List<InNetworkInsuranceDto> networks = inNetworkInsuranceService.findByIds(networkIds);
        model.addAttribute("selectedNetworks", networks);

        MarketplaceDto marketplace;
        if (communityId != 0) {
            marketplace = marketplaceService.getMarketplaceByCommunityId(communityId);
        } else {
            Long databaseId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
            marketplace = marketplaceService.getMarketplaceByOrganizationId(databaseId);
        }

        Map<Long, List<Long>> selectedInNetworkInsurancePlanIds = marketplace.getSelectedInNetworkInsurancePlanIds();
        model.addAttribute("selectedNetworkPlanIds", selectedInNetworkInsurancePlanIds);

        model.addAttribute("marketplace", marketplace);
        return "network.plan.table";
    }

    @RequestMapping(value="/organizations/{organizationId}/networks/{networkIds}", method = RequestMethod.GET)
    public String getOrganizationNetworkByIds(
            @PathVariable(value="organizationId") Long organizationId,
            @PathVariable(value="networkIds") List<Long> networkIds,
            Model model) {
        List<InNetworkInsuranceDto> networks = inNetworkInsuranceService.findByIds(networkIds);
        model.addAttribute("selectedNetworks", networks);

        MarketplaceDto marketplace = marketplaceService.getMarketplaceByOrganizationId(organizationId);

        Map<Long, List<Long>> selectedInNetworkInsurancePlanIds = marketplace.getSelectedInNetworkInsurancePlanIds();
        model.addAttribute("selectedNetworkPlanIds", selectedInNetworkInsurancePlanIds);

        model.addAttribute("marketplace", marketplace);
        return "network.plan.table";
    }
}
