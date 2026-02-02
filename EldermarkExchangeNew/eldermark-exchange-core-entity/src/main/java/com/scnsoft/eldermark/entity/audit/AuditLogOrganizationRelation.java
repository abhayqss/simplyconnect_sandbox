package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.Organization;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_Database")
public class AuditLogOrganizationRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "database_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Organization organization;

    @Column(name = "database_id", nullable = false)
    private Long organizationId;

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
    public List<Long> getRelatedIds() {
        return List.of(organizationId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.ORGANIZATION;
    }
}
