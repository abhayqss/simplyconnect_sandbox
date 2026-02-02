package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.dto.CareTeamSecurityFieldsAware;
import com.scnsoft.eldermark.beans.security.projection.entity.CommunitySecurityAwareEntity;
import com.scnsoft.eldermark.entity.CommunityCareTeamMember;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.CareTeamRoleService;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service
@Transactional(readOnly = true)
public class CommunityCareTeamSecurityServiceImpl extends BaseCareTeamSecurityService<CommunityCareTeamMember, CommunitySecurityAwareEntity>
        implements CommunityCareTeamSecurityService {

    private final List<Permission> VIEW_IN_LIST_PERMISSIONS = Arrays.asList(
            COMMUNITY_CARE_TEAM_VIEW_ALL,
            COMMUNITY_CARE_TEAM_VIEW_IF_ASSOCIATED_ORGANIZATION,
            COMMUNITY_CARE_TEAM_VIEW_IF_ASSOCIATED_COMMUNITY,
            COMMUNITY_CARE_TEAM_VIEW_IF_FROM_AFFILIATED_ORGANIZATION,
            COMMUNITY_CARE_TEAM_VIEW_IF_FROM_AFFILIATED_COMMUNITY,
            COMMUNITY_CARE_TEAM_VIEW_IF_CO_RP_CLIENT_CTM,
            COMMUNITY_CARE_TEAM_VIEW_IF_CO_RP_COMMUNITY_CTM,
            COMMUNITY_CARE_TEAM_VIEW_IF_SELF_RECORD);

    @Autowired
    private CommunityService communityService;

    @Autowired
    private CareTeamRoleService careTeamRoleService;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public boolean canView(CommunityCareTeamMember communityCareTeamMember) {
        var permissionFilter = currentUserFilter();

        var communityId = communityCareTeamMember.getCommunityId();
        var community = communityService.findSecurityAwareEntity(communityId);

        if (!isEligibleForDiscoveryCommunity(community)) {
            return false;
        }


        if (permissionFilter.hasPermission(COMMUNITY_CARE_TEAM_VIEW_ALL)) {
            return true;
        }

        if (permissionFilter.hasPermission(COMMUNITY_CARE_TEAM_VIEW_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(COMMUNITY_CARE_TEAM_VIEW_IF_ASSOCIATED_ORGANIZATION);

            if (isAnyCreatedUnderOrganization(employees, community.getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(COMMUNITY_CARE_TEAM_VIEW_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(COMMUNITY_CARE_TEAM_VIEW_IF_ASSOCIATED_COMMUNITY);

            if (isAnyCreatedUnderCommunity(employees, community.getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(COMMUNITY_CARE_TEAM_VIEW_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(COMMUNITY_CARE_TEAM_VIEW_IF_FROM_AFFILIATED_ORGANIZATION);

            if (isAnyInAffiliatedOrganizationOfCommunity(employees, communityId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(COMMUNITY_CARE_TEAM_VIEW_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(COMMUNITY_CARE_TEAM_VIEW_IF_FROM_AFFILIATED_COMMUNITY);

            if (isAnyInAffiliatedCommunity(employees, communityId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(COMMUNITY_CARE_TEAM_VIEW_IF_CO_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(COMMUNITY_CARE_TEAM_VIEW_IF_CO_RP_COMMUNITY_CTM);

            if (isAnyInCommunityCareTeam(
                    employees,
                    communityId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(COMMUNITY_CARE_TEAM_VIEW_IF_CO_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(COMMUNITY_CARE_TEAM_VIEW_IF_CO_RP_CLIENT_CTM);

            if (isAnyInAnyClientCareTeamOfCommunity(
                    employees,
                    communityId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(COMMUNITY_CARE_TEAM_VIEW_IF_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(COMMUNITY_CARE_TEAM_VIEW_IF_CLIENT_ADDED_BY_SELF);

            if (isAnyClientAddedBySelfInCommunity(employees, communityId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(COMMUNITY_CARE_TEAM_VIEW_IF_SELF_RECORD)) {
            var employees = permissionFilter.getEmployees(COMMUNITY_CARE_TEAM_VIEW_IF_SELF_RECORD);

            if (existsSelfClientRecordInCommunity(employees, communityId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canViewList() {
        return hasAnyPermission(VIEW_IN_LIST_PERMISSIONS);
    }

    @Override
    public boolean canEdit(CommunityCareTeamMember communityCareTeamMember, Long targetCareTeamRoleId) {
        var permissionFilter = currentUserFilter();
        var community = communityService.findSecurityAwareEntity(communityCareTeamMember.getCommunityId());
        var typeToCheck = AffiliatedCareTeamType.REGULAR_AND_PRIMARY;

        return hasCommonModifyingAccess(permissionFilter, communityCareTeamMember, community,
                targetCareTeamRoleId, typeToCheck,
                COMMUNITY_CARE_TEAM_EDIT_BY_MATRIX_ALL,
                COMMUNITY_CARE_TEAM_EDIT_BY_MATRIX_IF_ASSOCIATED_ORGANIZATION,
                COMMUNITY_CARE_TEAM_EDIT_BY_MATRIX_IF_ASSOCIATED_COMMUNITY,
                COMMUNITY_CARE_TEAM_EDIT_BY_MATRIX_IF_FROM_AFFILIATED_ORGANIZATION,
                COMMUNITY_CARE_TEAM_EDIT_BY_MATRIX_IF_FROM_AFFILIATED_COMMUNITY,
                COMMUNITY_CARE_TEAM_EDIT_BY_MATRIX_IF_CO_RP_CLIENT_CTM,
                COMMUNITY_CARE_TEAM_EDIT_BY_MATRIX_IF_CO_REGULAR_CLIENT_CTM,
                COMMUNITY_CARE_TEAM_EDIT_BY_MATRIX_IF_CO_RP_COMMUNITY_CTM,
                COMMUNITY_CARE_TEAM_EDIT_BY_MATRIX_IF_CO_REGULAR_COMMUNITY_CTM,
                COMMUNITY_CARE_TEAM_EDIT_BY_MATRIX_IF_CLIENT_ADDED_BY_SELF,
                COMMUNITY_CARE_TEAM_EDIT_BY_MATRIX_IF_SELF_CLIENT_RECORD
        ) || hasSelfCareTeamRecordModifyingAccess(permissionFilter, communityCareTeamMember,
                e -> true, //care team visibility doesn't apply to community care team
                community, typeToCheck,
                targetCareTeamRoleId, COMMUNITY_CARE_TEAM_EDIT_BY_MATRIX_IF_PRIMARY_SELF_CARE_TEAM_RECORD);
    }

    @Override
    public boolean canAdd(CareTeamSecurityFieldsAware dto) {
        return canAdd(dto, AffiliatedCareTeamType.REGULAR_AND_PRIMARY);
    }

    private boolean canAdd(CareTeamSecurityFieldsAware dto, AffiliatedCareTeamType type) {
        var permissionFilter = currentUserFilter();
        var community = communityService.findSecurityAwareEntity(dto.getCommunityId());

        return hasCommonModifyingAccess(permissionFilter, community,
                null, dto.getCareTeamRoleId(), dto.getEmployeeId(), type,
                COMMUNITY_CARE_TEAM_ADD_BY_MATRIX_ALL,
                COMMUNITY_CARE_TEAM_ADD_BY_MATRIX_IF_ASSOCIATED_ORGANIZATION,
                COMMUNITY_CARE_TEAM_ADD_BY_MATRIX_IF_ASSOCIATED_COMMUNITY,
                COMMUNITY_CARE_TEAM_ADD_BY_MATRIX_IF_FROM_AFFILIATED_ORGANIZATION,
                COMMUNITY_CARE_TEAM_ADD_BY_MATRIX_IF_FROM_AFFILIATED_COMMUNITY,
                COMMUNITY_CARE_TEAM_ADD_BY_MATRIX_IF_CO_RP_CLIENT_CTM,
                COMMUNITY_CARE_TEAM_ADD_BY_MATRIX_IF_CO_REGULAR_CLIENT_CTM,
                COMMUNITY_CARE_TEAM_ADD_BY_MATRIX_IF_CO_RP_COMMUNITY_CTM,
                COMMUNITY_CARE_TEAM_ADD_BY_MATRIX_IF_CO_REGULAR_COMMUNITY_CTM,
                COMMUNITY_CARE_TEAM_ADD_BY_MATRIX_IF_CLIENT_ADDED_BY_SELF,
                COMMUNITY_CARE_TEAM_ADD_BY_MATRIX_IF_SELF_CLIENT_RECORD);
    }

    @Override
    public boolean canDelete(CommunityCareTeamMember communityCareTeamMember) {
        return canDelete(communityCareTeamMember, currentUserFilter());
    }

    @Override
    public boolean canDelete(CommunityCareTeamMember communityCareTeamMember, PermissionFilter permissionFilter) {
        var community = communityService.findSecurityAwareEntity(communityCareTeamMember.getCommunityId());
        var typeToCheck = AffiliatedCareTeamType.REGULAR_AND_PRIMARY;

        return hasCommonModifyingAccess(permissionFilter, communityCareTeamMember, community,
                CareTeamRoleService.ANY_TARGET_ROLE, typeToCheck,
                COMMUNITY_CARE_TEAM_DELETE_BY_MATRIX_ALL,
                COMMUNITY_CARE_TEAM_DELETE_BY_MATRIX_IF_ASSOCIATED_ORGANIZATION,
                COMMUNITY_CARE_TEAM_DELETE_BY_MATRIX_IF_ASSOCIATED_COMMUNITY,
                COMMUNITY_CARE_TEAM_DELETE_BY_MATRIX_IF_FROM_AFFILIATED_ORGANIZATION,
                COMMUNITY_CARE_TEAM_DELETE_BY_MATRIX_IF_FROM_AFFILIATED_COMMUNITY,
                COMMUNITY_CARE_TEAM_DELETE_BY_MATRIX_IF_CO_RP_CLIENT_CTM,
                COMMUNITY_CARE_TEAM_DELETE_BY_MATRIX_IF_CO_REGULAR_CLIENT_CTM,
                COMMUNITY_CARE_TEAM_DELETE_BY_MATRIX_IF_CO_RP_COMMUNITY_CTM,
                COMMUNITY_CARE_TEAM_DELETE_BY_MATRIX_IF_CO_REGULAR_COMMUNITY_CTM,
                COMMUNITY_CARE_TEAM_DELETE_BY_MATRIX_IF_CLIENT_ADDED_BY_SELF,
                COMMUNITY_CARE_TEAM_DELETE_BY_MATRIX_IF_SELF_CLIENT_RECORD
        ) || hasSelfCareTeamRecordModifyingAccess(permissionFilter, communityCareTeamMember,
                e -> true, //care team visibility doesn't apply to community care team
                community, typeToCheck, communityCareTeamMember.getCareTeamRole().getId(),
                COMMUNITY_CARE_TEAM_DELETE_BY_MATRIX_IF_PRIMARY_SELF_CARE_TEAM_RECORD);
    }

    @Override
    public boolean canAddAnyRoleAndEmployee(Long communityId, AffiliatedCareTeamType type) {
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
                return null;
            }

            @Override
            public Long getCommunityId() {
                return communityId;
            }
        }, type);
    }

    private boolean hasCommonModifyingAccess(PermissionFilter permissionFilter,
                                             CommunityCareTeamMember careTeamMember, CommunitySecurityAwareEntity community,
                                             Long targetCareTeamRoleId, AffiliatedCareTeamType type,
                                             Permission byMatrixAll,
                                             Permission byMatrixAssociatedOrganization,
                                             Permission byMatrixAssociatedCommunity,
                                             Permission byMatrixFromAffiliatedOrganization,
                                             Permission byMatrixFromAffiliatedCommunity,
                                             Permission byMatrixCoRpClientCtm,
                                             Permission byMatrixCoRegularClientCtm,
                                             Permission byMatrixCoRpCommunityCtm,
                                             Permission byMatrixCoRegularCommunityCtm,
                                             Permission byMatrixClientAddedBySelf,
                                             Permission byMatrixSelfClientRecord) {
        return hasCommonModifyingAccess(permissionFilter, community,
                careTeamMember.getCareTeamRole().getId(), targetCareTeamRoleId,
                careTeamMember.getEmployeeId(), type,
                byMatrixAll,
                byMatrixAssociatedOrganization, byMatrixAssociatedCommunity,
                byMatrixFromAffiliatedOrganization, byMatrixFromAffiliatedCommunity,
                byMatrixCoRpClientCtm, byMatrixCoRegularClientCtm,
                byMatrixCoRpCommunityCtm, byMatrixCoRegularCommunityCtm,
                byMatrixClientAddedBySelf,
                byMatrixSelfClientRecord);
    }

    private boolean hasCommonModifyingAccess(PermissionFilter permissionFilter,
                                             CommunitySecurityAwareEntity community,
                                             Long currentCareTeamRoleId, Long targetCareTeamRoleId,
                                             Long targetEmployeeId, AffiliatedCareTeamType type,
                                             Permission byMatrixAll,
                                             Permission byMatrixAssociatedOrganization,
                                             Permission byMatrixAssociatedCommunity,
                                             Permission byMatrixFromAffiliatedOrganization,
                                             Permission byMatrixFromAffiliatedCommunity,
                                             Permission byMatrixCoRpClientCtm,
                                             Permission byMatrixCoRegularClientCtm,
                                             Permission byMatrixCoRpCommunityCtm,
                                             Permission byMatrixCoRegularCommunityCtm,
                                             Permission byMatrixClientAddedBySelf,
                                             Permission byMatrixSelfClientRecord) {
        Objects.requireNonNull(targetCareTeamRoleId, "Care team role id should not be null!");
        Objects.requireNonNull(targetEmployeeId, "Employee id should not be null!");

        if (!isEligibleForDiscoveryCommunity(community)) {
            return false;
        }

        if (byMatrixAll != null && permissionFilter.hasPermission(byMatrixAll)) {
            var allByMatrix = permissionFilter.getEmployees(byMatrixAll).stream();

            if (allByMatrix.anyMatch(employee -> isEditableCommunityCareTeamMemberRole(employee, currentCareTeamRoleId, targetCareTeamRoleId))) {
                return true;
            }
        }

        if (type.isIncludesRegular()) {
            if (byMatrixAssociatedOrganization != null && permissionFilter.hasPermission(byMatrixAssociatedOrganization)) {
                var employees = permissionFilter.getEmployees(byMatrixAssociatedOrganization);
                var employeeInOrganization = findEmployeeInOrganization(employees, community.getOrganizationId());

                if (employeeInOrganization.filter(employee -> isInSameOrgAndEditableCommunityCTMRole(employee, targetEmployeeId,
                                currentCareTeamRoleId, targetCareTeamRoleId))
                        .isPresent()) {
                    return true;
                }
            }

            if (byMatrixAssociatedCommunity != null && permissionFilter.hasPermission(byMatrixAssociatedCommunity)) {
                var employees = permissionFilter.getEmployees(byMatrixAssociatedCommunity);
                var employeeInCommunity = findEmployeeInCommunity(employees, community.getId());

                if (employeeInCommunity.filter(employee -> isInSameOrgAndEditableCommunityCTMRole(employee, targetEmployeeId,
                                currentCareTeamRoleId, targetCareTeamRoleId))
                        .isPresent()) {
                    return true;
                }
            }
        }

        if (type.isIncludesPrimary()) {
            if (byMatrixFromAffiliatedOrganization != null && permissionFilter.hasPermission(byMatrixFromAffiliatedOrganization)) {
                var employees = permissionFilter.getEmployees(byMatrixFromAffiliatedOrganization);
                var employeesFromAffiliatedOrgs = findInAffiliatedOrganizationOfCommunity(employees, community.getId());

                if (employeesFromAffiliatedOrgs.anyMatch(employee -> isInSameOrgAndEditableCommunityCTMRole(employee, targetEmployeeId,
                        currentCareTeamRoleId, targetCareTeamRoleId))) {
                    return true;
                }
            }

            if (byMatrixFromAffiliatedCommunity != null && permissionFilter.hasPermission(byMatrixFromAffiliatedCommunity)) {
                var employees = permissionFilter.getEmployees(byMatrixFromAffiliatedCommunity);
                var employeesFromAffiliatedComms = findInAffiliatedCommunity(employees, community.getId());

                if (employeesFromAffiliatedComms.anyMatch(employee -> isInSameOrgAndEditableCommunityCTMRole(employee, targetEmployeeId,
                        currentCareTeamRoleId, targetCareTeamRoleId))) {
                    return true;
                }
            }
        }

        if (byMatrixCoRpClientCtm != null && permissionFilter.hasPermission(byMatrixCoRpClientCtm)) {
            var employees = permissionFilter.getEmployees(byMatrixCoRpClientCtm);

            var downcastedAffType = type.downсast(AffiliatedCareTeamType.REGULAR_AND_PRIMARY);
            if (downcastedAffType.isPresent()) {
                var rpClientCareTeamMembers = findClientsCareTeamMembersInClientCommunity(
                        employees,
                        community.getId(),
                        downcastedAffType.get(),
                        HieConsentCareTeamType.currentAndOnHold()
                );

                if (rpClientCareTeamMembers.anyMatch(employee -> isInSameOrgAndEditableCommunityCTMRole(employee, targetEmployeeId,
                        currentCareTeamRoleId, targetCareTeamRoleId))) {
                    return true;
                }
            }
        }

        if (byMatrixCoRegularClientCtm != null && permissionFilter.hasPermission(byMatrixCoRegularClientCtm)) {
            var employees = permissionFilter.getEmployees(byMatrixCoRegularClientCtm);

            var downcastedAffType = type.downсast(AffiliatedCareTeamType.REGULAR);
            if (downcastedAffType.isPresent()) {
                var regularClientCareTeamMembers = findClientsCareTeamMembersInClientCommunity(
                        employees,
                        community.getId(),
                        downcastedAffType.get(),
                        HieConsentCareTeamType.currentAndOnHold()
                );

                if (regularClientCareTeamMembers.anyMatch(employee -> isInSameOrgAndEditableCommunityCTMRole(employee, targetEmployeeId,
                        currentCareTeamRoleId, targetCareTeamRoleId))) {
                    return true;
                }
            }
        }

        if (byMatrixCoRpCommunityCtm != null && permissionFilter.hasPermission(byMatrixCoRpCommunityCtm)) {
            var employees = permissionFilter.getEmployees(byMatrixCoRpCommunityCtm);

            var downcastedAffType = type.downсast(AffiliatedCareTeamType.REGULAR_AND_PRIMARY);
            if (downcastedAffType.isPresent()) {
                var rpCommunityCareTeamMembers = findCommunityCareTeamMembers(
                        employees,
                        community.getId(),
                        downcastedAffType.get(),
                        HieConsentCareTeamType.currentAndOnHold()
                );

                if (rpCommunityCareTeamMembers.anyMatch(employee -> isInSameOrgAndEditableCommunityCTMRole(employee, targetEmployeeId,
                        currentCareTeamRoleId, targetCareTeamRoleId))) {
                    return true;
                }
            }
        }

        if (byMatrixCoRegularCommunityCtm != null && permissionFilter.hasPermission(byMatrixCoRegularCommunityCtm)) {
            var employees = permissionFilter.getEmployees(byMatrixCoRegularCommunityCtm);

            var downcastedAffType = type.downсast(AffiliatedCareTeamType.REGULAR);
            if (downcastedAffType.isPresent()) {
                var regularCommunityCareTeamMembers = findCommunityCareTeamMembers(
                        employees,
                        community.getId(),
                        downcastedAffType.get(),
                        HieConsentCareTeamType.currentAndOnHold()
                );

                if (regularCommunityCareTeamMembers.anyMatch(employee -> isInSameOrgAndEditableCommunityCTMRole(employee, targetEmployeeId,
                        currentCareTeamRoleId, targetCareTeamRoleId))) {
                    return true;
                }
            }
        }

        if (byMatrixClientAddedBySelf != null && permissionFilter.hasPermission(byMatrixClientAddedBySelf)) {
            var employees = permissionFilter.getEmployees(byMatrixClientAddedBySelf);
            var creators = findClientCreatorsInCommunity(employees, community.getId()).stream();
            creators = filterByAffiliationType(creators, community, type);

            if (creators.anyMatch(employee -> isInSameOrgAndEditableCommunityCTMRole(employee, targetEmployeeId,
                    currentCareTeamRoleId, targetCareTeamRoleId))) {
                return true;
            }
        }

        if (byMatrixSelfClientRecord != null && permissionFilter.hasPermission(byMatrixSelfClientRecord)) {
            var employees = permissionFilter.getEmployees(byMatrixSelfClientRecord);
            var selfClientEmployeeRecords = findSelfClientRecordInCommunity(employees, community.getId());
            selfClientEmployeeRecords = filterByAffiliationType(selfClientEmployeeRecords, community, type);

            if (selfClientEmployeeRecords.anyMatch(employee -> isInSameOrgAndEditableCommunityCTMRole(employee, targetEmployeeId,
                    currentCareTeamRoleId, targetCareTeamRoleId))) {
                return true;
            }
        }

        return false;
    }

    private boolean isInSameOrgAndEditableCommunityCTMRole(Employee current, Long targetEmployeeId,
                                                           Long currentCareTeamRoleId, Long targetCareTeamRoleId) {
        if (!CareTeamSecurityService.ANY_TARGET_EMPLOYEE.equals(targetEmployeeId)) {
            var targetEmployeeOrg = employeeService.findEmployeeOrganizationId(targetEmployeeId);
            if (!current.getOrganizationId().equals(targetEmployeeOrg)) {
                return false;
            }
        }

        return isEditableCommunityCareTeamMemberRole(current, currentCareTeamRoleId, targetCareTeamRoleId);
    }

    private boolean isEditableCommunityCareTeamMemberRole(Employee current, Long currentCareTeamRoleId, Long targetCareTeamRoleId) {
        return careTeamRoleService.isEditableCommunityCareTeamMemberRole(current.getCareTeamRole(), currentCareTeamRoleId, targetCareTeamRoleId);
    }
}
