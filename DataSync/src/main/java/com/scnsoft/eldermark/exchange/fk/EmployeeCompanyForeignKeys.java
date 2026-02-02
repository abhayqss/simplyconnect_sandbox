package com.scnsoft.eldermark.exchange.fk;

import java.util.HashSet;
import java.util.Set;

public class EmployeeCompanyForeignKeys {
    private Long organizationId;
    private Long employeeId;
    private Set<Long> employeeRoleIds;
    private Set<Long> employeeGroupIds;

    public EmployeeCompanyForeignKeys() {
        employeeGroupIds = new HashSet<Long>();
        employeeRoleIds = new HashSet<Long>();
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Set<Long> getEmployeeRoleIds() {
        return employeeRoleIds;
    }

    public void addEmployeeRoleId(Long roleId) {
        if (roleId != null) {
            employeeRoleIds.add(roleId);
        }
    }
    public Set<Long> getEmployeeGroupIds() {
        return employeeGroupIds;
    }

    public void addEmployeeGroupId(Long groupId) {
        if (groupId != null) {
            employeeGroupIds.add(groupId);
        }
    }
}
