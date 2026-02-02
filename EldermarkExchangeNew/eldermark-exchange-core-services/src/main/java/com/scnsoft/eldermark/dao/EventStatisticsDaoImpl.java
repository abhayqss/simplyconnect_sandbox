package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.EventGroupStatistics;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.EventGroup_;
import com.scnsoft.eldermark.entity.event.EventType_;
import com.scnsoft.eldermark.entity.event.Event_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class EventStatisticsDaoImpl implements EventStatisticsDao {

    @Autowired
    private EntityManager entityManager;

    public List<EventGroupStatistics> getByAllGroups(Specification<Event> specification) {

        var cb = entityManager.getCriteriaBuilder();
        var crq = cb.createQuery(EventGroupStatistics.class);
        var eventRoot = crq.from(Event.class);

        var eventGroup = eventRoot.get(Event_.eventType).get(EventType_.eventGroup);

        crq.multiselect(eventGroup.get(EventGroup_.name), cb.count(eventRoot));
        crq.where(specification.toPredicate(eventRoot, crq, cb));
        crq.groupBy(eventGroup);

        var typed = entityManager.createQuery(crq);

        return typed.getResultList();
    }

}
