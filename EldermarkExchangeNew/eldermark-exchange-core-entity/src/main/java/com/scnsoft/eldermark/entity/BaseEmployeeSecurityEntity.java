package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.basic.StringLegacyIdAwareEntity;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.community.Community;
import org.hibernate.annotations.Nationalized;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseEmployeeSecurityEntity extends StringLegacyIdAwareEntity {

    @Column(name = "first_name", nullable = false, columnDefinition = "nvarchar(256)")
    @Nationalized
    private String firstName;

    @Column(name = "last_name", nullable = false, columnDefinition = "nvarchar(256)")
    @Nationalized
    private String lastName;

    @Column(name = "login", nullable = false, columnDefinition = "nvarchar(256)")
    @Nationalized
    private String loginName;

    @ManyToOne
    @JoinColumn(name = "ccn_community_id")
    private Community community;

    @Column(name = "ccn_community_id", insertable = false, updatable = false)
    private Long communityId;

    @Column(name = "inactive", nullable = false)
    private EmployeeStatus status;

    @JoinColumn(name = "care_team_role_id", referencedColumnName = "id")
    @ManyToOne
    private CareTeamRole careTeamRole;

    @ManyToOne
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private Employee creator;

    @Column(name = "creator_id", insertable = false, updatable = false)
    private Long creatorId;

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

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
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

    public CareTeamRole getCareTeamRole() {
        return careTeamRole;
    }

    public void setCareTeamRole(CareTeamRole careTeamRole) {
        this.careTeamRole = careTeamRole;
    }

    public Employee getCreator() {
        return creator;
    }

    public void setCreator(Employee creator) {
        this.creator = creator;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
}
