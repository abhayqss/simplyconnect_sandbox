package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.community.Community;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "PartnerNetworkCommunity")
public class PartnerNetworkCommunity {

    @EmbeddedId
    private Id id;

    @Column(name = "partner_network_id", nullable = false, insertable = false, updatable = false)
    private Long partnerNetworkId;

    @ManyToOne
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Community community;

    @Column(name = "organization_id", nullable = false, insertable = false, updatable = false)
    private Long communityId;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Long getPartnerNetworkId() {
        return partnerNetworkId;
    }

    public void setPartnerNetworkId(Long partnerNetworkId) {
        this.partnerNetworkId = partnerNetworkId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "partner_network_id", nullable = false, insertable = false, updatable = false)
        private Long partnerNetworkId;

        @Column(name = "organization_id", nullable = false, insertable = false, updatable = false)
        private Long communityId;

        public Long getPartnerNetworkId() {
            return partnerNetworkId;
        }

        public void setPartnerNetworkId(Long partnerNetworkId) {
            this.partnerNetworkId = partnerNetworkId;
        }

        public Long getCommunityId() {
            return communityId;
        }

        public void setCommunityId(Long communityId) {
            this.communityId = communityId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return Objects.equals(partnerNetworkId, id.partnerNetworkId) &&
                    Objects.equals(communityId, id.communityId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(partnerNetworkId, communityId);
        }
    }

}
