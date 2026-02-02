package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanStatus;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlan_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.time.Instant;
import java.util.ArrayList;

@Component
public class ServicePlanPredicateGenerator {

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;


    public Predicate byClientIdWithMerged(Long clientId, From<?, ServicePlan> from, CriteriaBuilder criteriaBuilder,
                                          CriteriaQuery criteriaQuery) {
        return clientPredicateGenerator.withMergedClients(
                from.join(ServicePlan_.client),
                clientFrom -> criteriaBuilder.equal(clientFrom.get(Client_.id), clientId),
                criteriaBuilder, criteriaQuery);
    }

    public Predicate hasAccess(From<?, ServicePlan> root,
                               CriteriaQuery<?> criteriaQuery,
                               CriteriaBuilder criteriaBuilder,
                               PermissionFilter permissionFilter) {

        var lazyClientJoin = Lazy.of(() -> root.join(ServicePlan_.client));

        var predicates = new ArrayList<Predicate>();

        if (permissionFilter.hasPermission(Permission.SERVICE_PLAN_VIEW_ALL_EXCEPT_OPTED_OUT)) {
            predicates.add(clientPredicateGenerator.isOptedIn(lazyClientJoin.get(), criteriaBuilder));
        }

        if (permissionFilter.hasPermission(Permission.SERVICE_PLAN_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(Permission.SERVICE_PLAN_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION);
            predicates.add(securityPredicateGenerator.associatedOrganizationWithMergedPredicate(criteriaBuilder,
                    lazyClientJoin.get(), criteriaQuery, employees));
        }

        if (permissionFilter.hasPermission(Permission.SERVICE_PLAN_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(Permission.SERVICE_PLAN_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY);
            predicates.add(securityPredicateGenerator.associatedCommunityWithMergedClients(criteriaBuilder,
                    lazyClientJoin.get(), criteriaQuery, employees));
        }

        if (permissionFilter.hasPermission(Permission.SERVICE_PLAN_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(Permission.SERVICE_PLAN_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION);
            predicates.add(criteriaBuilder.and(
                    securityPredicateGenerator.primaryCommunitiesOfOrganizationsWithMergedClient(criteriaBuilder,
                            lazyClientJoin.get(), criteriaQuery, employees),
                    clientPredicateGenerator.isOptedOut(lazyClientJoin.get(), criteriaBuilder).not()
            ));
        }

        if (permissionFilter.hasPermission(Permission.SERVICE_PLAN_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(Permission.SERVICE_PLAN_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY);
            predicates.add(criteriaBuilder.and(
                    securityPredicateGenerator.primaryCommunitiesWithMergedClient(criteriaBuilder,
                            lazyClientJoin.get(), criteriaQuery, employees),
                    clientPredicateGenerator.isOptedOut(lazyClientJoin.get(), criteriaBuilder).not()
            ));
        }

        if (permissionFilter.hasPermission(Permission.SERVICE_PLAN_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(Permission.SERVICE_PLAN_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM);
            predicates.add(securityPredicateGenerator.communityCareTeamWithMergedPredicate(
                    criteriaBuilder,
                    lazyClientJoin.get(),
                    criteriaQuery,
                    employees,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.current(lazyClientJoin.get())
            ));
        }

        if (permissionFilter.hasPermission(Permission.SERVICE_PLAN_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(Permission.SERVICE_PLAN_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM);
            predicates.add(securityPredicateGenerator.clientCareTeamWithMergedPredicate(
                    criteriaBuilder,
                    lazyClientJoin.get(),
                    criteriaQuery,
                    employees,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.current(lazyClientJoin.get())
            ));

        }

        if (permissionFilter.hasPermission(Permission.SERVICE_PLAN_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(Permission.SERVICE_PLAN_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);
            predicates.add(
                    criteriaBuilder.and(
                            securityPredicateGenerator.addedByEmployeesWithMergedPredicate(criteriaBuilder,
                                    lazyClientJoin.get(), criteriaQuery, employees),
                            clientPredicateGenerator.isOptedOut(lazyClientJoin.get(), criteriaBuilder).not()
                    )
            );
        }

        return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
    }

    public Predicate getInDevelopmentServicePlansTillDate(Instant dateEnd, Path<ServicePlan> path, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
                withStatus(ServicePlanStatus.IN_DEVELOPMENT, path, criteriaBuilder),
                criteriaBuilder.lessThanOrEqualTo(path.get(ServicePlan_.dateCreated), dateEnd)
        );
    }

    public Predicate getSharedWithClientServicePlansWithinPeriod(Instant dateStart, Instant dateEnd, Path<ServicePlan> path, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
                withStatus(ServicePlanStatus.SHARED_WITH_CLIENT, path, criteriaBuilder),
                criteriaBuilder.between(path.get(ServicePlan_.dateCompleted), dateStart, dateEnd) //between includes borders
        );
    }

    public Predicate withStatus(ServicePlanStatus status, Path<ServicePlan> path, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(path.get(ServicePlan_.servicePlanStatus), status);
    }
}
