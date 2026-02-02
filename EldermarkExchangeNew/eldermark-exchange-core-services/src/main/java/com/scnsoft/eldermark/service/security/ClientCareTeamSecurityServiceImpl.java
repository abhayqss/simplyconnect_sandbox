package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.dto.CareTeamSecurityFieldsAware;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientSecurityAwareEntity;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.CareTeamRoleService;
import com.scnsoft.eldermark.service.EmployeeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service
@Transactional(readOnly = true)
public class ClientCareTeamSecurityServiceImpl extends BaseCareTeamSecurityService<ClientCareTeamMember, ClientSecurityAwareEntity>
        implements ClientCareTeamSecurityService {

    private static final Set<Permission> VIEW_LIST_PERMISSIONS = EnumSet.of(CLIENT_CARE_TEAM_VIEW_VISIBLE_ALL_EXCEPT_OPTED_OUT,
            CLIENT_CARE_TEAM_VIEW_VISIBLE_IF_ASSOCIATED_ORGANIZATION,
            CLIENT_CARE_TEAM_VIEW_VISIBLE_IF_ASSOCIATED_COMMUNITY,
            CLIENT_CARE_TEAM_VIEW_VISIBLE_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
            CLIENT_CARE_TEAM_VIEW_VISIBLE_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
            CLIENT_CARE_TEAM_VIEW_VISIBLE_IF_CURRENT_RP_CLIENT_CTM,
            CLIENT_CARE_TEAM_VIEW_VISIBLE_IF_CURRENT_RP_COMMUNITY_CTM,
            CLIENT_CARE_TEAM_VIEW_VISIBLE_OPTED_IN_IF_CLIENT_ADDED_BY_SELF,
            CLIENT_CARE_TEAM_VIEW_VISIBLE_IF_SELF_CLIENT_RECORD,
            CLIENT_CARE_TEAM_VIEW_VISIBLE_IF_CLIENT_FOUND_IN_RECORD_SEARCH);

    @Autowired
    private CareTeamRoleService careTeamRoleService;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public boolean canView(ClientCareTeamMember clientCareTeamMember) {
        return canViewByClientId(clientCareTeamMember.getClientId());
    }

    @Override
    public boolean canViewList(Long clientId) {
        return hasAnyPermission(VIEW_LIST_PERMISSIONS) && canViewByClientId(clientId);
    }

    private boolean canViewByClientId(Long clientId) {
        var client = clientService.findSecurityAwareEntity(clientId);

        if (!isInEligibleForDiscoveryCommunity(client)) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_VISIBLE_ALL_EXCEPT_OPTED_OUT)) {
            var employees = permissionFilter.getEmployees(CLIENT_CARE_TEAM_VIEW_VISIBLE_ALL_EXCEPT_OPTED_OUT);

            if (isClientOptedIn(client) && isCareTeamVisibleForAny(employees, clientId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_VISIBLE_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(CLIENT_CARE_TEAM_VIEW_VISIBLE_IF_ASSOCIATED_ORGANIZATION);

            if (isCareTeamVisibleForAny(employees, clientId) && isAnyCreatedUnderOrganization(employees, client.getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_VISIBLE_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(CLIENT_CARE_TEAM_VIEW_VISIBLE_IF_ASSOCIATED_COMMUNITY);

            if (isCareTeamVisibleForAny(employees, clientId) && isAnyCreatedUnderCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_VISIBLE_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(CLIENT_CARE_TEAM_VIEW_VISIBLE_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION);

            if (isCareTeamVisibleForAny(employees, clientId)
                    && isClientOptedIn(client)
                    && isAnyInAffiliatedOrganizationOfCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_VISIBLE_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(CLIENT_CARE_TEAM_VIEW_VISIBLE_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY);

            if (isCareTeamVisibleForAny(employees, clientId)
                    && isClientOptedIn(client)
                    && isAnyInAffiliatedCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_VISIBLE_IF_CURRENT_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(CLIENT_CARE_TEAM_VIEW_VISIBLE_IF_CURRENT_RP_COMMUNITY_CTM);

            if (isCareTeamVisibleForAny(employees, clientId) && isAnyInCommunityCareTeam(
                    employees,
                    client.getCommunityId(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_VISIBLE_IF_CURRENT_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(CLIENT_CARE_TEAM_VIEW_VISIBLE_IF_CURRENT_RP_CLIENT_CTM);

            if (isCareTeamVisibleForAny(employees, clientId) && isAnyInClientCareTeam(
                    employees,
                    client.getId(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_VISIBLE_OPTED_IN_IF_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(CLIENT_CARE_TEAM_VIEW_VISIBLE_OPTED_IN_IF_CLIENT_ADDED_BY_SELF);

            if (isCareTeamVisibleForAny(employees, clientId)
                    && isClientOptedInAndAddedBySelf(employees, client)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_VISIBLE_IF_SELF_CLIENT_RECORD)) {
            var employees = permissionFilter.getEmployees(CLIENT_CARE_TEAM_VIEW_VISIBLE_IF_SELF_CLIENT_RECORD);

            if (isCareTeamVisibleForAny(employees, clientId) && isSelfClientRecord(employees, client)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_VISIBLE_IF_CLIENT_FOUND_IN_RECORD_SEARCH) && permissionFilter.containsClientRecordSearchFoundId(clientId)) {
            var employees = permissionFilter.getEmployees(CLIENT_CARE_TEAM_VIEW_VISIBLE_IF_CLIENT_FOUND_IN_RECORD_SEARCH);
            if (isCareTeamVisibleForAny(employees, clientId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canEdit(ClientCareTeamMember clientCareTeamMember, Long targetCareTeamRoleId) {
        var permissionFilter = currentUserFilter();
        var client = clientService.findSecurityAwareEntity(clientCareTeamMember.getClientId());

        return !clientCareTeamMember.getOnHold() &&
                hasModifyingVisibleAccess(
                        permissionFilter,
                        clientCareTeamMember,
                        client,
                        targetCareTeamRoleId,
                        AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                        CLIENT_CARE_TEAM_EDIT_VISIBLE_BY_MATRIX_ALL_EXCEPT_OPTED_OUT,
                        CLIENT_CARE_TEAM_EDIT_VISIBLE_BY_MATRIX_IF_ASSOCIATED_ORGANIZATION,
                        CLIENT_CARE_TEAM_EDIT_VISIBLE_BY_MATRIX_IF_ASSOCIATED_COMMUNITY,
                        CLIENT_CARE_TEAM_EDIT_VISIBLE_BY_MATRIX_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
                        CLIENT_CARE_TEAM_EDIT_VISIBLE_BY_MATRIX_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
                        CLIENT_CARE_TEAM_EDIT_VISIBLE_BY_MATRIX_IF_CURRENT_RP_CLIENT_CTM_EXCEPT_OPTED_OUT,
                        CLIENT_CARE_TEAM_EDIT_VISIBLE_BY_MATRIX_IF_CURRENT_REGULAR_CLIENT_CTM_EXCEPT_OPTED_OUT,
                        CLIENT_CARE_TEAM_EDIT_VISIBLE_BY_MATRIX_IF_CURRENT_RP_COMMUNITY_CTM_EXCEPT_OPTED_OUT,
                        CLIENT_CARE_TEAM_EDIT_VISIBLE_BY_MATRIX_IF_CURRENT_REGULAR_COMMUNITY_CTM_EXCEPT_OPTED_OUT,
                        CLIENT_CARE_TEAM_EDIT_VISIBLE_BY_MATRIX_OPTED_IN_IF_CLIENT_ADDED_BY_SELF,
                        CLIENT_CARE_TEAM_EDIT_VISIBLE_BY_MATRIX_IF_SELF_CLIENT_RECORD_EXCEPT_OPTED_OUT,
                        CLIENT_CARE_TEAM_EDIT_VISIBLE_BY_MATRIX_IF_PRIMARY_SELF_CARE_TEAM_RECORD_EXCEPT_OPTED_OUT,
                        CLIENT_CARE_TEAM_EDIT_VISIBLE_RP_BY_MATRIX_IF_CLIENT_FOUND_IN_RECORD_SEARCH_EXCEPT_OPTED_OUT,
                        CLIENT_CARE_TEAM_EDIT_VISIBLE_REGULAR_BY_MATRIX_IF_CLIENT_FOUND_IN_RECORD_SEARCH_EXCEPT_OPTED_OUT
                );
    }

    @Override
    public boolean canAdd(CareTeamSecurityFieldsAware dto) {
        return canAdd(dto, AffiliatedCareTeamType.REGULAR_AND_PRIMARY);
    }

    private boolean canAdd(CareTeamSecurityFieldsAware dto, AffiliatedCareTeamType type) {
        var permissionFilter = currentUserFilter();
        var client = clientService.findSecurityAwareEntity(dto.getClientId());

        return hasCommonModifyingVisibleAccess(permissionFilter,
                client,
                null, dto.getCareTeamRoleId(),
                dto.getEmployeeId(), type,
                CLIENT_CARE_TEAM_ADD_VISIBLE_BY_MATRIX_ALL_EXCEPT_OPTED_OUT,
                CLIENT_CARE_TEAM_ADD_VISIBLE_BY_MATRIX_IF_ASSOCIATED_ORGANIZATION,
                CLIENT_CARE_TEAM_ADD_VISIBLE_BY_MATRIX_IF_ASSOCIATED_COMMUNITY,
                CLIENT_CARE_TEAM_ADD_VISIBLE_BY_MATRIX_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
                CLIENT_CARE_TEAM_ADD_VISIBLE_BY_MATRIX_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
                CLIENT_CARE_TEAM_ADD_VISIBLE_BY_MATRIX_IF_CURRENT_RP_CLIENT_CTM_EXCEPT_OPTED_OUT,
                CLIENT_CARE_TEAM_ADD_VISIBLE_BY_MATRIX_IF_CURRENT_REGULAR_CLIENT_CTM_EXCEPT_OPTED_OUT,
                CLIENT_CARE_TEAM_ADD_VISIBLE_BY_MATRIX_IF_CURRENT_RP_COMMUNITY_CTM_EXCEPT_OPTED_OUT,
                CLIENT_CARE_TEAM_ADD_VISIBLE_BY_MATRIX_IF_CURRENT_REGULAR_COMMUNITY_CTM_EXCEPT_OPTED_OUT,
                CLIENT_CARE_TEAM_ADD_VISIBLE_BY_MATRIX_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                CLIENT_CARE_TEAM_ADD_VISIBLE_BY_MATRIX_IF_SELF_CLIENT_RECORD_EXCEPT_OPTED_OUT,
                CLIENT_CARE_TEAM_ADD_VISIBLE_RP_IF_CLIENT_FOUND_IN_RECORD_SEARCH_EXCEPT_OPTED_OUT,
                CLIENT_CARE_TEAM_ADD_VISIBLE_REGULAR_IF_CLIENT_FOUND_IN_RECORD_SEARCH_EXCEPT_OPTED_OUT
        );
    }

    @Override
    public boolean canDelete(ClientCareTeamMember clientCareTeamMember) {
        return canDelete(clientCareTeamMember, currentUserFilter());
    }

    @Override
    public boolean canDelete(ClientCareTeamMember clientCareTeamMember, PermissionFilter filter) {
        var clientAware = clientService.findSecurityAwareEntity(clientCareTeamMember.getClientId());

        return hasModifyingVisibleAccess(filter, clientCareTeamMember, clientAware, CareTeamRoleService.ANY_TARGET_ROLE,
                AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                CLIENT_CARE_TEAM_DELETE_VISIBLE_ALL_EXCEPT_OPTED_OUT,
                CLIENT_CARE_TEAM_DELETE_VISIBLE_BY_MATRIX_IF_ASSOCIATED_ORGANIZATION,
                CLIENT_CARE_TEAM_DELETE_VISIBLE_BY_MATRIX_IF_ASSOCIATED_COMMUNITY,
                CLIENT_CARE_TEAM_DELETE_VISIBLE_BY_MATRIX_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
                CLIENT_CARE_TEAM_DELETE_VISIBLE_BY_MATRIX_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
                CLIENT_CARE_TEAM_DELETE_VISIBLE_BY_MATRIX_IF_CURRENT_RP_CLIENT_CTM_EXCEPT_OPTED_OUT,
                CLIENT_CARE_TEAM_DELETE_VISIBLE_BY_MATRIX_IF_CURRENT_REGULAR_CLIENT_CTM_EXCEPT_OPTED_OUT,
                CLIENT_CARE_TEAM_DELETE_VISIBLE_BY_MATRIX_IF_CURRENT_RP_COMMUNITY_CTM_EXCEPT_OPTED_OUT,
                CLIENT_CARE_TEAM_DELETE_VISIBLE_BY_MATRIX_IF_CURRENT_REGULAR_COMMUNITY_CTM_EXCEPT_OPTED_OUT,
                CLIENT_CARE_TEAM_DELETE_VISIBLE_BY_MATRIX_OPTED_IN_IF_CLIENT_ADDED_BY_SELF,
                CLIENT_CARE_TEAM_DELETE_VISIBLE_BY_MATRIX_IF_SELF_CLIENT_RECORD_EXCEPT_OPTED_OUT,
                CLIENT_CARE_TEAM_DELETE_VISIBLE_BY_MATRIX_IF_PRIMARY_SELF_CARE_TEAM_RECORD_EXCEPT_OPTED_OUT,
                CLIENT_CARE_TEAM_DELETE_VISIBLE_RP_BY_MATRIX_IF_CLIENT_FOUND_IN_RECORD_SEARCH_EXCEPT_OPTED_OUT,
                CLIENT_CARE_TEAM_DELETE_VISIBLE_REGULAR_BY_MATRIX_IF_CLIENT_FOUND_IN_RECORD_SEARCH_EXCEPT_OPTED_OUT
        );
    }

    private boolean isInSameCommunityAndEditableClientCTMRole(Employee current, Long targetEmployeeId, Long currentCareTeamRoleId, Long targetCareTeamRoleId) {
        if (!CareTeamSecurityService.ANY_TARGET_EMPLOYEE.equals(targetEmployeeId)) {
            var targetEmployeeCommunity = employeeService.findEmployeeCommunityId(targetEmployeeId);
            if (!current.getCommunityId().equals(targetEmployeeCommunity)) {
                return false;
            }
        }

        return isEditableClientCareTeamMemberRole(current, currentCareTeamRoleId, targetCareTeamRoleId);
    }

    @Override
    public boolean canAddAnyRoleAndEmployee(Long clientId, AffiliatedCareTeamType type) {
        return canAdd(new CareTeamSecurityFieldsAware() {
            @Override
            public Long getEmployeeId() {
                return CareTeamSecurityService.ANY_TARGET_EMPLOYEE;
            }

            @Override
            public Long getCareTeamRoleId() {
                return CareTeamRoleService.ANY_TARGET_ROLE;
            }

            @Override
            public Long getClientId() {
                return clientId;
            }

            @Override
            public Long getCommunityId() {
                return null;
            }
        }, type);
    }

    private boolean hasModifyingVisibleAccess(PermissionFilter permissionFilter,
                                              ClientCareTeamMember careTeamMember, ClientSecurityAwareEntity client,
                                              Long targetCareTeamRoleId, AffiliatedCareTeamType type,
                                              Permission visibleAllExceptOptedOut,
                                              Permission visibleByMatrixAssociatedOrganization,
                                              Permission visibleByMatrixAssociatedCommunity,
                                              Permission visibleByMatrixOptedInFromAffiliatedOrganization,
                                              Permission visibleByMatrixOptedInFromAffiliatedCommunity,
                                              Permission visibleByMatrixCurrentRpClientCtm,
                                              Permission visibleByMatrixCurrentRegularClientCtm,
                                              Permission visibleByMatrixCurrentRpCommunityCtm,
                                              Permission visibleByMatrixCurrentRegularCommunityCtm,
                                              Permission visibleByMatrixOptedInClientAddedBySelf,
                                              Permission visibleByMatrixSelfClientRecord,
                                              Permission visibleByMatrixPrimarySelfCareTeamRecord,
                                              Permission visibleRpByMatrixClientFoundInRecordSearch,
                                              Permission visibleRegularByMatrixClientFoundInRecordSearch) {
        return hasCommonModifyingVisibleAccess(permissionFilter, client,
                careTeamMember.getCareTeamRole().getId(), targetCareTeamRoleId,
                careTeamMember.getEmployeeId(), type,
                visibleAllExceptOptedOut,
                visibleByMatrixAssociatedOrganization, visibleByMatrixAssociatedCommunity,
                visibleByMatrixOptedInFromAffiliatedOrganization,
                visibleByMatrixOptedInFromAffiliatedCommunity,
                visibleByMatrixCurrentRpClientCtm, visibleByMatrixCurrentRegularClientCtm,
                visibleByMatrixCurrentRpCommunityCtm, visibleByMatrixCurrentRegularCommunityCtm,
                visibleByMatrixOptedInClientAddedBySelf,
                visibleByMatrixSelfClientRecord,
                visibleRpByMatrixClientFoundInRecordSearch,
                visibleRegularByMatrixClientFoundInRecordSearch
        ) || hasSelfCareTeamRecordModifyingAccess(permissionFilter, careTeamMember,
                (employee) -> isCareTeamVisibleForAny(Collections.singletonList(employee), careTeamMember.getClientId()),
                client, type, targetCareTeamRoleId, visibleByMatrixPrimarySelfCareTeamRecord);
    }

    //passing 'type' to separate can-add checks for regular and affiliated care teams when needed
    private boolean hasCommonModifyingVisibleAccess(PermissionFilter permissionFilter,
                                                    ClientSecurityAwareEntity client,
                                                    Long currentCareTeamRoleId,
                                                    Long targetCareTeamRoleId,
                                                    Long targetEmployeeId,
                                                    AffiliatedCareTeamType type,
                                                    Permission visibleAllExceptOptedOut,
                                                    Permission visibleByMatrixAssociatedOrganization,
                                                    Permission visibleByMatrixAssociatedCommunity,
                                                    Permission visibleByMatrixOptedInFromAffiliatedOrganization,
                                                    Permission visibleByMatrixOptedInFromAffiliatedCommunity,
                                                    Permission visibleByMatrixCurrentRpClientCtm,
                                                    Permission visibleByMatrixCurrentRegularClientCtm,
                                                    Permission visibleByMatrixCurrentRpCommunityCtm,
                                                    Permission visibleByMatrixCurrentRegularCommunityCtm,
                                                    Permission visibleByMatrixOptedInClientAddedBySelf,
                                                    Permission visibleByMatrixSelfClientRecord,
                                                    Permission visibleRpByMatrixClientFoundInRecordSearch,
                                                    Permission visibleRegularByMatrixClientFoundInRecordSearch) {
        Objects.requireNonNull(targetCareTeamRoleId, "Care team role id should not be null!");
        Objects.requireNonNull(targetEmployeeId, "Employee id should not be null!");

        if (!isInEligibleForDiscoveryCommunity(client)) {
            return false;
        }

        if (visibleAllExceptOptedOut != null && permissionFilter.hasPermission(visibleAllExceptOptedOut)) {
            var employees = permissionFilter.getEmployees(visibleAllExceptOptedOut);

            if (isCareTeamVisibleForAny(employees, client.getId()) && isClientOptedIn(client)) {
                return true;
            }
        }

        if (type.isIncludesRegular()) {
            if (visibleByMatrixAssociatedOrganization != null && permissionFilter.hasPermission(visibleByMatrixAssociatedOrganization)) {
                var employees = permissionFilter.getEmployees(visibleByMatrixAssociatedOrganization);

                var employeeInOrganization = findEmployeeInOrganization(employees, client.getOrganizationId());
                var allowedEmployee = employeeInOrganization.filter(employee -> isInSameOrgAndEditableClientCTMRole(employee, targetEmployeeId,
                        currentCareTeamRoleId, targetCareTeamRoleId));


                if (allowedEmployee.isPresent() && isCareTeamVisibleForAny(Collections.singletonList(allowedEmployee.get()), client.getId())) {
                    return true;
                }
            }

            if (visibleByMatrixAssociatedCommunity != null && permissionFilter.hasPermission(visibleByMatrixAssociatedCommunity)) {
                var employees = permissionFilter.getEmployees(visibleByMatrixAssociatedCommunity);
                var employeeInCommunity = findEmployeeInCommunity(employees, client.getCommunityId());

                var allowedEmployee = employeeInCommunity
                        .filter(employee -> isInSameOrgAndEditableClientCTMRole(employee, targetEmployeeId,
                                currentCareTeamRoleId, targetCareTeamRoleId));

                if (allowedEmployee.isPresent() && isCareTeamVisibleForAny(Collections.singletonList(allowedEmployee.get()), client.getId())) {
                    return true;
                }
            }
        }

        if (type.isIncludesPrimary()) {
            if (visibleByMatrixOptedInFromAffiliatedOrganization != null
                    && permissionFilter.hasPermission(visibleByMatrixOptedInFromAffiliatedOrganization)
                    && isClientOptedIn(client)
            ) {
                var employees = permissionFilter.getEmployees(visibleByMatrixOptedInFromAffiliatedOrganization);
                var employeesFromAffiliatedOrgs = findInAffiliatedOrganizationOfCommunity(employees, client.getCommunityId());

                var allowedEmployees = employeesFromAffiliatedOrgs
                        .filter(employee -> isInSameOrgAndEditableClientCTMRole(employee, targetEmployeeId,
                                currentCareTeamRoleId, targetCareTeamRoleId))
                        .collect(Collectors.toList());


                if (!allowedEmployees.isEmpty() && isCareTeamVisibleForAny(allowedEmployees, client.getId())) {
                    return true;
                }
            }

            if (visibleByMatrixOptedInFromAffiliatedCommunity != null
                    && permissionFilter.hasPermission(visibleByMatrixOptedInFromAffiliatedCommunity)
                    && isClientOptedIn(client)
            ) {
                var employees = permissionFilter.getEmployees(visibleByMatrixOptedInFromAffiliatedCommunity);
                var employeesFromAffiliatedComms = findInAffiliatedCommunity(employees, client.getCommunityId());

                var allowedEmployees = employeesFromAffiliatedComms
                        .filter(employee -> isInSameOrgAndEditableClientCTMRole(employee, targetEmployeeId,
                                currentCareTeamRoleId, targetCareTeamRoleId))
                        .collect(Collectors.toList());

                if (!allowedEmployees.isEmpty() && isCareTeamVisibleForAny(allowedEmployees, client.getId())) {
                    return true;
                }
            }
        }

        if (visibleByMatrixCurrentRpClientCtm != null && permissionFilter.hasPermission(visibleByMatrixCurrentRpClientCtm)) {
            var employees = permissionFilter.getEmployees(visibleByMatrixCurrentRpClientCtm);

            if (hasClientCtmAccess(employees,
                    client,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    type,
                    targetEmployeeId,
                    currentCareTeamRoleId,
                    targetCareTeamRoleId,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (visibleByMatrixCurrentRegularClientCtm != null && permissionFilter.hasPermission(visibleByMatrixCurrentRegularClientCtm)) {
            var employees = permissionFilter.getEmployees(visibleByMatrixCurrentRegularClientCtm);

            if (hasClientCtmAccess(
                    employees,
                    client,
                    AffiliatedCareTeamType.REGULAR,
                    type,
                    targetEmployeeId,
                    currentCareTeamRoleId,
                    targetCareTeamRoleId,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (visibleByMatrixCurrentRpCommunityCtm != null && permissionFilter.hasPermission(visibleByMatrixCurrentRpCommunityCtm)) {
            var employees = permissionFilter.getEmployees(visibleByMatrixCurrentRpCommunityCtm);

            if (hasCommunityCtmAccess(
                    employees,
                    client,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    type,
                    targetEmployeeId,
                    currentCareTeamRoleId,
                    targetCareTeamRoleId,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (visibleByMatrixCurrentRegularCommunityCtm != null && permissionFilter.hasPermission(visibleByMatrixCurrentRegularCommunityCtm)) {
            var employees = permissionFilter.getEmployees(visibleByMatrixCurrentRegularCommunityCtm);

            if (hasCommunityCtmAccess(employees,
                    client,
                    AffiliatedCareTeamType.REGULAR,
                    type,
                    targetEmployeeId,
                    currentCareTeamRoleId,
                    targetCareTeamRoleId,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (visibleByMatrixOptedInClientAddedBySelf != null
                && permissionFilter.hasPermission(visibleByMatrixOptedInClientAddedBySelf)
                && isClientOptedIn(client)
        ) {
            var employees = permissionFilter.getEmployees(visibleByMatrixOptedInClientAddedBySelf);

            var creators = findClientsCreators(employees, Collections.singletonList(client));
            creators = filterByAffiliationType(creators, client, type);

            var allowedEmployees = creators
                    .filter(employee -> isInSameOrgAndEditableClientCTMRole(employee, targetEmployeeId,
                            currentCareTeamRoleId, targetCareTeamRoleId))
                    .collect(Collectors.toList());

            if (!allowedEmployees.isEmpty() && isCareTeamVisibleForAny(allowedEmployees, client.getId())) {
                return true;
            }
        }

        if (visibleByMatrixSelfClientRecord != null && permissionFilter.hasPermission(visibleByMatrixSelfClientRecord)) {
            var employees = permissionFilter.getEmployees(visibleByMatrixSelfClientRecord);
            var selfClientEmployeeRecords = findSelfClientRecord(employees, client);
            selfClientEmployeeRecords = filterByAffiliationType(selfClientEmployeeRecords, client, type);

            var allowedEmployees = selfClientEmployeeRecords
                    .filter(employee -> isInSameOrgAndEditableClientCTMRole(employee, targetEmployeeId,
                            currentCareTeamRoleId, targetCareTeamRoleId))
                    .collect(Collectors.toList());

            if (!allowedEmployees.isEmpty() && isCareTeamVisibleForAny(allowedEmployees, client.getId())) {
                return true;
            }
        }

        if (visibleRpByMatrixClientFoundInRecordSearch != null
                && permissionFilter.hasPermission(visibleRpByMatrixClientFoundInRecordSearch)
                && permissionFilter.containsClientRecordSearchFoundId(client.getId())) {

            var allowedEmployees = permissionFilter.getEmployees(visibleRpByMatrixClientFoundInRecordSearch).stream()
                    .filter(e -> isInSameOrgAndEditableClientCTMRole(e, targetEmployeeId, currentCareTeamRoleId, targetCareTeamRoleId))
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(allowedEmployees) && isCareTeamVisibleForAny(allowedEmployees, client.getId())) {
                return true;
            }
        }

        if (visibleRegularByMatrixClientFoundInRecordSearch != null
                && permissionFilter.hasPermission(visibleRegularByMatrixClientFoundInRecordSearch)
                && type.isIncludesRegular()
                && permissionFilter.containsClientRecordSearchFoundId(client.getId())) {

            var allowedEmployees = permissionFilter.getEmployees(visibleRegularByMatrixClientFoundInRecordSearch).stream()
                    .filter(e -> isInSameOrgAndEditableClientCTMRole(e, targetEmployeeId, currentCareTeamRoleId, targetCareTeamRoleId))
                    .filter(e -> Objects.equals(e.getOrganizationId(), client.getOrganizationId()))
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(allowedEmployees) && isCareTeamVisibleForAny(allowedEmployees, client.getId())) {
                return true;
            }
        }

        return false;
    }

    private boolean hasClientCtmAccess(
            Collection<Employee> employees,
            ClientSecurityAwareEntity client,
            AffiliatedCareTeamType permissionType,
            AffiliatedCareTeamType requestedType,
            Long targetEmployeeId,
            Long currentCareTeamRoleId,
            Long targetCareTeamRoleId,
            HieConsentCareTeamType hieConsentCareTeamType
    ) {
        return hasCtmAccess(
                client,
                permissionType,
                requestedType,
                targetEmployeeId,
                currentCareTeamRoleId,
                targetCareTeamRoleId,
                (type) -> findClientsCareTeamMembers(employees, Collections.singletonList(client), type, hieConsentCareTeamType)
        );
    }

    private boolean hasCommunityCtmAccess(
            Collection<Employee> employees,
            ClientSecurityAwareEntity client,
            AffiliatedCareTeamType permissionType,
            AffiliatedCareTeamType requestedType,
            Long targetEmployeeId,
            Long currentCareTeamRoleId,
            Long targetCareTeamRoleId,
            HieConsentCareTeamType consentType
    ) {
        return hasCtmAccess(
                client,
                permissionType,
                requestedType,
                targetEmployeeId,
                currentCareTeamRoleId,
                targetCareTeamRoleId,
                (type) -> findCommunityCareTeamMembers(employees, client.getCommunityId(), type, consentType)
        );
    }

    private boolean hasCtmAccess(
            ClientSecurityAwareEntity client,
            AffiliatedCareTeamType permissionType,
            AffiliatedCareTeamType requestedType,
            Long targetEmployeeId,
            Long currentCareTeamRoleId,
            Long targetCareTeamRoleId,
            Function<AffiliatedCareTeamType, Stream<Employee>> getCareTeamMembers
    ) {
        return requestedType.downсast(permissionType)
                .map(downcastedAffType -> {
                    var allowedEmployees = getCareTeamMembers.apply(downcastedAffType)
                            .filter(e -> isInSameOrgAndEditableClientCTMRole(e, targetEmployeeId, currentCareTeamRoleId, targetCareTeamRoleId))
                            .collect(Collectors.toList());

                    return !allowedEmployees.isEmpty() && isCareTeamVisibleForAny(allowedEmployees, client.getId());
                })
                .orElse(false);
    }

    private boolean isInSameOrgAndEditableClientCTMRole(Employee current, Long targetEmployeeId,
                                                        Long currentCareTeamRoleId,
                                                        Long targetCareTeamRoleId) {
        if (!CareTeamSecurityService.ANY_TARGET_EMPLOYEE.equals(targetEmployeeId)) {
            var targetEmployeeOrg = employeeService.findEmployeeOrganizationId(targetEmployeeId);
            if (!current.getOrganizationId().equals(targetEmployeeOrg)) {
                return false;
            }
        }

        return isEditableClientCareTeamMemberRole(current, currentCareTeamRoleId, targetCareTeamRoleId);
    }

    private boolean isEditableClientCareTeamMemberRole(Employee current, Long currentCareTeamRoleId, Long targetCareTeamRoleId) {
        return careTeamRoleService.isEditableClientCareTeamMemberRole(current.getCareTeamRole(), currentCareTeamRoleId, targetCareTeamRoleId);
    }

    private boolean isCareTeamVisibleForAny(Collection<Employee> employees, Long clientId) {
        return clientCareTeamMemberService.isCareTeamVisibleForAny(employees, clientId);
    }
}
