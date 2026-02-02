package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dao.referral.ReferralRequestListItemAware;
import com.scnsoft.eldermark.dto.referral.ReferralListItemDto;
import com.scnsoft.eldermark.entity.referral.ReferralResponse;
import com.scnsoft.eldermark.entity.referral.ReferralStatus;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class InboundReferralListItemDtoConverter implements Converter<ReferralRequestListItemAware, ReferralListItemDto> {

    @Override
    public ReferralListItemDto convert(ReferralRequestListItemAware source) {
        var target = new ReferralListItemDto();

        target.setId(source.getReferralId());
        if (source.getReferralClientId() != null) {
            target.setName(CareCoordinationUtils.getFullName(
                    source.getReferralClientFirstName(),
                    source.getReferralClientLastName()
            ));
        } else {
            target.setName(CareCoordinationUtils.getFullName(
                    source.getReferralRequestingEmployeeFirstName(),
                    source.getReferralRequestingEmployeeLastName()
            ));
        }
        target.setServiceTitle(source.getReferralServiceName());
        target.setDate(source.getReferralRequestDatetime().toEpochMilli());
        target.setPriorityName(source.getReferralPriorityCode());
        target.setPriorityTitle(source.getReferralPriorityDisplayName());

        target.setRequestId(source.getId());
        target.setReferredBy(source.getReferralRequestingCommunityName());

        var status = resolveInboundStatus(source);
        target.setStatusName(status.name());
        target.setStatusTitle(status.getDisplayName());

        return target;
    }

    private ReferralStatus resolveInboundStatus(ReferralRequestListItemAware source) {
        if (ReferralResponse.DECLINED.equals(source.getLastResponseResponse())) {
            return source.getLastResponseResponse().getReferralStatus();
        } else {
            return source.getReferralReferralStatus();
        }
    }
}

