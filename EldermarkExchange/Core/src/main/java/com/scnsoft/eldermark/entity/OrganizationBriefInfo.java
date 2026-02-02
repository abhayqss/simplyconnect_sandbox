package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Organization")
public class OrganizationBriefInfo {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "database_id", nullable = false, insertable = false, updatable = false)
    private long databaseId;

    @Column(name = "legacy_table", nullable = false)
    private String legacyTable;

    @Column(columnDefinition = "nvarchar(255)")
    private String name;


    @Column(name = "testing_training")
    private Boolean isTestingTraining;

    @Column(name = "inactive")
    private Boolean isInactive;

    @Column(name = "module_hie")
    private Boolean moduleHie;

    @Column(name = "module_cloud_storage")
    private Boolean moduleCloudStorage;


    @Column(name = "oid")
    private String oid;

    @Column(name = "created_automatically")
    private Boolean createdAutomatically;

    @Column(name = "main_logo_path")
    private String mainLogoPath;

    @Column(name = "additional_logo_path")
    private String additionalLogoPath;

    @Column(name = "last_modified")
    private Date lastModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(long databaseId) {
        this.databaseId = databaseId;
    }

    public String getLegacyTable() {
        return legacyTable;
    }

    public void setLegacyTable(String legacyTable) {
        this.legacyTable = legacyTable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsTestingTraining() {
        return isTestingTraining;
    }

    public void setIsTestingTraining(Boolean isTestingTraining) {
        this.isTestingTraining = isTestingTraining;
    }

    public Boolean getIsInactive() {
        return isInactive;
    }

    public void setIsInactive(Boolean isInactive) {
        this.isInactive = isInactive;
    }

    public Boolean getModuleHie() {
        return moduleHie;
    }

    public void setModuleHie(Boolean moduleHie) {
        this.moduleHie = moduleHie;
    }

    public Boolean getModuleCloudStorage() {
        return moduleCloudStorage;
    }

    public void setModuleCloudStorage(Boolean moduleCloudStorage) {
        this.moduleCloudStorage = moduleCloudStorage;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public Boolean getCreatedAutomatically() {
        return createdAutomatically;
    }

    public void setCreatedAutomatically(Boolean createdAutomatically) {
        this.createdAutomatically = createdAutomatically;
    }

    public String getMainLogoPath() {
        return mainLogoPath;
    }

    public void setMainLogoPath(String mainLogoPath) {
        this.mainLogoPath = mainLogoPath;
    }

    public String getAdditionalLogoPath() {
        return additionalLogoPath;
    }

    public void setAdditionalLogoPath(String additionalLogoPath) {
        this.additionalLogoPath = additionalLogoPath;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}
