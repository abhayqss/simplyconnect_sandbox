package com.scnsoft.eldermark.mobile.converters.careteam.invitation;

import com.scnsoft.eldermark.entity.careteam.invitation.ConfirmInviteCareTeamMemberData;
import com.scnsoft.eldermark.web.commons.dto.careteam.CareTeamInvitationConfirmDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class ConfirmInviteCareTeamMemberDataConverter extends BaseInviteCareTeamMemberDataConverter<CareTeamInvitationConfirmDto, ConfirmInviteCareTeamMemberData> {

    @Override
    public ConfirmInviteCareTeamMemberData convert(CareTeamInvitationConfirmDto source) {
        var data = new ConfirmInviteCareTeamMemberData();

        fillBaseData(source, data);

        data.setToken(source.getToken());
        data.setMobilePhone(source.getMobilePhone());
        data.setPassword(source.getPassword());

        return data;
    }
}
