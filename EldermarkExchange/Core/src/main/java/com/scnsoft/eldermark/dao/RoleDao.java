package com.scnsoft.eldermark.dao;


import com.scnsoft.eldermark.entity.Role;
import com.scnsoft.eldermark.entity.RoleCode;

import java.util.List;

public interface RoleDao {
    List<Role> getEmployeeRoles(long employeeId);

    List<Role> getEmployeeGroupRoles(long employeeId);

    List<Role> getEmployeeOrganizationRoles(long employeeId);

    List<Role> getEmployeeOrganizationGroupRoles(long employeeId);

    boolean hasRole(long employeeId, RoleCode role);

    Role findByCode(RoleCode code);
}
