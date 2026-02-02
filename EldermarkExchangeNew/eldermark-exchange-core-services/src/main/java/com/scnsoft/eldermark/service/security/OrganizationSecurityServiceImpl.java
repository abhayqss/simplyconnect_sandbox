package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.ClientMedicationService;
import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.util.PermissionFilterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("organizationSecurityService")
@Transactional(readOnly = true)
public class OrganizationSecurityServiceImpl extends BaseSecurityService implements OrganizationSecurityService {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ClientMedicationService clientMedicationService;

    private static final Set<Permission> VIEW_LIST_PERMISSIONS = EnumSet.of(
            ORGANIZATION_VIEW_IN_LIST_ALL,
            ORGANIZATION_VIEW_IN_LIST_IF_ASSOCIATED_ORGANIZATION,
            ORGANIZATION_VIEW_IN_LIST_IF_FROM_AFFILIATED_ORGANIZATION,
            ORGANIZATION_VIEW_IN_LIST_IF_FROM_AFFILIATED_COMMUNITY,
            ORGANIZATION_VIEW_IN_LIST_IF_CO_RP_COMMUNITY_CTM,
            ORGANIZATION_VIEW_IN_LIST_IF_CO_RP_CLIENT_CTM,
            ORGANIZATION_VIEW_IN_LIST_EXTERNAL_REFERRAL_REQUEST);

    @Override
    public boolean canAdd() {
        var permissionFilter = currentUserFilter();
        return permissionFilter.hasPermission(ORGANIZATION_ADD_ALL);
    }

    @Override
    public boolean canEdit(Long organizationId) {
        return hasPermission(organizationId, ORGANIZATION_EDIT_ALL, ORGANIZATION_EDIT_IF_ASSOCIATED_ORGANIZATION);
    }

    @Override
    public boolean canEditFeatures(Long organizationId) {
        return hasPermission(organizationId, ORGANIZATION_EDIT_FEATURES_ALL, null);
    }

    @Override
    public boolean canConfigureMarketplace(Long organizationId) {
        return hasPermission(
                organizationId,
                ORGANIZATION_CONFIGURE_MARKETPLACE_ALL,
                ORGANIZATION_CONFIGURE_MARKETPLACE_IF_ASSOCIATED_ORGANIZATION
        );
    }

    @Override
    public boolean canConfigureAffiliateRelationships(Long organizationId) {
        return hasPermission(
                organizationId,
                ORGANIZATION_EDIT_AFFILIATED_RELATIONSHIPS_ALL,
                ORGANIZATION_EDIT_AFFILIATED_RELATIONSHIPS_IF_ASSOCIATED_ORGANIZATION
        );
    }

    @Override
    public boolean canViewList() {
        return hasAnyPermission(VIEW_LIST_PERMISSIONS);
    }

    @Override
    public boolean canView(Long organizationId) {
        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(Permission.ORGANIZATION_VIEW_DETAILS_ALL)) {
            return true;
        }

        if (permissionFilter.hasPermission(Permission.ORGANIZATION_VIEW_DETAILS_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(Permission.ORGANIZATION_VIEW_DETAILS_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, organizationId))
                return true;

        }

