package com.scnsoft.eldermark.mobile.converters.careteam;

import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;
import com.scnsoft.eldermark.mobile.dto.careteam.CareTeamContactDto;
import com.scnsoft.eldermark.mobile.dto.careteam.CareTeamMemberDto;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class CareTeamMemberDtoConverter
        extends BaseCareTeamMemberDtoConverter
        implements Converter<CareTeamMember, CareTeamMemberDto> {

    @Override
    public CareTeamMemberDto convert(CareTeamMember source) {
        var target = new CareTeamMemberDto();
        target.setContact(new CareTeamContactDto());

        fillBaseCareTeamMemberDto(source, false, target);
        fillBaseContact(source.getEmployee(), target.getContact(), target.getIsOnHold());

        Person person = source.getEmployee().getPerson();
        target.getContact().setEmail(PersonTelecomUtils.findValue(person, PersonTelecomCode.EMAIL).orElse(null));
        target.getContact().setCellPhone(PersonTelecomUtils.findValue(person, PersonTelecomCode.MC).orElse(null));
        target.getContact().setOrganizationName(source.getEmployee().getOrganization().getName());

        return target;
    }
}
