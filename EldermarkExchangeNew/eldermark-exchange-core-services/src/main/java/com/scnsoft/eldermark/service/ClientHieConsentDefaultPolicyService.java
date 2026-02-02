package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

public interface ClientHieConsentDefaultPolicyService {

    void fillDefaultPolicy(Client client);

    void fillDefaultPolicy(Client client, String policyObtainedFrom);

     HieConsentPolicyType resolveDefaultHieConsentPolicyType(Long communityId);
}
