package com.scnsoft.eldermark.service.careteam.invitation;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.careteam.CareTeamMemberNotificationPreferences;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.service.CareTeamMemberService;
import com.scnsoft.eldermark.service.CareTeamRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
public class ClientCareTeamInvitationClientCareTeamMemberFactoryImpl implements ClientCareTeamInvitationClientCareTeamMemberFactory {

    @Autowired
    private CareTeamMemberService careTeamMemberService;

    @Autowired
    private CareTeamRoleService careTeamRoleService;

    @Override
    public ClientCareTeamMember createCareTeamMember(Employee employee, Client client, Long performedById) {
        var rctm = new ClientCareTeamMember();

        rctm.setEmployee(employee);
        rctm.setEmployeeId(employee.getId());

        rctm.setCareTeamRole(careTeamRoleService.get(CareTeamRoleCode.ROLE_PARENT_GUARDIAN));

        rctm.setClient(client);
        rctm.setClientId(client.getId());
        addDefaultNotificationPreferences(rctm);

        return careTeamMemberService.save(rctm, performedById);
    }

    private void addDefaultNotificationPreferences(ClientCareTeamMember rctm) {
        var npXref = careTeamMemberService.getResponsibilitiesForRole(rctm.getCareTeamRole().getId());

        var preferences = npXref.stream()
                .flatMap(xref -> careTeamMemberService.defaultNotificationChannels().stream()
                        .map(channel -> create(channel, rctm, xref)))
                .collect(Collectors.toList());

        rctm.setNotificationPreferences(preferences);
    }

    private CareTeamMemberNotificationPreferences create(NotificationType channel, ClientCareTeamMember clientCareTeamMember, EventTypeCareTeamRoleXref xref) {
        var np = new CareTeamMemberNotificationPreferences();
        np.setCareTeamMember(clientCareTeamMember);

        np.setNotificationType(channel);
        np.setResponsibility(xref.getResponsibility());
        np.setEventType(xref.getEventType());

        return np;
    }
}
