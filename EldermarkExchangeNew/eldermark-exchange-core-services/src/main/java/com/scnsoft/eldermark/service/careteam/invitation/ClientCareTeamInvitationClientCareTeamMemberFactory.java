package com.scnsoft.eldermark.service.careteam.invitation;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;

interface ClientCareTeamInvitationClientCareTeamMemberFactory {

    ClientCareTeamMember createCareTeamMember(Employee employee, Client client, Long performedById);

}
