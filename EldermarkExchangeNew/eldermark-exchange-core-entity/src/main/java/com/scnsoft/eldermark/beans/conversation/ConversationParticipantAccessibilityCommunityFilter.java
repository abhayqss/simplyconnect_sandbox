package com.scnsoft.eldermark.beans.conversation;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

public class ConversationParticipantAccessibilityCommunityFilter extends ConversationParticipantAccessibilityFilter {
    @NotEmpty
    private Set<Long> organizationIds;

    public Set<Long> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(Set<Long> organizationIds) {
        this.organizationIds = organizationIds;
    }
}
