package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.referral.ReferralCommunicationListItemDto;
import com.scnsoft.eldermark.dto.referral.ReferralCommunicationStatus;
import com.scnsoft.eldermark.entity.referral.ReferralInfoRequest;
import com.scnsoft.eldermark.service.ReferralService;
import com.scnsoft.eldermark.service.security.ReferralSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class RefCommunicationListConverter implements Converter<ReferralInfoRequest, ReferralCommunicationListItemDto> {

    @Autowired
    private ReferralService referralService;

    @Autowired
    private ReferralSecurityService referralSecurityService;

    @Override
    public ReferralCommunicationListItemDto convert(ReferralInfoRequest source) {
        var target = new ReferralCommunicationListItemDto();

        target.setId(source.getId());
        target.setRequestDate(DateTimeUtils.toEpochMilli(source.getRequestDatetime()));
        target.setAuthor(source.getRequesterName());
        target.setSubject(source.getSubject());

        var status = source.getResponseDatetime() == null ? ReferralCommunicationStatus.PENDING : ReferralCommunicationStatus.REPLIED;
        target.setStatusName(status.name());
        target.setStatusTitle(status.getDisplayName());

        target.setRequestAvailable(referralService.isVisibleInboundByStatus(source.getReferralRequest()));

        target.setCanRespond(ReferralCommunicationStatus.PENDING.equals(status) &&
                target.getRequestAvailable() &&
                referralSecurityService.canRespondToInfoRequest(source.getId()));

        return target;
    }
}
