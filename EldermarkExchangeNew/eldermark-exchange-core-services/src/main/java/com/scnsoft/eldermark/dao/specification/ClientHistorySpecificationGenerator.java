package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.entity.history.ClientHistory;
import com.scnsoft.eldermark.entity.history.ClientHistory_;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Component
public class ClientHistorySpecificationGenerator {

    @Autowired
    ClientPredicateGenerator clientPredicateGenerator;

    public Specification<ClientHistory> hasAccess(PermissionFilter permissionFilter) {

        return (root, criteriaQuery, criteriaBuilder) -> clientPredicateGenerator.hasDetailsAccess(
            permissionFilter,
            JpaUtils.getOrCreateJoin(root, ClientHistory_.client),
            criteriaQuery,
            criteriaBuilder
        );
    }

    public Specification<ClientHistory> latestForDate(Instant date) {
        return (root, q, cb) -> {

            var sq = q.subquery(Long.class);
            var sqRoot = sq.from(ClientHistory.class);
            sq.select(cb.max(sqRoot.get(ClientHistory_.id)));
            sq.where(cb.lessThan(sqRoot.get(ClientHistory_.updatedDatetime), date));
            sq.groupBy(sqRoot.get(ClientHistory_.clientId));

            return root.get(ClientHistory_.id).in(sq);
        };
    }

    public Specification<ClientHistory> byUpdatedDateTimeIn(Instant from, Instant to) {
        return (root, q, cb) -> cb.and(
            cb.greaterThanOrEqualTo(root.get(ClientHistory_.updatedDatetime), from),
            cb.lessThanOrEqualTo(root.get(ClientHistory_.updatedDatetime), to)
        );
    }

    public Specification<ClientHistory> withinReportPeriod(Instant from, Instant to) {
        return (root, q, cb) -> cb.and(
                cb.greaterThanOrEqualTo(root.get(ClientHistory_.updatedDatetime), from),
                cb.lessThanOrEqualTo(root.get(ClientHistory_.lastUpdated), to)
        );
    }

    public <T extends IdNameAware> Specification<ClientHistory> byCommunities(List<T> communities) {
        return (root, query, criteriaBuilder) -> {
            if (CollectionUtils.isNotEmpty(communities)) {
                var communityIds = CareCoordinationUtils.toIdsSet(communities);
                return root.get(ClientHistory_.communityId).in(communityIds);
            } else {
                return criteriaBuilder.or();
            }
        };
    }

    public Specification<ClientHistory> byClientIdIn(Collection<Long> clientIds) {
        return (root, query, criteriaBuilder) -> {
            if (CollectionUtils.isNotEmpty(clientIds)) {
                return root.get(ClientHistory_.clientId).in(clientIds);
            } else {
                return criteriaBuilder.or();
            }
        };
    }

    public Specification<ClientHistory> firstByUpdatedDateTimeAfter(Instant date) {
        return (root, query, cb) -> {

            var subQuery = query.subquery(Long.class);
            var subRoot = subQuery.from(ClientHistory.class);

            subQuery.select(cb.min(subRoot.get(ClientHistory_.id)));
            subQuery.groupBy(subRoot.get(ClientHistory_.clientId));
            subQuery.where(cb.greaterThan(subRoot.get(ClientHistory_.updatedDatetime), date));

            return root.get(ClientHistory_.id).in(subQuery);
        };
    }

    public Specification<ClientHistory> isHieConsentPolicyTypeNotNull() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNotNull(root.get(ClientHistory_.hieConsentPolicyType));
    }
}
