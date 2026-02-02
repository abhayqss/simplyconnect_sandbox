package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.projection.dto.EventSecurityFieldsAware;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.EventService;
import com.scnsoft.eldermark.service.EventTypeService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("eventSecurityService")
@Transactional(readOnly = true)
public class EventSecurityServiceImpl extends BaseSecurityService implements EventSecurityService {

    private static final Set<Permission> VIEW_LIST_PERMISSIONS = EnumSet.of(
            EVENT_VIEW_MERGED_ALL_EXCEPT_OPTED_OUT_CLIENT,
            EVENT_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION,
            EVENT_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY,
            EVENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
            EVENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
            EVENT_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM,
            EVENT_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM,
            EVENT_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
            EVENT_VIEW_MERGED_IF_SELF_CLIENT_RECORD,
            EVENT_VIEW_MERGED_IF_CLIENT_FOUND_IN_RECORD_SEARCH);

    @Autowired
    private EventService eventService;

    @Autowired
    private EventTypeService eventTypeService;

    @Override
    public boolean canAdd(EventSecurityFieldsAware dto) {
        return canAdd(dto, true);
    }

    @Override
    public boolean canAddToClient(Long clientId) {
        return canAdd(new EventSecurityFieldsAware() {
            @Override
            public Long getEventTypeId() {
                return null;
            }

            @Override
            public Long getClientId() {
                return clientId;
            }
        }, false);
    }

    private boolean canAdd(EventSecurityFieldsAware dto, boolean checkTypes) {
        var client = clientService.findSecurityAwareEntity(dto.getClientId());
        if (!isInEligibleForDiscoveryCommunity(client)) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (checkTypes) {
            var roles = permissionFilter.getEmployees().stream().map(employee -> employee.getCareTeamRole().getCode()).collect(Collectors.toList());
            var notViewableEventTypeIds = eventTypeService.findDisabledIdsByRoles(roles);
            if (notViewableEventTypeIds.contains(dto.getEventTypeId())) {
                return false;
            }
        }

        if (permissionFilter.hasPermission(EVENT_ADD_EXCEPT_OPTED_OUT_CLIENT) && isClientOptedIn(client)) {
            return true;
        }

        if (permissionFilter.hasPermission(EVENT_ADD_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(EVENT_ADD_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, client.getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(EVENT_ADD_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(EVENT_ADD_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(EVENT_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(EVENT_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM);
            if (isAnyInCommunityCareTeam(
                    employees,
                    client.getCommunityId(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(EVENT_ADD_IF_CURRENT_REGULAR_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(EVENT_ADD_IF_CURRENT_REGULAR_CLIENT_CTM);
            if (isAnyInClientCareTeam(
                    employees,
                    client.getId(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(EVENT_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(EVENT_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);
            if (isClientOptedInAndAddedBySelf(employees, client)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(EVENT_ADD_IF_SELF_CLIENT_RECORD)) {
            var employees = permissionFilter.getEmployees(EVENT_ADD_IF_SELF_CLIENT_RECORD);
            if (isSelfClientRecord(employees, client)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(EVENT_ADD_IF_CLIENT_FOUND_IN_RECORD_SEARCH) &&
                permissionFilter.containsClientRecordSearchFoundId(client.getId())) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canEdit(Long eventId) {
        //events are not editable
        return false;
    }

    @Override
    public boolean canViewList() {
        return hasAnyPermission(VIEW_LIST_PERMISSIONS);
    }

    @Override
    public boolean canView(Long eventId) {
        var event = eventService.findSecurityAwareEntity(eventId);

        if (!isInEligibleForDiscoveryClientCommunity(event)) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (!eventService.isViewableForAnyEmployeeIds(permissionFilter.getAllEmployeeIds(), eventId)) {
            return false;
        }

        var roles = permissionFilter.getEmployees().stream().map(employee -> employee.getCareTeamRole().getCode()).collect(Collectors.toList());
        var notViewableEventTypeIds = eventTypeService.findDisabledIdsByRoles(roles);
        if (notViewableEventTypeIds.contains(event.getEventTypeId())) {
            return false;
        }

        if (permissionFilter.hasPermission(EVENT_VIEW_MERGED_ALL_EXCEPT_OPTED_OUT_CLIENT) && isClientOptedIn(event)) {
            return true;
        }

        var mergedClients = lazyMergedSecurityClientsEligibleForDiscovery(event);
        var mergedClientOrganizationIds = lazyOrganizationIdsSet(mergedClients);
        var mergedClientsCommunityIds = lazyCommunityIdsSet(mergedClients);


        if (permissionFilter.hasPermission(EVENT_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(EVENT_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderAnyOrganization(employees, mergedClientOrganizationIds.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(EVENT_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(EVENT_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderAnyCommunity(employees, mergedClientsCommunityIds.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(EVENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(EVENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION);
            if (isClientOptedIn(event) && isAnyInAffiliatedOrganizationOfAnyCommunity(employees, mergedClientsCommunityIds.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(EVENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(EVENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY);
            if (isClientOptedIn(event) && isAnyInAffiliatedCommunityOfAny(employees, mergedClientsCommunityIds.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(EVENT_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(EVENT_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM);

            if (isAnyInAnyCommunityCareTeam(
                    employees,
                    mergedClientsCommunityIds.get(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentForAny(mergedClients.get())
            )) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(EVENT_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(EVENT_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM);
            if (isAnyInAnyClientCareTeam(
                    employees,
                    mergedClients.get(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentForAny(mergedClients.get())
            )) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(EVENT_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(EVENT_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);
            if (isClientOptedIn(event) && isAnyClientAddedBySelf(employees, mergedClients.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(EVENT_VIEW_MERGED_IF_SELF_CLIENT_RECORD)) {
            var employees = permissionFilter.getEmployees(EVENT_VIEW_MERGED_IF_SELF_CLIENT_RECORD);
            if (isAnySelfClientRecord(employees, mergedClients.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(EVENT_VIEW_MERGED_IF_CLIENT_FOUND_IN_RECORD_SEARCH) &&
                permissionFilter.containsAnyClientRecordSearchFoundIds(CareCoordinationUtils.toIdsSet(mergedClients.get()))) {
            return true;
        }

        return false;
    }

}
