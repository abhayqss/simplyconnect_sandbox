package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.referral.ReferralDto;
import com.scnsoft.eldermark.entity.referral.ReferralAction;
import com.scnsoft.eldermark.entity.referral.ReferralRequest;
import com.scnsoft.eldermark.entity.referral.ReferralStatus;
import com.scnsoft.eldermark.service.ReferralService;
import com.scnsoft.eldermark.service.security.ReferralSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InboundReferralDtoConverter extends BaseReferralDtoConverter implements Converter<ReferralRequest, ReferralDto> {

    @Autowired
    private ReferralService referralService;

    @Autowired
    private ReferralSecurityService referralSecurityService;

    @Override
    public ReferralDto convert(ReferralRequest source) {
        var target = new ReferralDto();
        fill(source.getReferral(), target);

        target.setRequestId(source.getId());

        var status = resolveInboundStatus(source);
        target.setStatusName(status.name());
        target.setStatusTitle(status.getDisplayName());
        if (target.getStatusName().equals(ReferralStatus.DECLINED.name())) {
            target.setDeclineReason(source.getLastResponse().getDeclineReason().getDisplayName());
        }
        target.setComment(source.getLastResponse() != null ? source.getLastResponse().getComment() : null);
        if (source.getAssignedEmployee() != null) {
            target.setAssigneeId(source.getAssignedEmployee().getId());
            target.setAssigneeName(source.getAssignedEmployee().getFullName());
        }

        if (target.getStatusName().equals(ReferralStatus.ACCEPTED.name())) {
            target.setServiceStartDate(DateTimeUtils.toEpochMilli(source.getLastResponse().getServiceStartDate()));
            target.setServiceEndDate(DateTimeUtils.toEpochMilli(source.getLastResponse().getServiceEndDate()));
        }

        target.setCanRequestInfo(referralService.isAvailableAction(source, ReferralAction.REQUEST_INFO)
                && referralSecurityService.canRequestInfo(source.getId()));
        target.setCanPreadmit(referralService.isAvailableAction(source, ReferralAction.PRE_ADMIT)
                && referralSecurityService.canPreadmit(source.getId()));
        target.setCanAccept(referralService.isAvailableAction(source, ReferralAction.ACCEPT)
                && referralSecurityService.canAccept(source.getId()));
        target.setCanDecline(referralService.isAvailableAction(source, ReferralAction.DECLINE)
                && referralSecurityService.canDecline(source.getId()));
        target.setCanAssign(referralService.isAvailableAction(source, ReferralAction.ASSIGN)
                && referralSecurityService.canReassign(source.getId()));

        return target;
    }
}
