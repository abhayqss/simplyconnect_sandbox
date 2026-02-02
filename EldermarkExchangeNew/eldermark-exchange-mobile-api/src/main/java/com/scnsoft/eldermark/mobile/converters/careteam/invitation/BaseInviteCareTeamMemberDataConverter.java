package com.scnsoft.eldermark.mobile.converters.careteam.invitation;

import com.scnsoft.eldermark.entity.careteam.invitation.BaseInviteCareTeamMemberData;
import com.scnsoft.eldermark.web.commons.dto.careteam.BaseCareTeamInvitationDto;
import org.springframework.core.convert.converter.Converter;

public abstract class BaseInviteCareTeamMemberDataConverter<T extends BaseCareTeamInvitationDto, R extends BaseInviteCareTeamMemberData> implements Converter<T, R> {

    protected void fillBaseData(T source, R target) {
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setBirthDate(source.getBirthDate());
    }
}
