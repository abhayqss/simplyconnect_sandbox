package com.scnsoft.eldermark.converter;

import com.scnsoft.eldermark.dto.notification.BaseFaxNotificationDto;
import com.scnsoft.eldermark.entity.referral.ReferralRequestNotification;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ReferralRequestNotificationFaxDtoConverter implements Converter<ReferralRequestNotification, BaseFaxNotificationDto> {

    @Override
    public BaseFaxNotificationDto convert(ReferralRequestNotification source) {
        var target = new BaseFaxNotificationDto();
        target.setReceiverFullName(source.getReferralRequest().getCommunity().getName());
        target.setFrom(source.getEmployee().getFullName());
        target.setFaxNumber(source.getDestination());
        target.setMobilePhone(source.getReferralRequest().getSharedPhone());
        target.setDate(source.getCreatedDatetime());
        target.setSubject("Referral Request");
        return target;
    }
}
