package com.scnsoft.eldermark.dump.entity;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public class BasicEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "database_id", nullable = false, insertable = true, updatable = true)
    private Organization organization;

    @Column(name = "database_id", nullable = false, insertable = false, updatable = false)
    private long organizationId;

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

    public void setOrganization(Organization community) {
        this.organization = community;
    }

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationAlternativeId() {
        return organization.getAlternativeId();
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
