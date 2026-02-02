package com.scnsoft.eldermark.beans.conversation;

public class ConversationParticipantAccessibilityFilter extends BaseConversationAccessibilityFilter {

    private boolean withAccessibleClients;
    private boolean withAccessibleNonAssociatedClients;
    private boolean withAccessibleCommunityCareTeamMembers;
    private boolean withAccessibleContacts;

    private boolean withExcludedOneToOneParticipants;

    public boolean getWithAccessibleClients() {
        return withAccessibleClients;
    }

    public void setWithAccessibleClients(boolean withAccessibleClients) {
        this.withAccessibleClients = withAccessibleClients;
    }

    public boolean getWithAccessibleNonAssociatedClients() {
        return withAccessibleNonAssociatedClients;
    }

    public void setWithAccessibleNonAssociatedClients(boolean withAccessibleNonAssociatedClients) {
        this.withAccessibleNonAssociatedClients = withAccessibleNonAssociatedClients;
    }

    public boolean getWithAccessibleCommunityCareTeamMembers() {
        return withAccessibleCommunityCareTeamMembers;
    }

    public void setWithAccessibleCommunityCareTeamMembers(boolean withAccessibleCommunityCareTeamMembers) {
        this.withAccessibleCommunityCareTeamMembers = withAccessibleCommunityCareTeamMembers;
    }

    public boolean getWithAccessibleContacts() {
        return withAccessibleContacts;
    }

    public void setWithAccessibleContacts(boolean withAccessibleContacts) {
        this.withAccessibleContacts = withAccessibleContacts;
    }

    public boolean getWithExcludedOneToOneParticipants() {
        return withExcludedOneToOneParticipants;
    }

    public void setWithExcludedOneToOneParticipants(boolean withExcludedOneToOneParticipants) {
        this.withExcludedOneToOneParticipants = withExcludedOneToOneParticipants;
    }
}
