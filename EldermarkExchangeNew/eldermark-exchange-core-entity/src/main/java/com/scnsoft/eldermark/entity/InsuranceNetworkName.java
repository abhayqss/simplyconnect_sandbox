package com.scnsoft.eldermark.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "InsuranceNetworkName")
public class InsuranceNetworkName {

    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "database_id", insertable = false, updatable = false)
    private Long organizationId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}