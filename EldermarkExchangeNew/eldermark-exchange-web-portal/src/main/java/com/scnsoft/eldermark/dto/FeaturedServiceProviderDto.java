package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;

import java.util.List;

public class FeaturedServiceProviderDto {
    private Long id;
    private Long organizationId;
    private String organizationName;

    private Long communityId;
    private String communityName;

    private boolean allowExternalInboundReferrals;
    private String websiteUrl;

    private boolean confirmVisibility;
    private Integer displayOrder;

    private List<IdentifiedTitledEntityDto> serviceCategories;

    private boolean canAddReferral;

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

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public boolean getAllowExternalInboundReferrals() {
        return allowExternalInboundReferrals;
    }

    public void setAllowExternalInboundReferrals(boolean allowExternalInboundReferrals) {
        this.allowExternalInboundReferrals = allowExternalInboundReferrals;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public boolean getConfirmVisibility() {
        return confirmVisibility;
    }

    public void setConfirmVisibility(boolean confirmVisibility) {
        this.confirmVisibility = confirmVisibility;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<IdentifiedTitledEntityDto> getServiceCategories() {
        return serviceCategories;
    }

    public void setServiceCategories(List<IdentifiedTitledEntityDto> serviceCategories) {
        this.serviceCategories = serviceCategories;
    }

    public boolean getCanAddReferral() {
        return canAddReferral;
    }

    public void setCanAddReferral(boolean canAddReferral) {
        this.canAddReferral = canAddReferral;
    }
}
