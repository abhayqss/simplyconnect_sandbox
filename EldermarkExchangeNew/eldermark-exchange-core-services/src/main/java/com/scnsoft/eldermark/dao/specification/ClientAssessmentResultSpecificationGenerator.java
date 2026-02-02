package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.AssessmentPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.ClientAssessmentResultPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.SecurityPredicateGenerator;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.assessment.*;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;

import static com.scnsoft.eldermark.dao.specification.SpecificationUtils.fixForEnum;

@Component
public class ClientAssessmentResultSpecificationGenerator extends AuditableEntitySpecificationGenerator<ClientAssessmentResult> {

    @Override
    protected Class<ClientAssessmentResult> getEntityClass() {
        return ClientAssessmentResult.class;
    }

    @Autowired
    private AssessmentPredicateGenerator assessmentPredicateGenerator;

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    @Autowired
    private ClientAssessmentResultPredicateGenerator clientAssessmentResultPredicateGenerator;

    @Autowired
    private ClientSpecificationGenerator clientSpecificationGenerator;

    public Specification<ClientAssessmentResult> byFilter(Long clientId, String searchString) {
        return (root, criteriaQuery, cb) -> {
            var unarchivedAndMergedClients = cb.and(
                    clientPredicateGenerator.clientAndMergedClients(cb, root.join(ClientAssessmentResult_.client),
                            criteriaQuery, Collections.singletonList(clientId)),
                    isUnarchived().toPredicate(root, criteriaQuery, cb)
            );

            if (StringUtils.isNotBlank(searchString)) {
                var search = SpecificationUtils.wrapWithWildcards(searchString.trim());
                var employeeJoin = root.join(ClientAssessmentResult_.employee);
                return cb.and(
                        unarchivedAndMergedClients,
                        cb.or(
                                cb.like(root.join(ClientAssessmentResult_.assessment).get(Assessment_.name), search),
                                cb.like(SpecificationUtils.employeeFullName(employeeJoin, cb), search),
                                cb.like(root.get(ClientAssessmentResult_.ASSESSMENT_STATUS).as(String.class), fixForEnum(search))
                        )
                );

            }
            return unarchivedAndMergedClients;
        };
    }

    public Specification<ClientAssessmentResult> isAssessmentTypeEnabled() {
        return (root, query, criteriaBuilder) -> {

            var organizationIdPath = JpaUtils.getOrCreateJoin(root, ClientAssessmentResult_.client, JoinType.INNER)
                    .get(Client_.organizationId);

            var assessmentJoin = JpaUtils.getOrCreateJoin(root, ClientAssessmentResult_.assessment, JoinType.INNER);

            return assessmentPredicateGenerator.typesAllowedInOrganization(
                    organizationIdPath,
                    assessmentJoin,
                    query,
                    criteriaBuilder
            );
        };
    }

    public Specification<ClientAssessmentResult> hasAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var clientJoin = JpaUtils.getOrCreateJoin(root, ClientAssessmentResult_.client, JoinType.INNER);
            var eligible = securityPredicateGenerator.clientInEligibleForDiscoveryCommunity(clientJoin, criteriaBuilder);

            var predicates = new ArrayList<Predicate>();
            var assessmentJoin = JpaUtils.getOrCreateJoin(root, ClientAssessmentResult_.assessment, JoinType.INNER);
            Predicate hidden = criteriaBuilder.and();

            if (!permissionFilter.hasPermission(Permission.ASSESSMENT_VIEW_HIDDEN_ALLOWED)) {
                hidden = criteriaBuilder.notEqual(root.get(ClientAssessmentResult_.ASSESSMENT_STATUS), AssessmentStatus.HIDDEN);
            }

            addPermissionPredicate(Permission.ASSESSMENT_VIEW_ALL_EXCEPT_OPTED_OUT,
                    criteriaBuilder, criteriaQuery, predicates, permissionFilter,
                    clientJoin, assessmentJoin,
                    (clientFrom, employees) -> clientPredicateGenerator.isOptedOut(clientFrom, criteriaBuilder).not());

            addPermissionPredicate(Permission.ASSESSMENT_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION,
                    criteriaBuilder, criteriaQuery, predicates, permissionFilter,
                    clientJoin, assessmentJoin,
                    (clientFrom, employees) -> SpecificationUtils.in(criteriaBuilder, clientFrom.get(Client_.organizationId),
                            SpecificationUtils.employeesOrganizationIds(employees)));

            addPermissionPredicate(Permission.ASSESSMENT_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY,
                    criteriaBuilder, criteriaQuery, predicates, permissionFilter,
                    clientJoin, assessmentJoin,
                    (clientFrom, employees) -> SpecificationUtils.in(criteriaBuilder, clientFrom.get(Client_.communityId),
                            SpecificationUtils.employeesCommunityIds(employees)));

