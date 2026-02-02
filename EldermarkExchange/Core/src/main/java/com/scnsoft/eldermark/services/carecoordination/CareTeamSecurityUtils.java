package com.scnsoft.eldermark.services.carecoordination;

import com.google.common.collect.Sets;
import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.CareTeamMember;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.ResidentCareTeamMember;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;

/**
 * @author phomal
 * Created on 2/26/2018.
 */
public class CareTeamSecurityUtils {

    private static final Set<CareTeamRoleCode> ADMINISTRATIVE_ROLES = EnumSet.of(
            CareTeamRoleCode.ROLE_COMMUNITY_ADMINISTRATOR,
            CareTeamRoleCode.ROLE_ADMINISTRATOR,
            CareTeamRoleCode.ROLE_SUPER_ADMINISTRATOR);

    public static boolean canEditNotificationSettings(ExchangeUserDetails authenticatedUser, Iterable<Long> employeeIds, Long patientId,
                                                       CareTeamMember ctm) {
        final boolean isResidentCareTeamMember = ctm instanceof ResidentCareTeamMember;
        if (isResidentCareTeamMember && !((ResidentCareTeamMember) ctm).getResidentId().equals(patientId)) {
            // record for merged patient is not editable
            return false;
        }
        // Super Administrator is allowed everything
        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            return true;
        }

        if (ctm == null) {
            return false;
        }

        final boolean affiliatedView = SecurityUtils.isAffiliatedView();

        for (Long employeeId : employeeIds) {
            final Set<GrantedAuthority> currentEmployeeAuthorities = authenticatedUser.getEmployeeAuthoritiesMap().get(employeeId);
            final boolean isAdministrator = SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.ADMINISTRATOR);
            final Long employeeDatabaseId = SecurityUtils.getAuthenticatedUser().getLinkedEmployeeById(employeeId).getDatabaseId();
            // Administrator is allowed to edit notification settings for members added by users from his/her organization
            if (isAdministrator && employeeDatabaseId.equals(ctm.getEmployee().getDatabaseId())) {
                return true;
            }

