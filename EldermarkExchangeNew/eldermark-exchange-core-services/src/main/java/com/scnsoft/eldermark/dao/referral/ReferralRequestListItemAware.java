package com.scnsoft.eldermark.dao.referral;

import com.scnsoft.eldermark.entity.referral.ReferralResponse;
import com.scnsoft.eldermark.entity.referral.ReferralStatus;

import java.time.Instant;

public interface ReferralRequestListItemAware {
    Long getId();
    Long getReferralId();
    Long getReferralClientId();
    String getReferralClientFirstName();
    String getReferralClientLastName();
    String getReferralRequestingEmployeeFirstName();
    String getReferralRequestingEmployeeLastName();
    String getReferralRequestingCommunityName();
    String getReferralServiceName();
    Instant getReferralRequestDatetime();
    String getReferralPriorityCode();
    String getReferralPriorityDisplayName();
    ReferralResponse getLastResponseResponse();
    ReferralStatus getReferralReferralStatus();
}
