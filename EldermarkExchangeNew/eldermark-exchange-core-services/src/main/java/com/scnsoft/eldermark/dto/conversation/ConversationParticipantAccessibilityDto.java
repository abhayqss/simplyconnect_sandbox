package com.scnsoft.eldermark.dto.conversation;

public class ConversationParticipantAccessibilityDto {

    private boolean areClientsAccessible;
    private boolean areClientCareTeamMembersAccessible;
    private boolean areCommunityCareTeamMembersAccessible;
    private boolean areContactsAccessible;

    public boolean getAreClientsAccessible() {
        return areClientsAccessible;
    }

    public void setAreClientsAccessible(boolean areClientsAccessible) {
        this.areClientsAccessible = areClientsAccessible;
    }

    public boolean getAreClientCareTeamMembersAccessible() {
        return areClientCareTeamMembersAccessible;
    }

    public void setAreClientCareTeamMembersAccessible(boolean areClientCareTeamMembersAccessible) {
        this.areClientCareTeamMembersAccessible = areClientCareTeamMembersAccessible;
    }

    public boolean getAreCommunityCareTeamMembersAccessible() {
        return areCommunityCareTeamMembersAccessible;
    }

    public void setAreCommunityCareTeamMembersAccessible(boolean areCommunityCareTeamMembersAccessible) {
        this.areCommunityCareTeamMembersAccessible = areCommunityCareTeamMembersAccessible;
    }

    public boolean getAreContactsAccessible() {
        return areContactsAccessible;
    }

    public void setAreContactsAccessible(boolean areContactsAccessible) {
        this.areContactsAccessible = areContactsAccessible;
    }
}
