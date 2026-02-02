package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.SecurityPredicateGenerator;
import com.scnsoft.eldermark.dto.prospect.ProspectFilter;
import com.scnsoft.eldermark.dto.prospect.ProspectStatus;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.document.CcdCode_;
import com.scnsoft.eldermark.entity.prospect.Prospect;
import com.scnsoft.eldermark.entity.prospect.Prospect_;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class ProspectSpecificationGenerator {

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    public Specification<Prospect> byExternalId(Long externalId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Prospect_.externalId), externalId);
    }

    public Specification<Prospect> byCommunityId(Long communityId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Prospect_.communityId), communityId);
    }

    public Specification<Prospect> byFilter(ProspectFilter filter) {
        return (root, query, criteriaBuilder) -> {

            var predicates = new ArrayList<Predicate>();

            if (filter.getOrganizationId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Prospect_.organizationId), filter.getOrganizationId()));
            }

            if (CollectionUtils.isNotEmpty(filter.getCommunityIds())) {
                predicates.add(root.get(Prospect_.communityId).in(filter.getCommunityIds()));
            }

            if (StringUtils.isNotBlank(filter.getFirstName())) {
                predicates.add(criteriaBuilder.like(
                        root.get(Prospect_.firstName),
                        SpecificationUtils.wrapWithWildcards(filter.getFirstName())
                ));
            }

            if (StringUtils.isNotBlank(filter.getLastName())) {
                predicates.add(criteriaBuilder.like(
                        root.get(Prospect_.lastName),
                        SpecificationUtils.wrapWithWildcards(filter.getLastName())
                ));
            }

            if (filter.getGenderId() != null) {
                predicates.add(criteriaBuilder.equal(
                        JpaUtils.getOrCreateJoin(root, Prospect_.gender).get(CcdCode_.id),
                        filter.getGenderId()
                ));
            }

            if (filter.getBirthDate() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Prospect_.birthDate), filter.getBirthDate()));
            }

            if (filter.getProspectStatus() == ProspectStatus.ACTIVE) {
                predicates.add(criteriaBuilder.isTrue(root.get(Prospect_.active)));
            }

            if (filter.getProspectStatus() == ProspectStatus.INACTIVE) {
                predicates.add(criteriaBuilder.isFalse(root.get(Prospect_.active)));
            }

            if (filter.getProspectStatus() == ProspectStatus.CONVERTED_TO_CLIENT) {
                // TODO fix filtering when convert to client is implemented
                predicates.add(criteriaBuilder.or());
            }

            if (filter.getPermissionFilter() != null) {
                predicates.add(hasAccess(filter.getPermissionFilter()).toPredicate(root, query, criteriaBuilder));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    public Specification<Prospect> hasAccess(PermissionFilter permissionFilter) {
        return (root, query, criteriaBuilder) -> {

            var eligible = inEligibleForDiscoveryCommunity().toPredicate(root, query, criteriaBuilder);

            if (permissionFilter.hasPermission(Permission.PROSPECT_VIEW_ALL)) {
                return eligible;
            }

            var predicates = new ArrayList<Predicate>();

            if (permissionFilter.hasPermission(Permission.PROSPECT_VIEW_IF_ASSOCIATED_ORGANIZATION)) {
                var employees = permissionFilter.getEmployees(Permission.PROSPECT_VIEW_IF_ASSOCIATED_ORGANIZATION);
                predicates.add(securityPredicateGenerator.prospectInAssociatedOrganization(criteriaBuilder, root, employees));
            }

            if (permissionFilter.hasPermission(Permission.PROSPECT_VIEW_IF_ASSOCIATED_COMMUNITY)) {
                var employees = permissionFilter.getEmployees(Permission.PROSPECT_VIEW_IF_ASSOCIATED_COMMUNITY);
                predicates.add(securityPredicateGenerator.prospectInAssociatedCommunity(criteriaBuilder, root, employees));
            }

            // TODO prospect care team implement

            if (permissionFilter.hasPermission(Permission.PROSPECT_VIEW_IF_ADDED_BY_SELF)) {
                var employees = permissionFilter.getEmployees(Permission.PROSPECT_VIEW_IF_ADDED_BY_SELF);
                predicates.add(addedByEmployees(employees).toPredicate(root, query, criteriaBuilder));
            }

            return criteriaBuilder.and(
                    eligible,
                    criteriaBuilder.or(predicates.toArray(Predicate[]::new))
            );
        };
    }

    public Specification<Prospect> byIdIn(Collection<Long> ids) {
        return (root, query, criteriaBuilder) -> root.get(Prospect_.id).in(ids);
    }

    public Specification<Prospect> addedByEmployees(List<Employee> employees) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get(Prospect_.CREATED_BY_ID))
                        .value(CareCoordinationUtils.toIdsSet(employees));
    }

    public Specification<Prospect> inEligibleForDiscoveryCommunity() {
        return (root, query, criteriaBuilder) ->
                securityPredicateGenerator.eligibleForDiscoveryCommunity(
                        JpaUtils.getOrCreateJoin(root, Prospect_.community),
                        criteriaBuilder
                );
    }
}
