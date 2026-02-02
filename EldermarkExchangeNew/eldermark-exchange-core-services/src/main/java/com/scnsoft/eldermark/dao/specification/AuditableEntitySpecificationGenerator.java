package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.dao.predicate.AuditableEntityPredicateGenerator;
import com.scnsoft.eldermark.entity.basic.AuditableEntity;
import com.scnsoft.eldermark.entity.basic.AuditableEntity_;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;

public abstract class AuditableEntitySpecificationGenerator<T extends AuditableEntity> {

    @Autowired
    protected AuditableEntityPredicateGenerator auditableEntityPredicateGenerator;

    public Specification<T> isUnarchived() {
        return (root, criteriaQuery, criteriaBuilder) -> auditableEntityPredicateGenerator.unarchived(root, criteriaBuilder);
    }

    public Specification<T> historyByChainId(Long chainId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.equal(root.get(AuditableEntity_.id), chainId),
                criteriaBuilder.equal(root.get(AuditableEntity_.chainId), chainId)
        );
    }

    public Specification<T> historyById(Long id) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var subQuery = criteriaQuery.subquery(Long.class);
            var from = subQuery.from(getEntityClass());
            subQuery.where(criteriaBuilder.equal(from.get(AuditableEntity_.id), id));
            subQuery.select(criteriaBuilder.coalesce(from.get(AuditableEntity_.chainId), from.get(AuditableEntity_.id)));

            return criteriaBuilder.or(
                    criteriaBuilder.equal(root.get(AuditableEntity_.id), subQuery),
                    criteriaBuilder.equal(root.get(AuditableEntity_.chainId), subQuery)
            );
        };
    }

    public Specification<T> leaveLatest(Instant till) {
        return (root, query, criteriaBuilder) -> auditableEntityPredicateGenerator.leaveLatest(getEntityClass(), till, root, query, criteriaBuilder);
    }

    public Specification<T> byIds(List<Long> ids) {
        return (root, criteriaQuery, criteriaBuilder) -> CollectionUtils.isEmpty(ids) ?
                criteriaBuilder.or() :
                root.get(AuditableEntity_.id).in(ids);
    }

    protected abstract Class<T> getEntityClass();
}
