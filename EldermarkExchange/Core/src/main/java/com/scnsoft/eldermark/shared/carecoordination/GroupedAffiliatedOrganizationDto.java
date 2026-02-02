package com.scnsoft.eldermark.shared.carecoordination;

import java.util.List;

public class GroupedAffiliatedOrganizationDto {
    private List<String> primaryOrganizationNames;
    private List<String> primaryCommunityNames;
    private List<String> affiliatedOrganizationNames;
    private List<String> affiliatedCommunityNames;

    public List<String> getPrimaryOrganizationNames() {
        return primaryOrganizationNames;
    }

    public void setPrimaryOrganizationNames(List<String> primaryOrganizationNames) {
        this.primaryOrganizationNames = primaryOrganizationNames;
    }

    public List<String> getPrimaryCommunityNames() {
        return primaryCommunityNames;
    }

    public void setPrimaryCommunityNames(List<String> primaryCommunityNames) {
        this.primaryCommunityNames = primaryCommunityNames;
    }

    public List<String> getAffiliatedOrganizationNames() {
        return affiliatedOrganizationNames;
    }

    public void setAffiliatedOrganizationNames(List<String> affiliatedOrganizationNames) {
        this.affiliatedOrganizationNames = affiliatedOrganizationNames;
    }

    public List<String> getAffiliatedCommunityNames() {
        return affiliatedCommunityNames;
    }

    public void setAffiliatedCommunityNames(List<String> affiliatedCommunityNames) {
        this.affiliatedCommunityNames = affiliatedCommunityNames;
    }
}
