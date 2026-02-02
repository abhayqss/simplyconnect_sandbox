package com.scnsoft.eldermark.dto;

public class CommunityPermissionsDto {
    private Boolean canAdd;
    private Boolean canEditDocutrack;
    private Boolean canEditSignatureSetup;
    private Boolean canEditHieConsentPolicy;
    private Boolean canEditAllowExternalInboundReferrals;
    private Boolean canEditConfirmMarketplaceVisibility;
    private Boolean canEditMarketplaceReferralEmails;
    private Boolean canEditFeaturedServiceProviders;

    public Boolean getCanAdd() {
        return canAdd;
    }

    public void setCanAdd(Boolean canAdd) {
        this.canAdd = canAdd;
    }

    public Boolean getCanEditDocutrack() {
        return canEditDocutrack;
    }

    public void setCanEditDocutrack(Boolean canEditDocutrack) {
        this.canEditDocutrack = canEditDocutrack;
    }

    public Boolean getCanEditSignatureSetup() {
        return canEditSignatureSetup;
    }

    public void setCanEditSignatureSetup(Boolean canEditSignatureSetup) {
        this.canEditSignatureSetup = canEditSignatureSetup;
    }

    public Boolean getCanEditHieConsentPolicy() {
        return canEditHieConsentPolicy;
    }

    public void setCanEditHieConsentPolicy(Boolean canEditHieConsentPolicy) {
        this.canEditHieConsentPolicy = canEditHieConsentPolicy;
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
