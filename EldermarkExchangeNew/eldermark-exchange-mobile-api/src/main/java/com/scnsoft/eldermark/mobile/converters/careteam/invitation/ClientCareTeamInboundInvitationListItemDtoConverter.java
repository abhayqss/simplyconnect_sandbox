package com.scnsoft.eldermark.mobile.converters.careteam.invitation;

import com.scnsoft.eldermark.mobile.dto.careteam.invitation.ClientCareTeamInboundInvitationListItemDto;
import com.scnsoft.eldermark.mobile.projection.careteam.invitation.ClientCareTeamInboundInvitationListItem;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ClientCareTeamInboundInvitationListItemDtoConverter implements Converter<ClientCareTeamInboundInvitationListItem, ClientCareTeamInboundInvitationListItemDto> {

    @Override
    public ClientCareTeamInboundInvitationListItemDto convert(ClientCareTeamInboundInvitationListItem source) {
        var target = new ClientCareTeamInboundInvitationListItemDto();

        target.setId(source.getId());

        target.setFirstName(source.getClientFirstName());
        target.setLastName(source.getClientLastName());
        target.setCommunityName(source.getClientCommunityName());
        target.setAvatarId(source.getClientAvatarId());
        target.setAvatarName(source.getClientAvatarAvatarName());
        target.setTwilioUserSid(source.getClientAssociatedEmployeeTwilioUserSid());

        target.setClientFirstName(source.getClientFirstName());
        target.setClientLastName(source.getClientLastName());
        target.setClientCommunityName(source.getClientCommunityName());
        target.setCreatedAt(DateTimeUtils.toEpochMilli(source.getCreatedAt()));
        target.setClientAvatarId(source.getClientAvatarId());
        target.setClientAvatarName(source.getClientAvatarAvatarName());
        target.setClientTwilioUserSid(source.getClientAssociatedEmployeeTwilioUserSid());

        return target;
    }
}
