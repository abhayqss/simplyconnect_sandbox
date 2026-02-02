package com.scnsoft.eldermark.dump.specification;

import com.scnsoft.eldermark.dump.entity.AuditableEntity;
import com.scnsoft.eldermark.dump.entity.AuditableEntity_;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

public abstract class AuditableEntitySpecificationGenerator<T extends AuditableEntity> {

    protected Predicate unarchived(From<?, T> from, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(from.get(AuditableEntity_.archived), false);
    }

}
