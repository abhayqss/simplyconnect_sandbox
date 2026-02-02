package com.scnsoft.eldermark.mobile.converters.careteam.invitation;

import com.scnsoft.eldermark.entity.careteam.invitation.InviteCareTeamMemberData;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.web.commons.dto.careteam.CareTeamInvitationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class InviteCareTeamMemberDataConverter extends BaseInviteCareTeamMemberDataConverter<CareTeamInvitationDto, InviteCareTeamMemberData> {

    @Autowired
    private LoggedUserService loggedUserService;

    @Override
    public InviteCareTeamMemberData convert(CareTeamInvitationDto source) {
        var target = new InviteCareTeamMemberData();

        fillBaseData(source, target);

        var currentEmployee = loggedUserService.getCurrentEmployee();
        target.setCreatedByEmployee(currentEmployee);
        target.setClientId(source.getClientId());
        target.setEmail(source.getEmail());

        return target;
    }
}
