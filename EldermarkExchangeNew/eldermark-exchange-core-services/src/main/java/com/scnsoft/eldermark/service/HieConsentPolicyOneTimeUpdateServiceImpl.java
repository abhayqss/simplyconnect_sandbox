package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.entity.hieconsentpolicy.CommunityHieConsentPolicy;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HieConsentPolicyOneTimeUpdateServiceImpl implements HieConsentPolicyOneTimeUpdateService {

    private final Logger logger = LoggerFactory.getLogger(HieConsentPolicyOneTimeUpdateServiceImpl.class);

    @Autowired
    private CommunityService communityService;

    @Autowired
    private CommunityHieConsentPolicyService communityHieConsentPolicyService;

    @Autowired
    private HieConsentPolicyUpdateService hieConsentPolicyUpdateService;

    @Override
    public boolean updateHieConsentPolicy() {
        try {
            var communityIds = communityService.findAllEligibleForDiscovery(IdAware.class);
            for (var communityId : CareCoordinationUtils.toIdsSet(communityIds)) {
                logger.info("Update hie consent policy for community id = {}", communityId);
                var policy = communityHieConsentPolicyService.findByCommunityIdAndArchived(communityId, false)
                        .map(CommunityHieConsentPolicy::getType)
                        .orElse(HieConsentPolicyType.OPT_OUT);

                hieConsentPolicyUpdateService.updateOnHoldCareTeamAndChatsConnection(communityId, policy, null);
                hieConsentPolicyUpdateService.updateSignatureRequests(communityId, policy, null);
            }
            return true;
        } catch (Exception e) {
            logger.error("Error during HIE consent policy one time update", e);
            return false;
        }
    }
}
