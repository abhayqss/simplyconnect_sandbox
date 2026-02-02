package com.scnsoft.eldermark.entity.hieconsentpolicy;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.basic.AuditableEntity;
import com.scnsoft.eldermark.entity.community.Community;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "OrganizationHieConsentPolicy")
public class CommunityHieConsentPolicy extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Community community;

    @Column(name = "organization_id", nullable = false, updatable = false, insertable = false)
    private Long communityId;

    @Column(name = "creator_id", updatable = false, insertable = false)
    private Long creatorId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private Employee creator;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private HieConsentPolicyType type;

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

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long updatedByEmployeeId) {
        this.creatorId = updatedByEmployeeId;
    }

    public Employee getCreator() {
        return creator;
    }

    public void setCreator(Employee creator) {
        this.creator = creator;
    }

    public HieConsentPolicyType getType() {
        return type;
    }

    public void setType(HieConsentPolicyType hieConsentPolicyType) {
        this.type = hieConsentPolicyType;
    }
}
