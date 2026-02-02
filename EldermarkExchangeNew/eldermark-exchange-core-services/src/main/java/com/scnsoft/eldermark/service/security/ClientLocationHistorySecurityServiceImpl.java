package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.OrganizationIdAndCommunityIdAware;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientHistoryLocationSecurityAwareEntity;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientSecurityAwareEntity;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.service.ClientLocationHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("clientLocationHistorySecurityService")
@Transactional(readOnly = true)
public class ClientLocationHistorySecurityServiceImpl
        extends BaseSecurityService
        implements ClientLocationHistorySecurityService {


    @Autowired
    private ClientLocationHistoryService clientLocationHistoryService;

    @Override
    public boolean canAdd(ClientIdAware dto) {
        var client = clientService.findById(
                dto.getClientId(),
                ClientSecurityAwareEntity.class
        );

        if (!isEligibleForDiscoveryCommunity(client.getCommunityId())) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(CLIENT_LOCATION_HISTORY_ADD_ALL_EXCEPT_OPTED_OUT) && isClientOptedIn(client)) {
            return true;
        }

        if (permissionFilter.hasPermission(CLIENT_LOCATION_HISTORY_ADD_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(CLIENT_LOCATION_HISTORY_ADD_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, client.getOrganizationId())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canViewList(Long clientId) {
        var client = clientService.findSecurityAwareEntity(clientId);
        return canViewByClient(client);
    }

    @Override
    public boolean canView(Long locationId) {
        var projection = clientLocationHistoryService.findById(
                locationId,
                ClientHistoryLocationSecurityAwareEntity.class
        );

        return canViewByClient(new ClientSecurityAwareEntity() {

            @Override
            public Long getCreatedById() {
                return projection.getClientCreatedById();
            }

            @Override
            public HieConsentPolicyType getHieConsentPolicyType() {
                return projection.getClientHieConsentPolicyType();
            }

            @Override
            public Long getOrganizationId() {
                return projection.getClientOrganizationId();
            }

            @Override
            public Long getId() {
                return projection.getClientId();
            }

            @Override
            public Long getCommunityId() {
                return projection.getClientCommunityId();
            }

        });
    }

    private boolean canViewByClient(ClientSecurityAwareEntity client) {
        if (!isEligibleForDiscoveryCommunity(client.getCommunityId())) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(CLIENT_LOCATION_HISTORY_VIEW_ALL_EXCEPT_OPTED_OUT) && isClientOptedIn(client)) {
            return true;
        }

        if (permissionFilter.hasPermission(CLIENT_LOCATION_HISTORY_VIEW_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(CLIENT_LOCATION_HISTORY_VIEW_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, client.getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_LOCATION_HISTORY_VIEW_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(CLIENT_LOCATION_HISTORY_VIEW_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_LOCATION_HISTORY_VIEW_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(CLIENT_LOCATION_HISTORY_VIEW_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION);
            if (isAnyInAffiliatedOrganizationOfCommunity(employees, client.getCommunityId()) && isClientOptedIn(client)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_LOCATION_HISTORY_VIEW_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(CLIENT_LOCATION_HISTORY_VIEW_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY);
            if (isAnyInAffiliatedCommunity(employees, client.getCommunityId()) && isClientOptedIn(client)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_LOCATION_HISTORY_VIEW_IF_CURRENT_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(CLIENT_LOCATION_HISTORY_VIEW_IF_CURRENT_RP_COMMUNITY_CTM);
            if (isAnyInCommunityCareTeam(
                    employees,
                    client.getCommunityId(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_LOCATION_HISTORY_VIEW_IF_CURRENT_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(CLIENT_LOCATION_HISTORY_VIEW_IF_CURRENT_RP_CLIENT_CTM);
            if (isAnyInClientCareTeam(
                    employees,
                    client.getId(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_LOCATION_HISTORY_VIEW_OPTED_IN_IF_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(CLIENT_LOCATION_HISTORY_VIEW_OPTED_IN_IF_ADDED_BY_SELF);
            if (isClientOptedInAndAddedBySelf(employees, client)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_LOCATION_HISTORY_VIEW_IF_SELF_RECORD)) {
            var employees = permissionFilter.getEmployees(CLIENT_LOCATION_HISTORY_VIEW_IF_SELF_RECORD);
            if (isSelfClientRecord(employees, client.getId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_LOCATION_HISTORY_VIEW_IF_CLIENT_FOUND_IN_RECORD_SEARCH) &&
                permissionFilter.containsClientRecordSearchFoundId(client.getId())) {
            return true;
        }

        return false;
    }
}
