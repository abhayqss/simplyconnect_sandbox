package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.entity.State_;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.CommunityAddress_;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomCommunityDaoImpl implements CustomCommunityDao {

    private final EntityManager entityManager;

    @Autowired
    public CustomCommunityDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Map<Long, HieConsentPolicyType> findCommunityStatePolicy(Specification<Community> specification) {
        var cb = entityManager.getCriteriaBuilder();
        var crq = cb.createTupleQuery();
        var root = crq.from(Community.class);

        var communityAddressJoin = JpaUtils.getOrCreateListJoin(root, Community_.addresses);

        var stateSubQuery = crq.subquery(String.class);
        var stateRoot = stateSubQuery.from(State.class);

        var stateAbbrSubQueryResult = stateSubQuery.select(stateRoot.get(State_.HIE_CONSENT_POLICY))
                .where(cb.equal(stateRoot.get(State_.abbr), communityAddressJoin.get(CommunityAddress_.state)));

        crq.multiselect(root.get(Community_.id).alias(Community_.ID), stateAbbrSubQueryResult.alias(State_.HIE_CONSENT_POLICY));

        crq.where(specification.toPredicate(root, crq, cb));

        var typed = entityManager.createQuery(crq);

        var resultList = typed.getResultList();

        return resultList.stream()
                .filter(tuple ->
                        tuple.get(Community_.ID, Long.class) != null
                                && tuple.get(State_.HIE_CONSENT_POLICY, HieConsentPolicyType.class) != null
                )
                .collect(Collectors.toMap(
                        tuple -> tuple.get(Community_.ID, Long.class),
                        tuple -> tuple.get(State_.HIE_CONSENT_POLICY, HieConsentPolicyType.class)
                ));
    }
}
