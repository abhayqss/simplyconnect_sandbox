package com.scnsoft.eldermark.dto;

public class BaseMarketplaceCommunityLocationDto {
    private Long communityId = null;
    private LocationWithDistanceDto location = null;

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public LocationWithDistanceDto getLocation() {
        return location;
    }

    public void setLocation(LocationWithDistanceDto location) {
        this.location = location;
    }
}
