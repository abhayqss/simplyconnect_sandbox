package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.validation.SpELAssert;
import com.scnsoft.eldermark.validation.ValidationGroups;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@SpELAssert.List(
        value = {
                @SpELAssert(
                        applyIf = "!(#isAllEmpty(city, street, zipCode) && stateId == null)",
                        value = "#isNoneEmpty(city, street, zipCode) && stateId != null",
                        message = "all of [city, street, zipCode, stateId] should be present",
                        helpers = StringUtils.class
                )
        }
)
public class OrganizationBaseDto {

    @NotNull(groups = ValidationGroups.Update.class)
    private Long id;

    @NotEmpty
    @Size(min = 3, max = 256)
    private String name;

    @NotEmpty(groups = ValidationGroups.Create.class)
    @Size(max = 256)
    private String oid;

    @NotEmpty(groups = ValidationGroups.Create.class)
    @Size(max = 25, groups = ValidationGroups.Create.class)
    private String companyId;

    private String logoName;

    @NotEmpty
    @Size(max = 256)
    @Pattern(regexp = ValidationRegExpConstants.EMAIL_REGEXP)
    private String email;

    @NotEmpty
    @Size(max = 16)
    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String phone;

    @Min(value = 0)
    private Long stateId;

    @Size(max = 256)
    private String city;

    @Size(max = 256)
    private String street;

    @Pattern(regexp = ValidationRegExpConstants.ZIP_CODE_REGEXP)
    private String zipCode;

    private MultipartFile logo;
    private boolean shouldRemoveLogo;
    private String displayAddress;
    private Boolean hasCommunities;

    private List<OrganizationAffiliationInfoItemDto> affiliationPrimary;
    private List<OrganizationAffiliationInfoItemDto> affiliationAffiliated;

    @Valid
    @NotNull(groups = ValidationGroups.OrganizationFeatures.class)
    private OrganizationFeaturesDto features;

    private Boolean canEdit;
    private Boolean canEditAllowExternalInboundReferrals;
    private Boolean canEditConfirmMarketplaceVisibility;
    private Boolean canEditAffiliateRelationships;

    public void setDisplayAddress(String displayAddress) {
        this.displayAddress = displayAddress;
    }

    public boolean isShouldRemoveLogo() {
        return shouldRemoveLogo;
    }

    public void setShouldRemoveLogo(boolean shouldRemoveLogo) {
        this.shouldRemoveLogo = shouldRemoveLogo;
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

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getLogoName() {
        return logoName;
    }

    public void setLogoName(String logoName) {
        this.logoName = logoName;
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

    public String getDisplayAddress() {
        return displayAddress;
    }

    public MultipartFile getLogo() {
        return logo;
    }

    public void setLogo(MultipartFile logo) {
        this.logo = logo;
    }

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    public Boolean getHasCommunities() {
        return hasCommunities;
    }

    public void setHasCommunities(Boolean hasCommunities) {
        this.hasCommunities = hasCommunities;
    }

    public List<OrganizationAffiliationInfoItemDto> getAffiliationPrimary() {
        return affiliationPrimary;
    }

    public void setAffiliationPrimary(List<OrganizationAffiliationInfoItemDto> affiliationPrimary) {
        this.affiliationPrimary = affiliationPrimary;
    }

    public List<OrganizationAffiliationInfoItemDto> getAffiliationAffiliated() {
        return affiliationAffiliated;
    }

    public void setAffiliationAffiliated(List<OrganizationAffiliationInfoItemDto> affiliationAffiliated) {
        this.affiliationAffiliated = affiliationAffiliated;
    }

    public OrganizationFeaturesDto getFeatures() {
        return features;
    }

    public void setFeatures(OrganizationFeaturesDto features) {
        this.features = features;
    }

    public Boolean getCanEditAllowExternalInboundReferrals() {
        return canEditAllowExternalInboundReferrals;
    }

    public void setCanEditAllowExternalInboundReferrals(Boolean canEditAllowExternalInboundReferrals) {
        this.canEditAllowExternalInboundReferrals = canEditAllowExternalInboundReferrals;
    }

    public Boolean getCanEditConfirmMarketplaceVisibility() {
        return canEditConfirmMarketplaceVisibility;
    }

    public void setCanEditConfirmMarketplaceVisibility(Boolean canEditConfirmMarketplaceVisibility) {
        this.canEditConfirmMarketplaceVisibility = canEditConfirmMarketplaceVisibility;
    }

    public Boolean getCanEditAffiliateRelationships() {
        return canEditAffiliateRelationships;
    }

    public void setCanEditAffiliateRelationships(Boolean canEditAffiliateRelationships) {
        this.canEditAffiliateRelationships = canEditAffiliateRelationships;
    }
}
