package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.ClientAppointmentFilter;
import com.scnsoft.eldermark.beans.ClientStatus;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.ClientAppointmentPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.SecurityPredicateGenerator;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentStatus;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment_;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class ClientAppointmentSpecificationGenerator extends AuditableEntitySpecificationGenerator<ClientAppointment> {

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    @Autowired
    private ClientAppointmentPredicateGenerator clientAppointmentPredicateGenerator;

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    public Specification<ClientAppointment> byFilter(ClientAppointmentFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(filter.getClientIds())) {
                predicates.add(root.get(ClientAppointment_.clientId).in(filter.getClientIds()));
            } else if (CollectionUtils.isNotEmpty(filter.getCommunityIds())) {
                var clientJoin = JpaUtils.getOrCreateJoin(root, ClientAppointment_.client);
                predicates.add(clientJoin.get(Client_.communityId).in(filter.getCommunityIds()));
            } else if (filter.getOrganizationId() != null) {
                var clientJoin = JpaUtils.getOrCreateJoin(root, ClientAppointment_.client);
                predicates.add(criteriaBuilder.equal(clientJoin.get(Client_.organizationId), filter.getOrganizationId()));
            }

            if (CollectionUtils.isNotEmpty(filter.getCreatorIds())) {
                predicates.add(root.get(ClientAppointment_.creatorId).in(filter.getCreatorIds()));
            }

            if (CollectionUtils.isNotEmpty(filter.getServiceProviderIds())
                    || BooleanUtils.isTrue(filter.getIsExternalProviderServiceProvider())
                    || BooleanUtils.isTrue(filter.getHasNoServiceProviders())) {
                criteriaQuery.distinct(true);
                List<Predicate> serviceProviderPredicates = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(filter.getServiceProviderIds())) {
                    var serviceProvidersJoin = JpaUtils.getOrCreateSetJoin(root, ClientAppointment_.serviceProviderIds, JoinType.LEFT);
                    serviceProviderPredicates.add(serviceProvidersJoin.in(filter.getServiceProviderIds()));
                }
                if (BooleanUtils.isTrue(filter.getIsExternalProviderServiceProvider())) {
                    serviceProviderPredicates.add(clientAppointmentPredicateGenerator.withExternalServiceProvider(root, criteriaBuilder));
                }
                if (BooleanUtils.isTrue(filter.getHasNoServiceProviders())) {
                    serviceProviderPredicates.add(clientAppointmentPredicateGenerator.withNoServiceProviders(root));
                }
                predicates.add(criteriaBuilder.or(serviceProviderPredicates.toArray(new Predicate[0])));
            }

            if (filter.getClientStatus() != null && filter.getClientStatus() != ClientStatus.ALL) {
                var clientActive = filter.getClientStatus() == ClientStatus.ACTIVE;
                var clientJoin = JpaUtils.getOrCreateJoin(root, ClientAppointment_.client);
                predicates.add(criteriaBuilder.equal(clientJoin.get(Client_.active), clientActive));
            }

            if (CollectionUtils.isNotEmpty(filter.getStatuses())) {
                predicates.add(root.get(ClientAppointment_.status).in(filter.getStatuses()));
            }

            if (CollectionUtils.isNotEmpty(filter.getTypes())) {
                predicates.add(root.get(ClientAppointment_.type).in(filter.getTypes()));
            }

            if (BooleanUtils.isTrue(filter.getIncludePlanned())) {
                predicates.add(criteriaBuilder.equal(root.get(ClientAppointment_.status), ClientAppointmentStatus.PLANNED));
            }

            if (BooleanUtils.isTrue(filter.getIncludeTriaged())) {
                predicates.add(criteriaBuilder.equal(root.get(ClientAppointment_.status), ClientAppointmentStatus.TRIAGED));
            }

            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(ClientAppointment_.dateFrom), Instant.ofEpochMilli(filter.getDateFrom())));
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(ClientAppointment_.dateTo), Instant.ofEpochMilli(filter.getDateTo())));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<ClientAppointment> hasAccessAndPrivateAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(hasAccess(permissionFilter).toPredicate(root, criteriaQuery, criteriaBuilder),
                hasPrivateAccess(permissionFilter).toPredicate(root, criteriaQuery, criteriaBuilder));
    }

    public Specification<ClientAppointment> hasAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            var lazyClientJoin = Lazy.of(() -> root.join(ClientAppointment_.client));
            var predicates = new ArrayList<Predicate>();

            if (permissionFilter.hasPermission(Permission.APPOINTMENT_VIEW_LIST_ALL)) {
                return criteriaBuilder.and();
            }

            if (permissionFilter.hasPermission(Permission.APPOINTMENT_VIEW_LIST_IF_ASSOCIATED_ORGANIZATION)) {
                var employees = permissionFilter.getEmployees(Permission.APPOINTMENT_VIEW_LIST_IF_ASSOCIATED_ORGANIZATION);
                predicates.add(securityPredicateGenerator.clientInAssociatedOrganization(criteriaBuilder,
                        lazyClientJoin.get(), employees));
            }

            if (permissionFilter.hasPermission(Permission.APPOINTMENT_VIEW_LIST_IF_ASSOCIATED_COMMUNITY)) {
                var employees = permissionFilter.getEmployees(Permission.APPOINTMENT_VIEW_LIST_IF_ASSOCIATED_COMMUNITY);
                predicates.add(securityPredicateGenerator.clientInAssociatedCommunity(criteriaBuilder,
                        lazyClientJoin.get(), employees));
            }

            if (permissionFilter.hasPermission(Permission.APPOINTMENT_VIEW_LIST_IF_FROM_AFFILIATED_ORGANIZATION)) {
                var employees = permissionFilter.getEmployees(Permission.APPOINTMENT_VIEW_LIST_IF_FROM_AFFILIATED_ORGANIZATION);
                predicates.add(securityPredicateGenerator.primaryCommunitiesOfOrganizations(criteriaBuilder, criteriaQuery,
                        lazyClientJoin.get().get(Client_.communityId), employees)
                );
            }

            if (permissionFilter.hasPermission(Permission.APPOINTMENT_VIEW_LIST_IF_FROM_AFFILIATED_COMMUNITY)) {
                var employees = permissionFilter.getEmployees(Permission.APPOINTMENT_VIEW_LIST_IF_FROM_AFFILIATED_COMMUNITY);
                predicates.add(securityPredicateGenerator.primaryCommunities(criteriaBuilder, criteriaQuery,
                        lazyClientJoin.get().get(Client_.communityId), employees)
                );
            }

            if (permissionFilter.hasPermission(Permission.APPOINTMENT_VIEW_LIST_IF_CO_RP_COMMUNITY_CTM)) {
                var employees = permissionFilter.getEmployees(Permission.APPOINTMENT_VIEW_LIST_IF_CO_RP_COMMUNITY_CTM);
                predicates.add(securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(
                        criteriaBuilder,
                        criteriaQuery,
                        lazyClientJoin.get().get(Client_.communityId),
                        employees,
                        AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                        HieConsentCareTeamType.currentAndOnHold()
                ));
            }

            if (permissionFilter.hasPermission(Permission.APPOINTMENT_VIEW_LIST_IF_CO_RP_CLIENT_CTM)) {
                var employees = permissionFilter.getEmployees(Permission.APPOINTMENT_VIEW_LIST_IF_CO_RP_CLIENT_CTM);
                predicates.add(securityPredicateGenerator.clientsInClientCareTeamPredicate(
                        criteriaBuilder,
                        criteriaQuery,
                        lazyClientJoin.get(),
                        employees,
                        AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                        HieConsentCareTeamType.currentAndOnHold()
                ));
            }

            if (permissionFilter.hasPermission(Permission.APPOINTMENT_VIEW_LIST_IF_SELF_CLIENT_RECORD)) {
                var employees = permissionFilter.getEmployees(Permission.APPOINTMENT_VIEW_LIST_IF_SELF_CLIENT_RECORD);
                predicates.add(securityPredicateGenerator.selfRecordClients(criteriaBuilder, lazyClientJoin.get().get(Client_.id), employees));
            }

            if (permissionFilter.hasPermission(Permission.APPOINTMENT_VIEW_LIST_IF_ADDED_BY_SELF)) {
                var employees = permissionFilter.getEmployees(Permission.APPOINTMENT_VIEW_LIST_IF_ADDED_BY_SELF);
                var employeeIds = CareCoordinationUtils.toIdsSet(employees);
                predicates.add(root.get(ClientAppointment_.creatorId).in(employeeIds));
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<ClientAppointment> hasPrivateAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var privateAccessPredicates = new ArrayList<Predicate>();
            var lazyClientJoin = Lazy.of(() -> root.join(ClientAppointment_.client));
            criteriaQuery.distinct(true);
            privateAccessPredicates.add(criteriaBuilder.equal(root.get(ClientAppointment_.isPublic), true));
            privateAccessPredicates.add(root.get(ClientAppointment_.isPublic).isNull());
            var allEmployees = permissionFilter.getEmployees();
            privateAccessPredicates.add(clientAppointmentPredicateGenerator.byCreatorOrServiceProviderIds(CareCoordinationUtils.toIdsSet(allEmployees), root, criteriaBuilder));
            privateAccessPredicates.add(securityPredicateGenerator.selfRecordClients(criteriaBuilder, lazyClientJoin.get().get(Client_.id), allEmployees));
            return criteriaBuilder.or(privateAccessPredicates.toArray(new Predicate[0]));
        };
    }

    public Specification<ClientAppointment> overlapsPeriod(Long dateFrom, Long dateTo) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            var dateFromInstant = DateTimeUtils.toInstant(dateFrom);
            var dateToInstant = DateTimeUtils.toInstant(dateTo);
            predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get(ClientAppointment_.dateFrom), dateFromInstant),
                    criteriaBuilder.greaterThan(root.get(ClientAppointment_.dateTo), dateFromInstant)));
            predicates.add(criteriaBuilder.and(criteriaBuilder.lessThan(root.get(ClientAppointment_.dateFrom), dateToInstant),
                    criteriaBuilder.greaterThanOrEqualTo(root.get(ClientAppointment_.dateTo), dateToInstant)));
            predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get(ClientAppointment_.dateFrom), dateFromInstant),
                    criteriaBuilder.lessThanOrEqualTo(root.get(ClientAppointment_.dateTo), dateToInstant)));
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<ClientAppointment> byStatusIn(Collection<ClientAppointmentStatus> appointmentStatuses) {
        return (root, criteriaQuery, criteriaBuilder) -> root.get(ClientAppointment_.status).in(appointmentStatuses);
    }

    public Specification<ClientAppointment> byClient(Long clientId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(ClientAppointment_.clientId), clientId);
    }

    public Specification<ClientAppointment> byCreatorOrServiceProvidersIds(Collection<Long> employeeIds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);
            return clientAppointmentPredicateGenerator.byCreatorOrServiceProviderIds(employeeIds, root, criteriaBuilder);
        };
    }

    public Specification<ClientAppointment> byIdNot(Long appointmentId) {
        return (root, criteriaQuery, criteriaBuilder) -> appointmentId != null
                ? criteriaBuilder.notEqual(root.get(ClientAppointment_.id), appointmentId)
                : criteriaBuilder.and();
    }

    public Specification<ClientAppointment> byOrganizationId(Long organizationId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var clientJoin = JpaUtils.getOrCreateJoin(root, ClientAppointment_.client);
            return criteriaBuilder.equal(clientJoin.get(Client_.organizationId), organizationId);
        };
    }

    @Override
    protected Class<ClientAppointment> getEntityClass() {
        return ClientAppointment.class;
    }

    public Specification<ClientAppointment> dateFrom(Instant dateFrom) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get(ClientAppointment_.dateFrom), dateFrom));
    }

    public Specification<ClientAppointment> withExternalServiceProvider() {
        return (root, criteriaQuery, criteriaBuilder) -> clientAppointmentPredicateGenerator.withExternalServiceProvider(root, criteriaBuilder);
    }

    public Specification<ClientAppointment> withNoServiceProviders() {
        return (root, criteriaQuery, criteriaBuilder) -> clientAppointmentPredicateGenerator.withNoServiceProviders(root);
    }
}
