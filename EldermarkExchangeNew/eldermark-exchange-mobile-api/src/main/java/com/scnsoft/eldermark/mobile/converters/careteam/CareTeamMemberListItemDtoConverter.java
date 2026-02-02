package com.scnsoft.eldermark.mobile.converters.careteam;

import com.scnsoft.eldermark.entity.careteam.CareTeamMember;
import com.scnsoft.eldermark.mobile.dto.careteam.CareTeamContactListItem;
import com.scnsoft.eldermark.mobile.dto.careteam.CareTeamMemberListItemDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.BiFunction;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class CareTeamMemberListItemDtoConverter extends BaseCareTeamMemberDtoConverter
        implements BiFunction<CareTeamMember, Boolean, CareTeamMemberListItemDto> {

    @Override
    public CareTeamMemberListItemDto apply(CareTeamMember source, Boolean isCanDeleteOnly) {
        CareTeamMemberListItemDto target = new CareTeamMemberListItemDto();
        target.setContact(new CareTeamContactListItem());

        fillBaseCareTeamMemberDto(source, isCanDeleteOnly, target);
        fillBaseContact(source.getEmployee(), target.getContact(), target.getIsOnHold());
        return target;
    }
}
