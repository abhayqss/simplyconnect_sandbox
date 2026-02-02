package com.scnsoft.eldermark.beans;

public class FeaturedServiceProviderFilter {
    private Long communityId;
    private Boolean isFeatured;

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(Boolean featured) {
        isFeatured = featured;
    }
}
