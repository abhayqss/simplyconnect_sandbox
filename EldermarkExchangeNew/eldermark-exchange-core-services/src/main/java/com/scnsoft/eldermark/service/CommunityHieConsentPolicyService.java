package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.hieconsentpolicy.CommunityHieConsentPolicy;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.service.basic.AuditableEntityService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommunityHieConsentPolicyService extends AuditableEntityService<CommunityHieConsentPolicy> {

    Optional<CommunityHieConsentPolicy> findByCommunityIdAndArchived(Long communityId, Boolean isArchived);

    <T> List<T> findAllByCommunityIdsAndArchived(Collection<Long> communityIds, boolean isArchived, Class<T> projectionClass);

    boolean saveOrUpdate(Community community, HieConsentPolicyType hieConsentPolicy, Employee currentEmployee);

    void createDefaultStatePolicyIfNotExist(Community community);

    <T> Optional<T> findByCommunityIdAndArchived(Long communityId, Boolean isArchived, Class<T> projectionClass);

    boolean isClientHieConsentPolicyNewerThenCommunityPolicy(Client client);
}
