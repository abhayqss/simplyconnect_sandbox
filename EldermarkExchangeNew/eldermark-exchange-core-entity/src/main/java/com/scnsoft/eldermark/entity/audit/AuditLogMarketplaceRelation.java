package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.community.Community;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_Marketplace")
public class AuditLogMarketplaceRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "marketplace_community_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Community marketplaceCommunity;

    @Column(name = "marketplace_community_id", nullable = false)
    private Long marketplaceCommunityId;

    public Community getMarketplaceCommunity() {
        return marketplaceCommunity;
    }

    public void setMarketplaceCommunity(Community marketplaceCommunity) {
        this.marketplaceCommunity = marketplaceCommunity;
    }

    public Long getMarketplaceCommunityId() {
        return marketplaceCommunityId;
    }

    public void setMarketplaceCommunityId(Long marketplaceCommunityId) {
        this.marketplaceCommunityId = marketplaceCommunityId;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(marketplaceCommunityId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.MARKETPLACE;
    }
}
