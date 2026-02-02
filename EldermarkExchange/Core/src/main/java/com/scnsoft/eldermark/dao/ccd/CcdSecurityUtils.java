package com.scnsoft.eldermark.dao.ccd;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.shared.carecoordination.contacts.LinkedContactDto;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Set;

/**
 * @author sparuchnik
 * Created on 5/21/2018.
 */
public class CcdSecurityUtils {


    public static boolean canDeleteCcd(ExchangeUserDetails authenticatedUser, Iterable<Long> employeeIds, List<Resident> residents) {
        //the same requirements
        return canViewCcd(authenticatedUser, employeeIds, residents);
    }

    public static boolean canEditCcd(ExchangeUserDetails authenticatedUser, Iterable<Long> employeeIds, List<Resident> residents) {
        //the same requirements
        return canViewCcd(authenticatedUser, employeeIds, residents);
    }

    public static boolean canAddCcd(ExchangeUserDetails authenticatedUser, Iterable<Long> employeeIds, List<Resident> residents) {
        //the same requirements
        return canViewCcd(authenticatedUser, employeeIds, residents);
    }

    public static boolean canViewCcd(ExchangeUserDetails authenticatedUser, Iterable<Long> employeeIds, List<Resident> residents) {
        // Super Administrator is allowed everything
        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            return true;
        }

        // no access to affiliated organisation
        if (SecurityUtils.isAffiliatedView()) {
            return false;
        }

        for (Long employeeId : employeeIds) {
            final LinkedContactDto linkedEmployee = SecurityUtils.getAuthenticatedUser().getLinkedEmployeeById(employeeId);
            final Set<GrantedAuthority> currentEmployeeAuthorities = authenticatedUser.getEmployeeAuthoritiesMap().get(employeeId);
            final boolean isAdministrator = SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.ADMINISTRATOR);
            final Long employeeDatabaseId = linkedEmployee.getDatabaseId();

            // Administrator is allowed to view ccd info for members added by users from his/her organization
            if (isAdministrator) {
                for (Resident resident: residents) {
                    if (employeeDatabaseId.equals(resident.getDatabaseId())) {
                        return true;
                    }
                }
            }

            // Community Administrator is allowed to view ccd info for members added by users from his/her community
            final boolean isCommunityAdministrator = SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.COMMUNITY_ADMINISTRATOR);
            final Long employeeCommunityId = linkedEmployee.getCommunityId();
            if (isCommunityAdministrator) {
                for (Resident resident: residents) {
                    if (employeeCommunityId.equals(resident.getFacility().getId())) {
                        return true;
                    }
                }
            }

            //ordinary users must have specific role...
            if (!SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_CAN_VIEW_CCD_SECTION)) {
                return false;
            }

            //... and access to resident info (same rules as for viewing resident in list)
            //TODO apply this rules here. Refactoring of resident code is required.
            return true;
        }

        return false;
    }
}
