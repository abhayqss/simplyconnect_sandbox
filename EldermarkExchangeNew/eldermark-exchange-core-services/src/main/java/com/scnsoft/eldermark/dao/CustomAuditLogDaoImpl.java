package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLog_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import java.time.Instant;
import java.util.Optional;
import java.util.function.BiFunction;

@Repository
public class CustomAuditLogDaoImpl implements CustomAuditLogDao {

    private final EntityManager entityManager;

    @Autowired
    public CustomAuditLogDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Instant> findMaxDate(Specification<AuditLog> specification) {
        return findDate(specification, CriteriaBuilder::greatest);
    }

    @Override
    public Optional<Instant> findMinDate(Specification<AuditLog> specification) {
        return findDate(specification, CriteriaBuilder::least);
    }

    private Optional<Instant> findDate(Specification<AuditLog> specification,
                                       BiFunction<CriteriaBuilder, Expression<Instant>, Expression<Instant>> selector) {
        var cb = entityManager.getCriteriaBuilder();
        var crq = cb.createQuery(Object.class);
        var root = crq.from(AuditLog.class);

        crq.multiselect(selector.apply(cb, root.get(AuditLog_.date)));
        crq.where(specification.toPredicate(root, crq, cb));

        var typed = entityManager.createQuery(crq);
        return Optional.ofNullable((Instant) typed.getSingleResult());
    }

}
