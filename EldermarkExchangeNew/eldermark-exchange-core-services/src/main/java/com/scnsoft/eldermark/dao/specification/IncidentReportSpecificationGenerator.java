package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.IncidentReportFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.EventPredicateGenerator;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.Event_;
import com.scnsoft.eldermark.entity.event.incident.IncidentReport;
import com.scnsoft.eldermark.entity.event.incident.IncidentReport_;
import com.scnsoft.eldermark.entity.security.Permission;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class IncidentReportSpecificationGenerator extends AuditableEntitySpecificationGenerator<IncidentReport> {

    @Autowired
    private EventPredicateGenerator eventPredicateGenerator;

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    public Specification<IncidentReport> byFilter(IncidentReportFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Event, Client> joinClient = root.join(IncidentReport_.event).join(Event_.client);

            if (filter.getOrganizationId() != null) {
                predicates.add(criteriaBuilder.equal(joinClient.get(Client_.organizationId), filter.getOrganizationId()));
            }

            if (CollectionUtils.isNotEmpty(filter.getCommunityIds())) {
                predicates.add(joinClient.get(Client_.communityId).in(filter.getCommunityIds()));
            }

            if (CollectionUtils.isNotEmpty(filter.getStatuses())) {
                predicates.add(root.get(IncidentReport_.status).in(filter.getStatuses()));
            }

            if (filter.getFromDate()!=null){
                predicates.add(fromDate(Instant.ofEpochMilli(filter.getFromDate()),criteriaBuilder,root));
            }

            if(filter.getToDate()!=null){
                predicates.add(toDate(Instant.ofEpochMilli(filter.getToDate()),criteriaBuilder,root));
            }

            if (filter.getClientId()!=null){
                predicates.add(criteriaBuilder.equal(joinClient.get(Client_.id), filter.getClientId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<IncidentReport> hasAccess(PermissionFilter predicatePermissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var eventJoin = JpaUtils.getOrCreateJoin(root, IncidentReport_.event);
            var clientJoin = JpaUtils.getOrCreateJoin(eventJoin, Event_.client);
            var communityJoin = JpaUtils.getOrCreateJoin(clientJoin, Client_.community);

            var labsEnabledPredicate = criteriaBuilder.equal(communityJoin.get(Community_.irEnabled), true);

            var eventAccessPredicate = eventPredicateGenerator.hasAccess(predicatePermissionFilter, eventJoin, criteriaQuery, criteriaBuilder);
            var irPredicates = new ArrayList<Predicate>();

            if (predicatePermissionFilter.hasPermission(Permission.IR_MANAGE_OPTED_IN_IF_QA)) {
                irPredicates.add(clientPredicateGenerator.isOptedIn(clientJoin, criteriaBuilder));
            }

            if (predicatePermissionFilter.hasPermission(Permission.IR_MANAGE_IF_ASSOCIATED_ORGANIZATION_AND_QA)) {
                var employees = predicatePermissionFilter.getEmployees(Permission.IR_MANAGE_IF_ASSOCIATED_ORGANIZATION_AND_QA);
                var employeeOrganizationIds = SpecificationUtils.employeesOrganizationIds(employees);
                irPredicates.add(clientJoin.get(Client_.organizationId).in(employeeOrganizationIds));
            }

            if (predicatePermissionFilter.hasPermission(Permission.IR_MANAGE_IF_ASSOCIATED_COMMUNITY_AND_QA)) {
                var employees = predicatePermissionFilter.getEmployees(Permission.IR_MANAGE_IF_ASSOCIATED_COMMUNITY_AND_QA);
                var employeeCommunityIds = SpecificationUtils.employeesCommunityIds(employees);
                irPredicates.add(clientJoin.get(Client_.communityId).in(employeeCommunityIds));
            }

            return criteriaBuilder.and(
                    labsEnabledPredicate,
                    eventAccessPredicate,
                    criteriaBuilder.or(irPredicates.toArray(new Predicate[0]))
            );
        };
    }

    private Predicate fromDate(Instant dateFrom, CriteriaBuilder criteriaBuilder, Root<IncidentReport> root) {
        if (dateFrom != null) {
            return criteriaBuilder.greaterThanOrEqualTo(root.get(IncidentReport_.incidentDatetime), dateFrom);
        }
        return criteriaBuilder.and();
    }

    private Predicate toDate(Instant dateTo, CriteriaBuilder criteriaBuilder, Root<IncidentReport> root) {
        if (dateTo != null) {
            return criteriaBuilder.lessThanOrEqualTo(root.get(IncidentReport_.incidentDatetime), dateTo);
        }
        return criteriaBuilder.and();
    }

    @Override
    protected Class<IncidentReport> getEntityClass() {
        return IncidentReport.class;
    }
}
