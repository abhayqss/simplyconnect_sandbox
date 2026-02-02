package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dao.referral.ReferralListItemAware;
import com.scnsoft.eldermark.dto.referral.ReferralListItemDto;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OutboundReferralListItemDtoConverter implements Converter<ReferralListItemAware, ReferralListItemDto> {

    @Override
    public ReferralListItemDto convert(ReferralListItemAware source) {
        var target = new ReferralListItemDto();

        target.setId(source.getId());
        if (source.getClientId() != null) {
            target.setName(CareCoordinationUtils.getFullName(
                    source.getClientFirstName(),
                    source.getClientLastName()
            ));
        } else {
            target.setName(CareCoordinationUtils.getFullName(
                    source.getRequestingEmployeeFirstName(),
                    source.getRequestingEmployeeLastName()
            ));
        }
        target.setServiceTitle(source.getServiceName());
        target.setDate(source.getRequestDatetime().toEpochMilli());
        target.setPriorityName(source.getPriorityCode());
        target.setPriorityTitle(source.getPriorityDisplayName());
        target.setReferredTo(source.getReferralRequestsCommunityName());
        target.setStatusName(source.getReferralStatus().name());
        target.setStatusTitle(source.getReferralStatus().getDisplayName());

        return target;
    }
}
