package com.scnsoft.eldermark.beans;

import java.util.List;

public class MarketplaceFilter {

    private Long serviceCategoryId;
    private List<Long> serviceIds;
    private String searchText;
    private Double latitude;
    private Double longitude;
    private Boolean includeMyCommunities;
    private Boolean includeInNetworkCommunities;

    public Long getServiceCategoryId() {
        return serviceCategoryId;
    }

    public void setServiceCategoryId(Long serviceCategoryId) {
        this.serviceCategoryId = serviceCategoryId;
    }

    public List<Long> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Long> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getIncludeMyCommunities() {
        return includeMyCommunities;
    }

    public void setIncludeMyCommunities(Boolean includeMyCommunities) {
        this.includeMyCommunities = includeMyCommunities;
    }

    public Boolean getIncludeInNetworkCommunities() {
        return includeInNetworkCommunities;
    }

    public void setIncludeInNetworkCommunities(Boolean includeInNetworkCommunities) {
        this.includeInNetworkCommunities = includeInNetworkCommunities;
    }
}
