package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ItemConverter;
import com.scnsoft.eldermark.dto.referral.ReferralSharedWithDetailsDto;
import com.scnsoft.eldermark.dto.referral.ReferralSharedWithListItemDto;
import com.scnsoft.eldermark.entity.referral.ReferralRequest;
import com.scnsoft.eldermark.entity.referral.ReferralRequestResponse;
import com.scnsoft.eldermark.entity.referral.ReferralResponse;
import com.scnsoft.eldermark.entity.referral.ReferralStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ReferralSharedWithDetailsDtoConverter implements Converter<ReferralRequest, ReferralSharedWithDetailsDto> {

    @Autowired
    private ItemConverter<ReferralRequest, ReferralSharedWithListItemDto> referralSharedWithListItemDtoConverter;

    @Override
    public ReferralSharedWithDetailsDto convert(ReferralRequest source) {
        var target = new ReferralSharedWithDetailsDto();
        referralSharedWithListItemDtoConverter.convert(source, target);
        if (target.getStatusName().equals(ReferralStatus.DECLINED.name())) {
            target.setDeclineReason(source.getLastResponse().getDeclineReason().getDisplayName());
        }
        target.setComment(source.getLastResponse() != null ? source.getLastResponse().getComment() : null);
        if (source.getResponses() != null) {
            target.setPreAdmitDate(findPreAdmitDate(source));
        }
        return target;
    }

    private Long findPreAdmitDate(ReferralRequest source) {
        //todo use column or drop
        return source.getResponses()
                .stream()
                .filter(response -> response.getResponse().equals(ReferralResponse.PRE_ADMIT))
                .findFirst()
                .map(ReferralRequestResponse::getResponseDatetime)
                .map(Instant::toEpochMilli).orElse(null);
    }
}