        if (permissionFilter.hasPermission(Permission.ORGANIZATION_VIEW_DETAILS_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(Permission.ORGANIZATION_VIEW_DETAILS_IF_FROM_AFFILIATED_ORGANIZATION);
            if (isAnyInAffiliatedOrganizationOfOrganization(employees, organizationId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(Permission.ORGANIZATION_VIEW_DETAILS_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(Permission.ORGANIZATION_VIEW_DETAILS_IF_FROM_AFFILIATED_COMMUNITY);
            if (isAnyInAffiliatedCommunityOfOrganization(employees, organizationId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(Permission.ORGANIZATION_VIEW_DETAILS_IF_CO_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(Permission.ORGANIZATION_VIEW_DETAILS_IF_CO_RP_COMMUNITY_CTM);
            if (isAnyInAnyCommunityCareTeamOfOrganization(
                    employees,
                    organizationId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold()))
                return true;
        }

        if (permissionFilter.hasPermission(Permission.ORGANIZATION_VIEW_DETAILS_IF_CO_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(Permission.ORGANIZATION_VIEW_DETAILS_IF_CO_RP_CLIENT_CTM);
            return isAnyInAnyClientCareTeamOfOrganization(
                    employees,
                    organizationId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold()
            );
        }

        return false;
    }

    @Override
    public boolean canViewLogo(Long organizationId) {
        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(Permission.ORGANIZATION_VIEW_LOGO_ALL)) {
            return true;
        }

        if (permissionFilter.hasPermission(Permission.ORGANIZATION_VIEW_LOGO_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(Permission.ORGANIZATION_VIEW_LOGO_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, organizationId))
                return true;

        }

        if (permissionFilter.hasPermission(Permission.ORGANIZATION_VIEW_LOGO_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(Permission.ORGANIZATION_VIEW_LOGO_IF_FROM_AFFILIATED_ORGANIZATION);
            if (isAnyInAffiliatedOrganizationOfOrganization(employees, organizationId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(Permission.ORGANIZATION_VIEW_LOGO_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(Permission.ORGANIZATION_VIEW_LOGO_IF_FROM_AFFILIATED_COMMUNITY);
            if (isAnyInAffiliatedCommunityOfOrganization(employees, organizationId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(Permission.ORGANIZATION_VIEW_LOGO_IF_CO_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(Permission.ORGANIZATION_VIEW_LOGO_IF_CO_RP_COMMUNITY_CTM);
            if (isAnyInAnyCommunityCareTeamOfOrganization(
                    employees,
                    organizationId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold()))
                return true;
        }

        if (permissionFilter.hasPermission(Permission.ORGANIZATION_VIEW_LOGO_IF_CO_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(Permission.ORGANIZATION_VIEW_LOGO_IF_CO_RP_CLIENT_CTM);
            if (isAnyInAnyClientCareTeamOfOrganization(
                    employees,
                    organizationId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ORGANIZATION_VIEW_LOGO_IF_ACCESSIBLE_CLIENT_CTM_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(Permission.ORGANIZATION_VIEW_LOGO_IF_ACCESSIBLE_CLIENT_CTM_ASSOCIATED_ORGANIZATION);
            var adjustedFilter = PermissionFilterUtils.filterWithEmployeesOnly(permissionFilter, employees);
            if (clientCareTeamMemberService.existAccessibleCareTeamMemberInOrganization(adjustedFilter, organizationId, HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ORGANIZATION_VIEW_LOGO_IF_ACCESSIBLE_COMMUNITY_CTM_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(Permission.ORGANIZATION_VIEW_LOGO_IF_ACCESSIBLE_COMMUNITY_CTM_ASSOCIATED_ORGANIZATION);
            var adjustedFilter = PermissionFilterUtils.filterWithEmployeesOnly(permissionFilter, employees);
            if (communityCareTeamMemberService.existAccessibleCareTeamMemberInOrganization(adjustedFilter, organizationId, HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ORGANIZATION_VIEW_LOGO_IF_FROM_PARTNER_COMMUNITY_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(ORGANIZATION_VIEW_LOGO_IF_FROM_PARTNER_COMMUNITY_ORGANIZATION);
            if (isAnyCreatedInPartnerCommunityOrganizationOfOrganization(employees, organizationId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ORGANIZATION_VIEW_LOGO_IF_ACCESSIBLE_MEDICATION_IN_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(Permission.ORGANIZATION_VIEW_LOGO_IF_ACCESSIBLE_MEDICATION_IN_ORGANIZATION);
            var adjustedFilter = PermissionFilterUtils.filterWithEmployeesOnly(permissionFilter, employees);

            if (clientMedicationService.existsInOrganization(adjustedFilter, organizationId)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasPermission(Long organizationId, Permission all, Permission ifAssociatedOrganization) {
        if (organizationId != null && !organizationService.hasEligibleForDiscoveryOrNoVisibleCommunities(organizationId)) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(all)) {
            return true;
        }

        if (organizationId != null && ifAssociatedOrganization != null && permissionFilter.hasPermission(ifAssociatedOrganization)) {
            var employees = permissionFilter.getEmployees(ifAssociatedOrganization);
            return isAnyCreatedUnderOrganization(employees, organizationId);
        }

        return false;
    }
}
