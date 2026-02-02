package com.scnsoft.eldermark.beans;

import java.util.List;

public class PartnerNetworkDetailsFilter {

    private Long partnerNetworkId;
    private Long excludeCommunityId;
    private List<Long> serviceIds;

    public Long getPartnerNetworkId() {
        return partnerNetworkId;
    }

    public void setPartnerNetworkId(Long partnerNetworkId) {
        this.partnerNetworkId = partnerNetworkId;
    }

    public Long getExcludeCommunityId() {
        return excludeCommunityId;
    }

    public void setExcludeCommunityId(Long excludeCommunityId) {
        this.excludeCommunityId = excludeCommunityId;
    }

    public List<Long> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Long> serviceIds) {
        this.serviceIds = serviceIds;
    }
}
