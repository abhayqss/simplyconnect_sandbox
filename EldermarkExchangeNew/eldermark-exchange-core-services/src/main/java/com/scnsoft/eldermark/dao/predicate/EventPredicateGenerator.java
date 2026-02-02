package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.intersector.ViewableEventMultipleEmployeesStatementInspector;
import com.scnsoft.eldermark.dao.intersector.ViewableEventStatementInspector;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.Event_;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.EventTypeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventPredicateGenerator {

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    public Predicate from(Instant dateFrom, CriteriaBuilder criteriaBuilder, Root<Event> root) {
        if (dateFrom != null) {
            return criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.eventDateTime), dateFrom);
        }
        return criteriaBuilder.or();
    }

    public Predicate to(Instant dateTo, CriteriaBuilder criteriaBuilder, Root<Event> root) {
        if (dateTo != null) {
            return criteriaBuilder.lessThanOrEqualTo(root.get(Event_.eventDateTime), dateTo);
        }
        return criteriaBuilder.or();
    }

    public Predicate hasAccess(PermissionFilter permissionFilter, From<?, Event> root,
                               AbstractQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return hasAccess(permissionFilter, root, JpaUtils.getOrCreateJoin(root, Event_.client), query, criteriaBuilder);
    }

    public Predicate hasAccessIgnoringNotViewable(PermissionFilter permissionFilter, From<?, Event> root,
                                                  AbstractQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return hasAccessIgnoringNotViewble(permissionFilter, root, JpaUtils.getOrCreateJoin(root, Event_.client), query, criteriaBuilder);
    }

    //passing clientJoin as separate parameter for better EventNotes performance - we already have join to clients through EventOrNote_Resident table
    public Predicate hasAccess(PermissionFilter permissionFilter, From<?, Event> root, From<?, Client> clientJoin,
                               AbstractQuery<?> query, CriteriaBuilder criteriaBuilder) {

        var permissionPredicate = permissionPredicateOnlyByClient(permissionFilter, root, clientJoin, query, criteriaBuilder);

        var employeesIds = permissionFilter.getAllEmployeeIds();
        return criteriaBuilder.and(
                viewableEvents(root, employeesIds, criteriaBuilder, query),
                criteriaBuilder.or(permissionPredicate)
        );
    }

    public Predicate hasAccessIgnoringNotViewble(PermissionFilter permissionFilter, From<?, Event> root, From<?, Client> clientJoin,
                                                 AbstractQuery<?> query, CriteriaBuilder criteriaBuilder) {

        return permissionPredicateOnlyByClient(permissionFilter, root, clientJoin, query, criteriaBuilder);
    }

    public Predicate permissionPredicateOnlyByClient(PermissionFilter permissionFilter, From<?, Event> root, From<?, Client> clientJoin,
                                                     AbstractQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

//      tried the following optimization - join to merged clients only once and check permissions inside:
//      clientPredicateGenerator.withMergedClients(root,
//          clientFrom -> iterate through merged permissions here
//          criteriaBuilder, criteriaQuery);

//      Result turned out to have greater amount of logical reads due to bad rows estimation and therefore slower performance.
//      Future investigation is needed on why estimation is so bad and what we can do.
//      Tested on both 100 and 120 database compatibility levels
//      Current implementation is faster on SQL Server 2014 (subquery merged clients on each permission check)

        var eligible = securityPredicateGenerator.clientInEligibleForDiscoveryCommunity(clientJoin, criteriaBuilder);

        var predicates = new ArrayList<Predicate>();

        if (permissionFilter.hasPermission(Permission.EVENT_VIEW_MERGED_ALL_EXCEPT_OPTED_OUT_CLIENT)) {
            predicates.add(clientPredicateGenerator.isOptedIn(clientJoin, criteriaBuilder));
        }

        if (permissionFilter.hasPermission(Permission.EVENT_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION)) {
            predicates.add(securityPredicateGenerator.associatedOrganizationWithMergedPredicate(criteriaBuilder, clientJoin,
                    criteriaQuery, permissionFilter.getEmployees(Permission.EVENT_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION)));
        }

        if (permissionFilter.hasPermission(Permission.EVENT_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY)) {
            predicates.add(securityPredicateGenerator.associatedCommunityWithMergedClients(criteriaBuilder, clientJoin, criteriaQuery,
                    permissionFilter.getEmployees(Permission.EVENT_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY)));
        }

        if (permissionFilter.hasPermission(Permission.EVENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)) {
            predicates.add(
                    criteriaBuilder.and(
                            clientPredicateGenerator.isOptedIn(clientJoin, criteriaBuilder),
                            securityPredicateGenerator.primaryCommunitiesOfOrganizationsWithMergedClient(
                                    criteriaBuilder,
                                    clientJoin,
                                    criteriaQuery,
                                    permissionFilter.getEmployees(Permission.EVENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)
                            )
                    )
            );
        }

        if (permissionFilter.hasPermission(Permission.EVENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)) {
            predicates.add(
                    criteriaBuilder.and(
                            clientPredicateGenerator.isOptedIn(clientJoin, criteriaBuilder),
                            securityPredicateGenerator.primaryCommunitiesWithMergedClient(
                                    criteriaBuilder,
                                    clientJoin,
                                    criteriaQuery,
                                    permissionFilter.getEmployees(Permission.EVENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)
                            )
                    )
            );
        }

        if (permissionFilter.hasPermission(Permission.EVENT_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM)) {
            predicates.add(securityPredicateGenerator.communityCareTeamWithMergedPredicate(
                    criteriaBuilder,
                    clientJoin,
                    criteriaQuery,
                    permissionFilter.getEmployees(Permission.EVENT_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.current(clientJoin)
            ));
        }

        if (permissionFilter.hasPermission(Permission.EVENT_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM)) {
            predicates.add(securityPredicateGenerator.clientCareTeamWithMergedPredicate(
                    criteriaBuilder,
                    clientJoin,
                    criteriaQuery,
                    permissionFilter.getEmployees(Permission.EVENT_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.current(clientJoin)
            ));
        }

        if (permissionFilter.hasPermission(Permission.EVENT_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            predicates.add(
                    criteriaBuilder.and(
                            clientPredicateGenerator.isOptedIn(clientJoin, criteriaBuilder),
                            securityPredicateGenerator.addedByEmployeesWithMergedPredicate(
                                    criteriaBuilder,
                                    clientJoin,
                                    criteriaQuery,
                                    permissionFilter.getEmployees(Permission.EVENT_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)
                            )
                    )
            );
        }

        if (permissionFilter.hasPermission(Permission.EVENT_VIEW_MERGED_IF_SELF_CLIENT_RECORD)) {
            predicates.add(securityPredicateGenerator.selfRecordWithMergedPredicate(criteriaBuilder, clientJoin, criteriaQuery,
                    permissionFilter.getEmployees(Permission.EVENT_VIEW_MERGED_IF_SELF_CLIENT_RECORD)));
        }

        if (permissionFilter.hasPermission(Permission.EVENT_VIEW_MERGED_IF_CLIENT_FOUND_IN_RECORD_SEARCH) &&
                CollectionUtils.isNotEmpty(permissionFilter.getClientRecordSearchFoundIds())) {
            predicates.add(clientJoin.get(Client_.id).in(permissionFilter.getClientRecordSearchFoundIds()));
        }

        var notViewableByRolePredicate = notViewableByRole(permissionFilter, root, criteriaBuilder);
        return criteriaBuilder.and(
                notViewableByRolePredicate,
                eligible,
                criteriaBuilder.or(predicates.toArray(new Predicate[0]))
        );
    }

    public Predicate notViewableByRole(PermissionFilter permissionFilter, From<?, Event> eventJoin, CriteriaBuilder criteriaBuilder) {
        var notViewableByRolePredicate = criteriaBuilder.and();
        var roles = permissionFilter.getEmployees().stream().map(employee -> employee.getCareTeamRole().getCode()).collect(Collectors.toList());
        var notViewableIds = eventTypeService.findDisabledIdsByRoles(roles);
        if (CollectionUtils.isNotEmpty(notViewableIds)) {
            notViewableByRolePredicate = criteriaBuilder.not(eventJoin.get(Event_.eventTypeId).in(notViewableIds));
        }
        return notViewableByRolePredicate;
    }

    public Predicate viewableEvents(From<?, Event> eventJoin, Collection<Long> employeeIds, CriteriaBuilder cb, AbstractQuery<?> query) {
        return viewableEvents(List.of(eventJoin), employeeIds, cb, query);
    }

    public Predicate viewableEvents(List<From<?, Event>> eventJoins, Collection<Long> employeeIds, CriteriaBuilder cb, AbstractQuery<?> query) {
        if (CollectionUtils.isEmpty(employeeIds)) {
            return cb.and();
        }

        var eventTypeParam = eventJoins.size() > 1 ?
                cb.function("ISNULL", Long.class, eventJoins.stream().map(ej -> ej.get(Event_.eventTypeId)).toArray(Expression[]::new))
                : eventJoins.get(0).get(Event_.eventTypeId);

        var clientIdParam = eventJoins.size() > 1 ?
                cb.function("ISNULL", Long.class, eventJoins.stream().map(ej -> ej.get(Event_.clientId)).toArray(Expression[]::new)) :
                eventJoins.get(0).get(Event_.clientId);

        final Expression<Boolean> notViewableFunctionCall;
        if (employeeIds.size() == 1) {
            notViewableFunctionCall = cb.function(
                    ViewableEventStatementInspector.NOT_VIEWABLE_EVENT_TYPE_FAKE_FUNCTION,
                    Boolean.class,

                    cb.literal(employeeIds.iterator().next()),
                    clientIdParam,
                    eventTypeParam
            );
        } else {
            notViewableFunctionCall = cb.function(
                    ViewableEventMultipleEmployeesStatementInspector.NOT_VIEWABLE_EVENT_TYPE_MULTIPLE_EMPLOYEES_FAKE_FUNCTION,
                    Boolean.class,

                    cb.literal(employeeIds.stream().map(Object::toString).collect(Collectors.joining(","))),
                    clientIdParam,
                    eventTypeParam,
                    cb.literal(employeeIds.size())
            );
        }

        return cb.isFalse(notViewableFunctionCall);
    }

}
