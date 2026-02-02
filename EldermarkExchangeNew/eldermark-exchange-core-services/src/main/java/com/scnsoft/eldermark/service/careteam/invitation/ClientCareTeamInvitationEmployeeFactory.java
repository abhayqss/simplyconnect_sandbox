package com.scnsoft.eldermark.service.careteam.invitation;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.careteam.invitation.InviteCareTeamMemberData;

interface ClientCareTeamInvitationEmployeeFactory {

    Employee createNewPendingContact(InviteCareTeamMemberData invitationData);

}
