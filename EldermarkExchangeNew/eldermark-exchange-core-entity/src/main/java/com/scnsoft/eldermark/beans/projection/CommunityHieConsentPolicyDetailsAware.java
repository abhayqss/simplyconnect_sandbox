package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

import java.time.Instant;

public interface CommunityHieConsentPolicyDetailsAware extends CommunityIdAware {
    HieConsentPolicyType getType();
    Instant getLastModifiedDate();
}
