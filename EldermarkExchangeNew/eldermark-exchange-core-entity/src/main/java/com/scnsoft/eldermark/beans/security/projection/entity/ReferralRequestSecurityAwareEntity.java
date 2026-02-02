package com.scnsoft.eldermark.beans.security.projection.entity;

import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.beans.projection.ReferralIdAware;
import com.scnsoft.eldermark.beans.projection.ReferralRequestSharedChannelAware;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

public interface ReferralRequestSecurityAwareEntity extends CommunityIdAware, ReferralIdAware, ReferralRequestSharedChannelAware {
    HieConsentPolicyType getReferralClientHieConsentPolicyType();
    Long getReferralClientId();
}
