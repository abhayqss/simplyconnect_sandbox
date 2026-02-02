package com.scnsoft.eldermark.service.careteam.invitation;

import com.scnsoft.eldermark.dao.careteam.invitation.ClientCareTeamInvitationDao;
import com.scnsoft.eldermark.dto.notification.PushNotificationVO;
import com.scnsoft.eldermark.dto.notification.careteam.*;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.PushNotificationRegistration;
import com.scnsoft.eldermark.entity.careteam.invitation.ClientCareTeamInvitation;
import com.scnsoft.eldermark.service.UrlService;
import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import com.scnsoft.eldermark.service.pushnotification.PushNotificationFactory;
import com.scnsoft.eldermark.service.pushnotification.PushNotificationService;
import com.scnsoft.eldermark.service.pushnotification.PushNotificationType;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class ClientCareTeamInvitationNotificationServiceImpl implements ClientCareTeamInvitationNotificationService {

    @Autowired
    private ExchangeMailService exchangeMailService;

    @Autowired
    private ClientCareTeamInvitationDao clientCareTeamInvitationDao;

    @Autowired
    private UrlService urlService;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Value("${portal.url}")
    private String portalUrl;

    @Value("${portal.backend.url}")
    private String portalBackendUrl;

    private int hoursInvitationExpiration;

    @Value("${invitation.expiration.time.ms:0}")
    public void setHoursInvitationExpiration(int hoursInvitationExpiration) {
        if (hoursInvitationExpiration < 0) {
            throw new IllegalArgumentException("Invalid \"invitation.expiration.time.ms\" property: expected a positive number or 0, but got " +
                    hoursInvitationExpiration + ".");
        }
        this.hoursInvitationExpiration = hoursInvitationExpiration / (1000 * 60 * 60);
    }

    @Override
    @Async
    @Transactional
    public void sendInvitationNotificationsAsync(Long clientCareTeamInvitationId) {
        var invitation = clientCareTeamInvitationDao.findById(clientCareTeamInvitationId).orElseThrow();

        if (StringUtils.isEmpty(invitation.getToken())) {
            var invitationEmailDto = createExistingUserInvitationEmailDto(invitation);
            exchangeMailService.sendExistingFamilyMemberInvitationNotification(invitationEmailDto);
            pushNotificationService.sendAndWait(createPushNotificationVo(invitation));
        } else {
            var invitationEmailDto = createNewUserInvitationEmailDto(invitation);
            exchangeMailService.sendInviteNewFamilyMemberNotification(invitationEmailDto);
        }
    }

    private PushNotificationVO createPushNotificationVo(ClientCareTeamInvitation invitation) {

        var vo = PushNotificationFactory.builder(PushNotificationType.NEW_CARE_TEAM_INVITATION)
                .receiver(PushNotificationRegistration.Application.SCM, invitation.getTargetEmployeeId())
                .build();

        vo.getPayload().put("invitationId", invitation.getId().toString());
        vo.getPayload().put("clientFirstName", invitation.getClient().getFirstName());
        vo.getPayload().put("clientLastName", invitation.getClient().getLastName());

        if (invitation.getClient().getAvatar() != null) {
            vo.getPayload().put("clientAvatarId", invitation.getClient().getAvatar().getId().toString());
            vo.getPayload().put("clientAvatarName", invitation.getClient().getAvatar().getAvatarName());
        }

        return vo;
    }

    private CareTeamMemberExistingUserInviteNotificationDto createExistingUserInvitationEmailDto(ClientCareTeamInvitation invitation) {
        var dto = new CareTeamMemberExistingUserInviteNotificationDto();

        fillBaseInvitationEmailDto(invitation, dto);

        dto.setInvitationLink(urlService.careTeamInvitationUrl(invitation.getId()));

        return dto;
    }

    private CareTeamMemberNewUserInviteNotificationDto createNewUserInvitationEmailDto(ClientCareTeamInvitation invitation) {
        var dto = new CareTeamMemberNewUserInviteNotificationDto();

        fillBaseInvitationEmailDto(invitation, dto);

        dto.setCreateAccountLink(buildCreateAccountLink(invitation));

        return dto;
    }

    private void fillBaseInvitationEmailDto(ClientCareTeamInvitation invitation, BaseCareTeamMemberInviteNotificationDto dto) {
        dto.setSubject("You have been invited to join the care team");
        dto.setReceiverEmail(invitation.getEmail());
        dto.setReceiverName(invitation.getTargetEmployee().getFullName());
        dto.setClientName(invitation.getClient().getFullName());
        dto.setInvitationExpiresInHours(hoursInvitationExpiration);
    }

    private String buildCreateAccountLink(ClientCareTeamInvitation invitation) {
        return String.format("%scare-team-invitations/email/create-account?token=%s",
                portalBackendUrl,
                urlEncode(invitation.getToken())
        );
    }

    private String urlEncode(String src) {
        return URLEncoder.encode(src, StandardCharsets.UTF_8);
    }

    @Override
    @Async
    @Transactional
    public void sendCancelledNotificationsAsync(Long clientCareTeamInvitationId) {
        var invitation = clientCareTeamInvitationDao.findById(clientCareTeamInvitationId).orElseThrow();
        exchangeMailService.sendFamilyMemberInvitationCancelledSenderNotification(
                createCancelledForInvitationSenderNotificationDto(invitation)
        );

        exchangeMailService.sendFamilyMemberInvitationCancelledRecipientNotification(
                createCancelledForInvitationRecipientNotificationDto(invitation)
        );
    }

    private CareTeamMemberCancelledInvitationSenderNotificationDto createCancelledForInvitationSenderNotificationDto(
            ClientCareTeamInvitation invitation) {

        var dto = new CareTeamMemberCancelledInvitationSenderNotificationDto();

        dto.setSubject("The invitation has been cancelled");
        dto.setReceiverEmail(PersonTelecomUtils.find(invitation.getCreatedByEmployee().getPerson(), PersonTelecomCode.EMAIL)
                .map(PersonTelecom::getValue)
                .orElse(invitation.getCreatedByEmployee().getLoginName()));
        dto.setReceiverName(invitation.getCreatedByEmployee().getFullName());
        dto.setInvitationReceiverName(invitation.getTargetEmployee().getFullName());
        dto.setClientName(invitation.getClient().getFullName());
        return dto;
    }

    private CareTeamMemberCancelledInvitationRecipientNotificationDto createCancelledForInvitationRecipientNotificationDto(
            ClientCareTeamInvitation invitation) {

        var dto = new CareTeamMemberCancelledInvitationRecipientNotificationDto();

        dto.setSubject("The invitation has been cancelled");
        dto.setReceiverEmail(PersonTelecomUtils.find(invitation.getCreatedByEmployee().getPerson(), PersonTelecomCode.EMAIL)
                .map(PersonTelecom::getValue)
                .orElse(invitation.getCreatedByEmployee().getLoginName()));
        dto.setReceiverName(invitation.getTargetEmployee().getFullName());
        dto.setClientName(invitation.getClient().getFullName());
        return dto;
    }
}
