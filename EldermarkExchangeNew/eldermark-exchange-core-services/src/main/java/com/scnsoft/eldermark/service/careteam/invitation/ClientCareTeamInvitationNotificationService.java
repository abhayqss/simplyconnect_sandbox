package com.scnsoft.eldermark.service.careteam.invitation;


interface ClientCareTeamInvitationNotificationService {

    void sendInvitationNotificationsAsync(Long clientCareTeamInvitationId);

    void sendCancelledNotificationsAsync(Long clientCareTeamInvitationId);
}
