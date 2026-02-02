package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.dto.ClientSecurityFieldsAware;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.client.SecuredClientProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("clientSecurityService")
@Transactional(readOnly = true)
public class ClientSecurityServiceImpl extends BaseSecurityService implements ClientSecurityService {

    private static final Logger logger = LoggerFactory.getLogger(ClientSecurityServiceImpl.class);

    private static final Set<Permission> VIEW_LIST_PERMISSIONS = EnumSet.of(
            CLIENT_VIEW_IN_LIST_ALL,
            CLIENT_VIEW_IN_LIST_IF_ASSOCIATED_ORGANIZATION,
            CLIENT_VIEW_IN_LIST_IF_ASSOCIATED_COMMUNITY,
            CLIENT_VIEW_IN_LIST_IF_FROM_AFFILIATED_ORGANIZATION,
            CLIENT_VIEW_IN_LIST_IF_FROM_AFFILIATED_COMMUNITY,
            CLIENT_VIEW_IN_LIST_IF_CO_RP_CLIENT_CTM,
            CLIENT_VIEW_IN_LIST_IF_CO_RP_COMMUNITY_CTM,
            CLIENT_VIEW_IN_LIST_IF_ADDED_BY_SELF,
            CLIENT_VIEW_IN_LIST_IF_SELF_RECORD,
            CLIENT_VIEW_IN_LIST_IF_CLIENT_FOUND_IN_RECORD_SEARCH
    );

    @Autowired
    private ClientService clientService;

