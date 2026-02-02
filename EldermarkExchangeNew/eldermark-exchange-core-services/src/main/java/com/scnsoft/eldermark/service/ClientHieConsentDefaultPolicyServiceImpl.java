package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.CommunityHieConsentPolicyTypeAware;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class ClientHieConsentDefaultPolicyServiceImpl implements ClientHieConsentDefaultPolicyService {

    @Autowired
    private CommunityHieConsentPolicyService communityHieConsentPolicyService;

    @Override
    public void fillDefaultPolicy(Client client) {
        fillDefaultPolicy(client, HieConsentPolicyUpdateService.OBTAINED_FROM_STATE_POLICY_VALUE);
    }

    @Override
    public void fillDefaultPolicy(Client client, String policyObtainedFrom) {
        client.setHieConsentPolicyObtainedFrom(policyObtainedFrom);
        client.setHieConsentPolicyUpdateDateTime(Instant.now());
        client.setHieConsentPolicyType(resolveDefaultHieConsentPolicyType(client.getCommunityId()));
    }

    @Override
    public HieConsentPolicyType resolveDefaultHieConsentPolicyType(Long communityId) {
        return communityHieConsentPolicyService.findByCommunityIdAndArchived(
                        communityId, false, CommunityHieConsentPolicyTypeAware.class)
                .map(CommunityHieConsentPolicyTypeAware::getType)
                .orElse(HieConsentPolicyType.OPT_OUT);
    }
}
