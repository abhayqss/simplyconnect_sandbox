package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.careteam.CareTeamMember;
import com.scnsoft.eldermark.entity.community.Community;

import javax.persistence.*;

@Entity
@Table(name = "OrganizationCareTeamMember")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
public class CommunityCareTeamMember extends CareTeamMember {
    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "organization_id", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Community community;

    @Column(name = "organization_id", nullable = false, insertable = false, updatable = false)
    private Long communityId;

    public CommunityCareTeamMember() {
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
}
