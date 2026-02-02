package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.CommunityHieConsentPolicyLastModifiedDateAware;
import com.scnsoft.eldermark.dao.CommunityHieConsentPolicyDao;
import com.scnsoft.eldermark.dao.specification.CommunityHieConsentPolicySpecificationGenerator;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.hieconsentpolicy.CommunityHieConsentPolicy;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.service.basic.BaseAuditableService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class CommunityHieConsentPolicyServiceImpl extends BaseAuditableService<CommunityHieConsentPolicy> implements CommunityHieConsentPolicyService {

    @Autowired
    private CommunityHieConsentPolicyDao communityHieConsentPolicyDao;

    @Autowired
    private StateService stateService;

    @Autowired
    private CommunityHieConsentPolicySpecificationGenerator communityHieConsentPolicySpecificationGenerator;

    @Override
    @Transactional(readOnly = true)
    public Optional<CommunityHieConsentPolicy> findByCommunityIdAndArchived(Long communityId, Boolean isArchived) {
        return communityHieConsentPolicyDao.findOne(
                communityHieConsentPolicySpecificationGenerator.byArchived(isArchived)
                        .and(communityHieConsentPolicySpecificationGenerator.byCommunityId(communityId))
        );
    }

    @Override
    public <T> List<T> findAllByCommunityIdsAndArchived(Collection<Long> communityIds, boolean isArchived, Class<T> projectionClass) {
        return communityHieConsentPolicyDao.findAll(
                communityHieConsentPolicySpecificationGenerator.byArchived(isArchived)
                        .and(communityHieConsentPolicySpecificationGenerator.byCommunityIds(communityIds)),
                projectionClass
        );
    }

    @Override
    public CommunityHieConsentPolicy save(CommunityHieConsentPolicy entity) {
        return communityHieConsentPolicyDao.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public CommunityHieConsentPolicy findById(Long id) {
        return communityHieConsentPolicyDao.findById(id).orElseThrow();
    }

    @Override
    public CommunityHieConsentPolicy createTransientClone(CommunityHieConsentPolicy entity) {
        var clone = new CommunityHieConsentPolicy();
        clone.setType(entity.getType());
        clone.setCommunity(entity.getCommunity());
        clone.setCommunityId(entity.getCommunityId());
        clone.setCreatorId(entity.getCreatorId());
        clone.setCreator(entity.getCreator());
        clone.setLastModifiedDate(entity.getLastModifiedDate());
        clone.setArchived(entity.getArchived());
        clone.setAuditableStatus(entity.getAuditableStatus());
        clone.setChainId(entity.getChainId());
        clone.setId(entity.getId());
        return clone;
    }

    @Override
    @Transactional
    public boolean saveOrUpdate(Community community, HieConsentPolicyType hieConsentPolicy, Employee currentEmployee) {
        return findByCommunityIdAndArchived(community.getId(), false)
                .map(policy -> {
                            if (!policy.getType().equals(hieConsentPolicy)) {
                                var updatedPolicy = new CommunityHieConsentPolicy();
                                updatedPolicy.setId(policy.getId());
                                updatedPolicy.setCreatorId(currentEmployee.getId());
                                updatedPolicy.setCreator(currentEmployee);
                                updatedPolicy.setType(hieConsentPolicy);
                                updatedPolicy.setCommunityId(policy.getCommunityId());
                                updatedPolicy.setCommunity(policy.getCommunity());
                                updateAuditableEntity(updatedPolicy);
                                return true;
                            } else {
                                return false;
                            }
                        }
                ).orElseGet(() -> {
                    var policy = new CommunityHieConsentPolicy();
                    policy.setCreatorId(currentEmployee.getCreatorId());
                    policy.setCreator(currentEmployee);
                    policy.setType(hieConsentPolicy);
                    policy.setCommunity(community);
                    policy.setCommunityId(community.getId());
                    createAuditableEntity(policy);
                    return true;
                });
    }

    @Override
    public void createDefaultStatePolicyIfNotExist(Community community) {
        var currentPolicy = findByCommunityIdAndArchived(community.getId(), false);

        if (currentPolicy.isEmpty() && CollectionUtils.isNotEmpty(community.getAddresses())) {
            var communityAddress = community.getAddresses().get(0);
            var newPolicy = new CommunityHieConsentPolicy();
            newPolicy.setType(stateService.findByAbbr(communityAddress.getState()).getHieConsentPolicy());
            newPolicy.setCommunity(community);
            newPolicy.setCommunityId(community.getId());
            createAuditableEntity(newPolicy);
        }
    }

    @Override
    public <T> Optional<T> findByCommunityIdAndArchived(Long communityId, Boolean isArchived, Class<T> projectionClass) {
        return communityHieConsentPolicyDao.findByCommunityIdAndArchived(communityId, isArchived, projectionClass);
    }

    @Override
    public boolean isClientHieConsentPolicyNewerThenCommunityPolicy(Client client) {
        var communityPolicy = findByCommunityIdAndArchived(client.getCommunityId(), false, CommunityHieConsentPolicyLastModifiedDateAware.class);

        var clientPolicyUpdateTime = client.getHieConsentPolicyUpdateDateTime() != null
                ? client.getHieConsentPolicyUpdateDateTime()
                : Instant.EPOCH;

        return communityPolicy.isEmpty() || clientPolicyUpdateTime.isAfter(communityPolicy.get().getLastModifiedDate());
    }
}
