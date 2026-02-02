package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.entity.basic.AuditableEntity;
import com.scnsoft.eldermark.entity.basic.AuditableEntity_;
import com.scnsoft.eldermark.entity.basic.HistoryIdsAware;
import com.scnsoft.eldermark.entity.basic.HistoryIdsAwareEntity_;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.time.Instant;
import java.util.function.Function;

@Component
public class AuditableEntityPredicateGenerator {

    public <T extends AuditableEntity> Predicate unarchived(From<?, T> from, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(from.get(AuditableEntity_.archived), false);
    }

    public <T extends AuditableEntity> Predicate leaveLatest(Class<T> tClass, Instant till, Path<T> path, CriteriaQuery query, CriteriaBuilder criteriaBuilder) {
        return leaveLatestWithRestriction(tClass, till, path, query, criteriaBuilder, tPath -> criteriaBuilder.and());
    }

    public <T extends AuditableEntity> Predicate leaveLatestWithRestriction(Class<T> tClass, Instant till, Path<T> path, CriteriaQuery query, CriteriaBuilder criteriaBuilder, Function<Path<T>, Predicate> restriction) {
        var latestEntities = query.subquery(Long.class);
        var fromOut = latestEntities.from(tClass);
        latestEntities.where(criteriaBuilder.lessThanOrEqualTo(fromOut.get(AuditableEntity_.lastModifiedDate), till));
        latestEntities.groupBy(historyId(fromOut, criteriaBuilder));

        var idOfLatestModified = query.subquery(Long.class);
        var fromInner = idOfLatestModified.from(tClass);
        idOfLatestModified.where(criteriaBuilder.and(
                criteriaBuilder.equal(historyId(fromInner, criteriaBuilder), historyId(fromOut, criteriaBuilder)),
                criteriaBuilder.equal(fromInner.get(AuditableEntity_.lastModifiedDate), criteriaBuilder.greatest(fromOut.get(AuditableEntity_.lastModifiedDate))),
                restriction.apply(fromInner)
        ));
        idOfLatestModified.select(fromInner.get(AuditableEntity_.id));

        latestEntities.select(idOfLatestModified);

        return path.in(latestEntities);
    }

    public <T extends HistoryIdsAware> Expression<Long> historyId(Path<T> path, CriteriaBuilder cb) {
        return cb.function("ISNULL", Long.class, path.get(HistoryIdsAwareEntity_.CHAIN_ID), path.get(HistoryIdsAwareEntity_.ID));
    }

    public <T extends HistoryIdsAware> Long historyId(T entity) {
        return ObjectUtils.firstNonNull(entity.getChainId(), entity.getId());
    }

}
