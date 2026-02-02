package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.beans.projection.HieConsentChangeCommunityInvitationProjection;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.hiepolicy.HieConsentChangeCareTeamInvitationListItemDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class HieConsentChangeCareTeamInvitationListItemDtoConverter implements ListAndItemConverter<HieConsentChangeCommunityInvitationProjection, HieConsentChangeCareTeamInvitationListItemDto> {
    @Override
    public HieConsentChangeCareTeamInvitationListItemDto convert(HieConsentChangeCommunityInvitationProjection source) {
        var target = new HieConsentChangeCareTeamInvitationListItemDto();

        target.setClientId(source.getClientId());
        target.setClientFirstName(source.getClientFirstName());
        target.setClientLastName(source.getClientLastName());
        target.setTargetEmployeeId(source.getTargetEmployeeId());
        target.setTargetFirstName(source.getTargetEmployeeFirstName());
        target.setTargetLastName(source.getTargetEmployeeLastName());

        return target;
    }
}
