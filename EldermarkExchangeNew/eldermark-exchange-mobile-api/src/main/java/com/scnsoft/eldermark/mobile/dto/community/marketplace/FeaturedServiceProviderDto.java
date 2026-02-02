package com.scnsoft.eldermark.mobile.dto.community.marketplace;

public class FeaturedServiceProviderDto {
    private Long organizationId;
    private Long communityId;
    private String communityName;
    private String description;
    private String websiteUrl;
    private String createExternalInboundReferralUrl;
    private String organizationLogoName;
    private String communityLogoName;
    private boolean hasReferralEmails;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getCreateExternalInboundReferralUrl() {
        return createExternalInboundReferralUrl;
    }

    public void setCreateExternalInboundReferralUrl(String createExternalInboundReferralUrl) {
        this.createExternalInboundReferralUrl = createExternalInboundReferralUrl;
    }

    public String getOrganizationLogoName() {
        return organizationLogoName;
    }

    public void setOrganizationLogoName(String organizationLogoName) {
        this.organizationLogoName = organizationLogoName;
    }

    public String getCommunityLogoName() {
        return communityLogoName;
    }

    public void setCommunityLogoName(String communityLogoName) {
        this.communityLogoName = communityLogoName;
    }

    public boolean getHasReferralEmails() {
        return hasReferralEmails;
    }

    public void setHasReferralEmails(boolean hasReferralEmails) {
        this.hasReferralEmails = hasReferralEmails;
    }
}
