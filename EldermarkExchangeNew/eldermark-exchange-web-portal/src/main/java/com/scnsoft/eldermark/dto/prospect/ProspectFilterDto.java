package com.scnsoft.eldermark.dto.prospect;

import java.util.List;

public class ProspectFilterDto {

    private List<Long> communityIds;
    private Long organizationId;
    private String firstName;
    private String lastName;
    private Long genderId;
    private String birthDate;
    private ProspectStatus prospectStatus;

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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public ProspectStatus getProspectStatus() {
        return prospectStatus;
    }

    public void setProspectStatus(ProspectStatus prospectStatus) {
        this.prospectStatus = prospectStatus;
    }
}
