package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.CommunityPredicateGenerator;
import com.scnsoft.eldermark.entity.FeaturedServiceProvider;
import com.scnsoft.eldermark.entity.FeaturedServiceProvider_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class FeaturedServiceProviderSpecificationGenerator {

    @Autowired
    private CommunityPredicateGenerator communityPredicateGenerator;

    public Specification<FeaturedServiceProvider> byServiceProviderCommunityId(Long serviceProviderCommunityId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(FeaturedServiceProvider_.providerId), serviceProviderCommunityId);
    }

    public Specification<FeaturedServiceProvider> byAccessibleCommunity(PermissionFilter permissionFilter) {
        return (root, query, criteriaBuilder) -> {
            var community = JpaUtils.getOrCreateJoin(root, FeaturedServiceProvider_.community);
            return criteriaBuilder.and(
                    communityPredicateGenerator.hasAccess(permissionFilter, community, criteriaBuilder, query),
                    communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, community)
            );
        };
    }
}
