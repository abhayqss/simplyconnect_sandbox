package com.scnsoft.eldermark.service.task;

import com.scnsoft.eldermark.service.InvitationService;
import com.scnsoft.eldermark.service.PasswordService;
import com.scnsoft.eldermark.service.careteam.invitation.ClientCareTeamInvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@ConditionalOnProperty(value = "invitation.expiration.enable", havingValue = "true")
@Service
public class EmployeeRequestCleanerImpl implements EmployeeRequestCleaner {

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private ClientCareTeamInvitationService clientCareTeamInvitationService;

    @Scheduled(cron = "${invitation.expiration.cron.expression}")
    @Transactional
    public void cleanExpiredInvitation() {
        invitationService.expireInvitations();
        passwordService.clearResetPasswordRequests();
        clientCareTeamInvitationService.expireInvitations();
    }
}
