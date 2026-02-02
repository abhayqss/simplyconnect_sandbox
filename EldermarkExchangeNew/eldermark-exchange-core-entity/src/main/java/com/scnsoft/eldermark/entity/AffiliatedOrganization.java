package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
@Table(name = "AffiliatedOrganizations")
public class AffiliatedOrganization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "primary_database_id", nullable = false)
    private Long primaryOrganizationId;

    @Column(name = "primary_organization_id")
    private Long primaryCommunityId;

    @Column(name = "affiliated_database_id", nullable = false)
    private Long affiliatedOrganizationId;

    @Column(name = "affiliated_organization_id")
    private Long affiliatedCommunityId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrimaryOrganizationId() {
        return primaryOrganizationId;
    }

    public void setPrimaryOrganizationId(Long primaryOrganizationId) {
        this.primaryOrganizationId = primaryOrganizationId;
    }

    public Long getPrimaryCommunityId() {
        return primaryCommunityId;
    }

    public void setPrimaryCommunityId(Long primaryCommunityId) {
        this.primaryCommunityId = primaryCommunityId;
    }

    public Long getAffiliatedOrganizationId() {
        return affiliatedOrganizationId;
    }

    public void setAffiliatedOrganizationId(Long affiliatedOrganizationId) {
        this.affiliatedOrganizationId = affiliatedOrganizationId;
    }

    public Long getAffiliatedCommunityId() {
        return affiliatedCommunityId;
    }

    public void setAffiliatedCommunityId(Long affiliatedCommunityId) {
        this.affiliatedCommunityId = affiliatedCommunityId;
    }
}
