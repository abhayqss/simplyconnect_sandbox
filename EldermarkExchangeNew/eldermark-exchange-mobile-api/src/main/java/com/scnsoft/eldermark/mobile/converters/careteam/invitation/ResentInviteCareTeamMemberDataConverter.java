package com.scnsoft.eldermark.mobile.converters.careteam.invitation;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.entity.careteam.invitation.InviteCareTeamMemberData;
import com.scnsoft.eldermark.service.careteam.invitation.ClientCareTeamInvitationService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.web.commons.dto.careteam.CareTeamInvitationResendDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class ResentInviteCareTeamMemberDataConverter extends BaseInviteCareTeamMemberDataConverter<CareTeamInvitationResendDto, InviteCareTeamMemberData> {

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ClientCareTeamInvitationService clientCareTeamInvitationService;

    @Override
    public InviteCareTeamMemberData convert(CareTeamInvitationResendDto source) {

        var target = new InviteCareTeamMemberData();

        fillBaseData(source, target);

        var originalInvite = clientCareTeamInvitationService.findById(source.getId(), ClientIdAware.class);

        var currentEmployee = loggedUserService.getCurrentEmployee();
        target.setCreatedByEmployee(currentEmployee);
        target.setClientId(originalInvite.getClientId());
        target.setEmail(source.getEmail());

        return target;
    }
}
