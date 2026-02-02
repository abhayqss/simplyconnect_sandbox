package com.scnsoft.eldermark.beans.conversation;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

public class AccessibleChatClientFilter extends BaseConversationAccessibilityFilter {

    @NotEmpty
    private Set<Long> communityIds;
    private boolean includeNonAssociatedClients;
    private boolean excludeParticipatingInOneToOne;

    public Set<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(Set<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public boolean getIncludeNonAssociatedClients() {
        return includeNonAssociatedClients;
    }

    public void setIncludeNonAssociatedClients(boolean includeNonAssociatedClients) {
        this.includeNonAssociatedClients = includeNonAssociatedClients;
    }

    public boolean getExcludeParticipatingInOneToOne() {
        return excludeParticipatingInOneToOne;
    }

    public void setExcludeParticipatingInOneToOne(boolean excludeParticipatingInOneToOne) {
        this.excludeParticipatingInOneToOne = excludeParticipatingInOneToOne;
    }
}
