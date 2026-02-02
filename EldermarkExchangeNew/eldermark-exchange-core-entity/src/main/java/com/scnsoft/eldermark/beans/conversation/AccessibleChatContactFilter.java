package com.scnsoft.eldermark.beans.conversation;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

public class AccessibleChatContactFilter extends BaseConversationAccessibilityFilter {

    @NotEmpty
    private Set<Long> organizationIds;

    @NotEmpty
    private Set<Long> communityIds;

    private boolean excludeParticipatingInOneToOne;

    public Set<Long> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(Set<Long> organizationIds) {
        this.organizationIds = organizationIds;
    }

    public Set<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(Set<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public boolean getExcludeParticipatingInOneToOne() {
        return excludeParticipatingInOneToOne;
    }

    public void setExcludeParticipatingInOneToOne(boolean excludeParticipatingInOneToOne) {
        this.excludeParticipatingInOneToOne = excludeParticipatingInOneToOne;
    }
}

