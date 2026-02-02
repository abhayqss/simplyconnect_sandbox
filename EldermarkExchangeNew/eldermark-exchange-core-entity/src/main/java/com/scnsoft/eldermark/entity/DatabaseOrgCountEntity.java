package com.scnsoft.eldermark.entity;

/**
 * Created by averazub on 4/14/2016.
 */

import javax.persistence.*;

@Entity
@Table(name = "database_org_count")
public class DatabaseOrgCountEntity {
    @Id
    @Column(name = "database_id")
    private Long organizationId;

    @Column(name = "org_count", columnDefinition = "int")
    private Long orgCount;

    @Column(name = "org_hie_count", columnDefinition = "int")
    private Long orgHieCount;

    @Column(name = "org_cloud_count", columnDefinition = "int")
    private Long orgCloudCount;

    @Column(name = "org_hie_or_cloud_count", columnDefinition = "int")
    private Long orgHieOrCloudCount;

    @Column(name = "affiliated_org_count", columnDefinition = "int")
    private Long affiliatedOrgCount;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getOrgCount() {
        return orgCount;
    }

    public void setOrgCount(Long orgCount) {
        this.orgCount = orgCount;
    }

    public Long getOrgHieCount() {
        return orgHieCount;
    }

    public void setOrgHieCount(Long orgHieCount) {
        this.orgHieCount = orgHieCount;
    }

    public Long getOrgCloudCount() {
        return orgCloudCount;
    }

    public void setOrgCloudCount(Long orgCloudCount) {
        this.orgCloudCount = orgCloudCount;
    }

    public Long getOrgHieOrCloudCount() {
        return orgHieOrCloudCount;
    }

    public void setOrgHieOrCloudCount(Long orgHieOrCloudCount) {
        this.orgHieOrCloudCount = orgHieOrCloudCount;
    }

    public Long getAffiliatedOrgCount() {
        return affiliatedOrgCount;
    }

    public void setAffiliatedOrgCount(Long affiliatedOrgCount) {
        this.affiliatedOrgCount = affiliatedOrgCount;
    }
}
