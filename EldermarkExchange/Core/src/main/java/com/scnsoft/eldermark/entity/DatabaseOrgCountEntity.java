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
    private Long databaseId;

    @Column(name = "org_count")
    private Long orgCount;

    @Column(name = "org_hie_count")
    private Long orgHieCount;

    @Column(name = "org_cloud_count")
    private Long orgCloudCount;

    @Column(name = "org_hie_or_cloud_count")
    private Long orgHieOrCloudCount;

    @Column(name = "affiliated_org_count")
    private Long affiliatedOrgCount;

    public Long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Long databaseId) {
        this.databaseId = databaseId;
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
