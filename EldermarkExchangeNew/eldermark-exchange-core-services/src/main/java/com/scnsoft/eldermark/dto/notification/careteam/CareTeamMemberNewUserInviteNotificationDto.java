package com.scnsoft.eldermark.dto.notification.careteam;


public class CareTeamMemberNewUserInviteNotificationDto extends BaseCareTeamMemberInviteNotificationDto {
    private String createAccountLink;

    public String getCreateAccountLink() {
        return createAccountLink;
    }

    public void setCreateAccountLink(String createAccountLink) {
        this.createAccountLink = createAccountLink;
    }
}
