package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.ClientProblemFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.SecurityPredicateGenerator;
import com.scnsoft.eldermark.entity.AccessRight;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.document.ccd.ClientProblem;
import com.scnsoft.eldermark.entity.document.ccd.ClientProblemStatus;
import com.scnsoft.eldermark.entity.document.ccd.ClientProblem_;
import com.scnsoft.eldermark.entity.security.Permission;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

@Component
public class ClientProblemSpecificationGenerator {

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    public Specification<ClientProblem> byFilterAndHasAccessWithoutDuplicates(PermissionFilter permissionFilter, ClientProblemFilter filter) {

        var specification = byStatuses(filter);

        var clientSpecification = Optional.ofNullable(filter.getClientId())
                .map(clientId ->
                        (SpecificationUtils.PathSpecification<Client>) (clientPath, criteriaQuery, criteriaBuilder) ->
                                criteriaBuilder.equal(clientPath.get(Client_.id), clientId)
                )
                .orElse(null);

        specification = hasAccess(permissionFilter, clientSpecification)
                .and(specification);

        return excludeDuplicates(specification);
    }

    private Specification<ClientProblem> byStatuses(ClientProblemFilter filter) {
        var statuses = new HashSet<ClientProblemStatus>();
        if (BooleanUtils.isTrue(filter.getIncludeActive())) {
            statuses.add(ClientProblemStatus.ACTIVE);
        }
        if (BooleanUtils.isTrue(filter.getIncludeResolved())) {
            statuses.add(ClientProblemStatus.RESOLVED);
        }
        if (BooleanUtils.isTrue(filter.getIncludeOther())) {
            statuses.add(ClientProblemStatus.OTHER);
        }
        return byStatuses(statuses);
    }

    private Specification<ClientProblem> byStatuses(Collection<ClientProblemStatus> statuses) {
        return (root, query, criteriaBuilder) ->
                CollectionUtils.isNotEmpty(statuses)
                        ? criteriaBuilder.in(root.get(ClientProblem_.STATUS)).value(statuses)
                        : criteriaBuilder.and();
    }

    private Specification<ClientProblem> excludeDuplicates(Specification<ClientProblem> restriction) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var sub = criteriaQuery.subquery(Long.class);
            var subProblem = sub.from(ClientProblem.class);

            sub.select(criteriaBuilder.min(subProblem.get(ClientProblem_.id)));
            sub.where(restriction.toPredicate(subProblem, criteriaQuery, criteriaBuilder));
            sub.groupBy(
                    subProblem.get(ClientProblem_.problem),
                    subProblem.get(ClientProblem_.identifiedDate),
                    subProblem.get(ClientProblem_.stoppedDate),
                    subProblem.get(ClientProblem_.status),
                    subProblem.get(ClientProblem_.problemCode),
                    subProblem.get(ClientProblem_.problemCodeSet)

            );
            return root.in(sub);
        };
    }

    private Specification<ClientProblem> hasAccess(
            PermissionFilter permissionFilter,
            SpecificationUtils.PathSpecification<Client> clientRestriction
    ) {
        return (root, criteriaQuery, criteriaBuilder) -> securityPredicateGenerator.hasAccessWithPHRFlagsAndMerged(
                JpaUtils.getOrCreateJoin(root, ClientProblem_.client),
                criteriaQuery, criteriaBuilder, permissionFilter,
                Permission.PROBLEM_VIEW_MERGED_ALL_EXCEPT_OPTED_OUT,
                Permission.PROBLEM_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION,
                Permission.PROBLEM_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY,
                Permission.PROBLEM_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
                Permission.PROBLEM_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
                Permission.PROBLEM_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM,
                Permission.PROBLEM_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM,
                Permission.PROBLEM_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                Permission.PROBLEM_VIEW_MERGED_IF_SELF_RECORD,
                Permission.PROBLEM_VIEW_MERGED_IF_ACCESSIBLE_REFERRAL_REQUEST,
                Permission.PROBLEM_VIEW_MERGED_IF_CLIENT_FOUND_IN_RECORD_SEARCH,
                clientRestriction,
                AccessRight.Code.MY_PHR);
    }

}
