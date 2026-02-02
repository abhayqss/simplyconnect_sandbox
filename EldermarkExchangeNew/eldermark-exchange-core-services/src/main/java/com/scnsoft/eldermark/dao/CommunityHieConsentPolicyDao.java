package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.hieconsentpolicy.CommunityHieConsentPolicy;

import java.util.Optional;

public interface CommunityHieConsentPolicyDao extends AppJpaRepository<CommunityHieConsentPolicy, Long> {
    <T> Optional<T> findByCommunityIdAndArchived(Long communityId, Boolean isArchived, Class<T> projectionClass);
}
