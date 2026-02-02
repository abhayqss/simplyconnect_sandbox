package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.audit.AuditLogAction;
import com.scnsoft.eldermark.beans.audit.AuditLogActionGroup;
import com.scnsoft.eldermark.beans.audit.AuditLogActionWithParams;
import com.scnsoft.eldermark.beans.audit.AuditLogFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.CommunityPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.EmployeePredicateGenerator;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.assessment.Assessment_;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult_;
import com.scnsoft.eldermark.entity.audit.*;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.note.NoteType;
import com.scnsoft.eldermark.entity.note.Note_;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuditLogSpecificationGenerator {


    @Autowired
    private EmployeePredicateGenerator employeePredicateGenerator;

    @Autowired
    private CommunityPredicateGenerator communityPredicateGenerator;

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    public Specification<AuditLog> byActions(Iterable<AuditLogAction> actions) {
        return (root, query, criteriaBuilder) -> root.get(AuditLog_.action).in(actions);
    }

    public Specification<AuditLog> byEmployeeStatus(EmployeeStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join(AuditLog_.employee).get(Employee_.status), status);
    }

    public Specification<AuditLog> notContact4d() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join(AuditLog_.employee).get(Employee_.contact4d), Boolean.FALSE);
    }

    public Specification<AuditLog> greatestBeforeDate(Instant before) {
        return (root, query, criteriaBuilder) -> {
            var subQuery = query.subquery(Long.class);
            var subRoot = subQuery.from(AuditLog.class);
            subQuery.select(subRoot.get(AuditLog_.employeeId))
                    .groupBy(subRoot.get(AuditLog_.employeeId))
                    .having(criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.greatest(subRoot.get(AuditLog_.date)), before));

            return root.get(AuditLog_.employeeId).in(subQuery);
        };
    }

    public Specification<AuditLog> withoutSentNotificationForDeactivateEmployees() {
        return (root, query, criteriaBuilder) -> {
            var subQuery = query.subquery(Long.class);
            var subRoot = subQuery.from(Employee.class);

            var auditLogSubQuery = query.subquery(Instant.class);
            var auditLogSubRoot = auditLogSubQuery.from(AuditLog.class);
            auditLogSubQuery.select(criteriaBuilder.greatest(auditLogSubRoot.get(AuditLog_.date)))
                    .where(criteriaBuilder.equal(auditLogSubRoot.get(AuditLog_.employeeId), root.get(AuditLog_.employeeId)));

            var notificationSubQuery = query.subquery(Instant.class);
            var notificationSubRoot = notificationSubQuery.from(DeactivateEmployeeNotification.class);
            notificationSubQuery.select(criteriaBuilder.greatest(notificationSubRoot.get(DeactivateEmployeeNotification_.sentDatetime)))
                    .where(criteriaBuilder.equal(notificationSubRoot.get(DeactivateEmployeeNotification_.employeeId), root.get(AuditLog_.employeeId)));

            subQuery.select(subRoot.get(Employee_.id))
                    .where(criteriaBuilder.and(
                            criteriaBuilder.equal(subRoot.get(Employee_.id), root.get(AuditLog_.employeeId)),
                            criteriaBuilder.greaterThan(notificationSubQuery, auditLogSubQuery))
                    );

            return criteriaBuilder.not(root.get(AuditLog_.employeeId).in(subQuery));
        };
    }

    public Specification<AuditLog> notManuallyActivatedAfter(Instant date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.isNull(
                                JpaUtils.getOrCreateJoin(root, AuditLog_.employee).get(Employee_.manualActivationDateTime)
                        ),
                        criteriaBuilder.lessThanOrEqualTo(
                                JpaUtils.getOrCreateJoin(root, AuditLog_.employee).get(Employee_.manualActivationDateTime), date
                        )
                );
    }

    public Specification<AuditLog> hasAccess(PermissionFilter permissionFilter, List<AuditLogActionWithParams> actions) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            if (permissionFilter.hasPermission(Permission.ROLE_SUPER_ADMINISTRATOR)) {
                return criteriaBuilder.and();
            }

            var predicates = new ArrayList<Predicate>();

            if (permissionFilter.hasPermission(Permission.AUDIT_LOG_VIEW_IF_ASSOCIATED_ORGANIZATION)) {
                var employees = permissionFilter.getEmployees(Permission.AUDIT_LOG_VIEW_IF_ASSOCIATED_ORGANIZATION);
                var employeeOrganizations = CareCoordinationUtils.getOrganizationIdsSet(employees);

                var subQuery = criteriaQuery.subquery(Long.class);
                var subRoot = subQuery.from(AuditLog.class);
                var join = JpaUtils.getOrCreateListJoin(subRoot, AuditLog_.organizations);
                subQuery.select(subRoot.get(AuditLog_.id)).where(join.get(Organization_.id).in(employeeOrganizations));

                predicates.add(root.get(AuditLog_.id).in(subQuery));
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<AuditLog> byFilter(AuditLogFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            if (CollectionUtils.isNotEmpty(filter.getClientIds())) {
                var subQuery = criteriaQuery.subquery(Long.class);
                var subRoot = subQuery.from(AuditLog.class);
                var join = JpaUtils.getOrCreateListJoin(subRoot, AuditLog_.clients);
                subQuery.select(subRoot.get(AuditLog_.id)).where(join.get(Client_.id).in(filter.getClientIds()));

                predicates.add(root.get(AuditLog_.id).in(subQuery));
            } else {
                if (BooleanUtils.isNotTrue(filter.getIncludeInactiveClients())) {
                    var subQuery = criteriaQuery.subquery(Long.class);
                    var subRoot = subQuery.from(AuditLog.class);
                    var join = JpaUtils.getOrCreateListJoin(subRoot, AuditLog_.clients);
                    subQuery.select(subRoot.get(AuditLog_.id)).where(clientPredicateGenerator.isActive(join, criteriaBuilder));

                    predicates.add(criteriaBuilder.or(
                            root.get(AuditLog_.id).in(subQuery),
                            criteriaBuilder.isEmpty(root.get(AuditLog_.clients))
                    ));
                }

                if (CollectionUtils.isNotEmpty(filter.getCommunityIds())) {
                    var subQuery = criteriaQuery.subquery(Long.class);
                    var subRoot = subQuery.from(AuditLog.class);
                    var join = JpaUtils.getOrCreateListJoin(subRoot, AuditLog_.communities);
                    subQuery.select(subRoot.get(AuditLog_.id)).where(join.get(Community_.id).in(filter.getCommunityIds()));

                    predicates.add(root.get(AuditLog_.id).in(subQuery));
                } else if (filter.getOrganizationId() != null) {
                    var orgSubQuery = criteriaQuery.subquery(Long.class);
                    var orgSubRoot = orgSubQuery.from(AuditLog.class);
                    var orgJoin = JpaUtils.getOrCreateListJoin(orgSubRoot, AuditLog_.organizations);
                    orgSubQuery.select(orgSubRoot.get(AuditLog_.id)).where(criteriaBuilder.equal(orgJoin.get(Organization_.id), filter.getOrganizationId()));

                    var includeInactiveCommunities = BooleanUtils.isTrue(filter.getIncludeInactiveCommunities());
                    var communitySubQuery = criteriaQuery.subquery(Long.class);
                    var communitySubRoot = communitySubQuery.from(AuditLog.class);
                    var communityJoin = JpaUtils.getOrCreateListJoin(communitySubRoot, AuditLog_.communities);
                    communitySubQuery.select(communitySubRoot.get(AuditLog_.id)).where(criteriaBuilder.and(
                            criteriaBuilder.equal(communityJoin.get(Community_.organizationId), filter.getOrganizationId()),
                            communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, communityJoin, includeInactiveCommunities)
                    ));

                    predicates.add(criteriaBuilder.or(
                            root.get(AuditLog_.id).in(communitySubQuery),
                            criteriaBuilder.and(root.get(AuditLog_.id).in(orgSubQuery),
                                    criteriaBuilder.isEmpty(root.get(AuditLog_.communityIds))
                            )
                    ));
                }
            }

            if (CollectionUtils.isNotEmpty(filter.getEmployeeIds())) {
                predicates.add(root.get(AuditLog_.employeeId).in(filter.getEmployeeIds()));
            } else if (BooleanUtils.isNotTrue(filter.getIncludeInactiveEmployees())) {
                predicates.add(employeePredicateGenerator.isActive(JpaUtils.getOrCreateJoin(root, AuditLog_.employee), criteriaBuilder));
            }

            var actionPredicates = new ArrayList<Predicate>();
            if (CollectionUtils.isNotEmpty(filter.getActions())) {
                var actionsWithoutGroup = filter.getActions().stream()
                        .filter(a -> a.getActionGroup() == null)
                        .map(AuditLogActionWithParams::getAction)
                        .collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(actionsWithoutGroup)) {
                    actionPredicates.add(root.get(AuditLog_.action).in(actionsWithoutGroup));
                }

                var assessmentActions = filter.getActions().stream().filter(a -> AuditLogActionGroup.ASSESSMENT == a.getActionGroup()).collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(assessmentActions)) {
                    var shortNames = assessmentActions.stream()
                            .map(AuditLogActionWithParams::getParams)
                            .flatMap(List::stream)
                            .collect(Collectors.toSet());
                    var actions = assessmentActions.stream()
                            .map(AuditLogActionWithParams::getAction)
                            .collect(Collectors.toSet());
                    var subQuery = criteriaQuery.subquery(Long.class);
                    var subRoot = subQuery.from(AuditLogAssessmentRelation.class);
                    subQuery.select(subRoot.get(AuditLogAssessmentRelation_.id))
                            .where(subRoot.get(AuditLogAssessmentRelation_.assessmentResult).get(ClientAssessmentResult_.assessment).get(Assessment_.shortName).in(shortNames));
                    actionPredicates.add(
                            criteriaBuilder.and(
                                    root.get(AuditLog_.action).in(actions),
                                    root.get(AuditLog_.auditLogRelationId).in(subQuery)
                            )
                    );
                }

                var noteActions = filter.getActions().stream().filter(a -> AuditLogActionGroup.NOTE == a.getActionGroup()).collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(noteActions)) {
                    var noteTypes = noteActions.stream()
                            .map(AuditLogActionWithParams::getParams)
                            .flatMap(List::stream)
                            .map(NoteType::valueOf)
                            .collect(Collectors.toSet());
                    var actions = noteActions.stream()
                            .map(AuditLogActionWithParams::getAction)
                            .collect(Collectors.toSet());
                    var subQuery = criteriaQuery.subquery(Long.class);
                    var subRoot = subQuery.from(AuditLogNoteRelation.class);
                    subQuery.select(subRoot.get(AuditLogNoteRelation_.id))
                            .where(subRoot.get(AuditLogNoteRelation_.note).get(Note_.type).in(noteTypes));
                    actionPredicates.add(
                            criteriaBuilder.and(
                                    root.get(AuditLog_.action).in(actions),
                                    root.get(AuditLog_.auditLogRelationId).in(subQuery)
                            )
                    );
                }
            }
            if (CollectionUtils.isNotEmpty(actionPredicates)) {
                var predicateArray = new Predicate[actionPredicates.size()];
                predicates.add(criteriaBuilder.or(actionPredicates.toArray(predicateArray)));
            }

            if (filter.getFromDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(AuditLog_.date), Instant.ofEpochMilli(filter.getFromDate())));
            }

            if (filter.getToDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(AuditLog_.date), Instant.ofEpochMilli(filter.getToDate())));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
