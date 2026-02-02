package com.scnsoft.eldermark.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by knetkachou on 3/10/2017.
 */
@Entity
public class AffiliatedOrganizations {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "primary_organization_id")
    private Long primaryOrganizationId;

    @Column(name = "primary_database_id")
    private Long primaryDatabaseId;

    @Column(name = "affiliated_organization_id")
    private Long affiliatedOrganizationId;

    @Column(name = "affiliated_database_id")
    private Long affiliatedDatabaseId;

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

    public Long getPrimaryDatabaseId() {
        return primaryDatabaseId;
    }

    public void setPrimaryDatabaseId(Long primaryDatabaseId) {
        this.primaryDatabaseId = primaryDatabaseId;
    }

    public Long getAffiliatedOrganizationId() {
        return affiliatedOrganizationId;
    }

    public void setAffiliatedOrganizationId(Long affiliatedOrganizationId) {
        this.affiliatedOrganizationId = affiliatedOrganizationId;
    }

    public Long getAffiliatedDatabaseId() {
        return affiliatedDatabaseId;
    }

    public void setAffiliatedDatabaseId(Long affiliatedDatabaseId) {
        this.affiliatedDatabaseId = affiliatedDatabaseId;
    }
}
