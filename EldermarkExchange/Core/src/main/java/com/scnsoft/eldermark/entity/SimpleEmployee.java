package com.scnsoft.eldermark.entity;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Entity
@Table(name = "Employee")
@Immutable
public class SimpleEmployee extends BasicEntity {

    @Column(name = "first_name", columnDefinition = "nvarchar(50)")
    private String firstName;

    @Column(name = "last_name", columnDefinition = "nvarchar(50)")
    private String lastName;

    @Column(name = "ccn_community_id")
    private Long communityId;

    @Column(name = "inactive", nullable = false)
    private EmployeeStatus status;

    public String getFullName() {
        String result = StringUtils.isNotEmpty(getFirstName()) ? getFirstName() : "";
        if (StringUtils.isNotEmpty(getLastName())) {
            result = StringUtils.isNotEmpty(result) ? result + " " + getLastName() : getLastName();
        }
        return result;
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

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }
}
