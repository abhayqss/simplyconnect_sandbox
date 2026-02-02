package com.scnsoft.eldermark.dump.specification;


import com.scnsoft.eldermark.dump.bean.DumpFilter;
import com.scnsoft.eldermark.dump.entity.Client_;
import com.scnsoft.eldermark.dump.entity.Client;
import com.scnsoft.eldermark.dump.entity.Community;
import com.scnsoft.eldermark.dump.entity.serviceplan.*;
import com.scnsoft.eldermark.dump.specification.predicate.ClientPredicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

@Component
public class ServicePlanSpecificationGenerator extends AuditableEntitySpecificationGenerator<ClientServicePlan> {

    private static final int GOAL_ACCOMPLISHED_THRESHOLD = 100;

    public Specification<ClientServicePlan> details(DumpFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.in(root.join(ClientServicePlan_.client).get(Client_.ID)).value(filter.getResidentIds()),
                        unarchived(root, criteriaBuilder)
                );
    }

    public Specification<ClientServicePlan> byOrganizationIdAndStatus(Long organizationId, ServicePlanStatus status) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        unarchivedInOrganization(root, criteriaBuilder, organizationId),
                        criteriaBuilder.equal(root.get(ClientServicePlan_.servicePlanStatus), status)
                );
    }

    public Specification<ClientServicePlan> byCommunityAndStatus(Community community, ServicePlanStatus status) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        unarchivedInCommunity(root, criteriaBuilder, community),
                        criteriaBuilder.equal(root.get(ClientServicePlan_.servicePlanStatus), status)
                );
    }

    public Specification<ClientServicePlan> ofActiveClient() {
        return (root, criteriaQuery, criteriaBuilder) ->
                ClientPredicate.isActive(root.join(ClientServicePlan_.client), criteriaBuilder);
    }

    public Specification<ServicePlanNeed> needsByOrganizationId(Long organizationId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(
                unarchivedInOrganization(root.join(ServicePlanNeed_.servicePlan), criteriaBuilder, organizationId)
        );
    }

    public Specification<ServicePlanNeed> needsByCommunity(Community community) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(
                unarchivedInCommunity(root.join(ServicePlanNeed_.servicePlan), criteriaBuilder, community)
        );
    }

    public Specification<ServicePlanNeed> needsOfActiveClient() {
        return (root, criteriaQuery, criteriaBuilder) ->
                ClientPredicate.isActive(root.join(ServicePlanNeed_.servicePlan).join(ClientServicePlan_.client), criteriaBuilder);
    }

    public Specification<ServicePlanGoal> goalsByOrganizationId(Long organizationId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(
                unarchivedGoalsInOrganization(root, criteriaBuilder, organizationId)
        );
    }

    public Specification<ServicePlanGoal> goalsByCommunity(Community community) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(
                unarchivedGoalsInCommunity(root, criteriaBuilder, community)
        );
    }

    public Specification<ServicePlanGoal> goalsOfActiveClient() {
        return (root, criteriaQuery, criteriaBuilder) ->
                ClientPredicate.isActive(root.join(ServicePlanGoal_.need).join(ServicePlanNeed_.servicePlan).join(ClientServicePlan_.client), criteriaBuilder);
    }


    public Specification<ServicePlanGoal> clientGoals(Client client) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var planJoin = root.join(ServicePlanGoal_.need).join(ServicePlanNeed_.servicePlan);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(planJoin.get(ClientServicePlan_.client), client),
                    unarchived(planJoin, criteriaBuilder)
            );

        };
    }

    public Specification<ServicePlanGoal> accomplishedGoalsByOrganizationId(Long organizationId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(
                unarchivedGoalsInOrganization(root, criteriaBuilder, organizationId),
                criteriaBuilder.equal(root.get(ServicePlanGoal_.goalCompletion), GOAL_ACCOMPLISHED_THRESHOLD)
        );
    }

    public Specification<ServicePlanGoal> accomplishedGoalsByCommunity(Community community) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(
                unarchivedGoalsInCommunity(root, criteriaBuilder, community),
                criteriaBuilder.equal(root.get(ServicePlanGoal_.goalCompletion), GOAL_ACCOMPLISHED_THRESHOLD)
        );
    }

    private Predicate unarchivedInOrganization(From<?, ClientServicePlan> spFrom, CriteriaBuilder criteriaBuilder, Long organizationId) {
        return criteriaBuilder.and(
                criteriaBuilder.equal(spFrom.join(ClientServicePlan_.client).get(Client_.organizationId), organizationId),
                unarchived(spFrom, criteriaBuilder)
        );
    }

    private Predicate unarchivedInCommunity(From<?, ClientServicePlan> spFrom, CriteriaBuilder criteriaBuilder, Community community) {
        return criteriaBuilder.and(
                criteriaBuilder.equal(spFrom.join(ClientServicePlan_.client).get(Client_.community), community),
                unarchived(spFrom, criteriaBuilder)
        );
    }

    private Predicate unarchivedGoalsInOrganization(From<?, ServicePlanGoal> spFrom, CriteriaBuilder criteriaBuilder, Long organizationId) {
        var planJoin = spFrom.join(ServicePlanGoal_.need).join(ServicePlanNeed_.servicePlan);
        return unarchivedInOrganization(planJoin, criteriaBuilder, organizationId);
    }

    private Predicate unarchivedGoalsInCommunity(From<?, ServicePlanGoal> spFrom, CriteriaBuilder criteriaBuilder, Community community) {
        var planJoin = spFrom.join(ServicePlanGoal_.need).join(ServicePlanNeed_.servicePlan);
        return unarchivedInCommunity(planJoin, criteriaBuilder, community);
    }
}