            addPermissionPredicate(Permission.ASSESSMENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
                    criteriaBuilder, criteriaQuery, predicates, permissionFilter,
                    clientJoin, assessmentJoin,
                    (clientFrom, employees) ->
                            criteriaBuilder.and(
                                    securityPredicateGenerator.primaryCommunitiesOfOrganizations(criteriaBuilder,
                                            criteriaQuery, clientFrom.get(Client_.communityId), employees),
                                    clientPredicateGenerator.isOptedOut(clientFrom, criteriaBuilder).not()
                            )
);

            addPermissionPredicate(Permission.ASSESSMENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
                    criteriaBuilder, criteriaQuery, predicates, permissionFilter,
                    clientJoin, assessmentJoin,
                    (clientFrom, employees) -> criteriaBuilder.and(
                            securityPredicateGenerator.primaryCommunities(
                                    criteriaBuilder,
                                    criteriaQuery,
                                    clientFrom.get(Client_.communityId),
                                    employees
                            ),
                            clientPredicateGenerator.isOptedOut(clientFrom, criteriaBuilder).not()
                    ));

            addPermissionPredicate(Permission.ASSESSMENT_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM,
                    criteriaBuilder, criteriaQuery, predicates, permissionFilter,
                    clientJoin, assessmentJoin,
                    (clientFrom, employees) -> securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(
                            criteriaBuilder,
                            criteriaQuery,
                            clientFrom.get(Client_.communityId),
                            employees,
                            AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                            HieConsentCareTeamType.current(clientFrom)));

            addPermissionPredicate(Permission.ASSESSMENT_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM,
                    criteriaBuilder, criteriaQuery, predicates, permissionFilter,
                    clientJoin, assessmentJoin,
                    (clientFrom, employees) -> securityPredicateGenerator.clientsInClientCareTeamPredicate(
                            criteriaBuilder,
                            criteriaQuery,
                            clientFrom,
                            employees,
                            AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                            HieConsentCareTeamType.current(clientFrom)
                    ));

            addPermissionPredicate(Permission.ASSESSMENT_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                    criteriaBuilder, criteriaQuery, predicates, permissionFilter,
                    clientJoin, assessmentJoin,
                    (clientFrom, employees) -> criteriaBuilder.and(
                            securityPredicateGenerator.clientAddedByEmployees(criteriaBuilder, clientFrom, employees),
                            clientPredicateGenerator.isOptedOut(clientFrom, criteriaBuilder).not()
                    ));

            addPermissionPredicate(Permission.ASSESSMENT_VIEW_MERGED_IF_SELF_RECORD,
                    criteriaBuilder, criteriaQuery, predicates, permissionFilter,
                    clientJoin, assessmentJoin,
                    (clientFrom, employees) -> securityPredicateGenerator.selfRecordClients(criteriaBuilder,
                            clientFrom.get(Client_.id), employees));

