package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.entity.security.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("transportationSecurityService")
@Transactional(readOnly = true)
public class TransportationSecurityServiceImpl extends BaseSecurityService implements TransportationSecurityService {

    private static final Set<Permission> RIDES_PERMISSIONS = EnumSet.of(
            CLIENT_REQUEST_RIDE_ALL_EXCEPT_OPTED_OUT,
            CLIENT_REQUEST_RIDE_IF_ASSOCIATED_ORGANIZATION,
            CLIENT_REQUEST_RIDE_IF_ASSOCIATED_COMMUNITY,
            CLIENT_REQUEST_RIDE_IF_CURRENT_REGULAR_CLIENT_CTM,
            CLIENT_REQUEST_RIDE_IF_CURRENT_REGULAR_COMMUNITY_CTM,
            CLIENT_REQUEST_RIDE_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
            CLIENT_REQUEST_RIDE_IF_SELF_CLIENT_RECORD,
            CLIENT_VIEW_RIDE_HISTORY_ALL_EXCEPT_OPTED_OUT,
            CLIENT_VIEW_RIDE_HISTORY_IF_ASSOCIATED_ORGANIZATION,
            CLIENT_VIEW_RIDE_HISTORY_IF_ASSOCIATED_COMMUNITY,
            CLIENT_VIEW_RIDE_HISTORY_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
            CLIENT_VIEW_RIDE_HISTORY_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
            CLIENT_VIEW_RIDE_HISTORY_IF_CURRENT_RP_CLIENT_CTM,
            CLIENT_VIEW_RIDE_HISTORY_IF_CURRENT_REGULAR_CLIENT_CTM,
            CLIENT_VIEW_RIDE_HISTORY_IF_CURRENT_RP_COMMUNITY_CTM,
            CLIENT_VIEW_RIDE_HISTORY_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
            CLIENT_VIEW_RIDE_HISTORY_IF_SELF_CLIENT_RECORD);

    @Override
    public boolean canRequestNewRide(Long clientId) {
        var permissionFilter = currentUserFilter();

        var client = lazyClient(clientId);

        if (permissionFilter.hasPermission(CLIENT_REQUEST_RIDE_ALL_EXCEPT_OPTED_OUT) && isClientOptedIn(client.get())) {
            return true;
        }

        if (permissionFilter.hasPermission(CLIENT_REQUEST_RIDE_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(CLIENT_REQUEST_RIDE_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, client.get().getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_REQUEST_RIDE_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(CLIENT_REQUEST_RIDE_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, client.get().getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_REQUEST_RIDE_IF_CURRENT_REGULAR_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(CLIENT_REQUEST_RIDE_IF_CURRENT_REGULAR_COMMUNITY_CTM);
            if (isAnyInCommunityCareTeam(
                    employees,
                    client.get().getCommunityId(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_REQUEST_RIDE_IF_CURRENT_REGULAR_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(CLIENT_REQUEST_RIDE_IF_CURRENT_REGULAR_CLIENT_CTM);
            if (isAnyInClientCareTeam(
                    employees,
                    clientId,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_REQUEST_RIDE_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(CLIENT_REQUEST_RIDE_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);
            if (isClientOptedInAndAddedBySelf(employees, client.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_REQUEST_RIDE_IF_SELF_CLIENT_RECORD)) {
            var employees = permissionFilter.getEmployees(CLIENT_REQUEST_RIDE_IF_SELF_CLIENT_RECORD);
            if (isSelfClientRecord(employees, client.get())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canViewRideHistory(Long clientId) {
        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }

        var client = lazyClient(clientId);

        if (permissionFilter.hasPermission(CLIENT_VIEW_RIDE_HISTORY_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(CLIENT_VIEW_RIDE_HISTORY_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, client.get().getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_VIEW_RIDE_HISTORY_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(CLIENT_VIEW_RIDE_HISTORY_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, client.get().getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_VIEW_RIDE_HISTORY_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(CLIENT_VIEW_RIDE_HISTORY_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION);
            if (isAnyInAffiliatedOrganizationOfCommunity(employees, client.get().getCommunityId()) &&
                    isClientOptedIn(client.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_VIEW_RIDE_HISTORY_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(CLIENT_VIEW_RIDE_HISTORY_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY);
            if (isAnyInAffiliatedCommunity(employees, client.get().getCommunityId()) &&
                    isClientOptedIn(client.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_VIEW_RIDE_HISTORY_IF_CURRENT_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(CLIENT_VIEW_RIDE_HISTORY_IF_CURRENT_RP_COMMUNITY_CTM);
            if (isAnyInCommunityCareTeam(
                    employees,
                    client.get().getCommunityId(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_VIEW_RIDE_HISTORY_IF_CURRENT_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(CLIENT_VIEW_RIDE_HISTORY_IF_CURRENT_RP_CLIENT_CTM);
            if (isAnyInClientCareTeam(
                    employees,
                    clientId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_VIEW_RIDE_HISTORY_IF_CURRENT_REGULAR_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(CLIENT_VIEW_RIDE_HISTORY_IF_CURRENT_REGULAR_CLIENT_CTM);
            if (isAnyInClientCareTeam(
                    employees,
                    clientId,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_VIEW_RIDE_HISTORY_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(CLIENT_VIEW_RIDE_HISTORY_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);
            if (isClientOptedInAndAddedBySelf(employees, client.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_VIEW_RIDE_HISTORY_IF_SELF_CLIENT_RECORD)) {
            var employees = permissionFilter.getEmployees(CLIENT_VIEW_RIDE_HISTORY_IF_SELF_CLIENT_RECORD);
            if (isSelfClientRecord(employees, client.get())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasAccess() {
        return hasAnyPermission(RIDES_PERMISSIONS);
    }
}
