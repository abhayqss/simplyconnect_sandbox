package com.scnsoft.scansol.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.scnsoft.scansol.shared.response.ScanSolResponseBase;

import java.util.List;

/**
 * Date: 14.05.15
 * Time: 10:00
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ScanSolEmployeeInfoDto extends ScanSolResponseBase {
    private ScanSolBaseEmployeeInfoDto employee;
    private List<ScanSolRoleDto> roles;
    @JsonProperty("communities")
    private List<ScanSolOrganizationDto> organizations;
    private ScanSolDatabaseDto company;

    public ScanSolBaseEmployeeInfoDto getEmployee () {
        return employee;
    }

    public void setEmployee (ScanSolBaseEmployeeInfoDto employee) {
        this.employee = employee;
    }

    public List<ScanSolOrganizationDto> getOrganizations () {
        return organizations;
    }

    public void setOrganizations (List<ScanSolOrganizationDto> organizations) {
        this.organizations = organizations;
    }

    public ScanSolDatabaseDto getCompany() {
        return company;
    }

    public void setCompany(ScanSolDatabaseDto company) {
        this.company = company;
    }

    public List<ScanSolRoleDto> getRoles() {
        return roles;
    }

    public void setRoles(List<ScanSolRoleDto> roles) {
        this.roles = roles;
    }
}
