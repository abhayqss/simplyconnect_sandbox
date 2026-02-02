package com.scnsoft.eldermark.dto.client;

public class ProspectivePrimaryContactDto {

    private Long careTeamMemberId;
    private String fullName;
    private boolean hasEmail;
    private boolean chatEnabled;

    public Long getCareTeamMemberId() {
        return careTeamMemberId;
    }

    public void setCareTeamMemberId(Long careTeamMemberId) {
        this.careTeamMemberId = careTeamMemberId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean getHasEmail() {
        return hasEmail;
    }

    public void setHasEmail(boolean hasEmail) {
        this.hasEmail = hasEmail;
    }

    public boolean getChatEnabled() {
        return chatEnabled;
    }

    public void setChatEnabled(boolean chatEnabled) {
        this.chatEnabled = chatEnabled;
    }
}
