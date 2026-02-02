package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.beans.projection.OrganizationIdAware;
import com.scnsoft.eldermark.dao.specification.SpecificationUtils;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.assessment.Assessment_;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AssessmentPredicateGenerator {

    public <T extends OrganizationIdAware> Predicate typesAllowedInAnyCommunity(
            Collection<T> communities,
            From<?, Assessment> root,
            CriteriaBuilder criteriaBuilder,
            CriteriaQuery<?> criteriaQuery
    ) {
        var organizationIds = CareCoordinationUtils.getOrganizationIds(communities)
                .distinct()
                .collect(Collectors.toList());

        return typesAllowedInOrganization(root, criteriaQuery, criteriaBuilder,
                org -> SpecificationUtils.in(criteriaBuilder, org, organizationIds));
    }

    public Predicate typesAllowedInOrganization(
            Expression<Long> organizationId,
            From<?, Assessment> root,
            AbstractQuery<?> query,
            CriteriaBuilder criteriaBuilder
    ) {
        return typesAllowedInOrganization(root, query, criteriaBuilder,
                org -> criteriaBuilder.equal(org, organizationId));
    }

    private Predicate typesAllowedInOrganization(
            From<?, Assessment> root,
            AbstractQuery<?> query,
            CriteriaBuilder criteriaBuilder,
            Function<SetJoin<?, Long>, Predicate> organizationEqualsComparator
    ) {

        var isSharedPath = root.get(Assessment_.isShared);
        var disabledOrganizationIdsSubquery = subSelectDisabledOrganizationIds(query, organizationEqualsComparator);
        var allowedOrganizationIdsSubquery = subSelectAllowedOrganizationIds(query, criteriaBuilder, organizationEqualsComparator);


        //Assessment_SourceDatabase_Disabled has priority over is_shared
        //is_shared has priority over Assessment_SourceDatabase
        var sharedForAllOrganizations = criteriaBuilder.isTrue(isSharedPath);
        var notDisabledInOProvidedOrganizations = criteriaBuilder.not(
                root.get(Assessment_.id).in(disabledOrganizationIdsSubquery)
        );
        var sharedToProvidedOrganizations = root.get(Assessment_.id).in(allowedOrganizationIdsSubquery);


        return criteriaBuilder.and(
                notDisabledInOProvidedOrganizations,
                criteriaBuilder.or(
                        sharedForAllOrganizations,
                        sharedToProvidedOrganizations
                )
        );
    }

    private Subquery<Long> subSelectDisabledOrganizationIds(
            AbstractQuery<?> query,
            Function<SetJoin<?, Long>, Predicate> organizationEqualsComparator
    ) {
        var disabledOrganizationIdsSubquery = query.subquery(Long.class);
        var allowedFrom = disabledOrganizationIdsSubquery.from(Assessment.class);
        disabledOrganizationIdsSubquery.select(allowedFrom.get(Assessment_.id));
        var allowedOrganizationIdJoin = allowedFrom.join(Assessment_.disabledOrganizationIds);
        disabledOrganizationIdsSubquery.where(organizationEqualsComparator.apply(allowedOrganizationIdJoin));

        return disabledOrganizationIdsSubquery;
    }

    private Subquery<Long> subSelectAllowedOrganizationIds(
            AbstractQuery<?> query,
            CriteriaBuilder criteriaBuilder,
            Function<SetJoin<?, Long>, Predicate> organizationEqualsComparator
    ) {
        var allowedOrganizationIdsSubquery = query.subquery(Long.class);
        var allowedFrom = allowedOrganizationIdsSubquery.from(Assessment.class);
        allowedOrganizationIdsSubquery.select(allowedFrom.get(Assessment_.id));
        var allowedOrganizationIdJoin = allowedFrom.join(Assessment_.organizationIds);
        allowedOrganizationIdsSubquery.where(
                organizationEqualsComparator.apply(allowedOrganizationIdJoin),
                criteriaBuilder.isFalse(allowedFrom.get(Assessment_.isShared))
        );

        return allowedOrganizationIdsSubquery;
    }
}
