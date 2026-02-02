package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.community.Community;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_Organization")
public class AuditLogCommunityRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "organization_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Community community;

    @Column(name = "organization_id", nullable = false)
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

    @Override
    public List<Long> getRelatedIds() {
        return List.of(communityId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.COMMUNITY;
    }
}
