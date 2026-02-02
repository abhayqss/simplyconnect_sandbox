package com.scnsoft.eldermark.shared.carecoordination;

public class AffiliatedOrganizationDto {
    private String primaryOrganizationName;
    private String primaryCommunityName;
    private String affiliatedOrganizationName;
    private String affiliatedCommunityName;
    private Long primaryOrganizationId;
    private Long primaryCommunityId;
    private Long affiliatedOrganizationId;
    private Long affiliatedCommunityId;

    public String getPrimaryOrganizationName() {
        return primaryOrganizationName;
    }

    public void setPrimaryOrganizationName(String primaryOrganizationName) {
        this.primaryOrganizationName = primaryOrganizationName;
    }

    public String getPrimaryCommunityName() {
        return primaryCommunityName;
    }

    public void setPrimaryCommunityName(String primaryCommunityName) {
        this.primaryCommunityName = primaryCommunityName;
    }

    public String getAffiliatedOrganizationName() {
        return affiliatedOrganizationName;
    }

    public void setAffiliatedOrganizationName(String affiliatedOrganizationName) {
        this.affiliatedOrganizationName = affiliatedOrganizationName;
    }

    public String getAffiliatedCommunityName() {
        return affiliatedCommunityName;
    }

    public void setAffiliatedCommunityName(String affiliatedCommunityName) {
        this.affiliatedCommunityName = affiliatedCommunityName;
    }

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
