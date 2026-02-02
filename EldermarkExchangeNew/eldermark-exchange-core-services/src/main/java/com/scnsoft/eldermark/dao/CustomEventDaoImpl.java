package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.client.report.EventCountByCommunityAndEventTypeItem;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.EventType_;
import com.scnsoft.eldermark.entity.event.Event_;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

@Repository
public class CustomEventDaoImpl implements CustomEventDao {

    private final EntityManager entityManager;

    @Autowired
    public CustomEventDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    public Map<Long, Long> countsByClientId(Specification<Event> spec) {
        var cb = entityManager.getCriteriaBuilder();

        var crq = cb.createTupleQuery();
        var root = crq.from(Event.class);

        crq.where(spec.toPredicate(root, crq, cb));
        crq.groupBy(root.get(Event_.clientId));

        crq.multiselect(root.get(Event_.clientId), cb.count(root.get(Event_.id)));

        var typed = entityManager.createQuery(crq);

        var resultList = typed.getResultList();
        var result = resultList.stream()
                .collect(StreamUtils.toMapOfUniqueKeysAndThen(
                        t -> t.get(0, Long.class),
                        t -> t.get(1, Long.class))
                );

        return result;
    }

    @Override
    public List<EventCountByCommunityAndEventTypeItem> countsByEventTypeAndCommunity(
        Specification<Event> spec
    ) {
        var cb = entityManager.getCriteriaBuilder();
        var q = cb.createQuery(EventCountByCommunityAndEventTypeItem.class);
        var root = q.from(Event.class);

        var eventTypeCodeColumn = JpaUtils.getOrCreateJoin(root, Event_.eventType).get(EventType_.code);
        var communityIdColumn = JpaUtils.getOrCreateJoin(root, Event_.client).get(Client_.communityId);

        q.where(spec.toPredicate(root, q, cb));
        q.groupBy(eventTypeCodeColumn, communityIdColumn);
        q.multiselect(eventTypeCodeColumn, communityIdColumn, cb.count(root));

        return entityManager.createQuery(q)
            .getResultList();
    }

    @Override
    public Map<Long, Pair<Long, Long>> countsAllAndWithRestrictionGroupByClientId(Specification<Event> spec, Specification<Event> restriction) {
        var cb = entityManager.getCriteriaBuilder();

        var crq = cb.createTupleQuery();
        var root = crq.from(Event.class);

        crq.where(spec.toPredicate(root, crq, cb));
        crq.groupBy(root.get(Event_.clientId));

        crq.multiselect(root.get(Event_.clientId), cb.count(root.get(Event_.id)),
                cb.sum(cb.selectCase().when(restriction.toPredicate(root, crq, cb), cb.literal(1L)).otherwise(cb.literal(0L)).as(Long.class)));

        var typed = entityManager.createQuery(crq);

        var resultList = typed.getResultList();
        var result = resultList.stream()
                .collect(StreamUtils.toMapOfUniqueKeysAndThen(
                        t -> t.get(0, Long.class),
                        t -> new Pair<>(t.get(1, Long.class), t.get(2, Long.class)))
                );

        return result;
    }
}
