package com.scnsoft.eldermark.dto.notification.careteam;

public class CareTeamMemberCancelledInvitationSenderNotificationDto {

    private String receiverEmail;
    private String subject;
    private String receiverName;
    private String invitationReceiverName;
    private String clientName;

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

    public String getInvitationReceiverName() {
        return invitationReceiverName;
    }

    public void setInvitationReceiverName(String invitationReceiverName) {
        this.invitationReceiverName = invitationReceiverName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
