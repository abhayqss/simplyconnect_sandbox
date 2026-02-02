package com.scnsoft.eldermark.dump.specification.predicate;

import com.scnsoft.eldermark.dump.entity.AuditableEntity;
import com.scnsoft.eldermark.dump.entity.AuditableEntity_;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.function.Function;

@Component
public class AuditableEntityPredicateGenerator {

    public <T extends AuditableEntity> Predicate leaveLatest(Class<T> tClass, LocalDateTime till, Path<T> path, AbstractQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return leaveLatestWithRestriction(tClass, till, path, query, criteriaBuilder, tPath -> criteriaBuilder.and());
    }

    public <T extends AuditableEntity> Predicate leaveLatestWithRestriction(Class<T> tClass, LocalDateTime till, Path<T> path, AbstractQuery<?> query, CriteriaBuilder criteriaBuilder, Function<Path<T>, Predicate> restriction) {
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

    public <T extends AuditableEntity> Expression<Long> historyId(Path<T> path, CriteriaBuilder cb) {
        return cb.function("ISNULL", Long.class, path.get(AuditableEntity_.CHAIN_ID), path.get(AuditableEntity_.ID));
    }
}
