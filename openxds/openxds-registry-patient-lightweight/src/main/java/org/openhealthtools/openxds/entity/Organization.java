package org.openhealthtools.openxds.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
// when changing XDS default community it is necessary to set previous default oid as assigning facility in MPI and postgres registry
// (table externalIdentifier with identification scheme identificationScheme='urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427')
// for default entries.
public class Organization extends StringLegacyIdAwareEntity {
    @Column(name = "legacy_table", nullable = false)
    private String legacyTable;

    @Column(columnDefinition = "nvarchar(255)")
    private String name;

    @Column(name = "logo_pict_id")
    private Long logoPictId;

    @Column(name = "provider_npi")
    private String providerNpi;

    @Column(name = "sales_region", length = 20)
    private String salesRegion;

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

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "main_logo_path")
    private String mainLogoPath;

    @Column(name = "additional_logo_path")
    private String additionalLogoPath;

    @Column(name = "external_logo_id")
    private String extenalLogoId;

    @Column(name = "is_xds_default")
    private Boolean isXdsDefault;

    public Boolean getXdsDefault() {
        return isXdsDefault;
    }

    public void setXdsDefault(Boolean xdsDefault) {
        isXdsDefault = xdsDefault;
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

    public String getProviderNpi() {
        return providerNpi;
    }

    public void setProviderNpi(String providerNpi) {
        this.providerNpi = providerNpi;
    }

    public Long getLogoPictId() {
        return logoPictId;
    }

    public void setLogoPictId(Long logoPictId) {
        this.logoPictId = logoPictId;
    }

    public String getSalesRegion() {
        return salesRegion;
    }

    public void setSalesRegion(String salesRegion) {
        this.salesRegion = salesRegion;
    }

    public Boolean getTestingTraining() {
        return isTestingTraining;
    }

    public void setTestingTraining(Boolean testingTraining) {
        isTestingTraining = testingTraining;
    }

    public Boolean getInactive() {
        return isInactive;
    }

    public void setInactive(Boolean inactive) {
        isInactive = inactive;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getExtenalLogoId() {
        return extenalLogoId;
    }

    public void setExtenalLogoId(String extenalLogoId) {
        this.extenalLogoId = extenalLogoId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
