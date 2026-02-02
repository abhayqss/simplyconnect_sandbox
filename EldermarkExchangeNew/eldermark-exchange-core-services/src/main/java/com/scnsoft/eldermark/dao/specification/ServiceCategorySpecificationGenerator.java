package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.predicate.CommunityPredicateGenerator;
import com.scnsoft.eldermark.entity.Marketplace_;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.marketplace.ServiceCategory;
import com.scnsoft.eldermark.entity.marketplace.ServiceCategory_;
import com.scnsoft.eldermark.entity.marketplace.ServiceType_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;

@Component
public class ServiceCategorySpecificationGenerator {

    @Autowired
    private CommunityPredicateGenerator communityPredicateGenerator;

    public Specification<ServiceCategory> fromAccessibleCommunities(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var serviceCategorySubquery = criteriaQuery.subquery(Long.class);
            serviceCategorySubquery.distinct(true);
            var communityFrom = serviceCategorySubquery.from(Community.class);
            var serviceCategoriesJoin = communityFrom.join(Community_.marketplace).join(Marketplace_.serviceCategories);
            serviceCategorySubquery.select(serviceCategoriesJoin.get(ServiceType_.id));

            var subQueryPredicates = new ArrayList<Predicate>();
            subQueryPredicates.add(communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, communityFrom));
            subQueryPredicates.add(communityPredicateGenerator.hasAccess(permissionFilter, communityFrom, criteriaBuilder, serviceCategorySubquery));

            serviceCategorySubquery.where(criteriaBuilder.and(subQueryPredicates.toArray(new Predicate[0])));

            return criteriaBuilder.and(root.get(ServiceCategory_.id).in(serviceCategorySubquery));
        };
    }

}
