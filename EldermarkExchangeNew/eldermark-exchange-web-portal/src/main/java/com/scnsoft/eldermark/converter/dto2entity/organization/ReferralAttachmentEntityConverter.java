package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.entity.referral.ReferralAttachment;
import org.springframework.stereotype.Component;

@Component
public class ReferralAttachmentEntityConverter extends BaseAttachmentEntityConverter<ReferralAttachment> {

    @Override
    protected ReferralAttachment getAttachment() {
        return new ReferralAttachment();
    }
}