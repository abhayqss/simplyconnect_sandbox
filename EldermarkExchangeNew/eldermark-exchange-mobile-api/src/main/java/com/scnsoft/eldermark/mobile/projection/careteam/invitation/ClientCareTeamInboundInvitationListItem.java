package com.scnsoft.eldermark.mobile.projection.careteam.invitation;

import com.scnsoft.eldermark.beans.projection.*;

public interface ClientCareTeamInboundInvitationListItem extends IdAware,
        ClientNamesAware,
        ClientCommunityNameAware,
        ClientCareTeamInvitationCreatedAtAware,
        ClientAvatarInvitationDataAware {

    String getClientAssociatedEmployeeTwilioUserSid();
}
