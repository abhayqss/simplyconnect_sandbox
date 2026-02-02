package com.scnsoft.eldermark.dto;

import java.util.List;

public class MarketplaceSavedCommunitySummaryDto extends BaseMarketplaceCommunityDetailsDto {
    private List<BaseAttachmentDto> pictures;
    private boolean hasReferralEmails;
    private boolean canAddReferral;
    private boolean isReferralEnabled;

    public List<BaseAttachmentDto> getPictures() {
        return pictures;
    }

    public void setPictures(List<BaseAttachmentDto> pictures) {
        this.pictures = pictures;
    }

    public boolean isHasReferralEmails() {
        return hasReferralEmails;
    }

    public void setHasReferralEmails(boolean hasReferralEmails) {
        this.hasReferralEmails = hasReferralEmails;
    }

    public boolean isCanAddReferral() {
        return canAddReferral;
    }

    public void setCanAddReferral(boolean canAddReferral) {
        this.canAddReferral = canAddReferral;
    }

    public boolean getIsReferralEnabled() {
        return isReferralEnabled;
    }

    public void setIsReferralEnabled(boolean referralEnabled) {
        isReferralEnabled = referralEnabled;
    }
}
