package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.entity.AffiliatedOrganization;
import com.scnsoft.eldermark.entity.AffiliatedOrganization_;
import com.scnsoft.eldermark.entity.AffiliatedRelationship;
import com.scnsoft.eldermark.entity.AffiliatedRelationship_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AffiliatedOrganizationSpecificationGenerator {

    public Specification<AffiliatedOrganization> forPrimaryCommunityId(Long communityId) {
        //though current approach may not be optimal, it leaves logic in single place - in AffiliatedRelationship view.
        //if performance is poor - refactor to direct checks
        return (root, query, criteriaBuilder) -> {
            var subquery = query.subquery(Long.class);
            var relationshipFrom = subquery.from(AffiliatedRelationship.class);

            subquery = subquery.select(relationshipFrom.get(AffiliatedRelationship_.id))
                    .where(criteriaBuilder.equal(relationshipFrom.get(AffiliatedRelationship_.primaryCommunityId), communityId));

            return root.get(AffiliatedOrganization_.id).in(subquery);
        };
    }

    public Specification<AffiliatedOrganization> forAffiliatedCommunityId(Long communityId) {
        return (root, query, criteriaBuilder) -> {
            var subquery = query.subquery(Long.class);
            var relationshipFrom = subquery.from(AffiliatedRelationship.class);

            subquery = subquery.select(relationshipFrom.get(AffiliatedRelationship_.id))
                    .where(criteriaBuilder.equal(relationshipFrom.get(AffiliatedRelationship_.affiliatedCommunityId), communityId));

            return root.get(AffiliatedOrganization_.id).in(subquery);
        };
    }

    public Specification<AffiliatedRelationship> byPrimaryOrganizationId(Long organizationId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);
            return criteriaBuilder.equal(root.get(AffiliatedRelationship_.primaryOrganizationId), organizationId);
        };
    }
}
