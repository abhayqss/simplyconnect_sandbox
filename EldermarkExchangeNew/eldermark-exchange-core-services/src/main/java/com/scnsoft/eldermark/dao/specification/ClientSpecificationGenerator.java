package com.scnsoft.eldermark.dao.specification;

import com.google.common.collect.ImmutableMap;
import com.scnsoft.eldermark.beans.ClientAccessType;
import com.scnsoft.eldermark.beans.ClientFilter;
import com.scnsoft.eldermark.beans.ClientRecordSearchFilter;
import com.scnsoft.eldermark.beans.ClientStatus;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.OrganizationPredicateGenerator;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult_;
import com.scnsoft.eldermark.entity.client.*;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment_;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.document.CcdCode_;
import com.scnsoft.eldermark.entity.document.facesheet.AdmittanceHistory_;
import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.entity.note.Note_;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Component
public class ClientSpecificationGenerator {

    private static final Map<ClientStatus, Boolean> RECORD_STATUS_MAPPING =
            ImmutableMap.of(
                    ClientStatus.ACTIVE, true,
                    ClientStatus.INACTIVE, false
            );


    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    @Autowired
    private OrganizationPredicateGenerator organizationPredicateGenerator;

    @Autowired
    private ClientAppointmentSpecificationGenerator clientAppointmentSpecificationGenerator;

    public Specification<Client> byFilter(ClientFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getOrganizationId() != null)
                predicates.add(criteriaBuilder.equal(root.get(Client_.organizationId), filter.getOrganizationId()));

            if (CollectionUtils.isNotEmpty(filter.getCommunityIds())) {
                predicates.add(root.get(Client_.communityId).in(filter.getCommunityIds()));
            }

            if (StringUtils.isNoneBlank(filter.getSsnLast4())) {
                predicates.add(criteriaBuilder.like(
                        root.get(Client_.ssnLastFourDigits),
                        SpecificationUtils.wrapWithWildcards(filter.getSsnLast4())
                ));
            }

            if (StringUtils.isNoneBlank(filter.getSsn())) {
                predicates.add(criteriaBuilder.like(
                        root.get(Client_.socialSecurity),
                        SpecificationUtils.wrapWithWildcards(filter.getSsn())
                ));
            }

            if (StringUtils.isNoneBlank(filter.getMedicaidNumber())) {
                predicates.add(criteriaBuilder.like(
                        root.get(Client_.medicaidNumber),
                        SpecificationUtils.wrapWithWildcards(filter.getMedicaidNumber())
                ));
            }

            if (StringUtils.isNoneBlank(filter.getMedicareNumber())) {
                predicates.add(criteriaBuilder.like(
                        root.get(Client_.medicareNumber),
                        SpecificationUtils.wrapWithWildcards(filter.getMedicareNumber())
                ));
            }

            if (filter.getGenderId() != null) {
                predicates.add(criteriaBuilder.equal(
                        JpaUtils.getOrCreateJoin(root, Client_.gender).get(CcdCode_.id),
                        filter.getGenderId()
                ));
            }

            if (StringUtils.isNotEmpty(filter.getFirstName())) {
                predicates.add(criteriaBuilder.like(
                        root.get(Client_.firstName),
                        SpecificationUtils.wrapWithWildcards(filter.getFirstName())
                ));
            }

            if (StringUtils.isNotEmpty(filter.getLastName())) {
                predicates.add(criteriaBuilder.like(
                        root.get(Client_.lastName),
                        SpecificationUtils.wrapWithWildcards(filter.getLastName())
                ));
            }

            if (StringUtils.isNotBlank(filter.getBirthDate())) {
                predicates.add(criteriaBuilder.equal(
                        root.get(Client_.birthDate),
                        DateTimeUtils.parseDateToLocalDate(filter.getBirthDate())
                ));
            }

            if (RECORD_STATUS_MAPPING.containsKey(filter.getRecordStatus())) {
                predicates.add(criteriaBuilder.equal(
                        root.get(Client_.active),
                        RECORD_STATUS_MAPPING.get(filter.getRecordStatus())
                ));
            }

            if (BooleanUtils.isTrue(filter.getIsAdmitted())) {
                predicates.add(root.get(Client_.id).in(
                        admittedClientIdsSubquery(criteriaQuery, criteriaBuilder))
                );
            }

