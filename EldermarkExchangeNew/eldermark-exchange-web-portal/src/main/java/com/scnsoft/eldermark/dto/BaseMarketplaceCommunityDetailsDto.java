package com.scnsoft.eldermark.dto;

import java.util.ArrayList;
import java.util.List;

public class BaseMarketplaceCommunityDetailsDto extends BaseMarketplaceCommunityLocationDto {
    private String communityName = null;
    private Long organizationId = null;
    private String organizationName = null;
    private List<KeyValueDto> serviceCategories = new ArrayList<>();
    private List<KeyValueDto> services = new ArrayList<>();
    private String address = null;
    private String phone;
    private Integer rating;

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public List<KeyValueDto> getServiceCategories() {
        return serviceCategories;
    }

    public void setServiceCategories(List<KeyValueDto> serviceCategories) {
        this.serviceCategories = serviceCategories;
    }

    public List<KeyValueDto> getServices() {
        return services;
    }

    public void setServices(List<KeyValueDto> services) {
        this.services = services;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
