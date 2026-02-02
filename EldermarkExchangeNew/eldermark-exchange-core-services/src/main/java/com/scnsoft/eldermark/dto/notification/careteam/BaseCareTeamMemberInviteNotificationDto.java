package com.scnsoft.eldermark.dto.notification.careteam;

public class BaseCareTeamMemberInviteNotificationDto {
    private String receiverEmail;
    private String subject;
    private String receiverName;
    private String clientName;
    private int invitationExpiresInHours;

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getInvitationExpiresInHours() {
        return invitationExpiresInHours;
    }

    public void setInvitationExpiresInHours(int invitationExpiresInHours) {
        this.invitationExpiresInHours = invitationExpiresInHours;
    }
}
