package com.scnsoft.eldermark.dto.prospect;

import com.scnsoft.eldermark.beans.security.PermissionFilter;

import java.time.LocalDate;
import java.util.List;

public class ProspectFilter {

    private List<Long> communityIds;
    private Long organizationId;
    private String firstName;
    private String lastName;
    private Long genderId;
    private LocalDate birthDate;
    private ProspectStatus prospectStatus;
    private PermissionFilter permissionFilter;

    public List<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(List<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

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

    public Long getGenderId() {
        return genderId;
    }

    public void setGenderId(Long genderId) {
        this.genderId = genderId;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public ProspectStatus getProspectStatus() {
        return prospectStatus;
    }

    public void setProspectStatus(ProspectStatus prospectStatus) {
        this.prospectStatus = prospectStatus;
    }

    public PermissionFilter getPermissionFilter() {
        return permissionFilter;
    }

    public void setPermissionFilter(PermissionFilter permissionFilter) {
        this.permissionFilter = permissionFilter;
    }
}
