package com.scnsoft.eldermark.dump.specification.predicate;

import com.scnsoft.eldermark.dump.entity.Event;
import com.scnsoft.eldermark.dump.entity.Event_;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;

@Component
public class EventPredicateGenerator {

    public Predicate from(Instant dateFrom, CriteriaBuilder criteriaBuilder, Root<Event> root) {
        if (dateFrom != null) {
            return criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.eventDateTime), dateFrom);
        }
        return criteriaBuilder.or();
    }

    public Predicate to(Instant dateTo, CriteriaBuilder criteriaBuilder, Root<Event> root) {
        if (dateTo != null) {
            return criteriaBuilder.lessThanOrEqualTo(root.get(Event_.eventDateTime), dateTo);
        }
        return criteriaBuilder.or();
    }

}
