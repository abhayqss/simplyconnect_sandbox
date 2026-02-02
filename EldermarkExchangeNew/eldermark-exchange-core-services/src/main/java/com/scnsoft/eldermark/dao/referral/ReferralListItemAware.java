package com.scnsoft.eldermark.dao.referral;

import com.scnsoft.eldermark.entity.referral.ReferralStatus;

import java.time.Instant;
import java.util.List;

public interface ReferralListItemAware {
    Long getId();
    Long getClientId();
    String getClientFirstName();
    String getClientLastName();
    String getRequestingEmployeeFirstName();
    String getRequestingEmployeeLastName();
    String getServiceName();
    Instant getRequestDatetime();
    List<String> getReferralRequestsCommunityName();
    String getPriorityCode();
    String getPriorityDisplayName();
    ReferralStatus getReferralStatus();
}
