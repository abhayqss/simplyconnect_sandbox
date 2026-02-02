package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.EventNote;
import com.scnsoft.eldermark.entity.EventNote_;
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
public class CustomEventNoteDaoImpl implements CustomEventNoteDao {

    private final EntityManager entityManager;

    @Autowired
    public CustomEventNoteDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Instant> findMaxDate(Specification<EventNote> specification) {
        return findDate(specification, CriteriaBuilder::greatest);
    }

    @Override
    public Optional<Instant> findMinDate(Specification<EventNote> specification) {
        return findDate(specification, CriteriaBuilder::least);
    }

    private Optional<Instant> findDate(Specification<EventNote> specification, BiFunction<CriteriaBuilder, Expression<Instant>, Expression<Instant>> selector) {
        var cb = entityManager.getCriteriaBuilder();
        var crq = cb.createQuery(Object.class);
        var root = crq.from(EventNote.class);

        crq.multiselect(selector.apply(cb, root.get(EventNote_.date)));

        crq.where(specification.toPredicate(root, crq, cb));

        var typed = entityManager.createQuery(crq);
        return Optional.ofNullable((Instant)typed.getSingleResult());
    }

}
