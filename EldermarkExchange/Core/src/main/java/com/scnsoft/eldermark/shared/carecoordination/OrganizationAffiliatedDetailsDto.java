package com.scnsoft.eldermark.shared.carecoordination;

import java.util.List;

/**
 * Created by knetkachou on 3/10/2017.
 */
public class OrganizationAffiliatedDetailsDto {
    List<Long>  communityIds;
    Long affOrgId;
    List<Long> affCommunitiesIds;

    public List<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(List<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public Long getAffOrgId() {
        return affOrgId;
    }

    public void setAffOrgId(Long affOrgId) {
        this.affOrgId = affOrgId;
    }

    public List<Long> getAffCommunitiesIds() {
        return affCommunitiesIds;
    }

    public void setAffCommunitiesIds(List<Long> affCommunitiesIds) {
        this.affCommunitiesIds = affCommunitiesIds;
    }
}
