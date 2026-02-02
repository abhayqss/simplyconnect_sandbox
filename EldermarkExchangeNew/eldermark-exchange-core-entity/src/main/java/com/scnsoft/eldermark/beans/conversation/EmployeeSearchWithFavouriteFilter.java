package com.scnsoft.eldermark.beans.conversation;

import com.scnsoft.eldermark.annotations.InternalFilterParameter;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;

import java.util.Set;

public class EmployeeSearchWithFavouriteFilter extends BaseConversationAccessibilityFilter {
    private Long organizationId;
    private Set<Long> communityIds;

    private String searchText;
    private boolean excludeParticipatingInOneToOne;
    private boolean excludeCanNotCall;

    @InternalFilterParameter
    private Long favouriteOfEmployeeIdHint;

    @InternalFilterParameter
    private CareTeamRoleCode excludeSystemRole;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Set<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(Set<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public boolean isExcludeParticipatingInOneToOne() {
        return excludeParticipatingInOneToOne;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public boolean getExcludeParticipatingInOneToOne() {
        return excludeParticipatingInOneToOne;
    }

    public void setExcludeParticipatingInOneToOne(boolean excludeParticipatingInOneToOne) {
        this.excludeParticipatingInOneToOne = excludeParticipatingInOneToOne;
    }

    public Long getFavouriteOfEmployeeIdHint() {
        return favouriteOfEmployeeIdHint;
    }

    public void setFavouriteOfEmployeeIdHint(Long favouriteOfEmployeeIdHint) {
        this.favouriteOfEmployeeIdHint = favouriteOfEmployeeIdHint;
    }

    public CareTeamRoleCode getExcludeSystemRole() {
        return excludeSystemRole;
    }

    public void setExcludeSystemRole(CareTeamRoleCode excludeSystemRole) {
        this.excludeSystemRole = excludeSystemRole;
    }

    public boolean getExcludeCanNotCall() {
        return excludeCanNotCall;
    }

    public void setExcludeCanNotCall(boolean excludeCanNotCall) {
        this.excludeCanNotCall = excludeCanNotCall;
    }
}
