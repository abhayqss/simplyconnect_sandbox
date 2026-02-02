package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.community.Community;

import javax.persistence.*;

@Entity
@Table(name = "AffiliatedRelationship")
public class AffiliatedRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "primary_database_id", nullable = false)
    private Organization primaryOrganization;

    @Column(name = "primary_database_id", nullable = false, insertable = false, updatable = false)
    private Long primaryOrganizationId;

    @ManyToOne
    @JoinColumn(name = "primary_organization_id")
    private Community primaryCommunity;

    @Column(name = "primary_organization_id", nullable = false, insertable = false, updatable = false)
    private Long primaryCommunityId;

    @ManyToOne
    @JoinColumn(name = "affiliated_database_id", nullable = false)
    private Organization affiliatedOrganization;

    @Column(name = "affiliated_database_id", nullable = false, insertable = false, updatable = false)
    private Long affiliatedOrganizationId;

    @ManyToOne
    @JoinColumn(name = "affiliated_organization_id")
    private Community affiliatedCommunity;

    @Column(name = "affiliated_organization_id", nullable = false, insertable = false, updatable = false)
    private Long affiliatedCommunityId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Organization getPrimaryOrganization() {
        return primaryOrganization;
    }

    public void setPrimaryOrganization(Organization primaryOrganization) {
        this.primaryOrganization = primaryOrganization;
    }

    public Long getPrimaryOrganizationId() {
        return primaryOrganizationId;
    }

    public void setPrimaryOrganizationId(Long primaryOrganizationId) {
        this.primaryOrganizationId = primaryOrganizationId;
    }

    public Community getPrimaryCommunity() {
        return primaryCommunity;
    }

    public void setPrimaryCommunity(Community primaryCommunity) {
        this.primaryCommunity = primaryCommunity;
    }

    public Long getPrimaryCommunityId() {
        return primaryCommunityId;
    }

    public void setPrimaryCommunityId(Long primaryCommunityId) {
        this.primaryCommunityId = primaryCommunityId;
    }

    public Organization getAffiliatedOrganization() {
        return affiliatedOrganization;
    }

    public void setAffiliatedOrganization(Organization affiliatedOrganization) {
        this.affiliatedOrganization = affiliatedOrganization;
    }

    public Long getAffiliatedOrganizationId() {
        return affiliatedOrganizationId;
    }

    public void setAffiliatedOrganizationId(Long affiliatedOrganizationId) {
        this.affiliatedOrganizationId = affiliatedOrganizationId;
    }

    public Community getAffiliatedCommunity() {
        return affiliatedCommunity;
    }

    public void setAffiliatedCommunity(Community affiliatedCommunity) {
        this.affiliatedCommunity = affiliatedCommunity;
    }

    public Long getAffiliatedCommunityId() {
        return affiliatedCommunityId;
    }

    public void setAffiliatedCommunityId(Long affiliatedCommunityId) {
        this.affiliatedCommunityId = affiliatedCommunityId;
    }
}
