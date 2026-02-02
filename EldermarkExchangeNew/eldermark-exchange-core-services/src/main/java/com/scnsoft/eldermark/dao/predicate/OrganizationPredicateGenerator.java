package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Organization_;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.Community_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

@Component
public class OrganizationPredicateGenerator {

    @Autowired
    private CommunityPredicateGenerator communityPredicateGenerator;

    public Predicate withEnabledChat(CriteriaBuilder criteriaBuilder, Path<Organization> organizationPath, boolean value) {
        return criteriaBuilder.equal(organizationPath.get(Organization_.isChatEnabled), value);
    }

    public Predicate withEnabledVideoCall(CriteriaBuilder criteriaBuilder, Path<Organization> organizationPath, boolean value) {
        return criteriaBuilder.equal(organizationPath.get(Organization_.isVideoEnabled), value);
    }

    public Predicate hasEligibleForDiscoveryCommunities(Path<Long> organizationIdPath, AbstractQuery<?> query, CriteriaBuilder cb) {
        var subQuery = query.subquery(Integer.class);
        var communityFrom = subQuery.from(Community.class);

        subQuery = subQuery.select(cb.literal(1))
                .where(
                        communityPredicateGenerator.eligibleForDiscovery(cb, communityFrom),
                        cb.equal(
                                communityFrom.get(Community_.organizationId),
                                organizationIdPath
                        )
                );

        return cb.exists(subQuery);
    }

    public Predicate hasVisibleCommunities(Path<Long> organizationIdPath, AbstractQuery<?> query, CriteriaBuilder cb) {
        var subQuery = query.subquery(Integer.class);
        var communityFrom = subQuery.from(Community.class);

        subQuery = subQuery.select(cb.literal(1))
                .where(
                        communityPredicateGenerator.isVisible(cb, communityFrom),
                        cb.equal(communityFrom.get(Community_.organizationId), organizationIdPath)
                );

        return cb.exists(subQuery);
    }

    public Predicate withEsignEnabled(CriteriaBuilder criteriaBuilder, Path<Organization> organizationPath, Boolean eSignEnabled) {
        return criteriaBuilder.equal(organizationPath.get(Organization_.isSignatureEnabled), eSignEnabled);
    }

    public Predicate withEnabledAppointments(CriteriaBuilder criteriaBuilder, Path<Organization> organizationPath, boolean value) {
        return criteriaBuilder.equal(organizationPath.get(Organization_.isAppointmentsEnabled), value);
    }
}
