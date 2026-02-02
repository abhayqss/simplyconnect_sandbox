package com.scnsoft.eldermark.dto;

public class AffiliatedOrganizationDto {

    private Long primaryOrganizationId;
    private Long primaryCommunityId;
    private Long affiliatedOrganizationId;
    private Long affiliatedCommunityId;

    public Long getPrimaryOrganizationId() {
        return primaryOrganizationId;
    }

    public void setPrimaryOrganizationId(Long primaryOrganizationId) {
        this.primaryOrganizationId = primaryOrganizationId;
    }

    public Long getPrimaryCommunityId() {
        return primaryCommunityId;
    }

    public void setPrimaryCommunityId(Long primaryCommunityId) {
        this.primaryCommunityId = primaryCommunityId;
    }

    public Long getAffiliatedOrganizationId() {
        return affiliatedOrganizationId;
    }

    public void setAffiliatedOrganizationId(Long affiliatedOrganizationId) {
        this.affiliatedOrganizationId = affiliatedOrganizationId;
    }

    public Long getAffiliatedCommunityId() {
        return affiliatedCommunityId;
    }

    public void setAffiliatedCommunityId(Long affiliatedCommunityId) {
        this.affiliatedCommunityId = affiliatedCommunityId;
    }
}
