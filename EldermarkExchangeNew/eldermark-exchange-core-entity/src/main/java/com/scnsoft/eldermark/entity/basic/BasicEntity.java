package com.scnsoft.eldermark.entity.basic;

import com.scnsoft.eldermark.beans.projection.OrganizationIdAware;
import com.scnsoft.eldermark.entity.Organization;

import javax.persistence.*;
import java.io.Serializable;

/**
 * This abstract class is the base class for all Entities in Eldermark Exchange
 * Web application.
 */
@MappedSuperclass
public abstract class BasicEntity implements Serializable, OrganizationIdAware {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "database_id", nullable = false, insertable = true, updatable = true)
    private Organization organization;

    @Column(name = "database_id", nullable = false, insertable = false, updatable = false)
    private Long organizationId;

    public BasicEntity() {
    }

    public BasicEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[id=").append(id);
        if (organization != null) {
            sb.append(", organizationId=").append(organization.getId());
        }
        sb.append("]");
        return sb.toString();
    }

}
