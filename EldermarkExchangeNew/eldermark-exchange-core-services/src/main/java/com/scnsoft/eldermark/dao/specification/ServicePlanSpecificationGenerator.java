package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.ServicePlanFilter;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.predicate.AuditableEntityPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.ServicePlanPredicateGenerator;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.serviceplan.*;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.scnsoft.eldermark.dao.specification.SpecificationUtils.*;

@Component
public class ServicePlanSpecificationGenerator extends AuditableEntitySpecificationGenerator<ServicePlan> {

    @Autowired
    private AuditableEntityPredicateGenerator auditableEntityPredicateGenerator;

    @Autowired
    private ServicePlanPredicateGenerator servicePlanPredicateGenerator;

    @Autowired
    private ClientSpecificationGenerator clientSpecificationGenerator;

    public Specification<ServicePlan> byFilter(ServicePlanFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            var subQuery = query.subquery(Long.class);
            var subRoot = subQuery.from(ServicePlan.class);
            subQuery.distinct(true);
            subQuery.select(subRoot.get(ServicePlan_.id));

            if (StringUtils.isNotEmpty(filter.getSearchText())) {
                var search = wrapWithWildcards(filter.getSearchText());
                List<Predicate> filterPredicates = new ArrayList<>();

                filterPredicates.add(
                        cb.like(americanDateFormat(subRoot.get(ServicePlan_.dateCreated), cb), search)
                );

                filterPredicates.add(
                        cb.like(americanDateFormat(subRoot.get(ServicePlan_.dateCompleted), cb), search)
                );

                filterPredicates.add(
                        cb.like(subRoot.join(ServicePlan_.scoring).get(ServicePlanScoring_.totalScore).as(String.class), search)
                );

                filterPredicates.add(
                        cb.like(subRoot.get(ServicePlan_.servicePlanStatus).as(String.class), fixForEnum(search))
                );

                filterPredicates.add(
                        cb.like(employeeFullName(subRoot.join(ServicePlan_.employee), cb), search)
                );

                predicates.add(cb.or(filterPredicates.toArray(new Predicate[0])));
            }

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(subRoot.get(ServicePlan_.servicePlanStatus), filter.getStatus()));
            }

            if (BooleanUtils.isTrue(filter.getResourceNamePopulated()) || filter.getOngoingService() != null) {
                var goalsJoin = cb.treat(subRoot.join(ServicePlan_.needs), ServicePlanGoalNeed.class).join(ServicePlanGoalNeed_.goals);

                if (BooleanUtils.isTrue(filter.getResourceNamePopulated())) {
                    predicates.add(cb.isNotNull(goalsJoin.get(ServicePlanGoal_.resourceName)));
                    predicates.add(cb.notEqual(goalsJoin.get(ServicePlanGoal_.resourceName), ""));
                }

                if (filter.getOngoingService() != null) {
                    predicates.add(cb.equal(goalsJoin.get(ServicePlanGoal_.ongoingService), filter.getOngoingService()));
                }
            }

            if (predicates.isEmpty()) {
                return servicePlanPredicateGenerator.byClientIdWithMerged(filter.getClientId(), root, cb, query);
            } else {

                subQuery.where(
                        servicePlanPredicateGenerator.byClientIdWithMerged(filter.getClientId(), root, cb, query),
                        cb.and(predicates.toArray(new Predicate[0]))
                );

                return root.get(ServicePlan_.id).in(subQuery);
            }
        };

    }

    public Specification<ServicePlan> distinct() {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.and();
        };
    }

    public Specification<ServicePlan> hasAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> servicePlanPredicateGenerator.hasAccess(root, criteriaQuery, criteriaBuilder, permissionFilter);
    }

    public <T extends ServicePlanNeed> Specification<T> hasAccessToNeed(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) ->
                servicePlanPredicateGenerator.hasAccess(root.join(ServicePlanNeed_.servicePlan), criteriaQuery, criteriaBuilder, permissionFilter);
    }

    public Specification<ServicePlanGoal> hasAccessToGoal(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) ->
                servicePlanPredicateGenerator.hasAccess(root.join(ServicePlanGoal_.need).join(ServicePlanGoalNeed_.servicePlan), criteriaQuery, criteriaBuilder, permissionFilter);
    }

    public Specification<ServicePlan> byClientIdWithMerged(Long clientId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                servicePlanPredicateGenerator.byClientIdWithMerged(clientId, root, criteriaBuilder, criteriaQuery);
    }

    public Specification<ServicePlan> byClientIdIn(Collection<Long> clientIds) {
        return (root, criteriaQuery, criteriaBuilder) -> root.get(ServicePlan_.clientId).in(clientIds);
    }

    public <T extends IdNameAware> Specification<ServicePlan> byClientCommunities(List<T> communities) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var communityIds = CareCoordinationUtils.toIdsSet(communities);
            return criteriaBuilder.and(
                    CollectionUtils.isEmpty(communityIds) ? criteriaBuilder.or() : root.join(ServicePlan_.client).get(Client_.communityId).in(communityIds)
            );
        };
    }

    public Specification<ServicePlan> inDevelopmentTillDate(Instant dateEnd) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Objects.requireNonNull(dateEnd);

            return servicePlanPredicateGenerator.getInDevelopmentServicePlansTillDate(dateEnd, root, criteriaBuilder);
        };
    }

    public Specification<ServicePlan> withinReportPeriod(Instant dateStart, Instant dateEnd) {
        return (root, criteriaQuery, criteriaBuilder) -> withinReportPeriod(dateStart, dateEnd, root, criteriaBuilder);
    }

    public Specification<ServicePlanNeed> needsWithinReportPeriod(Instant dateStart, Instant dateEnd) {
        return (root, criteriaQuery, criteriaBuilder) -> withinReportPeriod(dateStart, dateEnd, needToServicePlan(root), criteriaBuilder);

    }

    public Specification<ServicePlanGoal> goalsWithinReportPeriod(Instant dateStart, Instant dateEnd) {
        return (root, criteriaQuery, criteriaBuilder) -> withinReportPeriod(dateStart, dateEnd, goalToServicePlan(root), criteriaBuilder);
    }

    private Predicate withinReportPeriod(Instant dateStart, Instant dateEnd, Path<ServicePlan> path, CriteriaBuilder criteriaBuilder) {
        Objects.requireNonNull(dateStart);
        Objects.requireNonNull(dateEnd);

        return criteriaBuilder.or(
                servicePlanPredicateGenerator.getInDevelopmentServicePlansTillDate(dateEnd, path, criteriaBuilder),
                servicePlanPredicateGenerator.getSharedWithClientServicePlansWithinPeriod(dateStart, dateEnd, path, criteriaBuilder)
        );
    }

    public Specification<ServicePlanNeed> leaveLatestNeeds(Instant till) {
        return (root, criteriaQuery, criteriaBuilder) ->
                auditableEntityPredicateGenerator.leaveLatest(ServicePlan.class, till, needToServicePlan(root), criteriaQuery, criteriaBuilder);
    }

    public Specification<ServicePlanGoal> leaveLatestGoals(Instant till) {
        return (root, criteriaQuery, criteriaBuilder) ->
                auditableEntityPredicateGenerator.leaveLatest(ServicePlan.class, till, goalToServicePlan(root), criteriaQuery, criteriaBuilder);
    }

    public Specification<ServicePlanGoal> goalHistoryByChainId(ServicePlanGoal goal) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(
                //self goal is not included
                criteriaBuilder.lessThan(goalToServicePlan(root).get(ServicePlan_.lastModifiedDate), goal.getNeed().getServicePlan().getLastModifiedDate()),
                criteriaBuilder.equal(
                        auditableEntityPredicateGenerator.historyId(root, criteriaBuilder),
                        auditableEntityPredicateGenerator.historyId(goal))
        );
    }

    public Specification<ServicePlanGoal> goalFuzzyHistoryByGoalName(ServicePlanGoal goal) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var servicePlanPath = goalToServicePlan(root);
            return criteriaBuilder.and(
                    //self goal is not included
                    criteriaBuilder.lessThan(servicePlanPath.get(ServicePlan_.lastModifiedDate), goal.getNeed().getServicePlan().getLastModifiedDate()),
                    criteriaBuilder.equal(
                            auditableEntityPredicateGenerator.historyId(servicePlanPath, criteriaBuilder),
                            auditableEntityPredicateGenerator.historyId(goal.getNeed().getServicePlan())),
                    criteriaBuilder.equal(root.get(ServicePlanGoal_.goal), goal.getGoal())
            );
        };
    }

    public Specification<ServicePlan> unarchived() {
        return (root, query, criteriaBuilder) ->
            auditableEntityPredicateGenerator.unarchived(root, criteriaBuilder);
    }

    public Specification<ServicePlanNeed> unarchivedNeeds() {
        return (root, criteriaQuery, criteriaBuilder) ->
                auditableEntityPredicateGenerator.unarchived(needToServicePlan(root), criteriaBuilder);
    }

    public Specification<ServicePlanNeed> needsOfServicePlans(List<Long> servicePlanIds) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(needToServicePlan(root).get(ServicePlan_.ID)).value(servicePlanIds);
    }

    public Specification<ServicePlanGoal> goalsOfServicePlans(List<Long> servicePlanIds) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(goalToServicePlan(root).get(ServicePlan_.ID)).value(servicePlanIds);
    }

    public <T extends IdAware> Specification<ServicePlan> ofCommunities(List<T> communities) {
        return (root, query, criteriaBuilder) ->
            CollectionUtils.isEmpty(communities)
                ? criteriaBuilder.or()
                : root.get(ServicePlan_.client).get(Client_.communityId).in(CareCoordinationUtils.toIdsSet(communities));
    }

    public Specification<ServicePlan> ofCommunityIds(List<Long> communities) {
        return (root, query, criteriaBuilder) -> ofCommunityIds(communities, root, criteriaBuilder);
    }

    public Specification<ServicePlanScoring> scoringsOfServicePlans(List<Long> servicePlanIds) {
        return (root, query, criteriaBuilder) ->
                CollectionUtils.isEmpty(servicePlanIds) ? //
                        criteriaBuilder.or() : //
                        criteriaBuilder.in(root.get(ServicePlanScoring_.SERVICE_PLAN_ID)).value(servicePlanIds);
    }

    private Predicate ofCommunityIds(List<Long> communities, Path<ServicePlan> root, CriteriaBuilder criteriaBuilder) {
        return CollectionUtils.isEmpty(communities) ?
                criteriaBuilder.or() :
                criteriaBuilder.in(root.get(ServicePlan_.client).get(Client_.COMMUNITY_ID)).value(communities);
    }

    private <T extends ServicePlanNeed> From<?, ServicePlan> needToServicePlan(From<?, T> needPath) {
        return needPath.join(ServicePlanNeed_.servicePlan);
    }

    private From<?, ServicePlan> goalToServicePlan(From<?, ServicePlanGoal> goalPath) {
        return needToServicePlan(goalPath.join(ServicePlanGoal_.need));
    }

    @Override
    protected Class<ServicePlan> getEntityClass() {
        return ServicePlan.class;
    }

    public <T extends IdNameAware> Specification<ServicePlan> byAccessibleClientsInCommunitiesCreatedBeforeOrWithoutDateCreatedActiveUntilDate
            (PermissionFilter permissionFilter, Collection<T> communities, Instant createdDate, Instant activeDate) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var accessibleClients = SpecificationUtils.subquery(Client.class,
                    criteriaQuery,
                    clientRoot ->
                            clientSpecificationGenerator.accessibleClientsInCommunitiesCreatedBeforeOrWithoutDateCreatedActiveUntilDate(permissionFilter, communities, createdDate, activeDate)
                                    .toPredicate(clientRoot, criteriaQuery, criteriaBuilder));
            return root.get(ServicePlan_.clientId).in(accessibleClients);
        };
    }

    public Specification<ServicePlan> byStatus(ServicePlanStatus servicePlanStatus) {
        return (root, criteriaQuery, criteriaBuilder) ->
                servicePlanPredicateGenerator.withStatus(servicePlanStatus, root, criteriaBuilder);
    }
}