            if (StringUtils.isNotEmpty(filter.getPrimaryCarePhysician())) {
                predicates.add(generatePrimaryCarePhysicianPredicate(
                        root,
                        criteriaQuery,
                        criteriaBuilder,
                        filter.getPrimaryCarePhysician()
                ));
            }

            if (StringUtils.isNotEmpty(filter.getInsuranceNetworkAggregatedName())) {
                var clientHealthPlanSubquery = existClientHealthPlanWithName(
                        root.get(Client_.id),
                        criteriaQuery,
                        criteriaBuilder,
                        filter.getInsuranceNetworkAggregatedName()
                );

                var joinNetworkInsurance = JpaUtils.getOrCreateJoin(root, Client_.inNetworkInsurance, JoinType.LEFT);

                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(
                                joinNetworkInsurance.get(InNetworkInsurance_.displayName),
                                filter.getInsuranceNetworkAggregatedName()
                        ),
                        criteriaBuilder.exists(clientHealthPlanSubquery)
                ));
            }

            if (CollectionUtils.isNotEmpty(filter.getPharmacyNames()) || BooleanUtils.isTrue(filter.getHasNoPharmacies())) {
                var subQuery = criteriaQuery.subquery(Long.class);
                var subRoot = subQuery.from(ClientPharmacyFilterView.class);
                var pharmacyNamesParam = new ArrayList<String>();
                if (CollectionUtils.isNotEmpty(filter.getPharmacyNames())) {
                    pharmacyNamesParam.addAll(filter.getPharmacyNames());
                }
                if (BooleanUtils.isTrue(filter.getHasNoPharmacies())) {
                    pharmacyNamesParam.add(ClientPharmacyFilterView.NO_PHARMACY);
                }
                subQuery = subQuery
                        .select(subRoot.get(ClientPharmacyFilterView_.clientId))
                        .where(subRoot.get(ClientPharmacyFilterView_.PHARMACY_NAME).in(pharmacyNamesParam));

                predicates.add(root.get(Client_.id).in(subQuery));
            }

            if (StringUtils.isNotBlank(filter.getUnit())) {
                predicates.add(criteriaBuilder.like(root.get(Client_.unitNumber), SpecificationUtils.wrapWithWildcards(filter.getUnit())));
            }

            if (StringUtils.isNotBlank(filter.getSearchText())) {
                predicates.add(
                        SpecificationUtils.byNameLike(
                                root.get(Client_.firstName),
                                root.get(Client_.middleName),
                                root.get(Client_.lastName),
                                filter.getSearchText(),
                                criteriaBuilder
                        )
                );
            }

            if (BooleanUtils.isTrue(filter.getWithAccessibleAppointments())) {
                var subquery = criteriaQuery.subquery(Long.class);
                var subqueryRoot = subquery.from(ClientAppointment.class);
                subquery.select(subqueryRoot.get(ClientAppointment_.id));
                subquery.where(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(root.get(Client_.id), subqueryRoot.get(ClientAppointment_.clientId)),
                                clientAppointmentSpecificationGenerator.hasAccessAndPrivateAccess(filter.getPermissionFilter())
                                        .toPredicate(subqueryRoot, criteriaQuery, criteriaBuilder),
                                clientAppointmentSpecificationGenerator.isUnarchived()
                                        .toPredicate(subqueryRoot, criteriaQuery, criteriaBuilder))
                );
                predicates.add(criteriaBuilder.exists(subquery));
            }

            if (filter.getHieConsentPolicyName() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get(Client_.hieConsentPolicyType),
                        filter.getHieConsentPolicyName()
                ));
            }

            if (filter.getClientAccessType() != null && filter.getPermissionFilter() != null) {
                var permissionPredicate =
                        filter.getClientAccessType() == ClientAccessType.IN_LIST
                                ? clientPredicateGenerator.hasAccessInList(filter.getPermissionFilter(), root, criteriaQuery, criteriaBuilder)
                                : clientPredicateGenerator.hasDetailsAccess(filter.getPermissionFilter(), root, criteriaQuery, criteriaBuilder);

                predicates.add(permissionPredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Predicate generatePrimaryCarePhysicianPredicate(Path<Client> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder, String primaryCarePhysicianName) {
        var names = Arrays.stream(primaryCarePhysicianName.split(" "))
                .map(SpecificationUtils::wrapWithWildcards);

        var subQuery = criteriaQuery.subquery(Integer.class);
        var assessmentRoot = subQuery.from(ClientComprehensiveAssessment.class);

        var likeInAssessmentPredicates = new ArrayList<Predicate>();
        var likeInClientPredicates = new ArrayList<Predicate>();

        names.forEach(name -> {
            likeInAssessmentPredicates.add(
                    criteriaBuilder.or(
                            criteriaBuilder.like(
                                    assessmentRoot.get(ClientComprehensiveAssessment_.primaryCarePhysicianFirstName),
                                    name
                            ),
                            criteriaBuilder.like(
                                    assessmentRoot.get(ClientComprehensiveAssessment_.primaryCarePhysicianLastName),
                                    name
                            )
                    )
            );
            likeInClientPredicates.add(
                    criteriaBuilder.or(
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(root.get(Client_.primaryCarePhysicianFirstName)),
                                    name
                            ),
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(root.get(Client_.primaryCarePhysicianLastName)),
                                    name
                            )
                    )
            );
        });

        var assessmentSubQueryResult = subQuery.select(criteriaBuilder.literal(1))
                .where(criteriaBuilder.and(
                                criteriaBuilder.equal(assessmentRoot.get(ClientComprehensiveAssessment_.clientId), root.get(Client_.id)),
                                criteriaBuilder.or(likeInAssessmentPredicates.toArray(new Predicate[0])),
                                criteriaBuilder.equal(assessmentRoot.get(ClientComprehensiveAssessment_.clientAssessmentResult)
                                        .get(ClientAssessmentResult_.archived), false)
                        )
                );

        likeInClientPredicates.add(criteriaBuilder.exists(assessmentSubQueryResult));
        return criteriaBuilder.or(likeInClientPredicates.toArray(new Predicate[0]));
    }

    private Subquery<Long> admittedClientIdsSubquery(CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        var subQuery = criteriaQuery.subquery(Long.class);
        var subClient = subQuery.from(Client.class);
        var subAdmittanceHistory = subClient.join(Client_.admittanceHistories, JoinType.LEFT);

        var maxClientAdmit = criteriaBuilder.greatest(subClient.get(Client_.admitDate));
        var maxClientDischarge = criteriaBuilder.greatest(subClient.get(Client_.dischargeDate));
        var maxHistoryAdmit = criteriaBuilder.greatest(subAdmittanceHistory.get(AdmittanceHistory_.admitDate));
        var maxHistoryDischarge = criteriaBuilder.greatest(subAdmittanceHistory.get(AdmittanceHistory_.dischargeDate));

        var dischargeNotPresent = criteriaBuilder.and(
                criteriaBuilder.isNull(maxClientDischarge),
                criteriaBuilder.isNull(maxHistoryDischarge)
        );

        var hasAdmit = criteriaBuilder.or(
                criteriaBuilder.isNotNull(maxClientAdmit),
                criteriaBuilder.isNotNull(maxHistoryAdmit)
        );

        var admitAfterDischarge = criteriaBuilder.greaterThan(
                //both dates are not null
                SpecificationUtils.greatest(criteriaBuilder, maxClientAdmit, maxHistoryAdmit),
                SpecificationUtils.greatest(criteriaBuilder, maxClientDischarge, maxHistoryDischarge)
        );

        var isActiveClient = criteriaBuilder.isTrue(subClient.get(Client_.active));
        var admittedClients = subQuery
                .select(subClient.get(Client_.id))
                .groupBy(subClient.get(Client_.id))
                .where(isActiveClient)
                .having(criteriaBuilder.or(dischargeNotPresent, criteriaBuilder.and(hasAdmit, admitAfterDischarge)));

        return admittedClients;
    }

    public Subquery<Integer> existClientHealthPlanWithName(Path<Long> clientId, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
                                                           String insuranceNetworkAggregatedName) {
        var planSubQuery = criteriaQuery.subquery(Integer.class);
        var planRoot = planSubQuery.from(ClientHealthPlan.class);
        return planSubQuery.select(criteriaBuilder.literal(1))
                .where(criteriaBuilder.and(
                                criteriaBuilder.equal(planRoot.get(ClientHealthPlan_.healthPlanName), insuranceNetworkAggregatedName),
                                criteriaBuilder.equal(planRoot.get(ClientHealthPlan_.client).get(Client_.id), clientId)
                        )
                );
    }


    public Specification<Client> hasDetailsAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> clientPredicateGenerator.hasDetailsAccess(permissionFilter, root, criteriaQuery, criteriaBuilder);
    }

    public Specification<Client> byIds(Collection<Long> ids) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (CollectionUtils.isNotEmpty(ids)) {
                return criteriaBuilder.in(root.get(Client_.ID)).value(CollectionUtils.emptyIfNull(ids));
            }
            return criteriaBuilder.or();
        };
    }

    public Specification<Client> mergedClients(Long clientId) {
        return mergedClients(Collections.singletonList(clientId));
    }

    public Specification<Client> mergedClients(Collection<Long> clientIds) {
        return (root, criteriaQuery, criteriaBuilder) -> clientPredicateGenerator.clientAndMergedClients(criteriaBuilder, root,
                criteriaQuery, clientIds);
    }

    public Specification<Client> byCommunityId(Long communityId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Client_.community).get(Community_.ID), communityId);
    }

    public Specification<Client> byCommunity(Community community) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Client_.community), community);
    }

    public <T extends IdAware> Specification<Client> byCommunities(Collection<T> communities) {
        return (root, criteriaQuery, criteriaBuilder) ->
                clientPredicateGenerator.byCommunities(communities, root, criteriaQuery, criteriaBuilder);
    }

    public Specification<Client> byCommunityIds(Collection<Long> communityIds) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(root.get(Client_.COMMUNITY_ID)).value(communityIds);
    }

    public Specification<Client> byCreators(Collection<Employee> employees) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.in(root.get(Client_.CREATED_BY)).value(employees);
    }

    public Specification<Client> isActive() {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Client_.active), true);
    }

    public Specification<Client> associatedEmployeeIsNull() {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get(Client_.associatedEmployee));
    }

    public Specification<Client> hasActiveAssociatedEmployee() {
        return (root, criteriaQuery, criteriaBuilder) -> clientPredicateGenerator.hasActiveAssociatedEmployee(criteriaBuilder, root);
    }

    public Specification<Client> lastUpdatedBeforeOrEqual(Instant date) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get(Client_.lastUpdated), date);
    }

    public Specification<Client> lastUpdatedAfterOrEqual(Instant date) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(Client_.lastUpdated), date);
    }

    public Specification<Client> createdBeforeOrWithoutDateCreated(Instant instant) {
        //all the clients without date_created was actually created before 2017-07-11 09:00:57.0500000
        //(very very probable), but we can't say for sure when exactly
        return (root, criteriaQuery, criteriaBuilder) ->
                clientPredicateGenerator.createdBeforeOrWithoutDateCreated(instant, root, criteriaQuery, criteriaBuilder);
    }

    public Specification<Client> ofNote(Long noteId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var subQuery = criteriaQuery.subquery(Long.class);

            var from = subQuery.from(Note.class);

            var noteClients = subQuery.select(from.join(Note_.noteClients).get(Client_.id))
                    .where(criteriaBuilder.equal(from.get(Note_.id), noteId));

            return root.in(noteClients);
        };
    }

    public Specification<Client> byOrganizationId(Long organizationId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Client_.organizationId), organizationId);
    }

    public Specification<Client> byOrganizationIds(Collection<Long> organizationIds) {
        return (root, criteriaQuery, criteriaBuilder) -> SpecificationUtils.in(criteriaBuilder, root.get(Client_.organizationId), organizationIds);
    }

    public Specification<Client> byId(Long id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Client_.id), id);
    }

    public <T extends IdNameAware> Specification<Client> accessibleClientsInCommunitiesCreatedBeforeOrWithoutDateCreatedActiveUntilDate
            (PermissionFilter permissionFilter, Collection<T> communities, Instant createdDate, Instant activeDate) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(clientPredicateGenerator.hasDetailsAccess(permissionFilter, root, criteriaQuery, criteriaBuilder),
                        clientPredicateGenerator.byCommunities(communities, root, criteriaQuery, criteriaBuilder),
                        clientPredicateGenerator.createdBeforeOrWithoutDateCreated(createdDate, root, criteriaQuery, criteriaBuilder),
                        //we currently don't store history so just excluding inactive with last_updated before report start date
                        criteriaBuilder.or(clientPredicateGenerator.isActive(root, criteriaBuilder),
                                clientPredicateGenerator.lastUpdatedAfterOrEqual(activeDate, root, criteriaQuery, criteriaBuilder)));
    }

    public Specification<Client> byIdentityFields(Long communityId, String ssn, LocalDate dateOfBirth, String lastName, String firstName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get(Client_.communityId), communityId),
                criteriaBuilder.equal(root.get(Client_.socialSecurity), ssn),
                criteriaBuilder.equal(root.get(Client_.birthDate), dateOfBirth),
                criteriaBuilder.equal(root.get(Client_.lastName), lastName),
                criteriaBuilder.equal(root.get(Client_.firstName), firstName)
        );
    }

    public Specification<Client> withEnabledChat() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var orgJoin = JpaUtils.getOrCreateJoin(root, Client_.organization);
            return organizationPredicateGenerator.withEnabledChat(criteriaBuilder, orgJoin, true);
        };
    }

    public Specification<Client> chatAccessibleClients(PermissionFilter permissionFilter, Long excludedEmployeeId) {
        return (root, query, criteriaBuilder) -> clientPredicateGenerator.chatAccessibleClients(permissionFilter,
                excludedEmployeeId, root, query, criteriaBuilder);
    }

    public Specification<Client> byLegacyId(String legacyId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Client_.legacyId), legacyId);
    }

    public Specification<Client> byLoginCompanyId(String loginCompanyId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var organization = JpaUtils.getOrCreateJoin(root, Client_.organization);
            var systemSetup = JpaUtils.getOrCreateJoin(organization, Organization_.systemSetup);
            return criteriaBuilder.equal(systemSetup.get(SystemSetup_.loginCompanyId), loginCompanyId);
        };
    }

    public Specification<Client> excludeAssociatedParticipatingInOneToOneChatWithAny(Collection<Long> employeeIds) {
        return (root, criteriaQuery, criteriaBuilder) -> clientPredicateGenerator
                .excludeAssociatedParticipatingInOneToOneChatWithAny(employeeIds, root, criteriaQuery, criteriaBuilder);
    }

    public Specification<Client> recordsSearch(ClientRecordSearchFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.isFalse(JpaUtils.getOrCreateJoin(root, Client_.organization)
                    .get(Organization_.excludeFromRecordSearch)));

            predicates.add(criteriaBuilder.equal(root.get(Client_.ssnLastFourDigits), filter.getSsnLast4()));
            predicates.add(criteriaBuilder.equal(root.join(Client_.gender).get(CcdCode_.id), filter.getGenderId()));
            predicates.add(criteriaBuilder.equal(root.get(Client_.firstName), filter.getFirstName()));
            predicates.add(criteriaBuilder.equal(root.get(Client_.lastName), filter.getLastName()));
            predicates.add(criteriaBuilder.equal(root.get(Client_.birthDate), DateTimeUtils.parseDateToLocalDate(filter.getBirthDate())));

            if (StringUtils.isNotBlank(filter.getMiddleName())) {
                predicates.add(criteriaBuilder.equal(root.get(Client_.middleName), filter.getMiddleName()));
            }

            if (StringUtils.isNotBlank(filter.getStreet()) || StringUtils.isNotBlank(filter.getCity()) ||
                    StringUtils.isNotBlank(filter.getZip()) || filter.getStateId() != null) {
                criteriaQuery.distinct(true);

                Join<Object, Object> addresses = root.join(Client_.person).join(Person_.ADDRESSES);

                if (StringUtils.isNotBlank(filter.getStreet())) {
                    predicates.add(
                            criteriaBuilder.equal(
                                    SpecificationUtils.deleteMultipleSpaces(addresses.get(PersonAddress_.STREET_ADDRESS), criteriaBuilder),
                                    CareCoordinationUtils.deleteMultipleSpaces(filter.getStreet())
                            )
                    );
                }

                if (StringUtils.isNotBlank(filter.getCity())) {
                    predicates.add(criteriaBuilder.equal(addresses.get(PersonAddress_.CITY), filter.getCity()));
                }

                if (StringUtils.isNotBlank(filter.getZip())) {
                    predicates.add(criteriaBuilder.equal(addresses.get(PersonAddress_.POSTAL_CODE), filter.getZip()));
                }

                if (filter.getStateId() != null) {
                    var stateQuery = criteriaQuery.subquery(String.class);
                    var stateRoot = stateQuery.from(State.class);
                    var stateResult = stateQuery.select(stateRoot.get(State_.abbr)).where(criteriaBuilder.equal(stateRoot.get(State_.id), filter.getStateId()));
                    predicates.add(criteriaBuilder.equal(addresses.get(PersonAddress_.STATE), stateResult));
                }
            }


            if (StringUtils.isNotBlank(filter.getPhone())) {
                Join<Object, Object> telecoms = root.join(Client_.person).join(Person_.TELECOMS);
                criteriaQuery.distinct(true);
                predicates.add(criteriaBuilder.equal(telecoms.get(PersonTelecom_.NORMALIZED), CareCoordinationUtils.normalizePhone(filter.getPhone())));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<Client> hasRecordSearchAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var eligible = clientPredicateGenerator.clientInEligibleForDiscoveryCommunity(
                    root,
                    criteriaBuilder
            );
            if (permissionFilter.hasPermission(Permission.ROLE_SUPER_ADMINISTRATOR)) {
                return eligible;
            }
            var predicates = new ArrayList<Predicate>();
            if (permissionFilter.hasPermission(Permission.CLIENT_RECORD_SEARCH_IF_ASSOCIATED_ORGANIZATION)) {
                var employees = permissionFilter.getEmployees(Permission.CLIENT_RECORD_SEARCH_IF_ASSOCIATED_ORGANIZATION);
                predicates.add(criteriaBuilder.in(root.get(Client_.ORGANIZATION_ID))
                        .value(SpecificationUtils.employeesOrganizationIds(employees)));
            }
            return criteriaBuilder.and(
                    eligible,
                    criteriaBuilder.or(predicates.toArray(new Predicate[0]))
            );
        };
    }

    public Specification<Client> clientsInEligibleForDiscoveryCommunity() {
        return (root, criteriaQuery, criteriaBuilder) ->
                clientPredicateGenerator.clientInEligibleForDiscoveryCommunity(root, criteriaBuilder);
    }

    public Specification<Client> byCompanyAlternativeId(String companyAlternativeId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var organization = JpaUtils.getOrCreateJoin(root, Client_.organization);
            return criteriaBuilder.equal(organization.get(Organization_.alternativeId), companyAlternativeId);
        };
    }

    public Specification<Client> hasPermissionToRequestSignatureFrom(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) ->
                clientPredicateGenerator.hasPermissionToRequestSignatureFrom(permissionFilter, root, criteriaQuery, criteriaBuilder);
    }

    public Specification<Client> byPrimaryContactEmployeeId(Long employeeId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var primaryContact = JpaUtils.getOrCreateJoin(root, Client_.primaryContact);
            var clientCtm = JpaUtils.getOrCreateJoin(primaryContact, ClientPrimaryContact_.clientCareTeamMember);
            return criteriaBuilder.equal(clientCtm.get(ClientCareTeamMember_.employeeId), employeeId);
        };
    }

    public Specification<Client> byAssociatedEmployeeIdIn(Collection<Long> employeeIds) {
        return (root, criteriaQuery, criteriaBuilder) -> SpecificationUtils.in(
                criteriaBuilder,
                root.get(Client_.associatedEmployee).get(Employee_.id),
                employeeIds
        );
    }

    public Specification<Client> isOptOutPolicy() {
        return (root, criteriaQuery, criteriaBuilder) -> clientPredicateGenerator.isOptedOut(root, criteriaBuilder);
    }

    public Specification<Client> isOptedIn() {
        return (root, criteriaQuery, criteriaBuilder) -> clientPredicateGenerator.isOptedIn(root, criteriaBuilder);
    }

    public Specification<Client> isInactiveAndDeactivationDateBefore(Instant date) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get(Client_.active), false),
                criteriaBuilder.lessThanOrEqualTo(root.get(Client_.deactivationDate), date)
        );
    }

    public Specification<Client> isActiveInPeriod(Instant fromDate, Instant toDate) {
        return (root, criteriaQuery, criteriaBuilder) ->
                clientPredicateGenerator.isActiveInPeriod(
                        fromDate, toDate, root, criteriaQuery, criteriaBuilder
                );
    }

    public Specification<Client> byHieConsentPolicyUpdatedByEmployeeId(Long employeeId) {
        return (root, query, criteriaBuilder) -> employeeId == null
                ? criteriaBuilder.isNull(root.get(Client_.hieConsentPolicyUpdatedByEmployeeId))
                : criteriaBuilder.equal(root.get(Client_.hieConsentPolicyUpdatedByEmployeeId), employeeId);
    }
}
