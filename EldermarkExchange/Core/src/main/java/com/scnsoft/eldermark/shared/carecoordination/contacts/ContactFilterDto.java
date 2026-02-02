package com.scnsoft.eldermark.shared.carecoordination.contacts;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by pzhurba on 29-Oct-15.
 */
public class ContactFilterDto implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private Long roleId;
    private Integer status;
    private Set<Long> employeeIds;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Set<Long> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(Set<Long> employeeIds) {
        this.employeeIds = employeeIds;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