            //assessment types are not checked: if assessment exists, it should be visible on the list
            return criteriaBuilder.and(
                    eligible,
                    hidden,
                    criteriaBuilder.or(predicates.toArray(new Predicate[0]))
            );
        };
    }

    private void addPermissionPredicate(Permission permission, CriteriaBuilder cb, CriteriaQuery cq, List<Predicate> predicates,
                                        PermissionFilter permissionFilter,
                                        From<?, Client> clientJoin,
                                        From<?, Assessment> assessmentJoin,
                                        BiFunction<From<?, Client>, Collection<Employee>, Predicate> permissionPredicate) {
        if (permissionFilter.hasPermission(permission)) {
            var employees = permissionFilter.getEmployees(permission);

            var allTypesAllowedEmployees = Collections.<Employee>emptyList();

            if (permissionFilter.hasPermission(Permission.ASSESSMENT_VIEW_ALL_TYPES_ALLOWED)) {
                allTypesAllowedEmployees = permissionFilter.getEmployees(Permission.ASSESSMENT_VIEW_ALL_TYPES_ALLOWED);
            }

            //without assessment type restrictions by role
            var allTypesAllowedForPermission = CollectionUtils.intersection(employees, allTypesAllowedEmployees);
            if (CollectionUtils.isNotEmpty(allTypesAllowedForPermission)) {
                predicates.add(
                        clientPredicateGenerator.withMergedClients(clientJoin,
                                clientFrom -> permissionPredicate.apply(clientFrom, allTypesAllowedForPermission),
                                cb, cq)
                );
            }

            if (allTypesAllowedForPermission.size() != employees.size()) {

                //add employees with assessment type restriction by role
                var notAllTypesAllowed = CollectionUtils.removeAll(employees, allTypesAllowedForPermission);
                notAllTypesAllowed.forEach(employee ->
                        predicates.add(
                                cb.and(
                                        clientPredicateGenerator.withMergedClients(clientJoin,
                                                clientFrom -> permissionPredicate.apply(clientFrom, Collections.singletonList(employee)),
                                                cb, cq),
                                        cb.isMember(permissionFilter.getEmployeeRole(employee), assessmentJoin.get(Assessment_.allowedRoles))
                                )
                        )
                );
            }
        }
    }

    public Specification<ClientAssessmentResult> gad7CompletedOfCommunity(Community community) {
        return completedOfCommunity(community, Assessment.GAD7);
    }

    public Specification<ClientAssessmentResult> phq9CompletedOfCommunity(Community community) {
        return completedOfCommunity(community, Assessment.PHQ9);
    }

    public Specification<ClientAssessmentResult> comprehensiveCompletedOfCommunity(Community community) {
        return completedOfCommunity(community, Assessment.COMPREHENSIVE);
    }

    public <T extends IdAware> Specification<ClientAssessmentResult> ofCommunities(List<T> communities) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var communityIds = CareCoordinationUtils.toIdsSet(communities);
            return criteriaBuilder.and(
                    CollectionUtils.isEmpty(communityIds) ? criteriaBuilder.or() : root.join(ClientAssessmentResult_.client).get(Client_.communityId).in(communityIds)
            );
        };
    }

    public Specification<ClientAssessmentResult> ofCommunity(Community community) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (community == null) {
                return criteriaBuilder.or();
            }
            return criteriaBuilder.equal(root.get(ClientAssessmentResult_.client).get(Client_.community), community);
        };
    }

    public Specification<ClientAssessmentResult> ofOrganization(Long organizationId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (organizationId == null) {
                return criteriaBuilder.or();
            }
            return criteriaBuilder.equal(root.get(ClientAssessmentResult_.client).get(Client_.organizationId), organizationId);
        };
    }

    public Specification<ClientAssessmentResult> comprehensiveType() {
        return byType(Assessment.COMPREHENSIVE);
    }

    public <T extends IdNameAware> Specification<ClientAssessmentResult> completedOfCommunitiesByType(
            List<T> communities, Specification<ClientAssessmentResult> byType
    ) {
        return completed().and(ofCommunities(communities)).and(byType);
    }

    private Specification<ClientAssessmentResult> completedOfCommunity(Community community, String shortName) {
        return completed().and(ofCommunity(community)).and(byType(shortName));
    }

    public Specification<ClientAssessmentResult> gad7OfClient(Long clientId) {
        return ofClient(clientId).and(byType(Assessment.GAD7));
    }

    public Specification<ClientAssessmentResult> phq9OfClient(Long clientId) {
        return ofClient(clientId).and(byType(Assessment.PHQ9));
    }

    public Specification<ClientAssessmentResult> comprehensiveOfClient(Long clientId) {
        return ofClient(clientId).and(byType(Assessment.COMPREHENSIVE));
    }

    public Specification<ClientAssessmentResult> byType(String shortName) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.join(ClientAssessmentResult_.assessment).get(Assessment_.shortName), shortName);
    }

    public Specification<ClientAssessmentResult> byTypeIn(Collection<String> shortNames) {
        return (root, criteriaQuery, criteriaBuilder) ->
                JpaUtils.getOrCreateJoin(root, ClientAssessmentResult_.assessment).get(Assessment_.shortName).in(shortNames);
    }

    public Specification<ClientAssessmentResult> ofClient(Long clientId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(ClientAssessmentResult_.clientId), clientId);
    }

    public Specification<ClientAssessmentResult> byClientIdIn(Collection<Long> clientIds) {
        return (root, criteriaQuery, criteriaBuilder) ->
                root.get(ClientAssessmentResult_.clientId).in(clientIds);
    }

    public Specification<ClientAssessmentResult> comprehensiveOfMergedClients(Long clientId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        clientPredicateGenerator.clientAndMergedClients(criteriaBuilder, root.join(ClientAssessmentResult_.client), criteriaQuery,
                                Collections.singletonList(clientId)),
                        criteriaBuilder.equal(root.join(ClientAssessmentResult_.assessment).get(Assessment_.shortName), Assessment.COMPREHENSIVE)
                );
    }

    public Specification<ClientAssessmentResult> norCalComprehensiveOfMergedClients(Long clientId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        clientPredicateGenerator.clientAndMergedClients(criteriaBuilder, root.join(ClientAssessmentResult_.client), criteriaQuery,
                                Collections.singletonList(clientId)),
                        criteriaBuilder.equal(root.join(ClientAssessmentResult_.assessment).get(Assessment_.shortName), Assessment.NOR_CAL_COMPREHENSIVE)
                );
    }

    public Specification<ClientAssessmentResult> withinReportPeriod(Instant start, Instant end) {
        return inProgressTillDate(end).or(completedWithinPeriod(start, end));
    }

    public Specification<ClientAssessmentResult> inProgressTillDate(Instant end) {
        Objects.requireNonNull(end);
        return (root, criteriaQuery, criteriaBuilder) ->
                clientAssessmentResultPredicateGenerator.inProgressTillDate(end, root, criteriaBuilder);
    }

    public Specification<ClientAssessmentResult> completedWithinPeriod(Instant start, Instant end) {
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        return (root, criteriaQuery, criteriaBuilder) ->
                clientAssessmentResultPredicateGenerator.completedWithinPeriod(start, end, root, criteriaBuilder);
    }

    public Specification<ClientAssessmentResult> completedFromDate(Instant date) {
        return (root, query, criteriaBuilder) ->
                clientAssessmentResultPredicateGenerator.completedFromDate(date, root, criteriaBuilder);
    }

    public Specification<ClientAssessmentResult> inProgress() {
        return (root, criteriaQuery, criteriaBuilder) ->
                clientAssessmentResultPredicateGenerator.inProgress(root, criteriaBuilder);
    }

    public Specification<ClientAssessmentResult> completed() {
        return (root, criteriaQuery, criteriaBuilder) ->
                clientAssessmentResultPredicateGenerator.completed(root, criteriaBuilder);
    }

    public Specification<ClientAssessmentResult> notHidden() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate hidden = clientAssessmentResultPredicateGenerator.hidden(root, criteriaBuilder);
            return criteriaBuilder.not(hidden);
        };
    }

    public Specification<ClientAssessmentResult> createdByAny(Collection<Long> employeeIds) {
        return (root, query, criteriaBuilder) ->
                CollectionUtils.isEmpty(employeeIds) ?
                        criteriaBuilder.or() :
                        criteriaBuilder.in(root.get(ClientAssessmentResult_.EMPLOYEE_ID)).value(employeeIds);
    }

    public <T extends IdNameAware> Specification<ClientAssessmentResult> byAccessibleClientsInCommunitiesCreatedBeforeOrWithoutDateCreatedActiveUntilDate
            (PermissionFilter permissionFilter, Collection<T> communities, Instant createdDate, Instant activeDate) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var accessibleClients = SpecificationUtils.subquery(Client.class,
                    criteriaQuery,
                    clientRoot ->
                            clientSpecificationGenerator.accessibleClientsInCommunitiesCreatedBeforeOrWithoutDateCreatedActiveUntilDate(permissionFilter, communities, createdDate, activeDate)
                                    .toPredicate(clientRoot, criteriaQuery, criteriaBuilder));
            return root.get(ClientAssessmentResult_.clientId).in(accessibleClients);
        };
    }

    public Specification<ClientAssessmentResult> byMaxDateStartedPerClient(Specification<ClientAssessmentResult> spec) {
        return (root, query, criteriaBuilder) -> {
            var subquery = query.subquery(Instant.class);
            var subqueryRoot = subquery.from(ClientAssessmentResult.class);

            subquery.select(criteriaBuilder.greatest(subqueryRoot.get(ClientAssessmentResult_.dateStarted)));
            subquery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(root.get(ClientAssessmentResult_.clientId), subqueryRoot.get(ClientAssessmentResult_.clientId)),
                            spec.toPredicate(subqueryRoot, CriteriaSubqueryWrapper.wrap(subquery), criteriaBuilder)
                    )
            );

            return criteriaBuilder.equal(root.get(ClientAssessmentResult_.dateStarted), subquery);
        };
    }

    public Specification<ClientAssessmentResult> byTypeId(Long typeId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(ClientAssessmentResult_.assessmentId), typeId);
    }

    public Specification<ClientAssessmentResult> latestCompletedBeforeDate(Instant instantTo) {
        return (root, query, criteriaBuilder) -> {
            var subQuery = query.subquery(Long.class);
            var subRoot = subQuery.from(ClientAssessmentResult.class);
            subQuery.select(criteriaBuilder.max(subRoot.get(ClientAssessmentResult_.id)));
            subQuery.groupBy(subRoot.get(ClientAssessmentResult_.clientId), subRoot.get(ClientAssessmentResult_.assessmentId));
            subQuery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.lessThanOrEqualTo(subRoot.get(ClientAssessmentResult_.dateCompleted), instantTo),
                            completed().toPredicate(subRoot, query, criteriaBuilder)
                    )
            );
            return criteriaBuilder.and(root.get(ClientAssessmentResult_.id).in(subQuery));
        };
    }

    public Specification<ClientAssessmentResult> byClientInCommunities(List<IdNameAware> communities) {
        return (root, query, criteriaBuilder) -> {
            var client = JpaUtils.getOrCreateJoin(root, ClientAssessmentResult_.client);
            return clientPredicateGenerator.byCommunities(communities, client, query, criteriaBuilder);
        };
    }
}
