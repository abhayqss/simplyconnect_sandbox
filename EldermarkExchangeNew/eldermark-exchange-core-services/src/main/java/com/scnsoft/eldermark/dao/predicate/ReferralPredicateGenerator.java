package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.specification.SpecificationUtils;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.basic.DisplayableNamedKeyEntity;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.marketplace.ServiceType;
import com.scnsoft.eldermark.entity.referral.ReferralRequest;
import com.scnsoft.eldermark.entity.referral.ReferralRequest_;
import com.scnsoft.eldermark.entity.referral.Referral_;
import com.scnsoft.eldermark.service.ServiceTypeService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Component
public class ReferralPredicateGenerator {

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    @Autowired
    private ExternalEmployeeInboundReferralCommunityPredicateGenerator externalEmployeeInboundReferralCommunityPredicateGenerator;

    @Autowired
    private PartnerNetworkPredicateGenerator partnerNetworkPredicateGenerator;

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    @Autowired
    private ServiceTypeService serviceTypeService;

    public Predicate hasAccessToInbound(PermissionFilter filter, From<?, ReferralRequest> requestFrom,
                                        CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

        var communityJoin = JpaUtils.getOrCreateJoin(requestFrom, ReferralRequest_.community);
        var eligible = securityPredicateGenerator.eligibleForDiscoveryCommunity(communityJoin, criteriaBuilder);
        var referralJoin = JpaUtils.getOrCreateJoin(requestFrom, ReferralRequest_.referral);
        var clientJoin = JpaUtils.getOrCreateJoin(referralJoin, Referral_.client, JoinType.LEFT);

        var predicates = new ArrayList<Predicate>();

        if (filter.hasPermission(REFERRAL_VIEW_INBOUND_ALL_EXCEPT_CLIENT_OPTED_OUT)) {
            predicates.add(
                    criteriaBuilder.or(
                            clientJoin.isNull(),
                            clientPredicateGenerator.isOptedIn(clientJoin, criteriaBuilder)
                    )
            );
        }

        if (filter.hasPermission(REFERRAL_VIEW_INBOUND_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(REFERRAL_VIEW_INBOUND_IF_ASSOCIATED_ORGANIZATION);
            var employeeOrganizationIds = SpecificationUtils.employeesOrganizationIds(employees);

            predicates.add(SpecificationUtils.in(
                    criteriaBuilder,
                    communityJoin.get(Community_.organizationId),
                    employeeOrganizationIds)
            );
        }

        if (filter.hasPermission(REFERRAL_VIEW_INBOUND_IF_ASSOCIATED_COMMUNITY)) {
            var employees = filter.getEmployees(REFERRAL_VIEW_INBOUND_IF_ASSOCIATED_COMMUNITY);
            var employeeCommunityIds = SpecificationUtils.employeesCommunityIds(employees);

            predicates.add(SpecificationUtils.in(
                    criteriaBuilder,
                    requestFrom.get(ReferralRequest_.communityId),
                    employeeCommunityIds
            ));
        }

        if (filter.hasPermission(REFERRAL_VIEW_INBOUND_IF_CO_REGULAR_COMMUNITY_CTM)) {
            var employees = filter.getEmployees(REFERRAL_VIEW_INBOUND_IF_CO_REGULAR_COMMUNITY_CTM);

            predicates.add(securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(
                    criteriaBuilder,
                    criteriaQuery,
                    requestFrom.get(ReferralRequest_.communityId),
                    employees,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentAndOnHold()
            ));

        }

        if (filter.hasPermission(REFERRAL_VIEW_INBOUND_IF_EXTERNAL_REFERRAL_REQUEST_EXCEPT_CLIENT_OPTED_OUT)) {
            var employees = filter.getEmployees(REFERRAL_VIEW_INBOUND_IF_EXTERNAL_REFERRAL_REQUEST_EXCEPT_CLIENT_OPTED_OUT);

            predicates.add(
                    criteriaBuilder.and(
                            externalEmployeeInboundReferralCommunityPredicateGenerator.communityIdsInReferralSharedCommunities(
                                    requestFrom.get(ReferralRequest_.communityId),
                                    criteriaQuery,
                                    employees
                            ),
                            criteriaBuilder.or(
                                    clientJoin.isNull(),
                                    clientPredicateGenerator.isOptedIn(clientJoin, criteriaBuilder)
                            )
                    )
            );
        }

        return criteriaBuilder.and(
                eligible,
                criteriaBuilder.or(predicates.toArray(new Predicate[0]))
        );
    }

    public Predicate communitiesInMarketplaceAllowedCommunities(
            PermissionFilter filter,
            Community marketplaceCommunity,
            Path<Community> communityPath,
            AbstractQuery<?> query,
            CriteriaBuilder cb
    ) {
        var allAllowedServiceTypes = serviceTypeService.findAllowedForReferral(filter).stream()
                .map(DisplayableNamedKeyEntity::getKey)
                .collect(Collectors.toSet());

        var allowedMarketplaceServiceTypes = marketplaceCommunity.getMarketplace()
                .getServiceTypes()
                .stream()
                .filter(it -> allAllowedServiceTypes.contains(it.getKey()))
                .collect(Collectors.toSet());

        if (allowedMarketplaceServiceTypes.isEmpty()) {
            return cb.or();
        }

        var marketplaceHasBusinessRelatedService = allowedMarketplaceServiceTypes
                .stream()
                .anyMatch(ServiceType::getIsBusinessRelated);

        var marketplaceHasClientRelatedService = allowedMarketplaceServiceTypes
                .stream()
                .anyMatch(ServiceType::getIsBusinessRelated);

        var predicates = new ArrayList<Predicate>();

        var communityIdPath = communityPath.get(Community_.id);

        if (marketplaceHasClientRelatedService) {

            var subQuery = query.subquery(Integer.class);
            subQuery.select(cb.literal(1));

            var subClientRoot = subQuery.from(Client.class);
            var subCommunityId = subClientRoot.get(Client_.communityId);

            var clientPredicates = new ArrayList<Predicate>();

            if (filter.hasPermission(CLIENT_REFERRAL_ADD_ALL_EXCEPT_OPTED_OUT)) {
                clientPredicates.add(clientPredicateGenerator.isOptedIn(subClientRoot, cb));
            }

            if (filter.hasPermission(CLIENT_REFERRAL_ADD_IF_ASSOCIATED_ORGANIZATION)) {
                var employeesOrganizations = CareCoordinationUtils.getOrganizationIdsSet(filter.getEmployees(CLIENT_REFERRAL_ADD_IF_ASSOCIATED_ORGANIZATION));
                clientPredicates.add(SpecificationUtils.in(cb, subClientRoot.get(Client_.organizationId), employeesOrganizations));
            }

            if (filter.hasPermission(CLIENT_REFERRAL_ADD_IF_ASSOCIATED_COMMUNITY)) {
                var employeeCommunities = CareCoordinationUtils.getCommunityIdsSet(filter.getEmployees(CLIENT_REFERRAL_ADD_IF_ASSOCIATED_COMMUNITY));
                clientPredicates.add(SpecificationUtils.in(cb, subCommunityId, employeeCommunities));
            }

            if (filter.hasPermission(CLIENT_REFERRAL_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM)) {
                var employees = filter.getEmployees(CLIENT_REFERRAL_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM);
                clientPredicates.add(securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(
                        cb,
                        subQuery,
                        subCommunityId,
                        employees,
                        AffiliatedCareTeamType.REGULAR,
                        HieConsentCareTeamType.current(subClientRoot)
                ));
            }

            if (filter.hasPermission(CLIENT_REFERRAL_ADD_IF_CURRENT_REGULAR_CLIENT_CTM)) {
                var employees = filter.getEmployees(CLIENT_REFERRAL_ADD_IF_CURRENT_REGULAR_CLIENT_CTM);
                clientPredicates.add(securityPredicateGenerator.clientsInClientCareTeamPredicate(
                        cb,
                        subQuery,
                        subClientRoot,
                        employees,
                        AffiliatedCareTeamType.REGULAR,
                        HieConsentCareTeamType.current(subClientRoot)
                ));
            }

            if (filter.hasPermission(CLIENT_REFERRAL_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
                var employees = filter.getEmployees(CLIENT_REFERRAL_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);
                clientPredicates.add(cb.and(
                        securityPredicateGenerator.clientAddedByEmployees(cb, subClientRoot, employees),
                        clientPredicateGenerator.isOptedIn(subClientRoot, cb)
                ));
            }

            if (filter.hasPermission(CLIENT_REFERRAL_ADD_IF_SELF_RECORD)) {
                var employees = filter.getEmployees(CLIENT_REFERRAL_ADD_IF_SELF_RECORD);
                clientPredicates.add(securityPredicateGenerator.selfRecordClients(cb, subClientRoot.get(Client_.id), employees));
            }

            subQuery.where(
                    cb.or(clientPredicates.toArray(new Predicate[0])),
                    cb.equal(communityIdPath, subCommunityId)
            );

            if (!clientPredicates.isEmpty()) {
                predicates.add(cb.exists(subQuery));
            }
        }

        if (marketplaceHasBusinessRelatedService) {
            var communityPredicates = new ArrayList<Predicate>();

            if (filter.hasPermission(B2B_REFERRAL_ADD_ALL)) {
                communityPredicates.add(cb.and());
            }

            if (filter.hasPermission(B2B_REFERRAL_ADD_IF_ASSOCIATED_ORGANIZATION)) {
                var employeesOrganizations = CareCoordinationUtils.getOrganizationIdsSet(filter.getEmployees(B2B_REFERRAL_ADD_IF_ASSOCIATED_ORGANIZATION));
                communityPredicates.add(SpecificationUtils.in(cb, communityPath.get(Community_.organizationId), employeesOrganizations));
            }

            if (filter.hasPermission(B2B_REFERRAL_ADD_IF_ASSOCIATED_COMMUNITY)) {
                var employeeCommunities = CareCoordinationUtils.getCommunityIdsSet(filter.getEmployees(B2B_REFERRAL_ADD_IF_ASSOCIATED_COMMUNITY));
                communityPredicates.add(SpecificationUtils.in(cb, communityIdPath, employeeCommunities));
            }

            if (filter.hasPermission(CLIENT_REFERRAL_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM)) {
                var employees = filter.getEmployees(CLIENT_REFERRAL_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM);
                communityPredicates.add(securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(
                        cb,
                        query,
                        communityIdPath,
                        employees,
                        AffiliatedCareTeamType.REGULAR,
                        HieConsentCareTeamType.currentAndOnHold()
                ));
            }

            if (!communityPredicates.isEmpty()) {
                predicates.add(cb.or(communityPredicates.toArray(Predicate[]::new)));
            }
        }

        var networksRestriction = marketplaceCommunity.isReceiveNonNetworkReferrals() ?
                cb.and() :
                partnerNetworkPredicateGenerator.areInSameNetworksEligibleForDiscovery(
                        marketplaceCommunity.getId(),
                        communityIdPath,
                        query,
                        cb
                );

        return cb.and(
                networksRestriction,
                cb.notEqual(communityIdPath, marketplaceCommunity.getId()),
                cb.or(predicates.toArray(Predicate[]::new))
        );
    }
}
