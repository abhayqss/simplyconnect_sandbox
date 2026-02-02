package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.referral.ReferralCommunicationDto;
import com.scnsoft.eldermark.dto.referral.ReferralCommunicationItemDto;
import com.scnsoft.eldermark.dto.referral.ReferralCommunicationStatus;
import com.scnsoft.eldermark.entity.referral.ReferralInfoRequest;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ReferralCommunicationDtoConverter implements Converter<ReferralInfoRequest, ReferralCommunicationDto> {

    @Override
    public ReferralCommunicationDto convert(ReferralInfoRequest source) {
        var target = new ReferralCommunicationDto();

        target.setId(source.getId());
        target.setSubject(source.getSubject());

        var status = source.getResponseDatetime() == null ? ReferralCommunicationStatus.PENDING : ReferralCommunicationStatus.REPLIED;
        target.setStatusName(status.name());
        target.setStatusTitle(status.getDisplayName());

        var request = new ReferralCommunicationItemDto();
        request.setAuthorFullName(source.getRequesterName());
        request.setAuthorPhone(source.getRequesterPhoneNumber());
        request.setDate(DateTimeUtils.toEpochMilli(source.getRequestDatetime()));
        request.setText(source.getRequestMessage());
        target.setRequest(request);

        var response = new ReferralCommunicationItemDto();
        response.setAuthorFullName(source.getResponderName());
        response.setAuthorPhone(source.getResponderPhoneNumber());
        response.setDate(DateTimeUtils.toEpochMilli(source.getResponseDatetime()));
        response.setText(source.getResponseMessage());
        target.setResponse(response);

        return target;
    }

}
