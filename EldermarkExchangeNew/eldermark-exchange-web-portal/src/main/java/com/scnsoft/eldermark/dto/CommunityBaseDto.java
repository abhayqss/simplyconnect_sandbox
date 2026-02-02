package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.beans.security.projection.dto.CommunitySecurityFieldsAware;
import com.scnsoft.eldermark.dto.docutrack.DocutrackPharmacyConfigDto;
import com.scnsoft.eldermark.validation.ValidationGroups;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

public class CommunityBaseDto implements CommunitySecurityFieldsAware {

    @NotNull(groups = ValidationGroups.Update.class)
    private Long id;

    @NotEmpty
    @Size(min = 3, max = 256)
    private String name;

    @NotEmpty(groups = ValidationGroups.Create.class)
    @Size(max = 256)
    private String oid;

    private Long organizationId;

    private String organizationName;

    @NotEmpty
    @Size(max = 256)
    @Pattern(regexp = ValidationRegExpConstants.EMAIL_REGEXP)
    private String email;

    @NotEmpty
    @Size(max = 16)
    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String phone;

    private String licenseNumber;

    @NotNull
    private Long stateId;

//    private String stateName;

    private LocationWithDistanceDto location;

    @NotEmpty
    @Size(max = 256)
    private String city;

    @NotEmpty
    @Size(max = 256)
    private String street;

    @NotEmpty
    @Pattern(regexp = ValidationRegExpConstants.ZIP_CODE_REGEXP)
    private String zipCode;

    private String logoName;

    private boolean isSharingData;

    private MultipartFile logo;

    private boolean shouldRemoveLogo;

    private String displayAddress;

    private Boolean canEdit;

    private List<AffiliationInfoItemDto> affiliationPrimary;
    private List<AffiliationInfoItemDto> affiliationAffiliated;

    @Size(max = 10)
    private List<MultipartFile> pictureFiles;
    private List<BaseAttachmentDto> pictures;
    @Size(max = 256)
    private String numberOfBeds;
    @Size(max = 256)
    private String numberOfVacantBeds;

    @Valid
    private DocutrackPharmacyConfigDto docutrackPharmacyConfig;

    @Valid
    @NotNull(groups = ValidationGroups.CommunitySignatureConfig.class)
    private CommunitySignatureConfigDto signatureConfig;

    private String websiteUrl;

    public boolean isShouldRemoveLogo() {
        return shouldRemoveLogo;
    }

    public void setShouldRemoveLogo(boolean shouldRemoveLogo) {
        this.shouldRemoveLogo = shouldRemoveLogo;
    }

    public LocationWithDistanceDto getLocation() {
        return location;
    }

    public void setLocation(LocationWithDistanceDto location) {
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getLogoName() {
        return logoName;
    }

    public String getDisplayAddress() {
        return displayAddress;
    }

    public void setDisplayAddress(String displayAddress) {
        this.displayAddress = displayAddress;
    }

    public void setLogoName(String logoName) {
        this.logoName = logoName;
    }

    public boolean getIsSharingData() {
        return isSharingData;
    }

    public MultipartFile getLogo() {
        return logo;
    }

    public void setLogo(MultipartFile logo) {
        this.logo = logo;
    }

    public void setIsSharingData(boolean isSharingData) {
        this.isSharingData = isSharingData;
    }

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    public List<AffiliationInfoItemDto> getAffiliationPrimary() {
        return affiliationPrimary;
    }

    public void setAffiliationPrimary(List<AffiliationInfoItemDto> affiliationPrimary) {
        this.affiliationPrimary = affiliationPrimary;
    }

    public List<AffiliationInfoItemDto> getAffiliationAffiliated() {
        return affiliationAffiliated;
    }

    public void setAffiliationAffiliated(List<AffiliationInfoItemDto> affiliationAffiliated) {
        this.affiliationAffiliated = affiliationAffiliated;
    }

    public List<MultipartFile> getPictureFiles() {
        return pictureFiles;
    }

    public void setPictureFiles(List<MultipartFile> pictureFiles) {
        this.pictureFiles = pictureFiles;
    }

    public List<BaseAttachmentDto> getPictures() {
        return pictures;
    }

    public void setPictures(List<BaseAttachmentDto> pictures) {
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

    public DocutrackPharmacyConfigDto getDocutrackPharmacyConfig() {
        return docutrackPharmacyConfig;
    }

    public void setDocutrackPharmacyConfig(DocutrackPharmacyConfigDto docutrackPharmacyConfig) {
        this.docutrackPharmacyConfig = docutrackPharmacyConfig;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(final String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public CommunitySignatureConfigDto getSignatureConfig() {
        return signatureConfig;
    }

    public void setSignatureConfig(CommunitySignatureConfigDto signatureConfig) {
        this.signatureConfig = signatureConfig;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }
}
