package com.scnsoft.eldermark.dao.predicate;

import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.time.Instant;

@Component
public class ClientInsuranceAuthorizationPredicateGenerator {

    public Predicate intersectsWithPeriod(
            Instant dateFrom,
            Instant dateTo,
            CriteriaBuilder criteriaBuilder,
            Path<Instant> startDatePath,
            Path<Instant> endDatePath
    ) {
        return criteriaBuilder.and(
                criteriaBuilder.lessThan(startDatePath, dateTo),
                criteriaBuilder.greaterThan(endDatePath, dateFrom)
        );
    }
}
