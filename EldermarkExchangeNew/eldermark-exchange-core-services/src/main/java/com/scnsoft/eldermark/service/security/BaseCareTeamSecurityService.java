package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.projection.OrganizationIdAware;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.CareTeamRoleService;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class BaseCareTeamSecurityService<T extends CareTeamMember, SEC extends OrganizationIdAware> extends BaseSecurityService {

    protected boolean hasSelfCareTeamRecordModifyingAccess(PermissionFilter permissionFilter,
                                                           T careTeamMember,
                                                           Predicate<Employee> visibleCareTeamChecker,
                                                           SEC memberSecurityProjection,
                                                           AffiliatedCareTeamType type,
                                                           Long targetCareTeamRoleId,
                                                           Permission visibleByMatrixPrimarySelfCareTeamRecord) {

        //role is not editable for self care team records:
        if (CareTeamRoleService.ANOTHER_TARGET_ROLE.equals(targetCareTeamRoleId)) {
            return false;
        }

        if (!CareTeamRoleService.ANY_TARGET_ROLE.equals(targetCareTeamRoleId) &&
                !careTeamMember.getCareTeamRole().getId().equals(targetCareTeamRoleId)) {
            return false;
        }

        if (type.isIncludesPrimary()) {
            if (visibleByMatrixPrimarySelfCareTeamRecord != null && permissionFilter.hasPermission(visibleByMatrixPrimarySelfCareTeamRecord)) {
                var employees = permissionFilter.getEmployees(visibleByMatrixPrimarySelfCareTeamRecord);
                var selfEmployee = findSelfEmployeeRecord(employees, careTeamMember.getEmployeeId());

                var allowedEmployee = selfEmployee.filter(e -> areInDifferentOrganizations(e, memberSecurityProjection));

                if (allowedEmployee.isPresent() && visibleCareTeamChecker.test(allowedEmployee.get())) {
                    return true;
                }
            }
        }

        return false;
    }

    protected Stream<Employee> filterByAffiliationType(Stream<Employee> employees, OrganizationIdAware ctm, AffiliatedCareTeamType type) {
        if (!type.isIncludesRegular()) {
            employees = employees.filter(employee -> areInDifferentOrganizations(employee, ctm));
        }
        if (!type.isIncludesPrimary()) {
            employees = employees.filter(employee -> !areInDifferentOrganizations(employee, ctm));
        }

        return employees;
    }
}
