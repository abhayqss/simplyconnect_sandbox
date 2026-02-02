package com.scnsoft.eldermark.api.shared.dto;

/**
 * Created by pzhurba on 05-Oct-15.
 */
public class EmployeeDto extends NameDto {

    private Long roleId;
    private String role;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
