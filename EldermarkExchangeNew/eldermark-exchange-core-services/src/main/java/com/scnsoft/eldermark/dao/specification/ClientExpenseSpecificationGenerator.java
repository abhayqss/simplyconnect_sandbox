package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.SecurityPredicateGenerator;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.client.expense.ClientExpense;
import com.scnsoft.eldermark.entity.client.expense.ClientExpense_;
import com.scnsoft.eldermark.entity.security.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class ClientExpenseSpecificationGenerator {

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    public Specification<ClientExpense> byClientId(Long clientId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(ClientExpense_.clientId), clientId);
    }

    public Specification<ClientExpense> hasAccess(PermissionFilter permissionFilter) {
        return (root, query, criteriaBuilder) -> {

            var clientJoin = JpaUtils.getOrCreateJoin(root, ClientExpense_.client);

            var eligible = clientPredicateGenerator.clientInEligibleForDiscoveryCommunity(clientJoin, criteriaBuilder);

            var predicates = new ArrayList<Predicate>();

            if (permissionFilter.hasPermission(Permission.CLIENT_EXPENSE_VIEW_ALL_EXCEPT_OPTED_OUT)) {
                predicates.add(clientPredicateGenerator.isOptedIn(clientJoin, criteriaBuilder));
            }

            if (permissionFilter.hasPermission(Permission.CLIENT_EXPENSE_VIEW_IF_ASSOCIATED_ORGANIZATION)) {
                var employees = permissionFilter.getEmployees(Permission.CLIENT_EXPENSE_VIEW_IF_ASSOCIATED_ORGANIZATION);
                predicates.add(criteriaBuilder.in(clientJoin.get(Client_.ORGANIZATION_ID))
                        .value(SpecificationUtils.employeesOrganizationIds(employees)));
            }

            if (permissionFilter.hasPermission(Permission.CLIENT_EXPENSE_VIEW_IF_ASSOCIATED_COMMUNITY)) {
                var employees = permissionFilter.getEmployees(Permission.CLIENT_EXPENSE_VIEW_IF_ASSOCIATED_COMMUNITY);
                predicates.add(criteriaBuilder.in(clientJoin.get(Client_.COMMUNITY_ID))
                        .value(SpecificationUtils.employeesCommunityIds(employees)));
            }

            if (permissionFilter.hasPermission(Permission.CLIENT_EXPENSE_VIEW_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)) {
                var employees = permissionFilter.getEmployees(Permission.CLIENT_EXPENSE_VIEW_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION);
                predicates.add(
                        criteriaBuilder.and(
                                clientPredicateGenerator.isOptedIn(clientJoin, criteriaBuilder),
                                securityPredicateGenerator.primaryCommunitiesOfOrganizations(criteriaBuilder, query, clientJoin.get(Client_.communityId), employees)
                        )
                );
            }

            if (permissionFilter.hasPermission(Permission.CLIENT_EXPENSE_VIEW_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)) {
                var employees = permissionFilter.getEmployees(Permission.CLIENT_EXPENSE_VIEW_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY);
                predicates.add(
                        criteriaBuilder.and(
                                clientPredicateGenerator.isOptedIn(clientJoin, criteriaBuilder),
                                securityPredicateGenerator.primaryCommunities(criteriaBuilder, query, clientJoin.get(Client_.communityId), employees)
                        )
                );
            }

            if (permissionFilter.hasPermission(Permission.CLIENT_EXPENSE_VIEW_IF_CURRENT_RP_COMMUNITY_CTM)) {
                var employees = permissionFilter.getEmployees(Permission.CLIENT_EXPENSE_VIEW_IF_CURRENT_RP_COMMUNITY_CTM);

                predicates.add(securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(
                        criteriaBuilder,
                        query,
                        clientJoin.get(Client_.communityId),
                        employees,
                        AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                        HieConsentCareTeamType.current(clientJoin)
                ));
            }

            if (permissionFilter.hasPermission(Permission.CLIENT_EXPENSE_VIEW_IF_CURRENT_RP_CLIENT_CTM)) {
                var employees = permissionFilter.getEmployees(Permission.CLIENT_EXPENSE_VIEW_IF_CURRENT_RP_CLIENT_CTM);
                var resultCriteria = securityPredicateGenerator.clientsInClientCareTeamPredicate(
                        criteriaBuilder,
                        query,
                        clientJoin,
                        employees,
                        AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                        HieConsentCareTeamType.current(clientJoin)
                );
                predicates.add(resultCriteria);
            }

            if (permissionFilter.hasPermission(Permission.CLIENT_EXPENSE_VIEW_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
                var employees = permissionFilter.getEmployees(Permission.CLIENT_EXPENSE_VIEW_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);
                predicates.add(criteriaBuilder.and(
                        securityPredicateGenerator.clientAddedByEmployees(criteriaBuilder, clientJoin, employees),
                        clientPredicateGenerator.isOptedIn(clientJoin, criteriaBuilder)
                ));
            }

            if (permissionFilter.hasPermission(Permission.CLIENT_EXPENSE_VIEW_IF_SELF_RECORD)) {
                var employees = permissionFilter.getEmployees(Permission.CLIENT_EXPENSE_VIEW_IF_SELF_RECORD);
                var resultCriteria = securityPredicateGenerator.selfRecordClients(criteriaBuilder, clientJoin.get(Client_.id), employees);
                predicates.add(resultCriteria);
            }

            return criteriaBuilder.and(
                    eligible,
                    criteriaBuilder.or(predicates.toArray(new Predicate[0]))
            );
        };
    }

    public <T extends IdAware> Specification<ClientExpense> byClientCommunities(List<T> communities) {
        return (root, query, criteriaBuilder) ->
                clientPredicateGenerator.byCommunities(
                        communities,
                        JpaUtils.getOrCreateJoin(root, ClientExpense_.client),
                        query,
                        criteriaBuilder
                );
    }

    public <T extends IdAware> Specification<ClientExpense> isClientActiveInPeriod(Instant from, Instant to) {
        return (root, query, criteriaBuilder) ->
                clientPredicateGenerator.isActiveInPeriod(
                        from,
                        to,
                        JpaUtils.getOrCreateJoin(root, ClientExpense_.client),
                        query,
                        criteriaBuilder
                );
    }
}
