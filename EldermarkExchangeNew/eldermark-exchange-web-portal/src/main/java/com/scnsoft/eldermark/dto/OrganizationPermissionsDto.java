package com.scnsoft.eldermark.dto;

public class OrganizationPermissionsDto {
    private Boolean canAdd;
    private Boolean canEditFeatures;
    private Boolean canEditAllowExternalInboundReferrals;
    private Boolean canEditConfirmMarketplaceVisibility;
    private Boolean canEditAffiliateRelationships;

    public Boolean getCanAdd() {
        return canAdd;
    }

    public void setCanAdd(Boolean canAdd) {
        this.canAdd = canAdd;
    }

    public Boolean getCanEditFeatures() {
        return canEditFeatures;
    }

    public void setCanEditFeatures(Boolean canEditFeatures) {
        this.canEditFeatures = canEditFeatures;
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
