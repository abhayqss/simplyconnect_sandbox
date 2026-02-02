package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.entity.PartnerNetworkCommunity;
import com.scnsoft.eldermark.entity.PartnerNetworkCommunity_;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

@Component
public class PartnerNetworkPredicateGenerator {

    @Autowired
    private CommunityPredicateGenerator communityPredicateGenerator;

    public Predicate networksContainingAllEligibleForDiscovery(Collection<Long> networkCommunityIds, Path<Long> networkPath,
                                                               AbstractQuery<?> query, CriteriaBuilder cb) {
        return eligibleForDiscoveryInSameNetwork(networkCommunityIds, null, query, cb,
                networkRoot -> networkRoot.get(PartnerNetworkCommunity_.partnerNetworkId)
        ).map(
                networkPath::in
        ).orElseGet(cb::or);

    }

    public Predicate areInSameNetworksEligibleForDiscovery(Long community1, Path<Long> community2,
                                                           AbstractQuery<?> query, CriteriaBuilder cb) {
        return eligibleForDiscoveryInSameNetwork(Collections.singleton(community1), community2, query, cb,
                networkRoot -> cb.literal(1L)
        ).map(
                cb::exists
        ).orElseGet(cb::or);
    }

    private Optional<Subquery<Long>> eligibleForDiscoveryInSameNetwork(Collection<Long> communities1, Path<Long> communityId2Path,
                                                                       AbstractQuery<?> query, CriteriaBuilder cb,
                                                                       Function<Root<PartnerNetworkCommunity>, Expression<Long>> selector) {
        if (CollectionUtils.isEmpty(communities1) && communityId2Path == null) {
            return Optional.empty();
        }

        var totalSize = CollectionUtils.size(communities1) + (communityId2Path == null ? 0 : 1);

        var networkCommunitySubquery = query.subquery(Long.class);
        var networkCommunityFrom = networkCommunitySubquery.from(PartnerNetworkCommunity.class);
        var communityJoin = networkCommunityFrom.join(PartnerNetworkCommunity_.community);

        var result = networkCommunitySubquery
                .select(selector.apply(networkCommunityFrom))
                .where(
                        cb.and(
                                cb.or(
                                        CollectionUtils.isEmpty(communities1) ? cb.or() :
                                                cb.in(networkCommunityFrom.get(PartnerNetworkCommunity_.COMMUNITY_ID)).value(communities1),

                                        communityId2Path == null ? cb.or() :
                                                cb.equal(networkCommunityFrom.get(PartnerNetworkCommunity_.communityId), communityId2Path)

                                ),
                                communityPredicateGenerator.eligibleForDiscovery(cb, communityJoin)
                        ))
                .groupBy(networkCommunityFrom.get(PartnerNetworkCommunity_.partnerNetworkId))
                .having(cb
                        .equal(cb.countDistinct(networkCommunityFrom.get(PartnerNetworkCommunity_.communityId)), totalSize));

        return Optional.of(result);
    }

    public Predicate communityNotInAnyNetwork(Path<Long> communityId, AbstractQuery<?> query, CriteriaBuilder cb) {
        var networkSubQuery = query.subquery(Integer.class);
        networkSubQuery.select(cb.literal(1));

        var networkFrom = networkSubQuery.from(PartnerNetworkCommunity.class);
        networkSubQuery.where(cb.equal(networkFrom.get(PartnerNetworkCommunity_.communityId), communityId));


        return cb.not(cb.exists(networkSubQuery));
    }
}
