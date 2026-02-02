package com.scnsoft.eldermark.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class OrganizationDto extends OrganizationBaseDto {
    private Boolean copyEventNotificationsForPatients;

    @NotNull
    @Valid
    private MarketplaceDto marketplace;

    private List<AffiliatedRelationshipItemDto> affiliatedRelationships = new ArrayList<>();

    private boolean allowExternalInboundReferrals;

    public Boolean getCopyEventNotificationsForPatients() {
        return copyEventNotificationsForPatients;
    }

    public void setCopyEventNotificationsForPatients(Boolean copyEventNotificationsForPatients) {
        this.copyEventNotificationsForPatients = copyEventNotificationsForPatients;
    }

    public MarketplaceDto getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(MarketplaceDto marketplace) {
        this.marketplace = marketplace;
    }

    public boolean getAllowExternalInboundReferrals() {
        return allowExternalInboundReferrals;
    }

    public void setAllowExternalInboundReferrals(boolean allowExternalInboundReferrals) {
        this.allowExternalInboundReferrals = allowExternalInboundReferrals;
    }

    public List<AffiliatedRelationshipItemDto> getAffiliatedRelationships() {
        return affiliatedRelationships;
    }

    public void setAffiliatedRelationships(List<AffiliatedRelationshipItemDto> affiliatedRelationships) {
        this.affiliatedRelationships = affiliatedRelationships;
    }
}
