package com.scnsoft.eldermark.mobile.dto.ccd;

public class DataSource {
    private Long organizationId;
    private String organizationName;
    private String organizationLogoName;

    private Long communityId;
    private String communityName;
    private String communityLogoName;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getOrganizationLogoName() {
        return organizationLogoName;
    }

    public void setOrganizationLogoName(String organizationLogoName) {
        this.organizationLogoName = organizationLogoName;
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

    public String getCommunityLogoName() {
        return communityLogoName;
    }

    public void setCommunityLogoName(String communityLogoName) {
        this.communityLogoName = communityLogoName;
    }
}
