package com.scnsoft.eldermark.shared.marketplace;

import org.apache.commons.math3.util.Precision;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Null;
import java.util.Date;
import java.util.List;

/**
 * Created by pzhurba on 09-Oct-15.
 */
public class MarketplaceFilterDto {

    private static final double PRECISION = 0.001;


    private List<Long> primaryFocusIds;
    private List<Long> communityTypeIds;
    private List<Long> serviceIds;
    private Long inNetworkInsuranceId;
    private Long insurancePlanId;
    private String searchText;
    private Integer pageNumber=0;
    private Double initLatitude;
    private Double initLongitude;

    public List<Long> getPrimaryFocusIds() {
        return primaryFocusIds;
    }

    public void setPrimaryFocusIds(List<Long> primaryFocusIds) {
        this.primaryFocusIds = primaryFocusIds;
    }

    public List<Long> getCommunityTypeIds() {
        return communityTypeIds;
    }

    public void setCommunityTypeIds(List<Long> communityTypeIds) {
        this.communityTypeIds = communityTypeIds;
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

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber==null?0:pageNumber;
    }

    public void incrementPageNumber() {
        pageNumber++;
    }

    public Long getInNetworkInsuranceId() {
        return inNetworkInsuranceId;
    }

    public void setInNetworkInsuranceId(Long inNetworkInsuranceId) {
        this.inNetworkInsuranceId = inNetworkInsuranceId;
    }

    public Long getInsurancePlanId() {
        return insurancePlanId;
    }

    public void setInsurancePlanId(Long insurancePlanId) {
        this.insurancePlanId = insurancePlanId;
    }

    public Double getInitLatitude() {
        return initLatitude;
    }

    public void setInitLatitude(Double initLatitude) {
        this.initLatitude = initLatitude;
    }

    public Double getInitLongitude() {
        return initLongitude;
    }

    public void setInitLongitude(Double initLongitude) {
        this.initLongitude = initLongitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MarketplaceFilterDto that = (MarketplaceFilterDto) o;

        if (primaryFocusIds != null ? !primaryFocusIds.equals(that.primaryFocusIds) : that.primaryFocusIds != null)
            return false;
        if (communityTypeIds != null ? !communityTypeIds.equals(that.communityTypeIds) : that.communityTypeIds != null)
            return false;
        if (serviceIds != null ? !serviceIds.equals(that.serviceIds) : that.serviceIds != null) return false;
        if (inNetworkInsuranceId != null ? !inNetworkInsuranceId.equals(that.inNetworkInsuranceId) : that.inNetworkInsuranceId != null)
            return false;
        if (insurancePlanId != null ? !insurancePlanId.equals(that.insurancePlanId) : that.insurancePlanId != null)
            return false;
        if (searchText != null ? !searchText.equals(that.searchText) : that.searchText != null) return false;
        if (pageNumber != null ? !pageNumber.equals(that.pageNumber) : that.pageNumber != null) return false;
        if (initLatitude != null ? !Precision.equals(initLatitude, that.initLatitude, PRECISION) : that.initLatitude != null) return false;
        return initLongitude != null ? Precision.equals(initLongitude, that.initLongitude, PRECISION) : that.initLongitude == null;
    }

    @Override
    public int hashCode() {
        int result = primaryFocusIds != null ? primaryFocusIds.hashCode() : 0;
        result = 31 * result + (communityTypeIds != null ? communityTypeIds.hashCode() : 0);
        result = 31 * result + (serviceIds != null ? serviceIds.hashCode() : 0);
        result = 31 * result + (inNetworkInsuranceId != null ? inNetworkInsuranceId.hashCode() : 0);
        result = 31 * result + (insurancePlanId != null ? insurancePlanId.hashCode() : 0);
        result = 31 * result + (searchText != null ? searchText.hashCode() : 0);
        result = 31 * result + (pageNumber != null ? pageNumber.hashCode() : 0);
        result = 31 * result + (initLatitude != null ? initLatitude.hashCode() : 0);
        result = 31 * result + (initLongitude != null ? initLongitude.hashCode() : 0);
        return result;
    }
}
