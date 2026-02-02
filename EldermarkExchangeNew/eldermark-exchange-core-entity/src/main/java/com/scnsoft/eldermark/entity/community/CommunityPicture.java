package com.scnsoft.eldermark.entity.community;

import com.scnsoft.eldermark.entity.BaseAttachment;

import javax.persistence.*;

@Entity
@Table(name = "CommunityPicture")
public class CommunityPicture extends BaseAttachment {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", referencedColumnName = "id", nullable = false)
    private Community community;

    @Column(name = "community_id", insertable = false, updatable = false, nullable = false)
    private Long communityId;

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
