package com.scnsoft.eldermark.beans;

import java.util.List;

public class PartnerNetworkFilter {

    private Long communityId;
    private List<Long> serviceIds;
    private boolean includeCommunityInServiceSearch;

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public List<Long> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Long> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public boolean getIncludeCommunityInServiceSearch() {
        return includeCommunityInServiceSearch;
    }

    public void setIncludeCommunityInServiceSearch(boolean includeCommunityInServiceSearch) {
        this.includeCommunityInServiceSearch = includeCommunityInServiceSearch;
    }
}
