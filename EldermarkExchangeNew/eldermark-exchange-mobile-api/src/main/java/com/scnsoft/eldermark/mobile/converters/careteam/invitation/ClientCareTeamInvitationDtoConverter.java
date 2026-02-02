package com.scnsoft.eldermark.mobile.converters.careteam.invitation;

import com.scnsoft.eldermark.entity.careteam.invitation.ClientCareTeamInvitationStatus;
import com.scnsoft.eldermark.mobile.dto.careteam.invitation.ClientCareTeamInvitationDto;
import com.scnsoft.eldermark.mobile.projection.careteam.invitation.ClientCareTeamInvitationDetails;
import com.scnsoft.eldermark.service.security.ClientCareTeamInvitationSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ClientCareTeamInvitationDtoConverter implements Converter<ClientCareTeamInvitationDetails, ClientCareTeamInvitationDto> {

    @Autowired
    private ClientCareTeamInvitationSecurityService clientCareTeamInvitationSecurityService;

    @Value("${invitation.expiration.time.ms}")
    private Long expirationTimeMs;

    @Override
    public ClientCareTeamInvitationDto convert(ClientCareTeamInvitationDetails source) {
        var target = new ClientCareTeamInvitationDto();

        target.setId(source.getId());

        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setBirthDate(source.getBirthDate());
        target.setEmail(source.getEmail());

        var expirationTime = source.getCreatedAt().plus(expirationTimeMs, ChronoUnit.MILLIS);
        target.setExpirationTime(DateTimeUtils.toEpochMilli(expirationTime));

        var status = source.getStatus();
        if (expirationTime.isBefore(Instant.now()) && source.getStatus().equals(ClientCareTeamInvitationStatus.PENDING)) {
            status = ClientCareTeamInvitationStatus.EXPIRED;
        }
        target.setStatusName(status.name());
        target.setStatusTitle(status.getDisplayName());

        target.setRecipientEmployeeId(source.getTargetEmployeeId());
        target.setRecipientAvatarId(source.getTargetEmployeeAvatarId());
        target.setRecipientAvatarName(source.getTargetEmployeeAvatarAvatarName());

        target.setClientFirstName(source.getClientFirstName());
        target.setClientLastName(source.getClientLastName());
        target.setClientAvatarId(source.getClientAvatarId());
        target.setClientAvatarName(source.getClientAvatarAvatarName());

        target.setCanResend(clientCareTeamInvitationSecurityService.canResend(source.getId()));
        target.setCanCancel(clientCareTeamInvitationSecurityService.canCancel(source.getId()));
        target.setCanAcceptOrDecline(clientCareTeamInvitationSecurityService.canAcceptOrDecline(source.getId()));

        return target;
    }
}
