package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.EmployeeStatus;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

public class ContactNameFilter {

    @NotEmpty
    private Set<Long> organizationIds;

    private Set<Long> communityIds;

    private String name;

    private List<CareTeamRoleCode> roles;

    private List<EmployeeStatus> statuses;

    public Set<Long> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(Set<Long> organizationIds) {
        this.organizationIds = organizationIds;
    }

    public Set<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(Set<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CareTeamRoleCode> getRoles() {
        return roles;
    }

    public void setRoles(List<CareTeamRoleCode> roles) {
        this.roles = roles;
    }

    public List<EmployeeStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<EmployeeStatus> statuses) {
        this.statuses = statuses;
    }

}
