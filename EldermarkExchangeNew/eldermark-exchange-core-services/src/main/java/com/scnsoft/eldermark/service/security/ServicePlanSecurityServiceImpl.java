package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.security.projection.dto.ServicePlanSecurityFieldsAware;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.ServicePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Supplier;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("servicePlanSecurityService")
@Transactional(readOnly = true)
public class ServicePlanSecurityServiceImpl extends BaseSecurityService implements ServicePlanSecurityService {

    private static final Set<Permission> VIEW_LIST_PERMISSIONS = EnumSet.of(
            SERVICE_PLAN_VIEW_ALL_EXCEPT_OPTED_OUT,
            SERVICE_PLAN_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION,
            SERVICE_PLAN_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY,
            SERVICE_PLAN_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
            SERVICE_PLAN_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
            SERVICE_PLAN_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM,
            SERVICE_PLAN_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM,
            SERVICE_PLAN_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);

    @Autowired
    private ServicePlanService servicePlanService;

    @Override
    public boolean canAdd(ServicePlanSecurityFieldsAware dto) {
        var permissionFilter = currentUserFilter();

        var client = lazyClient(dto.getClientId());

        if (permissionFilter.hasPermission(SERVICE_PLAN_ADD_ALL_EXCEPT_OPTED_OUT) && isClientOptedIn(client.get())) {
            return true;
        }

        if (permissionFilter.hasPermission(SERVICE_PLAN_ADD_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(SERVICE_PLAN_ADD_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, client.get().getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(SERVICE_PLAN_ADD_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(SERVICE_PLAN_ADD_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, client.get().getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(SERVICE_PLAN_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(SERVICE_PLAN_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM);
            if (isAnyInCommunityCareTeam(
                    employees,
                    client.get().getCommunityId(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(SERVICE_PLAN_ADD_IF_CURRENT_REGULAR_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(SERVICE_PLAN_ADD_IF_CURRENT_REGULAR_CLIENT_CTM);
            if (isAnyInClientCareTeam(
                    employees,
                    client.get(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(SERVICE_PLAN_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(SERVICE_PLAN_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);
            if (isClientOptedInAndAddedBySelf(employees, client.get())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canEdit(Long servicePlanId) {

        var permissionFilter = currentUserFilter();

        var servicePlan = Lazy.of(() -> servicePlanService.findSecurityAwareEntity(servicePlanId));
        var client = lazyClient(servicePlan);

        if (permissionFilter.hasPermission(SERVICE_PLAN_EDIT_ALL_EXCEPT_OPTED_OUT) && isClientOptedIn(client.get())) {
            return true;
        }

        if (permissionFilter.hasPermission(SERVICE_PLAN_EDIT_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(SERVICE_PLAN_EDIT_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, client.get().getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(SERVICE_PLAN_EDIT_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(SERVICE_PLAN_EDIT_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, client.get().getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(SERVICE_PLAN_EDIT_IF_CURRENT_REGULAR_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(SERVICE_PLAN_EDIT_IF_CURRENT_REGULAR_COMMUNITY_CTM);
            if (isAnyInCommunityCareTeam(
                    employees,
                    client.get().getCommunityId(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(SERVICE_PLAN_EDIT_ADDED_BY_SELF_IF_CURRENT_REGULAR_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(SERVICE_PLAN_EDIT_ADDED_BY_SELF_IF_CURRENT_REGULAR_COMMUNITY_CTM);
            if (isSelfEmployeeRecord(employees, servicePlan.get().getEmployeeId()) &&
                    isAnyInCommunityCareTeam(
                            employees,
                            client.get().getCommunityId(),
                            AffiliatedCareTeamType.REGULAR,
                            HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(SERVICE_PLAN_EDIT_IF_CURRENT_REGULAR_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(SERVICE_PLAN_EDIT_IF_CURRENT_REGULAR_CLIENT_CTM);
            if (isAnyInClientCareTeam(
                    employees,
                    client.get(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(SERVICE_PLAN_EDIT_ADDED_BY_SELF_IF_CURRENT_REGULAR_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(SERVICE_PLAN_EDIT_ADDED_BY_SELF_IF_CURRENT_REGULAR_CLIENT_CTM);
            if (isSelfEmployeeRecord(employees, servicePlan.get().getEmployeeId()) &&
                    isAnyInClientCareTeam(
                            employees,
                            client.get(),
                            AffiliatedCareTeamType.REGULAR,
                            HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(SERVICE_PLAN_EDIT_ADDED_BY_SELF_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(SERVICE_PLAN_EDIT_ADDED_BY_SELF_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);
            if (isSelfEmployeeRecord(employees, servicePlan.get().getEmployeeId())
                    && isClientOptedInAndAddedBySelf(employees, client.get())) {
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
    public boolean canView(Long servicePlanId) {
        return canViewByClient(() -> servicePlanService.findSecurityAwareEntity(servicePlanId));
    }

    @Override
    public boolean canViewByClientId(Long clientId) {
        return canViewByClient(Lazy.of(() -> new ClientIdAware() {
            @Override
            public Long getClientId() {
                return clientId;
            }
        }));
    }

    private <T extends ClientIdAware> boolean canViewByClient(Supplier<T> client) {
        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(Permission.ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }

        var mergedClients = lazyMergedSecurityClients(client);
        var mergedOrganizations = lazyOrganizationIdsSet(mergedClients);
        var mergedCommunities = lazyCommunityIdsSet(mergedClients);

        if (permissionFilter.hasPermission(SERVICE_PLAN_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(SERVICE_PLAN_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderAnyOrganization(employees, mergedOrganizations.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(SERVICE_PLAN_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(SERVICE_PLAN_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderAnyCommunity(employees, mergedCommunities.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(SERVICE_PLAN_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(SERVICE_PLAN_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION);
            if (isAnyInAffiliatedOrganizationOfAnyCommunity(employees, mergedCommunities.get()) &&
                    areAllClientsOptedIn(mergedClients.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(SERVICE_PLAN_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(SERVICE_PLAN_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY);
            if (isAnyInAffiliatedCommunityOfAny(employees, mergedCommunities.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(SERVICE_PLAN_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(SERVICE_PLAN_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM);
            if (isAnyInAnyCommunityCareTeam(
                    employees,
                    mergedCommunities.get(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentForAny(mergedClients.get()))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(SERVICE_PLAN_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(SERVICE_PLAN_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM);
            if (isAnyInAnyClientCareTeam(
                    employees,
                    mergedClients.get(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentForAny(mergedClients.get()))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(SERVICE_PLAN_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(SERVICE_PLAN_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);
            if (isAnyClientAddedBySelf(employees, mergedClients.get()) && areAllClientsOptedIn(mergedClients.get())) {
                return true;
            }
        }

        return false;

    }
}
