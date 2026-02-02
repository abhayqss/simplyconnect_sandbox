package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.EventNotification;
import com.scnsoft.eldermark.entity.EventNotification_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.Optional;

@Repository
public class CustomEventNotificationDaoImpl implements CustomEventNotificationDao {

    private final EntityManager entityManager;

    @Autowired
    public CustomEventNotificationDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Instant> findFirstSentDatetime(Specification<EventNotification> specification) {
        var cb = entityManager.getCriteriaBuilder();
        var crq = cb.createQuery(Instant.class);
        var root = crq.from(EventNotification.class);
        crq.select(root.get(EventNotification_.sentDatetime));
        crq.where(specification.toPredicate(root, crq, cb));
        var typed = entityManager.createQuery(crq);
        return typed.setFirstResult(0).setMaxResults(1).getResultStream().findFirst();
    }
}
