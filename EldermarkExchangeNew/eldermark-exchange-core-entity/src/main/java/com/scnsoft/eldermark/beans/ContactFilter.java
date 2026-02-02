package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.entity.EmployeeStatus;

import java.util.List;
import java.util.Set;

public class ContactFilter {

    private Long clientId;  //todo delete?

    private Long employeeId;    //todo delete?

    private Long communityId;   //todo delete?

    private Long careTeamMemberId; //todo delete?

    private Boolean affiliated;  //todo delete?

    private Long organizationId;

    private List<Long> communityIds;

    private Boolean excludeWithoutCommunity;

    private String firstName;

    private String lastName;

    private String email;

    private Set<Long> systemRoleIds;

    private Boolean includeWithoutSystemRole;

    private Set<EmployeeStatus> statuses;

    public Long getOrganizationId() {
        return organizationId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Set<Long> getSystemRoleIds() {
        return systemRoleIds;
    }

    public Set<EmployeeStatus> getStatuses() {
        return statuses;
    }

    public List<Long> getCommunityIds() {
        return communityIds;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSystemRoleIds(Set<Long> systemRoleIds) {
        this.systemRoleIds = systemRoleIds;
    }

    public void setStatuses(Set<EmployeeStatus> statuses) {
        this.statuses = statuses;
    }

    public void setCommunityIds(List<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public Boolean getExcludeWithoutCommunity() {
        return excludeWithoutCommunity;
    }

    public void setExcludeWithoutCommunity(Boolean excludeWithoutCommunity) {
        this.excludeWithoutCommunity = excludeWithoutCommunity;
    }

    public Boolean getIncludeWithoutSystemRole() {
        return includeWithoutSystemRole;
    }

    public void setIncludeWithoutSystemRole(Boolean includeWithoutSystemRole) {
        this.includeWithoutSystemRole = includeWithoutSystemRole;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Long getCareTeamMemberId() {
        return careTeamMemberId;
    }

    public void setCareTeamMemberId(Long careTeamMemberId) {
        this.careTeamMemberId = careTeamMemberId;
    }

    public Boolean getAffiliated() {
        return affiliated;
    }

    public void setAffiliated(Boolean affiliated) {
        this.affiliated = affiliated;
    }
}
