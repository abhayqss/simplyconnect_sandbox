package com.scnsoft.eldermark.dto;

import java.util.List;

public class MarketplaceCommunitySummaryDto extends BaseMarketplaceCommunityDetailsDto {

    private List<BaseAttachmentDto> pictures;
    private boolean isSaved;

    public List<BaseAttachmentDto> getPictures() {
        return pictures;
    }

    public void setPictures(List<BaseAttachmentDto> pictures) {
        this.pictures = pictures;
    }

    public boolean getIsSaved() {
        return isSaved;
    }

    public void setIsSaved(boolean saved) {
        isSaved = saved;
    }

}
