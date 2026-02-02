package com.scnsoft.eldermark.dump.specification;

import com.scnsoft.eldermark.dump.bean.DumpFilter;
import com.scnsoft.eldermark.dump.entity.*;
import com.scnsoft.eldermark.dump.specification.predicate.EventPredicateGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Component
public class EventSpecificationGenerator {

    @Autowired
    private EventPredicateGenerator eventPredicateGenerator;

    public Specification<Event> byClientId(DumpFilter dumpFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.or();
    }

    public Specification<Event> byEventType(EventTypeEnum eventTypeCode) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.join(Event_.EVENT_TYPE).get(EventType_.CODE), eventTypeCode);
    }

    public Specification<Event> betweenDates(Instant dateFrom, Instant dateTo) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        eventPredicateGenerator.from(dateFrom, criteriaBuilder, root),
                        eventPredicateGenerator.to(dateTo, criteriaBuilder, root)
                );
    }

    public Specification<Event> byClientOrganizationId(Long organizationId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.join(Event_.client).get(Client_.organizationId), organizationId);
    }
}
