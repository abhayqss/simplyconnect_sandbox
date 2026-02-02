package com.scnsoft.eldermark.entity;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@javax.persistence.Table(
        indexes = {
                @Index(name = "IX_organization_legacytable_inactive_modulehie_training", columnList = "legacy_table, testing_training, inactive, module_hie")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = {"legacy_id", "database_id", "legacy_table"}))
//@Cacheable
//@org.hibernate.annotations.Cache(usage= CacheConcurrencyStrategy.READ_WRITE, region="community")
public class Organization extends StringLegacyTableAwareEntity implements Comparable<Organization> {

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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "organization")
    private List<OrganizationAddress> addresses;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "organization")
    private OrganizationTelecom telecom;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "organization", fetch = FetchType.LAZY)
    private List<OrganizationCareTeamMember> organizationCareTeamMembers;

    @JoinColumn(name = "interfax_config_id", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private InterfaxConfiguration interfaxConfiguration;

    @Column(name = "oid")
    private String oid;

    @Column(name = "created_automatically")
    private Boolean createdAutomatically;

    // We are using accessor type on properties
    // so we put @Transient annotation to tell JPA not to use these fields for storing
    @Transient
    private String email;

    @Transient
    private String phone;

    @Column(name = "main_logo_path")
    private String mainLogoPath;

    @Column(name = "additional_logo_path")
    private String additionalLogoPath;

    @Column(name = "external_logo_id")
    private String externalLogoId;

    @Column(name = "last_modified")
    private Date lastModified;

    @Column(name = "is_ir_enabled")
    private Boolean irEnabled;

    @Column(name = "is_consana_enabled", nullable = false)
    @ColumnDefault("0")
    private boolean isConsanaIntegrationEnabled;

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

    public List<OrganizationAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<OrganizationAddress> addresses) {
        this.addresses = addresses;
    }

    public OrganizationTelecom getTelecom() {
        return telecom;
    }

    public void setTelecom(OrganizationTelecom telecom) {
        this.telecom = telecom;
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

    public InterfaxConfiguration getInterfaxConfiguration() {
        return interfaxConfiguration;
    }

    public void setInterfaxConfiguration(InterfaxConfiguration interfaxConfiguration) {
        this.interfaxConfiguration = interfaxConfiguration;
    }

    public List<OrganizationCareTeamMember> getOrganizationCareTeamMembers() {
        return organizationCareTeamMembers;
    }

    public void setOrganizationCareTeamMembers(List<OrganizationCareTeamMember> organizationCareTeamMembers) {
        this.organizationCareTeamMembers = organizationCareTeamMembers;
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

    @Access(AccessType.PROPERTY)
    @Column
    public String getEmail() {
        if (this.email != null) {
            return this.email;
        }
        String email = null;
        OrganizationTelecom telecom = getTelecom();
        if (telecom != null && PersonTelecomCode.EMAIL.name().equals(telecom.getUseCode())) {
            email = telecom.getValue();
        }
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

    public String getExternalLogoId() {
        return externalLogoId;
    }

    public void setExternalLogoId(String extenalLogoId) {
        this.externalLogoId = extenalLogoId;
    }

    public Boolean getIrEnabled() {
        return irEnabled;
    }

    public void setIrEnabled(Boolean irEnabled) {
        this.irEnabled = irEnabled;
    }

    @Access(AccessType.PROPERTY)
    @Column
    public String getPhone() {
        if (this.phone != null) {
            return this.phone;
        }
        String phone = null;
        OrganizationTelecom telecom = getTelecom();
        if (telecom != null && PersonTelecomCode.WP.name().equals(telecom.getUseCode())) {
            phone = telecom.getValue();
        }
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public boolean getConsanaIntegrationEnabled() {
        return isConsanaIntegrationEnabled;
    }

    public void setConsanaIntegrationEnabled(boolean consanaIntegrationEnabled) {
        isConsanaIntegrationEnabled = consanaIntegrationEnabled;
    }

    @Override
    public int compareTo(Organization o) {
        return ObjectUtils.compare(this.getName(), o.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Organization that = (Organization) o;

        return new EqualsBuilder()
                .append(getId(), that.getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getId())
                .toHashCode();
    }

}