            final boolean isCommunityAdministrator = SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.COMMUNITY_ADMINISTRATOR);
            final Long employeeCommunityId = SecurityUtils.getAuthenticatedUser().getLinkedEmployeeById(employeeId).getCommunityId();
            // Community Administrator is allowed to edit notification settings for members added by users from his/her community
            if (isCommunityAdministrator && employeeCommunityId.equals(ctm.getEmployee().getCommunityId())) {
                return true;
            }

            if (affiliatedView && !isResidentCareTeamMember) {
                final Boolean canManageOrganizationCareTeam = SecurityUtils.hasAnyRole(currentEmployeeAuthorities,
                        CareTeamRoleCode.ROLES_CAN_EDIT_ORGANIZATION_PATIENT_CARE_TEAM_MEMBER);
                if (canManageOrganizationCareTeam && employeeDatabaseId.equals(ctm.getEmployee().getDatabaseId())) {
                    return true;
                }
            }
        }

        final Set<CareTeamRoleCode> filteredCareTeamRoleCodes = Sets.filter(SecurityUtils.getCareTeamRoleCodes(), not(in(ADMINISTRATIVE_ROLES)));
        return CareTeamRoleCode.isRoleAbleForEditing(filteredCareTeamRoleCodes, ctm.getCareTeamRole().getCode());
    }

    public static boolean canAddCtm(ExchangeUserDetails authenticatedUser, Iterable<Long> employeeIds, Long patientOrganizationId, Long patientCommunityId) {
        // Super Administrator is allowed everything
        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            return true;
        }

        for (Long employeeId : employeeIds) {
            final Set<GrantedAuthority> currentEmployeeAuthorities = authenticatedUser.getEmployeeAuthoritiesMap().get(employeeId);
            final boolean isAdministrator = SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.ADMINISTRATOR);
            final Long employeeDatabaseId = SecurityUtils.getAuthenticatedUser().getLinkedEmployeeById(employeeId).getDatabaseId();
            // Administrator is allowed to add new care team members for patients from his/her organization
            if (isAdministrator && patientOrganizationId.equals(employeeDatabaseId)) {
                return true;
            }

            final Long employeeCommunityId = SecurityUtils.getAuthenticatedUser().getLinkedEmployeeById(employeeId).getCommunityId();
            final Boolean isCommunityAdministrator = SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.COMMUNITY_ADMINISTRATOR);
            // Community Administrator is allowed to add new care team members for patients from his/her community
            if (isCommunityAdministrator && patientCommunityId.equals(employeeCommunityId)) {
                return true;
            }
        }

        final Set<CareTeamRoleCode> filteredCareTeamRoleCodes = Sets.filter(SecurityUtils.getCareTeamRoleCodes(), not(in(ADMINISTRATIVE_ROLES)));
        return CareTeamRoleCode.isRoleAbleForCreating(filteredCareTeamRoleCodes);
    }

    public static Collection<CareTeamRoleCode> getAllowedCareTeamRolesForEdit(ExchangeUserDetails authenticatedUser, Iterable<Long> employeeIds,
                                                                              Long patientId, CareTeamMember ctm) {
        final Set<CareTeamRoleCode> mergedRoles = EnumSet.noneOf(CareTeamRoleCode.class);

        final boolean isResidentCareTeamMember = ctm instanceof ResidentCareTeamMember;
        if (isResidentCareTeamMember && !((ResidentCareTeamMember) ctm).getResidentId().equals(patientId)) {
            // record for merged patient is not editable
            return Collections.emptyList();
        }
        // Super Administrator is allowed everything
        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            mergedRoles.addAll(CareTeamRoleCode.getRoleListAbleForEditing(CareTeamRoleCode.ROLE_SUPER_ADMINISTRATOR));
            return Sets.filter(mergedRoles, not(in(ADMINISTRATIVE_ROLES)));
        }

        if (ctm == null) {
            return Collections.emptyList();
        }

        for (Long employeeId : employeeIds) {
            final Set<GrantedAuthority> currentEmployeeAuthorities = authenticatedUser.getEmployeeAuthoritiesMap().get(employeeId);
            final boolean isAdministrator = SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.ADMINISTRATOR);
            final Long employeeDatabaseId = SecurityUtils.getAuthenticatedUser().getLinkedEmployeeById(employeeId).getDatabaseId();
            // Administrator is allowed to edit notification settings for members added by users from his/her organization
            if (isAdministrator && employeeDatabaseId.equals(ctm.getEmployee().getDatabaseId())) {
                mergedRoles.addAll(CareTeamRoleCode.getRoleListAbleForEditing(CareTeamRoleCode.ROLE_ADMINISTRATOR));
                break;
            }

            final boolean isCommunityAdministrator = SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.COMMUNITY_ADMINISTRATOR);
            final Long employeeCommunityId = SecurityUtils.getAuthenticatedUser().getLinkedEmployeeById(employeeId).getCommunityId();
            // Community Administrator is allowed to edit notification settings for members added by users from his/her community
            if (isCommunityAdministrator && employeeCommunityId.equals(ctm.getEmployee().getCommunityId())) {
                mergedRoles.addAll(CareTeamRoleCode.getRoleListAbleForEditing(CareTeamRoleCode.ROLE_COMMUNITY_ADMINISTRATOR));
                break;
            }

            final Set<CareTeamRoleCode> currentEmployeeCareTeamRoleCodes = SecurityUtils.getCareTeamRoleCodes(currentEmployeeAuthorities);
            mergedRoles.addAll(CareTeamRoleCode.getRoleListAbleForEditing(currentEmployeeCareTeamRoleCodes));
        }
        mergedRoles.removeAll(ADMINISTRATIVE_ROLES);
        return mergedRoles;
    }

    public static Collection<CareTeamRoleCode> getAllowedCareTeamRolesForCreate(ExchangeUserDetails authenticatedUser, Iterable<Long> employeeIds,
                                                                                Long patientOrganizationId, Long patientCommunityId) {
        final Set<CareTeamRoleCode> mergedRoles = EnumSet.noneOf(CareTeamRoleCode.class);

        // Super Administrator is allowed everything
        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            mergedRoles.addAll(CareTeamRoleCode.getRoleListAbleForEditing(CareTeamRoleCode.ROLE_SUPER_ADMINISTRATOR));
            return Sets.filter(mergedRoles, not(in(ADMINISTRATIVE_ROLES)));
        }

        for (Long employeeId : employeeIds) {
            final Set<GrantedAuthority> currentEmployeeAuthorities = authenticatedUser.getEmployeeAuthoritiesMap().get(employeeId);
            final boolean isAdministrator = SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.ADMINISTRATOR);
            final Long employeeDatabaseId = SecurityUtils.getAuthenticatedUser().getLinkedEmployeeById(employeeId).getDatabaseId();
            // Administrator is allowed to add new care team members for patients from his/her organization
            if (isAdministrator && patientOrganizationId.equals(employeeDatabaseId)) {
                mergedRoles.addAll(CareTeamRoleCode.getRoleListAbleForEditing(CareTeamRoleCode.ROLE_ADMINISTRATOR));
                break;
            }

            final Long employeeCommunityId = SecurityUtils.getAuthenticatedUser().getLinkedEmployeeById(employeeId).getCommunityId();
            final Boolean isCommunityAdministrator = SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.COMMUNITY_ADMINISTRATOR);
            // Community Administrator is allowed to add new care team members for patients from his/her community
            if (isCommunityAdministrator && patientCommunityId.equals(employeeCommunityId)) {
                mergedRoles.addAll(CareTeamRoleCode.getRoleListAbleForEditing(CareTeamRoleCode.ROLE_COMMUNITY_ADMINISTRATOR));
                break;
            }

            final Set<CareTeamRoleCode> currentEmployeeCareTeamRoleCodes = SecurityUtils.getCareTeamRoleCodes(currentEmployeeAuthorities);
            mergedRoles.addAll(CareTeamRoleCode.getRoleListAbleForEditing(currentEmployeeCareTeamRoleCodes));
        }
        mergedRoles.removeAll(ADMINISTRATIVE_ROLES);
        return mergedRoles;
    }
}
