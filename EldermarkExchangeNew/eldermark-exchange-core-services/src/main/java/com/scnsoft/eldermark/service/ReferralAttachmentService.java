package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.ReferralIdAware;
import com.scnsoft.eldermark.entity.referral.ReferralAttachment;

public interface ReferralAttachmentService extends BaseAttachmentService<ReferralAttachment, Long> {

    ReferralIdAware findReferralIdAwareById(Long id);
}
