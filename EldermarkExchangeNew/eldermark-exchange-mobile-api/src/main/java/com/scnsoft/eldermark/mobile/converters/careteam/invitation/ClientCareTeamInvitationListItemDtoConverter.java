package com.scnsoft.eldermark.mobile.converters.careteam.invitation;

import com.scnsoft.eldermark.entity.careteam.invitation.ClientCareTeamInvitationStatus;
import com.scnsoft.eldermark.mobile.dto.careteam.invitation.ClientCareTeamInvitationListItemDto;
import com.scnsoft.eldermark.mobile.projection.careteam.invitation.ClientCareTeamInvitationListItem;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ClientCareTeamInvitationListItemDtoConverter implements Converter<ClientCareTeamInvitationListItem, ClientCareTeamInvitationListItemDto> {

    @Value("${invitation.expiration.time.ms}")
    private Long expirationTimeMs;

    @Override
    public ClientCareTeamInvitationListItemDto convert(ClientCareTeamInvitationListItem source) {
        var target = new ClientCareTeamInvitationListItemDto();

        target.setId(source.getId());
        target.setRecipientEmployeeId(source.getTargetEmployeeId());
        target.setRecipientAvatarId(source.getTargetEmployeeAvatarId());
        target.setRecipientAvatarName(source.getTargetEmployeeAvatarAvatarName());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());

        if (source.getStatus() == ClientCareTeamInvitationStatus.PENDING) {
            target.setCreatedAt(DateTimeUtils.toEpochMilli(source.getCreatedAt()));
        }

        //todo refactor status logic (the same logic in details dto converter)
        var expirationTime = source.getCreatedAt().plus(expirationTimeMs, ChronoUnit.MILLIS);
        var status = source.getStatus();
        if (expirationTime.isBefore(Instant.now()) && source.getStatus().equals(ClientCareTeamInvitationStatus.PENDING)) {
            status = ClientCareTeamInvitationStatus.EXPIRED;
        }
        target.setStatusName(status.name());
        target.setStatusTitle(status.getDisplayName());

        return target;
    }
}