    @Override
    public boolean canAdd(ClientSecurityFieldsAware dto) {
        var permissionFilter = currentUserFilter();

        var communityId = dto.getCommunityId();
        Long organizationId;
        try {
            organizationId = resolveAndValidateOrganizationId(communityId, dto.getOrganizationId(), ANY_TARGET_COMMUNITY);
        } catch (Exception e) {
            logger.warn("Failed to resolve organization", e);
            return false;
        }

        if (!isEligibleForDiscovery(communityId, organizationId, ANY_TARGET_COMMUNITY)) {
            return false;
        }

        if (permissionFilter.hasPermission(ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }

        if (permissionFilter.hasPermission(CLIENT_ADD_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(CLIENT_ADD_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, organizationId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_ADD_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(CLIENT_ADD_IF_ASSOCIATED_COMMUNITY);
            if (ANY_TARGET_COMMUNITY.equals(communityId)) {
                if (isAnyCreatedUnderOrganization(employees, organizationId)) {
                    return true;
                }
            } else if (isAnyCreatedUnderCommunity(employees, communityId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_ADD_IF_REGULAR_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(CLIENT_ADD_IF_REGULAR_COMMUNITY_CTM);
            if (ANY_TARGET_COMMUNITY.equals(communityId)) {
                if (isAnyInAnyCommunityCareTeamOfOrganization(
                        employees,
                        organizationId,
                        AffiliatedCareTeamType.REGULAR,
                        HieConsentCareTeamType.currentAndOnHold())) {
                    return true;
                }
            } else if (isAnyInCommunityCareTeam(
                    employees,
                    communityId,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canEdit(Long clientId) {
        var permissionFilter = currentUserFilter();

        var client = clientService.findSecurityAwareEntity(clientId);
        if (!isEligibleForDiscoveryCommunity(client.getCommunityId())) {
            return false;
        }

        if (permissionFilter.hasPermission(CLIENT_EDIT_ALL_EXCEPT_OPTED_OUT)) {
            if (isClientOptedIn(client)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_EDIT_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(CLIENT_EDIT_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, client.getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_EDIT_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(CLIENT_EDIT_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_EDIT_IF_CURRENT_REGULAR_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(CLIENT_EDIT_IF_CURRENT_REGULAR_COMMUNITY_CTM);
            if (isAnyInCommunityCareTeam(
                    employees,
                    client.getCommunityId(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client)
            )) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_EDIT_IF_CURRENT_REGULAR_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(CLIENT_EDIT_IF_CURRENT_REGULAR_CLIENT_CTM);
            if (isAnyInClientCareTeam(
                    employees,
                    clientId,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client)
            )) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_EDIT_OPTED_IN_IF_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(CLIENT_EDIT_OPTED_IN_IF_ADDED_BY_SELF);
            if (isClientOptedInAndAddedBySelf(employees, client)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canViewList() {
        return hasAnyPermission(VIEW_LIST_PERMISSIONS);
    }

    @Override
    public boolean canView(Long clientId) {
        return canView(clientId, currentUserFilter());
    }

    @Override
    public boolean canView(Long clientId, PermissionFilter permissionFilter) {
        var client = clientService.findSecurityAwareEntity(clientId);
        if (!isEligibleForDiscoveryCommunity(client.getCommunityId())) {
            return false;
        }

        if (permissionFilter.hasPermission(CLIENT_VIEW_ALL_EXCEPT_OPTED_OUT)) {
            if (isClientOptedIn(client)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_VIEW_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(CLIENT_VIEW_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, client.getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_VIEW_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(CLIENT_VIEW_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_VIEW_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(CLIENT_VIEW_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION);
            if (isClientOptedIn(client) && isAnyInAffiliatedOrganizationOfCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_VIEW_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(CLIENT_VIEW_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY);
            if (isClientOptedIn(client) && isAnyInAffiliatedCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_VIEW_IF_CURRENT_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(CLIENT_VIEW_IF_CURRENT_RP_COMMUNITY_CTM);
            if (isAnyInCommunityCareTeam(employees,
                    client.getCommunityId(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_VIEW_IF_CURRENT_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(CLIENT_VIEW_IF_CURRENT_RP_CLIENT_CTM);
            if (isAnyInClientCareTeam(employees,
                    clientId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_VIEW_OPTED_IN_IF_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(CLIENT_VIEW_OPTED_IN_IF_ADDED_BY_SELF);
            if (isClientOptedInAndAddedBySelf(employees, client)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_VIEW_IF_SELF_RECORD)) {
            var employees = permissionFilter.getEmployees(CLIENT_VIEW_IF_SELF_RECORD);
            if (isSelfClientRecord(employees, client)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_VIEW_IF_CLIENT_FOUND_IN_RECORD_SEARCH) &&
                permissionFilter.containsClientRecordSearchFoundId(clientId)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canViewRecordSearchList() {
        return hasAnyPermission(List.of(ROLE_SUPER_ADMINISTRATOR, CLIENT_RECORD_SEARCH_IF_ASSOCIATED_ORGANIZATION));
    }

    @Override
    public boolean canEditSsn(Long clientId) {
        var permissionFilter = currentUserFilter();

        var client = clientService.findSecurityAwareEntity(clientId);
        if (!isEligibleForDiscoveryCommunity(client.getCommunityId())) {
            return false;
        }

        if (permissionFilter.hasPermission(CLIENT_EDIT_SSN_ALL_EXPECT_OPTED_OUT)) {
            if (isClientOptedIn(client)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_EDIT_SSN_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(CLIENT_EDIT_SSN_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, client.getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_EDIT_SSN_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(CLIENT_EDIT_SSN_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_EDIT_SSN_OPTED_IN_IF_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(CLIENT_EDIT_SSN_OPTED_IN_IF_ADDED_BY_SELF);
            if (isClientOptedInAndAddedBySelf(employees, client)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Collection<SecuredClientProperty> getAccessibleSecuredProperties() {
        return currentUserFilter().getEmployees().stream()
                .map(Employee::getCareTeamRole)
                .map(CareTeamRole::getCode)
                .flatMap(it -> getAccessibleSecuredProperties(it).stream())
                .collect(Collectors.toSet());
    }

    private EnumSet<SecuredClientProperty> getAccessibleSecuredProperties(CareTeamRoleCode role) {
        if (role == CareTeamRoleCode.ROLE_HCA) {
            return EnumSet.noneOf(SecuredClientProperty.class);
        } else {
            return EnumSet.allOf(SecuredClientProperty.class);
        }
    }
}
