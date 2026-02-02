package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.event.incident.IncidentReport;
import com.scnsoft.eldermark.entity.event.incident.IncidentReport_;
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
public class CustomIncidentReportDaoImpl implements CustomIncidentReportDao {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Optional<Instant> findMaxDate(Specification<IncidentReport> specification) {
        return findDate(specification, CriteriaBuilder::greatest);
    }

    @Override
    public Optional<Instant> findMinDate(Specification<IncidentReport> specification) {
        return findDate(specification, CriteriaBuilder::least);
    }

    private Optional<Instant> findDate(Specification<IncidentReport> specification, BiFunction<CriteriaBuilder, Expression<Instant>, Expression<Instant>> selector) {
        var cb = entityManager.getCriteriaBuilder();
        var crq = cb.createQuery(Object.class);
        var root = crq.from(IncidentReport.class);

        crq.multiselect(selector.apply(cb, root.get(IncidentReport_.incidentDatetime)));

        crq.where(specification.toPredicate(root, crq, cb));

        var typed = entityManager.createQuery(crq);
        return Optional.ofNullable((Instant) typed.getSingleResult());
    }
}
