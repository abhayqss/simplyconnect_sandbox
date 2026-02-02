package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.community.Community;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
public class FeaturedServiceProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "marketplace_id")
    private Long marketplaceId;

    @Column(name = "organization_id")
    private Long organizationId;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "community_id", insertable = false, updatable = false)
    private Long communityId;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private Community provider;

    @Column(name = "provider_id", insertable = false, updatable = false)
    private Long providerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMarketplaceId() {
        return marketplaceId;
    }

    public void setMarketplaceId(Long marketplaceId) {
        this.marketplaceId = marketplaceId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
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

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Community getProvider() {
        return provider;
    }

    public void setProvider(Community community) {
        this.provider = community;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long provider_id) {
        this.providerId = provider_id;
    }
}
