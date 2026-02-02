package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.entity.IdNamesAware;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyObtainedBy;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicySource;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

import java.time.Instant;

public interface HieConsentPolicyDetailsAware extends IdNamesAware, OrganizationIdAware, CommunityIdNameAware, ActiveAware {
    String getHieConsentPolicyObtainedFrom();
    HieConsentPolicyObtainedBy getHieConsentPolicyObtainedBy();
    HieConsentPolicySource getHieConsentPolicySource();
    HieConsentPolicyType getHieConsentPolicyType();
    Instant getHieConsentPolicyUpdateDateTime();
    Instant getLastUpdated();
}
