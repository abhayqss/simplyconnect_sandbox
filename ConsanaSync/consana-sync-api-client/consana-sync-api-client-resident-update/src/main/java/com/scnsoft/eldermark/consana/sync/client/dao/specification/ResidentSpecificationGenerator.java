package com.scnsoft.eldermark.consana.sync.client.dao.specification;

import com.scnsoft.eldermark.consana.sync.client.model.entities.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import java.util.Set;

@Component
public class ResidentSpecificationGenerator {

    public Specification<Resident> byId(Resident resident) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Resident_.id), resident.getId());
    }

    public Specification<Resident> byPharmacyNames(Set<String> pharmacyNames) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var subQuery = criteriaQuery.subquery(Long.class);
            var subRoot = subQuery.from(ResidentPharmacyFilterView.class);
            return root.get(Resident_.id).in(subQuery
                    .select(subRoot.get(ResidentPharmacyFilterView_.residentId))
                    .where(subRoot.get(ResidentPharmacyFilterView_.PHARMACY_NAME).in(pharmacyNames)));
        };
    }

    public Specification<Resident> isAdmitted() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var subQuery = criteriaQuery.subquery(Long.class);
            var subClient = subQuery.from(Resident.class);
            var subAdmittanceHistory = subClient.join(Resident_.admittanceHistories, JoinType.LEFT);

            var maxClientAdmit = criteriaBuilder.greatest(subClient.get(Resident_.admitDate));
            var maxClientDischarge = criteriaBuilder.greatest(subClient.get(Resident_.dischargeDate));
            var maxHistoryAdmit = criteriaBuilder.greatest(subAdmittanceHistory.get(AdmittanceHistory_.admitDate));
            var maxHistoryDischarge = criteriaBuilder.greatest(subAdmittanceHistory.get(AdmittanceHistory_.dischargeDate));

            var dischargeNotPresent = criteriaBuilder.and(
                    criteriaBuilder.isNull(maxClientDischarge),
                    criteriaBuilder.isNull(maxHistoryDischarge)
            );

            var hasAdmit = criteriaBuilder.or(
                    criteriaBuilder.isNotNull(maxClientAdmit),
                    criteriaBuilder.isNotNull(maxHistoryAdmit)
            );

            var admitAfterDischarge = criteriaBuilder.greaterThan(
                    //both dates are not null
                    greatest(criteriaBuilder, maxClientAdmit, maxHistoryAdmit),
                    greatest(criteriaBuilder, maxClientDischarge, maxHistoryDischarge)
            );

            var isActiveClient = criteriaBuilder.isTrue(subClient.get(Resident_.active));
            var admittedClients = subQuery
                    .select(subClient.get(Resident_.id))
                    .groupBy(subClient.get(Resident_.id))
                    .where(isActiveClient)
                    .having(criteriaBuilder.or(dischargeNotPresent, criteriaBuilder.and(hasAdmit, admitAfterDischarge)));

            return root.get(Resident_.id).in(admittedClients);
        };
    }

    private <Y extends Comparable<? super Y>> Expression<Y> greatest(CriteriaBuilder cb, Expression<Y> x, Expression<Y> y) {
        return cb.<Y>selectCase()
                .when(cb.greaterThan(x, y), x)
                .otherwise(y);
    }
}
