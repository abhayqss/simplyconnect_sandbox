package com.scnsoft.eldermark.beans.conversation;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

public class AccessibleChatCommunityCareTeamFilter extends BaseConversationAccessibilityFilter {

    @NotEmpty
    private Set<Long> communityIds;

    public Set<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(Set<Long> communityIds) {
        this.communityIds = communityIds;
    }
}
