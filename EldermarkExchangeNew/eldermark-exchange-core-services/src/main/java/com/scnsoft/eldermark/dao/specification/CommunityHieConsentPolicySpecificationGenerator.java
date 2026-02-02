package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.CommunityPredicateGenerator;
import com.scnsoft.eldermark.entity.hieconsentpolicy.CommunityHieConsentPolicy;
import com.scnsoft.eldermark.entity.hieconsentpolicy.CommunityHieConsentPolicy_;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Component
public class CommunityHieConsentPolicySpecificationGenerator {

    @Autowired
    private CommunityPredicateGenerator communityPredicateGenerator;

    public Specification<CommunityHieConsentPolicy> byCommunityIdIn(Collection<IdNameAware> communities) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (CollectionUtils.isEmpty(communities)) {
                return criteriaBuilder.or();
            }
            var communityIds = CareCoordinationUtils.toIdsSet(communities);
            return criteriaBuilder.in(root.get(CommunityHieConsentPolicy_.COMMUNITY_ID)).value(communityIds);
        };
    }

    public Specification<CommunityHieConsentPolicy> hasAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> communityPredicateGenerator.hasAccess(
                permissionFilter,
                JpaUtils.getOrCreateJoin(root, CommunityHieConsentPolicy_.community),
                criteriaBuilder,
                criteriaQuery
        );
    }

    public Specification<CommunityHieConsentPolicy> byLastModifiedInPeriod(Instant from, Instant to) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(root.get(CommunityHieConsentPolicy_.lastModifiedDate), from),
                        criteriaBuilder.lessThanOrEqualTo(root.get(CommunityHieConsentPolicy_.lastModifiedDate), to)
                );
    }

    public Specification<CommunityHieConsentPolicy> byArchived(boolean archived) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(CommunityHieConsentPolicy_.archived), archived);
    }

    public Specification<CommunityHieConsentPolicy> byCommunityId(Long communityId) {
        return byCommunityIds(List.of(communityId));
    }

    public Specification<CommunityHieConsentPolicy> byCommunityIds(Collection<Long> communityIds) {
        return (root, query, criteriaBuilder) ->
                CollectionUtils.isEmpty(communityIds)
                        ? criteriaBuilder.disjunction()
                        : criteriaBuilder.in(root.get(CommunityHieConsentPolicy_.COMMUNITY_ID)).value(communityIds);

    }

    public Specification<CommunityHieConsentPolicy> latestBeforeDate(Instant date) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var sq = criteriaQuery.subquery(Long.class);
            var sqRoot = sq.from(CommunityHieConsentPolicy.class);
            sq.select(criteriaBuilder.max(sqRoot.get(CommunityHieConsentPolicy_.communityId)));
            sq.where(criteriaBuilder.lessThan(sqRoot.get(CommunityHieConsentPolicy_.lastModifiedDate), date));
            sq.groupBy(sqRoot.get(CommunityHieConsentPolicy_.communityId));

            return root.get(CommunityHieConsentPolicy_.communityId).in(sq);
        };
    }
}
