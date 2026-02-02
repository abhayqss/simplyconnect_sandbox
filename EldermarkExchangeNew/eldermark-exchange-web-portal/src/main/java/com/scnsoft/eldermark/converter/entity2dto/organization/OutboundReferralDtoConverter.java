package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.referral.ReferralDto;
import com.scnsoft.eldermark.entity.referral.Referral;
import com.scnsoft.eldermark.entity.referral.ReferralAction;
import com.scnsoft.eldermark.service.ReferralService;
import com.scnsoft.eldermark.service.security.ReferralSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class OutboundReferralDtoConverter extends BaseReferralDtoConverter implements Converter<Referral, ReferralDto> {

    @Autowired
    private ReferralService referralService;

    @Autowired
    private ReferralSecurityService referralSecurityService;

    @Override
    public ReferralDto convert(Referral source) {
        var target = new ReferralDto();
        fill(source, target);

        target.setStatusName(source.getReferralStatus().name());
        target.setStatusTitle(source.getReferralStatus().getDisplayName());

        target.setCanCancel(referralService.isAvailableAction(source, ReferralAction.CANCEL) && referralSecurityService.canCancel(source.getId()));
        var referralRequestId = source.getReferralRequestIds().stream()
                .min(Long::compare)
                .orElseThrow();
        target.setCanAccept(referralService.isAvailableAction(source, ReferralAction.ACCEPT) && referralSecurityService.canAccept(referralRequestId));
        target.setCanDecline(referralService.isAvailableAction(source, ReferralAction.DECLINE) && referralSecurityService.canDecline(referralRequestId));

        if (target.getCanAccept() || target.getCanDecline()) {
            target.setReferralRequestId(referralRequestId);
        }

        return target;
    }
}
