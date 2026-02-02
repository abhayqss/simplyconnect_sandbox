package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class CommunityDto extends CommunityBaseDto {

    @Valid
    @NotNull
    private MarketplaceDto marketplace;

    private Boolean canViewPartners;

    private boolean allowExternalInboundReferrals;

    private Boolean canEditHieConsentPolicy;

    private HieConsentPolicyType hieConsentPolicyName;
    private String hieConsentPolicyTitle;

    private List<FeaturedServiceProviderDto> featuredServiceProviders;

    private Boolean canEditDocutrack;
    private Boolean canEditAllowExternalInboundReferrals;
    private Boolean canEditConfirmMarketplaceVisibility;
    private Boolean canEditMarketplaceReferralEmails;
    private Boolean canEditFeaturedServiceProviders;

    public MarketplaceDto getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(MarketplaceDto marketplace) {
        this.marketplace = marketplace;
    }

    public Boolean getCanViewPartners() {
        return canViewPartners;
    }

    public void setCanViewPartners(Boolean canViewPartners) {
        this.canViewPartners = canViewPartners;
    }

    public boolean getAllowExternalInboundReferrals() {
        return allowExternalInboundReferrals;
    }

    public void setAllowExternalInboundReferrals(boolean allowExternalInboundReferrals) {
        this.allowExternalInboundReferrals = allowExternalInboundReferrals;
    }

    public List<FeaturedServiceProviderDto> getFeaturedServiceProviders() {
        return featuredServiceProviders;
    }

    public void setFeaturedServiceProviders(List<FeaturedServiceProviderDto> featuredServiceProviders) {
        this.featuredServiceProviders = featuredServiceProviders;
    }

    public Boolean getCanEditHieConsentPolicy() {
        return canEditHieConsentPolicy;
    }

    public void setCanEditHieConsentPolicy(Boolean canEditHieConsentPolicy) {
        this.canEditHieConsentPolicy = canEditHieConsentPolicy;
    }

    public HieConsentPolicyType getHieConsentPolicyName() {
        return hieConsentPolicyName;
    }

    public void setHieConsentPolicyName(HieConsentPolicyType hieConsentPolicyType) {
        this.hieConsentPolicyName = hieConsentPolicyType;
    }

    public String getHieConsentPolicyTitle() {
        return hieConsentPolicyTitle;
    }

    public void setHieConsentPolicyTitle(String hieConsentPolicyTitle) {
        this.hieConsentPolicyTitle = hieConsentPolicyTitle;
    }

    public Boolean getCanEditDocutrack() {
        return canEditDocutrack;
    }

    public void setCanEditDocutrack(Boolean canEditDocutrack) {
        this.canEditDocutrack = canEditDocutrack;
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

    public Boolean getCanEditMarketplaceReferralEmails() {
        return canEditMarketplaceReferralEmails;
    }

    public void setCanEditMarketplaceReferralEmails(Boolean canEditMarketplaceReferralEmails) {
        this.canEditMarketplaceReferralEmails = canEditMarketplaceReferralEmails;
    }

    public Boolean getCanEditFeaturedServiceProviders() {
        return canEditFeaturedServiceProviders;
    }

    public void setCanEditFeaturedServiceProviders(Boolean canEditFeaturedServiceProviders) {
        this.canEditFeaturedServiceProviders = canEditFeaturedServiceProviders;
    }
}
