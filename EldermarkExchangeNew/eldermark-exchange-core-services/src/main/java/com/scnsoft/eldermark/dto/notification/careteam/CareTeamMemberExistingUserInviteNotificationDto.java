package com.scnsoft.eldermark.dto.notification.careteam;


public class CareTeamMemberExistingUserInviteNotificationDto extends BaseCareTeamMemberInviteNotificationDto {

    private String invitationLink;

    public String getInvitationLink() {
        return invitationLink;
    }

    public void setInvitationLink(String invitationLink) {
        this.invitationLink = invitationLink;
    }

}
