package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientSecurityAwareEntity;
import com.scnsoft.eldermark.entity.AccessRight;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.entity.security.SecurityConstants;
import com.scnsoft.eldermark.service.ReferralService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.PermissionFilterUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.util.CareCoordinationUtils.toIdsSet;

public abstract class AccessFlagsCheckingSecurityService extends BaseSecurityService {

    @Autowired
    private ReferralService referralService;

    //refer to https://confluence.scnsoft.com/display/CCNP/Security+checks+with+PHR+Access+flags
    //for better understanding of underlying logic
    protected boolean canViewByClientOrMerged(Long requestedClientId,
                                              PermissionScopeProvider permissionsScope,
                                              AccessRight.Code... accessRights) {
        if (!isInEligibleForDiscoveryCommunity(clientService.findById(requestedClientId, CommunityIdAware.class))) {
            return false;
        }

        var filter = currentUserFilter();

        var merged = mergedClientsEligibleForDiscovery(requestedClientId);

        var allEmployees = filter.getAllEmployees();

        var disabledClientsPerEmployee = findClientCTMWithAnyDisabledAccessRight(allEmployees, merged,
                SecurityConstants.ACCESS_FLAGS_CHECK_AMONG_CTM_TYPE, accessRights);

        for (var employee : allEmployees) {
            var permissions = filter.getEmployeePermissions(employee);

            if (permissions.contains(permissionsScope.getMergedAllExceptOptedOut()) && areAllClientsOptedIn(merged)) {
                if (!disabledClientsPerEmployee.get(employee.getId()).contains(requestedClientId)) {
                    return true;
                }
            }

            if (permissions.contains(permissionsScope.getMergedAssociatedOrganization())) {
                var accessibleClients = merged.stream()
                        .filter(client -> isClientInOrganization(client, employee.getOrganizationId()))
                        .map(IdAware::getId)
                        .collect(Collectors.toSet());

                if (isClientDataEnabled(requestedClientId, accessibleClients, employee, disabledClientsPerEmployee)) {
                    return true;
                }
            }

            if (permissions.contains(permissionsScope.getMergedAssociatedCommunity())) {
                var accessibleClients = merged.stream()
                        .filter(client -> isClientInCommunity(client, employee.getCommunityId()))
                        .map(IdAware::getId)
                        .collect(Collectors.toSet());

                if (isClientDataEnabled(requestedClientId, accessibleClients, employee, disabledClientsPerEmployee)) {
                    return true;
                }
            }

            if (permissions.contains(permissionsScope.getMergedOptedInFromAffiliatedOrganization())) {
                if (areAllClientsOptedIn(merged)) {
                    var accessibleClients = findClientIdsInPrimaryCommunitiesOfOrganization(merged, employee.getOrganizationId());

                    if (isClientDataEnabled(requestedClientId, accessibleClients, employee, disabledClientsPerEmployee)) {
                        return true;
                    }
                }
            }

            if (permissions.contains(permissionsScope.getMergedOptedInFromAffiliatedCommunity())) {
                if (areAllClientsOptedIn(merged)) {
                    var accessibleClients = findClientIdsInPrimaryCommunities(merged, employee.getCommunityId());

                    if (isClientDataEnabled(requestedClientId, accessibleClients, employee, disabledClientsPerEmployee)) {
                        return true;
                    }
                }
            }

            if (permissions.contains(permissionsScope.getMergedIfCurrentRpCommunityCtm())) {
                var accessibleClients = findClientIdsInCommunityCareTeam(
                        merged,
                        employee,
                        AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                        HieConsentCareTeamType.currentForAny(merged)
                );

                if (isClientDataEnabled(requestedClientId, accessibleClients, employee, disabledClientsPerEmployee)) {
                    return true;
                }
            }

            if (permissions.contains(permissionsScope.getMergedIfCurrentRpClientCtm())) {
                //don't pass access rights because clients with disabled flags are still in client care team.
                var accessibleClients = findClientIdsInClientCareTeam(
                        merged,
                        employee,
                        AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                        HieConsentCareTeamType.currentForAny(merged)
                );

                if (isClientDataEnabled(requestedClientId, accessibleClients, employee, disabledClientsPerEmployee)) {
                    return true;
                }
            }

            if (permissions.contains(permissionsScope.getMergedIfOptedInClientAddedBySelf())) {
                var employees = List.of(employee);
                var accessibleClients = merged.stream()
                        .filter(client -> isClientOptedInAndAddedBySelf(employees, client))
                        .map(IdAware::getId)
                        .collect(Collectors.toSet());

                if (isClientDataEnabled(requestedClientId, accessibleClients, employee, disabledClientsPerEmployee)) {
                    return true;
                }
            }

            if (permissions.contains(permissionsScope.getMergedIfSelfRecord())) {
                var accessibleClients = findClientIdsSelfRecordForEmployee(merged, employee);

                if (isClientDataEnabled(requestedClientId, accessibleClients, employee, disabledClientsPerEmployee)) {
                    return true;
                }
            }

            if (permissions.contains(permissionsScope.getMergedAccessibleReferralRequest())) {
                var employees = filter.getEmployees(Permission.PROBLEM_VIEW_MERGED_IF_ACCESSIBLE_REFERRAL_REQUEST);
                var filteredFilter = PermissionFilterUtils.filterWithEmployeesOnly(filter, employees);
                var accessibleClients = findClientsWithAccessibleReferrals(merged, filteredFilter);

                if (isClientDataEnabled(requestedClientId, accessibleClients, employee, disabledClientsPerEmployee)) {
                    return true;
                }
            }

            if (permissions.contains(permissionsScope.getMergedIfFoundInRecordSearch())) {
                var recordSearchClients = filter.getClientRecordSearchFoundIds();
                var accessibleClients = CareCoordinationUtils.toIdsSet(merged).stream().filter(recordSearchClients::contains).collect(Collectors.toSet());
                if (isClientDataEnabled(requestedClientId, accessibleClients, employee, disabledClientsPerEmployee)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isClientDataEnabled(Long clientId, Collection<Long> accessibleClientsByPermission,
                                        Employee employee,
                                        Map<Long, Set<Long>> disabledClientsMap) {
        var disabledClientsForEmployee = disabledClientsMap.get(employee.getId());

        //data of requested client should not be disabled by PHR flags
        if (disabledClientsForEmployee.contains(clientId)) {
            return false;
        }

        if (accessibleClientsByPermission.contains(clientId)) {
            //client is within accessible and not disabled by flags
            return true;
        }

        //client himself is not within accessible and not disabled, but there may be accessible MERGED client
        //who will grand access. So there should exist at least one client among accessible who is not
        //disabled by flags
        return !accessibleClientsByPermission.isEmpty() && !disabledClientsForEmployee.containsAll(accessibleClientsByPermission);
    }

    //employee id -> set of disabled client ids
    private Map<Long, Set<Long>> findClientCTMWithAnyDisabledAccessRight(Collection<Employee> employees,
                                                                         Collection<ClientSecurityAwareEntity> clients,
                                                                         AffiliatedCareTeamType type,
                                                                         AccessRight.Code... accessRights) {
        var rights = Arrays.asList(accessRights);
        var result = new HashMap<Long, Set<Long>>();
        employees.forEach(employee -> result.put(employee.getId(), new HashSet<>()));

        var clientIds = toIdsSet(clients);

        clientCareTeamMemberService.findClientsCareTeamMembersAmongEmployees(employees, clientIds, type).stream()
                .filter(ctm -> !ctm.getAccessRights()
                        .stream()
                        .map(AccessRight::getCode)
                        .collect(Collectors.toSet()).containsAll(rights))
                .forEach(ctm -> result.get(ctm.getEmployeeId()).add(ctm.getClientId()));
        return result;
    }

    protected boolean isClientInOrganization(ClientSecurityAwareEntity client, Long organizationId) {
        return organizationId.equals(client.getOrganizationId());
    }

    protected boolean isClientInCommunity(ClientSecurityAwareEntity client, Long communityId) {
        return communityId.equals(client.getCommunityId());
    }

    protected Set<Long> findClientIdsInCommunities(Collection<ClientSecurityAwareEntity> clients, Collection<Long> communityIds) {
        return toIdsSet(clients.stream().filter(c -> communityIds.contains(c.getCommunityId())));
    }

    protected Set<Long> findClientIdsInPrimaryCommunitiesOfOrganization(Collection<ClientSecurityAwareEntity> clients, Long affiliatedOrganizationId) {
        var primaryCommunityIds = affiliatedRelationshipDao.findPrimaryCommunityIdsForAffiliatedOrganization(
                affiliatedOrganizationId, CareCoordinationUtils.getCommunityIdsSet(clients));

        return findClientIdsInCommunities(clients, primaryCommunityIds);
    }

    protected Set<Long> findClientIdsInPrimaryCommunities(Collection<ClientSecurityAwareEntity> clients, Long affiliatedCommunityId) {
        var primaryCommunityIds = affiliatedRelationshipDao.findPrimaryCommunityIdsForAffiliated(
                affiliatedCommunityId, CareCoordinationUtils.getCommunityIdsSet(clients));

        return findClientIdsInCommunities(clients, primaryCommunityIds);
    }

    protected Set<Long> findClientIdsInCommunityCareTeam(Collection<ClientSecurityAwareEntity> clients, Employee employee,
                                                         AffiliatedCareTeamType type, HieConsentCareTeamType consentType) {
        var ctmCommunities = CareCoordinationUtils.getCommunityIdsSet(
                communityCareTeamMemberService.findByEmployeeId(employee.getId(), type, consentType, CommunityIdAware.class));

        return findClientIdsInCommunities(clients, ctmCommunities);
    }

    protected Set<Long> findClientIdsInClientCareTeam(Collection<ClientSecurityAwareEntity> clients, Employee employee,
                                                      AffiliatedCareTeamType type, HieConsentCareTeamType consentType) {
        return clientCareTeamMemberService.findClientsCareTeamMembersAmongEmployees(Collections.singletonList(employee),
                        toIdsSet(clients), type, consentType).stream()
                .map(ClientCareTeamMember::getClientId)
                .collect(Collectors.toSet());

    }

    protected Set<Long> findClientIdsSelfRecordForEmployee(Collection<ClientSecurityAwareEntity> clients, Employee employee) {
        var wrappedEmployee = Collections.singletonList(employee);
        return toIdsSet(clients.stream().filter(client -> isSelfClientRecord(wrappedEmployee, client)));
    }

    protected Set<Long> findClientsWithAccessibleReferrals(Collection<ClientSecurityAwareEntity> clients, PermissionFilter filter) {
        return toIdsSet(clients.stream().filter(client -> referralService.existsAccessibleReferralRequest(client.getId(), filter)));
    }

    protected static PermissionScopeProvider buildScopeProvider(Permission mergedAllExceptOptedOut,
                                                                Permission mergedAssociatedOrganization,
                                                                Permission mergedAssociatedCommunity,
                                                                Permission mergedOptedInFromAffiliatedOrganization,
                                                                Permission mergedOptedInFromAffiliatedCommunity,
                                                                Permission mergedCurrentRpCommunityCTM,
                                                                Permission mergedCurrentRpClientCTM,
                                                                Permission mergedOptedInClientAddedBySelf,
                                                                Permission mergedSelfRecord,
                                                                Permission mergedAccessibleReferralRequest,
                                                                Permission mergedFromFoundInRecordSearch) {
        var provider = new PermissionScopeProvider()
                .withMergedAllExceptOptedOut(mergedAllExceptOptedOut)
                .withMergedAssociatedOrganization(mergedAssociatedOrganization)
                .withMergedAssociatedCommunity(mergedAssociatedCommunity)
                .withMergedOptedInFromAffiliatedOrganization(mergedOptedInFromAffiliatedOrganization)
                .withMergedOptedInFromAffiliatedCommunity(mergedOptedInFromAffiliatedCommunity)
                .withMergedCurrentRpCommunityCtm(mergedCurrentRpCommunityCTM)
                .withMergedCurrentRpClientCtm(mergedCurrentRpClientCTM)
                .withMergedOptedInClientAddedBySelf(mergedOptedInClientAddedBySelf)
                .withMergedSelfRecord(mergedSelfRecord)
                .withMergedFromFoundInRecordSearch(mergedFromFoundInRecordSearch);
        if (mergedAccessibleReferralRequest != null) {
            return provider.withMergedAccessibleReferralRequest(mergedAccessibleReferralRequest);
        }
        return provider;
    }


    //this concept can be useful in other places
    protected static class PermissionScopeProvider {
        private final Map<PermissionLevel, Permission> map = new EnumMap<>(PermissionLevel.class);

        public PermissionScopeProvider withMergedAllExceptOptedOut(Permission p) {
            return put(PermissionLevel.MERGED_ALL_EXCEPT_OPTED_OUT, p);
        }

        public PermissionScopeProvider withMergedAssociatedOrganization(Permission p) {
            return put(PermissionLevel.MERGED_ASSOCIATED_ORGANIZATION, p);
        }

        public PermissionScopeProvider withMergedAssociatedCommunity(Permission p) {
            return put(PermissionLevel.MERGED_ASSOCIATED_COMMUNITY, p);
        }

        public PermissionScopeProvider withMergedOptedInFromAffiliatedOrganization(Permission p) {
            return put(PermissionLevel.MERGED_OPTED_IN_FROM_AFFILIATED_ORGANIZATION, p);
        }

        public PermissionScopeProvider withMergedOptedInFromAffiliatedCommunity(Permission p) {
            return put(PermissionLevel.MERGED_OPTED_IN_FROM_AFFILIATED_COMMUNITY, p);
        }

        public PermissionScopeProvider withMergedCurrentRpCommunityCtm(Permission p) {
            return put(PermissionLevel.MERGED_IF_CURRENT_RP_COMMUNITY_CTM, p);
        }

        public PermissionScopeProvider withMergedCurrentRpClientCtm(Permission p) {
            return put(PermissionLevel.MERGED_IF_CURRENT_RP_CLIENT_CTM, p);
        }

        public PermissionScopeProvider withMergedOptedInClientAddedBySelf(Permission p) {
            return put(PermissionLevel.MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF, p);
        }

        public PermissionScopeProvider withMergedSelfRecord(Permission p) {
            return put(PermissionLevel.MERGED_IF_SELF_RECORD, p);
        }

        public PermissionScopeProvider withMergedAccessibleReferralRequest(Permission p) {
            return put(PermissionLevel.MERGED_ACCESSIBLE_REFERRAL_REQUEST, p);
        }

        public PermissionScopeProvider withMergedFromFoundInRecordSearch(Permission p) {
            return put(PermissionLevel.MERGED_IF_FOUND_IN_RECORD_SEARCH, p);
        }

        private PermissionScopeProvider put(PermissionLevel pl, Permission p) {
            map.put(pl, p);
            return this;
        }

        public Permission getMergedAllExceptOptedOut() {
            return map.get(PermissionLevel.MERGED_ALL_EXCEPT_OPTED_OUT);
        }

        public Permission getMergedAssociatedOrganization() {
            return map.get(PermissionLevel.MERGED_ASSOCIATED_ORGANIZATION);
        }

        public Permission getMergedAssociatedCommunity() {
            return map.get(PermissionLevel.MERGED_ASSOCIATED_COMMUNITY);
        }

        public Permission getMergedOptedInFromAffiliatedOrganization() {
            return map.get(PermissionLevel.MERGED_OPTED_IN_FROM_AFFILIATED_ORGANIZATION);
        }

        public Permission getMergedOptedInFromAffiliatedCommunity() {
            return map.get(PermissionLevel.MERGED_OPTED_IN_FROM_AFFILIATED_COMMUNITY);
        }

        public Permission getMergedIfCurrentRpCommunityCtm() {
            return map.get(PermissionLevel.MERGED_IF_CURRENT_RP_COMMUNITY_CTM);
        }

        public Permission getMergedIfCurrentRpClientCtm() {
            return map.get(PermissionLevel.MERGED_IF_CURRENT_RP_CLIENT_CTM);
        }

        public Permission getMergedIfOptedInClientAddedBySelf() {
            return map.get(PermissionLevel.MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);
        }

        public Permission getMergedIfSelfRecord() {
            return map.get(PermissionLevel.MERGED_IF_SELF_RECORD);
        }

        public Permission getMergedAccessibleReferralRequest() {
            return map.get(PermissionLevel.MERGED_ACCESSIBLE_REFERRAL_REQUEST);
        }

        public Permission getMergedIfFoundInRecordSearch() {
            return map.get(PermissionLevel.MERGED_IF_FOUND_IN_RECORD_SEARCH);
        }

        public Set<Permission> getAllPermissions() {
            return EnumSet.copyOf(map.values());
        }
    }

    public enum PermissionLevel {
        MERGED_ALL_EXCEPT_OPTED_OUT,
        MERGED_ASSOCIATED_ORGANIZATION,
        MERGED_ASSOCIATED_COMMUNITY,
        MERGED_OPTED_IN_FROM_AFFILIATED_ORGANIZATION,
        MERGED_OPTED_IN_FROM_AFFILIATED_COMMUNITY,
        MERGED_IF_CURRENT_RP_COMMUNITY_CTM,
        MERGED_IF_CURRENT_RP_CLIENT_CTM,
        MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
        MERGED_IF_SELF_RECORD,
        MERGED_ACCESSIBLE_REFERRAL_REQUEST,
        MERGED_FROM_EXTERNAL_REFERRAL_REQUEST,
        MERGED_IF_FOUND_IN_RECORD_SEARCH
    }
}
