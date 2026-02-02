package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.PharmacyFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.entity.client.ClientPharmacyFilterView;
import com.scnsoft.eldermark.entity.client.ClientPharmacyFilterView_;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ClientPharmacyFilterViewSpecificationGenerator {

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    public Specification<ClientPharmacyFilterView> hasAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> clientPredicateGenerator
                .hasDetailsAccess(permissionFilter, JpaUtils.getOrCreateJoin(root, ClientPharmacyFilterView_.client), criteriaQuery, criteriaBuilder);
    }

    public Specification<ClientPharmacyFilterView> byFilter(PharmacyFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getOrganizationId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(ClientPharmacyFilterView_.organizationId), filter.getOrganizationId()));
            }

            if (CollectionUtils.isNotEmpty(filter.getCommunityIds())) {
                predicates.add(root.get(ClientPharmacyFilterView_.communityId).in(filter.getCommunityIds()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<ClientPharmacyFilterView> distinct() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);
            return criteriaBuilder.and();
        };
    }
}
