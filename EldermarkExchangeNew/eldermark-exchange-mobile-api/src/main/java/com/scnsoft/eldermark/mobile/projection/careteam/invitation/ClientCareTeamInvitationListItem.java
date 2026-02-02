package com.scnsoft.eldermark.mobile.projection.careteam.invitation;

import com.scnsoft.eldermark.beans.projection.ClientCareTeamInvitationCreatedAtAware;
import com.scnsoft.eldermark.beans.projection.ClientCareTeamInvitationStatusAware;
import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;

public interface ClientCareTeamInvitationListItem extends IdAware, ClientIdAware,
        ClientCareTeamInvitationStatusAware,
        ClientCareTeamInvitationCreatedAtAware,
        TargetEmployeeInvitationDataAware {

    String getFirstName();

    String getLastName();

}
