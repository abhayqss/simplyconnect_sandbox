package com.scnsoft.eldermark.entity.community;

import com.scnsoft.eldermark.beans.projection.EligibleForDiscoveryAware;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.security.projection.entity.CommunitySecurityAwareEntity;
import com.scnsoft.eldermark.entity.Marketplace;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.basic.StringLegacyTableAwareEntity;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "Organization", indexes = {
        @Index(name = "IX_organization_legacytable_inactive_modulehie_training", columnList = "legacy_table, testing_training, inactive, module_hie")}, uniqueConstraints = @UniqueConstraint(columnNames = {
        "legacy_id", "database_id", "legacy_table"}))
public class Community extends StringLegacyTableAwareEntity implements Comparable<Community>,
        CommunitySecurityAwareEntity,
        EligibleForDiscoveryAware,
        IdNameAware {

    private static final long serialVersionUID = 1L;

    @Column(columnDefinition = "varchar(255)")
    private String name;

    @Column(name = "logo_pict_id")
    private Long logoPictId;

    @Column(name = "provider_npi")
    private String providerNpi;

    @Column(name = "sales_region", length = 20)
    private String salesRegion;

    @Column(name = "testing_training")
    private Boolean testingTraining;

    @Column(name = "inactive")
    private Boolean inactive;

    @Column(name = "module_hie")
    private Boolean moduleHie;

    @Column(name = "module_cloud_storage")
    private Boolean moduleCloudStorage;

    @Column(name = "is_sharing_data")
    private boolean isSharingData;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "community")
    private List<CommunityAddress> addresses;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "community")
    private CommunityTelecom telecom;

    @JoinColumn(name = "interfax_config_id", referencedColumnName = "id")
    @ManyToOne
    private InterfaxConfiguration interfaxConfiguration;

    @Column(name = "oid")
    private String oid;

    @Column(name = "created_automatically")
    private Boolean createdAutomatically;

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
    private Instant lastModified;

    @Column(name = "is_ir_enabled")
    private Boolean irEnabled;

    @Column(name = "receive_non_network_referrals")
    private boolean receiveNonNetworkReferrals;

    @OneToOne(mappedBy = "community")
    private Marketplace marketplace;

    @Column(name = "is_consana_enabled", nullable = false)
    @ColumnDefault("0")
    private boolean isConsanaIntegrationEnabled;

    @Column(name = "consana_org_id")
    private String consanaOrgId;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityPicture> pictures;

    @Column(name = "number_of_beds")
    private String numberOfBeds;

    @Column(name = "number_of_vacant_beds")
    private String numberOfVacantBeds;

    @Column(name = "is_docutrack_pharmacy")
    private Boolean isDocutrackPharmacy;

    @Column(name = "docutrack_client_type")
    private String docutrackClientType;

    @ElementCollection
    @CollectionTable(name = "Organization_DocutrackBusinessUnitCode", joinColumns = @JoinColumn(name = "organization_id", nullable = false))
    @Column(name = "business_unit_code", nullable = false)
    private List<String> businessUnitCodes;

    @Column(name = "docutrack_server_domain")
    private String docutrackServerDomain;

    @Column(name = "docutrack_server_certificate_sha1")
    private byte[] docutrackServerCertificateSha1;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "hp_claim_billing_provider_ref")
    private String healthPartnersBillingProviderRef;

    @Column(name = "is_xds_default", nullable = false, columnDefinition = "bit default false")
    private boolean xdsDefault;

    @Column(name = "is_signature_pin_enabled", nullable = false, columnDefinition = "bit default true")
    private boolean isSignaturePinEnabled = true;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "pcc_facility_id")
    private Long pccFacilityId;
    @Column(name = "pcc_facility_country")
    private String pccFacilityCountry;

    @Column(name = "pcc_facility_timezone")
    private String pccFacilityTimezone;

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

    public List<CommunityAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<CommunityAddress> addresses) {
        this.addresses = addresses;
    }

    public CommunityTelecom getTelecom() {
        return telecom;
    }

    public void setTelecom(CommunityTelecom telecom) {
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
        return testingTraining;
    }

    public void setTestingTraining(Boolean testingTraining) {
        this.testingTraining = testingTraining;
    }

    public Boolean getInactive() {
        return inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    public Boolean getModuleHie() {
        return moduleHie;
    }

    public void setModuleHie(Boolean moduleHie) {
        this.moduleHie = moduleHie;
    }

    public boolean getIsSharingData() {
        return isSharingData;
    }

    public void setIsSharingData(boolean isSharingData) {
        this.isSharingData = isSharingData;
    }

    public InterfaxConfiguration getInterfaxConfiguration() {
        return interfaxConfiguration;
    }

    public void setInterfaxConfiguration(InterfaxConfiguration interfaxConfiguration) {
        this.interfaxConfiguration = interfaxConfiguration;
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
        CommunityTelecom telecom = getTelecom();
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

    @Access(AccessType.PROPERTY)
    @Column
    public String getPhone() {
        if (this.phone != null) {
            return this.phone;
        }
        String phone = null;
        CommunityTelecom telecom = getTelecom();
        if (telecom != null && PersonTelecomCode.WP.name().equals(telecom.getUseCode())) {
            phone = telecom.getValue();
        }
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    public Boolean getIrEnabled() {
        return irEnabled;
    }

    public void setIrEnabled(Boolean irEnabled) {
        this.irEnabled = irEnabled;
    }

    public boolean isReceiveNonNetworkReferrals() {
        return receiveNonNetworkReferrals;
    }

    public void setReceiveNonNetworkReferrals(boolean receiveNonNetworkReferrals) {
        this.receiveNonNetworkReferrals = receiveNonNetworkReferrals;
    }

    public Long getPccFacilityId() {
        return pccFacilityId;
    }

    public void setPccFacilityId(Long pccFacilityId) {
        this.pccFacilityId = pccFacilityId;
    }

    public String getPccFacilityCountry() {
        return pccFacilityCountry;
    }

    public void setPccFacilityCountry(String pccFacilityCountry) {
        this.pccFacilityCountry = pccFacilityCountry;
    }

    public String getPccFacilityTimezone() {
        return pccFacilityTimezone;
    }

    public void setPccFacilityTimezone(String pccFacilityTimezone) {
        this.pccFacilityTimezone = pccFacilityTimezone;
    }

    @Override
    public int compareTo(Community o) {
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

        Community that = (Community) o;

        return new EqualsBuilder().append(getId(), that.getId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).toHashCode();
    }

    public Marketplace getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(Marketplace marketplace) {
        this.marketplace = marketplace;
    }

    public boolean isConsanaIntegrationEnabled() {
        return isConsanaIntegrationEnabled;
    }

    public void setConsanaIntegrationEnabled(boolean consanaIntegrationEnabled) {
        isConsanaIntegrationEnabled = consanaIntegrationEnabled;
    }

    public String getConsanaOrgId() {
        return consanaOrgId;
    }

    public void setConsanaOrgId(String consanaOrgId) {
        this.consanaOrgId = consanaOrgId;
    }

    public List<CommunityPicture> getPictures() {
        return pictures;
    }

    public void setPictures(List<CommunityPicture> pictures) {
        this.pictures = pictures;
    }

    public String getNumberOfBeds() {
        return numberOfBeds;
    }

    public void setNumberOfBeds(String numberOfBeds) {
        this.numberOfBeds = numberOfBeds;
    }

    public String getNumberOfVacantBeds() {
        return numberOfVacantBeds;
    }

    public void setNumberOfVacantBeds(String numberOfVacantBeds) {
        this.numberOfVacantBeds = numberOfVacantBeds;
    }

    public Boolean getIsDocutrackPharmacy() {
        return isDocutrackPharmacy;
    }

    public void setIsDocutrackPharmacy(Boolean docutrackPharmacy) {
        isDocutrackPharmacy = docutrackPharmacy;
    }

    public List<String> getBusinessUnitCodes() {
        return businessUnitCodes;
    }

    public void setBusinessUnitCodes(List<String> businessUnitCodes) {
        this.businessUnitCodes = businessUnitCodes;
    }

    public String getDocutrackClientType() {
        return docutrackClientType;
    }

    public void setDocutrackClientType(String docutrackClientType) {
        this.docutrackClientType = docutrackClientType;
    }

    public String getDocutrackServerDomain() {
        return docutrackServerDomain;
    }

    public void setDocutrackServerDomain(String docutrackServerDomain) {
        this.docutrackServerDomain = docutrackServerDomain;
    }

    public byte[] getDocutrackServerCertificateSha1() {
        return docutrackServerCertificateSha1;
    }

    public void setDocutrackServerCertificateSha1(byte[] docutrackServerCertificateSha1) {
        this.docutrackServerCertificateSha1 = docutrackServerCertificateSha1;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(final String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getHealthPartnersBillingProviderRef() {
        return healthPartnersBillingProviderRef;
    }

    public void setHealthPartnersBillingProviderRef(String healthPartnerBillingProviderRef) {
        this.healthPartnersBillingProviderRef = healthPartnerBillingProviderRef;
    }

    public boolean getXdsDefault() {
        return xdsDefault;
    }

    public void setXdsDefault(boolean xdsDefault) {
        this.xdsDefault = xdsDefault;
    }

    public boolean getIsSignaturePinEnabled() {
        return isSignaturePinEnabled;
    }

    public void setIsSignaturePinEnabled(boolean signaturePinEnabled) {
        isSignaturePinEnabled = signaturePinEnabled;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }
}
