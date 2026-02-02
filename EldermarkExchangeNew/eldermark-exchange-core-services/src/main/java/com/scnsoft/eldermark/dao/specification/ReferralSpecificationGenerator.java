package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.ReferralFilter;
import com.scnsoft.eldermark.beans.ReferralPriority;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.CommunityCareTeamMemberPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.ReferralPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.SecurityPredicateGenerator;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole_;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.entity.referral.*;
import com.scnsoft.eldermark.entity.security.CareTeamRolePermissionMapping;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Component
public class ReferralSpecificationGenerator {

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    @Autowired
    private CommunityCareTeamMemberPredicateGenerator communityCareTeamMemberPredicateGenerator;

    @Autowired
    private ReferralPredicateGenerator referralPredicateGenerator;

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    public Specification<ReferralInfoRequest> inboundInfoRequestsByRequestId(Long requestId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .equal(root.get(ReferralInfoRequest_.referralRequestId), requestId);
    }

    public Specification<ReferralInfoRequest> outboundInfoRequestsByReferralId(Long referralId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(
                JpaUtils.getOrCreateJoin(root, ReferralInfoRequest_.referralRequest).get(ReferralRequest_.referralId),
                referralId
        );
    }

    public Specification<Referral> byFilter(ReferralFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getClientId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Referral_.clientId), filter.getClientId()));
            }

            if (CollectionUtils.isNotEmpty(filter.getServiceIds())) {
                predicates.add(root.in(referralSubquery(criteriaQuery,
                        subFrom -> subFrom.join(Referral_.services).get(ServicesTreatmentApproach_.id).in(filter.getServiceIds()))));
            }

            if (CollectionUtils.isNotEmpty(filter.getPriorityIds())) {
                //sorry for this
                if (filter.getPriorityIds().contains(ReferralPriority.OTHER.getId())) {
                    filter.getPriorityIds().add(ReferralPriority.ASAP.getId());
                    filter.getPriorityIds().add(ReferralPriority.STAT.getId());
                    filter.getPriorityIds().remove(ReferralPriority.OTHER.getId());
                }
                predicates.add(root.join(Referral_.priority).get(ReferralPriority_.id).in(filter.getPriorityIds()));
            }

            if (CollectionUtils.isNotEmpty(filter.getStatuses())) {
                predicates.add(criteriaBuilder.in(root.get(Referral_.REFERRAL_STATUS)).value(filter.getStatuses()));
            }
            if (CollectionUtils.isNotEmpty(filter.getReferredTo())) {
                predicates.add(root.in(referralSubquery(criteriaQuery,
                        subFrom -> subFrom.join(Referral_.referralRequests).get(ReferralRequest_.communityId).in(filter.getReferredTo()))));
            }

            if (filter.getAssignedTo() != null) {
                predicates.add(root.in(referralSubquery(criteriaQuery,
                        subRoot -> criteriaBuilder.equal(
                                SpecificationUtils.employeeFullName(subRoot.join(Referral_.referralRequests)
                                        .join(ReferralRequest_.assignedEmployee), criteriaBuilder),
                                filter.getAssignedTo()
                        ))));
            }

            if (CollectionUtils.isNotEmpty(filter.getCommunityIds())) {
                predicates.add(root.get(Referral_.requestingCommunityId).in(filter.getCommunityIds()));
            }

            if (filter.getOrganizationId() != null) {
                var requestingCommunityJoin = JpaUtils.getOrCreateJoin(root, Referral_.requestingCommunity);
                var requestingCommunityOrganizationId = requestingCommunityJoin.get(Community_.organizationId);
                predicates.add(criteriaBuilder.equal(requestingCommunityOrganizationId, filter.getOrganizationId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    public Specification<ReferralRequest> requestsOfReferral(Long referralId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(ReferralRequest_.referralId), referralId);
    }

    public Specification<ReferralRequest> requestByFilter(ReferralFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(filter.getCommunityIds())) {
                predicates.add(root.get(ReferralRequest_.communityId).in(filter.getCommunityIds()));
            } else {
                predicates.add(criteriaBuilder.equal(
                        JpaUtils.getOrCreateJoin(root, ReferralRequest_.community).get(Community_.organizationId),
                        filter.getOrganizationId()
                ));
            }

            var referralJoin = JpaUtils.getOrCreateJoin(root, ReferralRequest_.referral);

            if (CollectionUtils.isNotEmpty(filter.getServiceIds())) {
                predicates.add(root.in(referralRequestSubquery(query,
                        subFrom -> subFrom
                                .join(ReferralRequest_.referral)
                                .join(Referral_.services)
                                .get(ServicesTreatmentApproach_.id)
                                .in(filter.getServiceIds()))));
            }

            if (CollectionUtils.isNotEmpty(filter.getPriorityIds())) {
                //sorry for this
                if (filter.getPriorityIds().contains(ReferralPriority.OTHER.getId())) {
                    filter.getPriorityIds().add(ReferralPriority.ASAP.getId());
                    filter.getPriorityIds().add(ReferralPriority.STAT.getId());
                    filter.getPriorityIds().remove(ReferralPriority.OTHER.getId());
                }
                predicates.add(referralJoin.join(Referral_.priority)
                        .get(ReferralPriority_.id)
                        .in(filter.getPriorityIds()));
            }

            var statuses = filter.getStatuses();

            if (CollectionUtils.isNotEmpty(statuses)) {
                var statusPredicates = new ArrayList<Predicate>();
                var splitted = statuses.stream().collect(Collectors.partitioningBy(ReferralStatus.PENDING::equals));
                if (CollectionUtils.isNotEmpty(splitted.getOrDefault(Boolean.TRUE, Collections.emptyList()))) {
                    statusPredicates.add(
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(referralJoin.get(Referral_.referralStatus), ReferralStatus.PENDING),
                                    criteriaBuilder.isNull(root.get(ReferralRequest_.lastResponse))
                            )
                    );
                }

                if (CollectionUtils.isNotEmpty(splitted.getOrDefault(Boolean.FALSE, Collections.emptyList()))) {
                    var statusesFromResponse = statuses.stream()
                            .filter(referralStatus -> !referralStatus.equals(ReferralStatus.PENDING))
                            .map(ReferralResponse::fromReferralStatus)
                            .collect(Collectors.toList());

                    statusPredicates.add(
                            criteriaBuilder.in(JpaUtils.getOrCreateJoin(root, ReferralRequest_.lastResponse, JoinType.LEFT)
                                    .get(ReferralRequestResponse_.RESPONSE)).value(statusesFromResponse)
                    );
                }
                predicates.add(
                        criteriaBuilder.or(statusPredicates.toArray(new Predicate[0])));
            }


            if (CollectionUtils.isNotEmpty(filter.getReferredBy())) {
                predicates.add(referralJoin.get(Referral_.requestingCommunityId).in(filter.getReferredBy()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Subquery<Referral> referralSubquery(CriteriaQuery criteriaQuery, Function<Root<Referral>, Predicate> restriction) {
        return SpecificationUtils.subquery(Referral.class, criteriaQuery, restriction);
    }

    private Subquery<ReferralRequest> referralRequestSubquery(CriteriaQuery criteriaQuery, Function<Root<ReferralRequest>, Predicate> restriction) {
        return SpecificationUtils.subquery(ReferralRequest.class, criteriaQuery, restriction);
    }

    public Specification<ReferralRequest> isInboundRequestVisibleByStatus() {
        return (root, criteriaQuery, criteriaBuilder) -> isInboundVisibleByStatus(root, criteriaBuilder);
    }

    public Specification<ReferralInfoRequest> isInboundInfoRequestVisibleByStatus() {
        return (root, criteriaQuery, criteriaBuilder) -> isInboundVisibleByStatus(root.join(ReferralInfoRequest_.referralRequest),
                criteriaBuilder);
    }

    private Predicate isInboundVisibleByStatus(From<?, ReferralRequest> requestFrom, CriteriaBuilder criteriaBuilder) {
        var referral = JpaUtils.getOrCreateJoin(requestFrom, ReferralRequest_.referral);
        var referralStatus = referral.get(Referral_.referralStatus);

        var pendingReferral = criteriaBuilder.equal(referralStatus, ReferralStatus.PENDING);
        var declinedReferral = criteriaBuilder.equal(referralStatus, ReferralStatus.DECLINED);
        var preAdmitReferral = criteriaBuilder.equal(referralStatus, ReferralStatus.PRE_ADMIT);
        var acceptedReferral = criteriaBuilder.equal(referralStatus, ReferralStatus.ACCEPTED);

        var requestUpdatedByReferral = criteriaBuilder.equal(
                referral.get(Referral_.updatedByResponseId),
                requestFrom.get(ReferralRequest_.lastResponseId)
        );

        return criteriaBuilder.or(
                pendingReferral,
                declinedReferral,
                criteriaBuilder.and(
                        criteriaBuilder.or(preAdmitReferral, acceptedReferral),
                        requestUpdatedByReferral
                )
        );
    }

    public <T extends IdNameAware> Specification<Referral> byClientCommunities(List<T> communities) {
        return (root, criteriaQuery, criteriaBuilder) ->
                CollectionUtils.isEmpty(communities) ? criteriaBuilder.or() :
                        criteriaBuilder.in(JpaUtils.getOrCreateJoin(root, Referral_.client).get(Client_.COMMUNITY_ID))
                                .value(CareCoordinationUtils.toIdsSet(communities));
    }

    public Specification<Referral> betweenDates(Instant from, Instant to) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var referralModifiedDate = root.get(Referral_.modifiedDate);
            var referralHistoryModifiedDate = JpaUtils.getOrCreateListJoin(root, Referral_.referralHistories, JoinType.LEFT)
                    .get(ReferralHistory_.modifiedDate);

            var referralDate = criteriaBuilder.and(
                    from == null ? criteriaBuilder.or() : criteriaBuilder.greaterThanOrEqualTo(referralModifiedDate, from),
                    to == null ? criteriaBuilder.or() : criteriaBuilder.lessThanOrEqualTo(referralModifiedDate, to)
            );

            var referralHistoryDate = criteriaBuilder.and(
                    criteriaBuilder.greaterThan(root.get(Referral_.modifiedDate), to),
                    from == null ? criteriaBuilder.or() : criteriaBuilder.greaterThanOrEqualTo(referralHistoryModifiedDate, from),
                    to == null ? criteriaBuilder.or() : criteriaBuilder.lessThanOrEqualTo(referralHistoryModifiedDate, to)
            );

            return criteriaBuilder.or(referralDate, referralHistoryDate);
        };
    }

    public <T extends IdAware> Specification<ReferralRequest> requestByCommunities(List<T> communities) {
        return (root, criteriaQuery, criteriaBuilder) -> CollectionUtils.isEmpty(communities) ? criteriaBuilder.or() :
                criteriaBuilder.in(root.get(ReferralRequest_.COMMUNITY_ID))
                        .value(CareCoordinationUtils.toIdsSet(communities));

    }

    public Specification<ReferralRequest> requestBetweenDates(Instant from, Instant to) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var requestDate = JpaUtils.getOrCreateJoin(root, ReferralRequest_.referral).get(Referral_.requestDatetime);
            var requestResponseDate = JpaUtils.getOrCreateListJoin(root, ReferralRequest_.responses, JoinType.LEFT)
                    .get(ReferralRequestResponse_.responseDatetime);

            return criteriaBuilder.or(
                    criteriaBuilder.and(
                            from == null ? criteriaBuilder.or() : criteriaBuilder.greaterThanOrEqualTo(requestDate, from),
                            to == null ? criteriaBuilder.or() : criteriaBuilder.lessThanOrEqualTo(requestDate, to)
                    ),
                    criteriaBuilder.and(
                            from == null ? criteriaBuilder.or() : criteriaBuilder.greaterThanOrEqualTo(requestResponseDate, from),
                            to == null ? criteriaBuilder.or() : criteriaBuilder.lessThanOrEqualTo(requestResponseDate, to)
                    )
            );
        };
    }

    public Specification<Referral> byClientId(Long clientId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Referral_.clientId), clientId);
    }

    public Specification<Referral> withSharedFacesheet() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isTrue(root.get(Referral_.isFacesheetShared));
    }

    public Specification<Referral> withSharedCcd() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isTrue(root.get(Referral_.isCcdShared));
    }

    public Specification<Referral> withSharedServicePlan() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isTrue(root.get(Referral_.isServicePlanShared));
    }

    public Specification<ReferralRequest> hasAccessToInboundRequests(PermissionFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> referralPredicateGenerator.hasAccessToInbound(filter, root, criteriaQuery, criteriaBuilder);
    }

    public Specification<Referral> hasAccessToInboundReferrals(PermissionFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> root.in(referralSubquery(criteriaQuery,
                subRoot -> referralPredicateGenerator.hasAccessToInbound(
                        filter,
                        JpaUtils.getOrCreateListJoin(subRoot, Referral_.referralRequests),
                        criteriaQuery,
                        criteriaBuilder)
        ));
    }

    public Specification<ReferralInfoRequest> hasAccessToInboundInfoRequests(PermissionFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> referralPredicateGenerator.hasAccessToInbound(
                filter,
                JpaUtils.getOrCreateJoin(root, ReferralInfoRequest_.referralRequest),
                criteriaQuery, criteriaBuilder);
    }

    public Specification<Referral> hasAccessToOutboundReferrals(PermissionFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> hasAccessToOutbound(filter, root, criteriaQuery, criteriaBuilder);
    }

    public Specification<ReferralInfoRequest> hasAccessToOutboundInfoRequests(PermissionFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> hasAccessToOutbound(
                filter,
                JpaUtils.getOrCreateJoin(JpaUtils.getOrCreateJoin(root, ReferralInfoRequest_.referralRequest), ReferralRequest_.referral),
                criteriaQuery, criteriaBuilder);
    }

    private Predicate hasAccessToOutbound(PermissionFilter filter, From<?, Referral> referralFrom,
                                          CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

        var clientJoin = JpaUtils.getOrCreateJoin(referralFrom, Referral_.client, JoinType.LEFT);
        var requestingCommunityJoin = JpaUtils.getOrCreateJoin(referralFrom, Referral_.requestingCommunity);
        var requestingCommunityIdPath = requestingCommunityJoin.get(Community_.id);

        var eligible = securityPredicateGenerator.eligibleForDiscoveryCommunity(requestingCommunityJoin, criteriaBuilder);

        var predicates = new ArrayList<Predicate>();
        var clientPredicates = new ArrayList<Predicate>();

        if (filter.hasPermission(CLIENT_REFERRAL_VIEW_OUTBOUND_ALL_EXCEPT_OPTED_OUT)) {
            clientPredicates.add(clientPredicateGenerator.isOptedIn(clientJoin, criteriaBuilder));
        }

        if (filter.hasPermission(CLIENT_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(CLIENT_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_ORGANIZATION);
            var employeeOrganizationIds = SpecificationUtils.employeesOrganizationIds(employees);

            clientPredicates.add(criteriaBuilder.in(clientJoin.get(Client_.ORGANIZATION_ID)).value(employeeOrganizationIds));
        }

        if (filter.hasPermission(CLIENT_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_COMMUNITY)) {
            var employees = filter.getEmployees(CLIENT_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_COMMUNITY);
            var employeeCommunityIds = SpecificationUtils.employeesCommunityIds(employees);

            clientPredicates.add(criteriaBuilder.in(clientJoin.get(Client_.COMMUNITY_ID)).value(employeeCommunityIds));
        }

        if (filter.hasPermission(CLIENT_REFERRAL_VIEW_OUTBOUND_IF_CURRENT_REGULAR_COMMUNITY_CTM)) {
            var employees = filter.getEmployees(CLIENT_REFERRAL_VIEW_OUTBOUND_IF_CURRENT_REGULAR_COMMUNITY_CTM);

            clientPredicates.add(securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(
                    criteriaBuilder,
                    criteriaQuery,
                    clientJoin.get(Client_.communityId),
                    employees,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.current(clientJoin)
            ));
        }

        if (filter.hasPermission(CLIENT_REFERRAL_VIEW_OUTBOUND_IF_CURRENT_REGULAR_CLIENT_CTM)) {
            var employees = filter.getEmployees(CLIENT_REFERRAL_VIEW_OUTBOUND_IF_CURRENT_REGULAR_CLIENT_CTM);

            clientPredicates.add(
                    securityPredicateGenerator.clientsInClientCareTeamPredicate(
                            criteriaBuilder,
                            criteriaQuery,
                            clientJoin,
                            employees,
                            AffiliatedCareTeamType.REGULAR,
                            HieConsentCareTeamType.current(clientJoin)
                    ));

        }

        if (filter.hasPermission(CLIENT_REFERRAL_VIEW_OUTBOUND_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            var employees = filter.getEmployees(CLIENT_REFERRAL_VIEW_OUTBOUND_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);

            clientPredicates.add(criteriaBuilder.and(
                    securityPredicateGenerator.clientAddedByEmployees(criteriaBuilder, clientJoin, employees),
                    clientPredicateGenerator.isOptedIn(clientJoin, criteriaBuilder)
            ));
        }

        if (!clientPredicates.isEmpty()) {
            predicates.add(
                    criteriaBuilder.and(
                            clientJoin.isNotNull(),
                            criteriaBuilder.or(clientPredicates.toArray(Predicate[]::new))
                    )
            );
        }

        var b2bPredicates = new ArrayList<Predicate>();

        if (filter.hasPermission(B2B_REFERRAL_VIEW_OUTBOUND_ALL)) {
            b2bPredicates.add(criteriaBuilder.and());
        }

        if (filter.hasPermission(B2B_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_ORGANIZATION)) {

            var employees = filter.getEmployees(B2B_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_ORGANIZATION);
            var employeeOrganizationIds = SpecificationUtils.employeesOrganizationIds(employees);

            b2bPredicates.add(requestingCommunityJoin.get(Community_.organizationId).in(employeeOrganizationIds));
        }

        if (filter.hasPermission(B2B_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_COMMUNITY)) {
            var employees = filter.getEmployees(B2B_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_COMMUNITY);
            var employeeCommunityIds = SpecificationUtils.employeesCommunityIds(employees);

            clientPredicates.add(requestingCommunityIdPath.in(employeeCommunityIds));
        }

        if (filter.hasPermission(B2B_REFERRAL_VIEW_OUTBOUND_IF_CO_REGULAR_COMMUNITY_CTM)) {
            var employees = filter.getEmployees(B2B_REFERRAL_VIEW_OUTBOUND_IF_CO_REGULAR_COMMUNITY_CTM);

            clientPredicates.add(securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(
                    criteriaBuilder,
                    criteriaQuery,
                    requestingCommunityIdPath,
                    employees,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentAndOnHold()
            ));
        }

        if (!b2bPredicates.isEmpty()) {
            predicates.add(criteriaBuilder.and(
                    clientJoin.isNull(),
                    criteriaBuilder.or(b2bPredicates.toArray(Predicate[]::new))
            ));
        }

        return criteriaBuilder.and(
                eligible,
                criteriaBuilder.or(predicates.toArray(Predicate[]::new))
        );
    }

    public Specification<Employee> possibleAssignees(ReferralRequest request) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            var isClientOptedIn = request.getReferral().getClient().getHieConsentPolicyType() == HieConsentPolicyType.OPT_IN;
            var systemRole = root.get(Employee_.careTeamRole).get(CareTeamRole_.CODE);

            var superAdminsAndAssociatedOrg = CareTeamRolePermissionMapping.findCareTeamRoleCodesWithPermission(REFERRAL_VIEW_INBOUND_IF_ASSOCIATED_ORGANIZATION);

            if (isClientOptedIn) {
                superAdminsAndAssociatedOrg = SetUtils.union(
                        superAdminsAndAssociatedOrg,
                        CareTeamRolePermissionMapping.findCareTeamRoleCodesWithPermission(REFERRAL_VIEW_INBOUND_ALL_EXCEPT_CLIENT_OPTED_OUT)
                );
            }

            predicates.add(criteriaBuilder.and(
                    criteriaBuilder.in(systemRole).value(superAdminsAndAssociatedOrg),
                    criteriaBuilder.equal(root.get(Employee_.organizationId), request.getCommunity().getOrganizationId())
            ));

            var associatedComm = CareTeamRolePermissionMapping.findCareTeamRoleCodesWithPermission(REFERRAL_VIEW_INBOUND_IF_ASSOCIATED_COMMUNITY);
            predicates.add(criteriaBuilder.and(
                    criteriaBuilder.in(systemRole).value(associatedComm),
                    criteriaBuilder.equal(root.get(Employee_.communityId), request.getCommunityId())
            ));

            var communityCTMs = CareTeamRolePermissionMapping.findCareTeamRoleCodesWithPermission(REFERRAL_VIEW_INBOUND_IF_CO_REGULAR_COMMUNITY_CTM);
            predicates.add(criteriaBuilder.and(
                    criteriaBuilder.in(systemRole).value(communityCTMs),
                    communityCareTeamMemberPredicateGenerator.isCommunityCareTeamMember(
                            criteriaBuilder,
                            criteriaQuery,
                            root,
                            request.getCommunityId(),
                            AffiliatedCareTeamType.REGULAR,
                            HieConsentCareTeamType.currentWithOptimizations(request.getReferral().getClient())
                    )
            ));

            return criteriaBuilder.and(
                    securityPredicateGenerator.eligibleForDiscoveryCommunity(
                            JpaUtils.getOrCreateJoin(root, Employee_.community), criteriaBuilder
                    ),
                    criteriaBuilder.equal(root.get(Employee_.status), EmployeeStatus.ACTIVE),
                    criteriaBuilder.or(predicates.toArray(new Predicate[0]))
            );
        };
    }

    public Specification<Referral> referralOfInfoRequest(Long infoRequestId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        JpaUtils.getOrCreateListJoin(
                                JpaUtils.getOrCreateListJoin(root, Referral_.referralRequests),
                                ReferralRequest_.infoRequests).get(ReferralInfoRequest_.id),
                        infoRequestId
                );
    }

    public Specification<ReferralRequest> referralRequestOfInfoRequest(Long infoRequestId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        JpaUtils.getOrCreateListJoin(root, ReferralRequest_.infoRequests).get(ReferralInfoRequest_.id),
                        infoRequestId
                );
    }
}
