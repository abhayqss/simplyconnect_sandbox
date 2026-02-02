package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.dao.predicate.ClientInsuranceAuthorizationPredicateGenerator;
import com.scnsoft.eldermark.entity.client.insurance.ClientInsuranceAuthorization;
import com.scnsoft.eldermark.entity.client.insurance.ClientInsuranceAuthorization_;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;

@Component
public class ClientInsuranceAuthorizationSpecificationGenerator {

    @Autowired
    private ClientInsuranceAuthorizationPredicateGenerator clientInsuranceAuthorizationPredicateGenerator;

    public Specification<ClientInsuranceAuthorization> byClientIdIn(Collection<Long> clientIds) {
        return (root, query, criteriaBuilder) -> CollectionUtils.isEmpty(clientIds) ?
                criteriaBuilder.or() :
                criteriaBuilder.in(root.get(ClientInsuranceAuthorization_.CLIENT_ID)).value(clientIds);
    }


    public Specification<ClientInsuranceAuthorization> intersectsWithPeriod(Instant dateFrom, Instant dateTo) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                clientInsuranceAuthorizationPredicateGenerator.intersectsWithPeriod(
                        dateFrom,
                        dateTo,
                        criteriaBuilder,
                        root.get(ClientInsuranceAuthorization_.startDate),
                        root.get(ClientInsuranceAuthorization_.endDate)
                )
        );
    }
}
