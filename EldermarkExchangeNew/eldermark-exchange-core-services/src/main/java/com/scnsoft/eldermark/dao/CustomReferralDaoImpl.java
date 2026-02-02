package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.CommunityName;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.referral.Referral;
import com.scnsoft.eldermark.entity.referral.ReferralRequest;
import com.scnsoft.eldermark.entity.referral.ReferralRequest_;
import com.scnsoft.eldermark.entity.referral.Referral_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Join;
import java.util.List;

@Repository
public class CustomReferralDaoImpl implements CustomReferralDao {

    private final EntityManager entityManager;

    @Autowired
    public CustomReferralDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<CommunityName> findCommunityNames(Specification<Referral> specification) {
        var cb = entityManager.getCriteriaBuilder();
        var crq = cb.createQuery(CommunityName.class);
        var root = crq.from(Referral.class);
        Join<ReferralRequest, Community> join = root.join(Referral_.referralRequests).join(ReferralRequest_.community);

        crq.multiselect(join.get(Community_.id), join.get(Community_.name));
        crq.where(specification.toPredicate(root, crq, cb));
        crq.orderBy(cb.asc(join.get(Community_.name)));
        crq.distinct(true);
        var typed = entityManager.createQuery(crq);
        return typed.getResultList();
    }
}
