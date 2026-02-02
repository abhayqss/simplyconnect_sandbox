package com.scnsoft.eldermark.beans.conversation;

public class ConversationParticipatingAccessibilityFilter extends BaseConversationAccessibilityFilter {

    private boolean includeNonAssociatedClients;
    private boolean excludeOneToOneParticipants;

    public boolean getIncludeNonAssociatedClients() {
        return includeNonAssociatedClients;
    }

    public void setIncludeNonAssociatedClients(boolean includeNonAssociatedClients) {
        this.includeNonAssociatedClients = includeNonAssociatedClients;
    }

    public boolean getExcludeOneToOneParticipants() {
        return excludeOneToOneParticipants;
    }

    public void setExcludeOneToOneParticipants(boolean excludeOneToOneParticipants) {
        this.excludeOneToOneParticipants = excludeOneToOneParticipants;
    }

    @Override
    public String toString() {
        return "ConversationParticipatingAccessibilityFilter{" +
                "includeNonAssociatedClients=" + includeNonAssociatedClients +
                ", excludeOneToOneParticipants=" + excludeOneToOneParticipants +
                '}';
    }
}
