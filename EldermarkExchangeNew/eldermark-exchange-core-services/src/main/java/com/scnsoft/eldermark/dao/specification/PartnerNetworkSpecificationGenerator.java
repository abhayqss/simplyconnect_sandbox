package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.PartnerNetworkDetailsFilter;
import com.scnsoft.eldermark.beans.PartnerNetworkFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.predicate.CommunityPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.PartnerNetworkPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.SecurityPredicateGenerator;
import com.scnsoft.eldermark.entity.Marketplace_;
import com.scnsoft.eldermark.entity.PartnerNetworkCommunity;
import com.scnsoft.eldermark.entity.PartnerNetworkCommunity_;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.marketplace.ServiceType_;
import com.scnsoft.eldermark.entity.network.PartnerNetwork;
import com.scnsoft.eldermark.entity.network.PartnerNetwork_;
import com.scnsoft.eldermark.entity.security.Permission;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

@Component
public class PartnerNetworkSpecificationGenerator {

    @Autowired
    private CommunityPredicateGenerator communityPredicateGenerator;

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    public Specification<PartnerNetwork> hasAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> root.in(networkCommunitySubquery(criteriaQuery,
                networkCommunityFrom -> hasAccess(permissionFilter, networkCommunityFrom, criteriaQuery, criteriaBuilder)));
    }

    @Autowired
    private PartnerNetworkPredicateGenerator partnerNetworkPredicateGenerator;

    private Predicate hasAccess(PermissionFilter permissionFilter, From<?, PartnerNetworkCommunity> nc, CriteriaQuery cq, CriteriaBuilder cb) {
        if (permissionFilter.hasPermission(Permission.ROLE_SUPER_ADMINISTRATOR)) {
            return cb.and();
        }

        var predicates = new ArrayList<Predicate>();

        if (permissionFilter.hasPermission(Permission.PARTNER_NETWORK_VIEW_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(Permission.PARTNER_NETWORK_VIEW_IF_ASSOCIATED_ORGANIZATION);

            predicates.add(cb
                    .in(nc.get(PartnerNetworkCommunity_.community).get(Community_.ORGANIZATION_ID))
                    .value(SpecificationUtils.employeesOrganizationIds(employees))
            );
        }

        if (permissionFilter.hasPermission(Permission.PARTNER_NETWORK_VIEW_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(Permission.PARTNER_NETWORK_VIEW_IF_ASSOCIATED_COMMUNITY);

            predicates.add(cb
                    .in(nc.get(PartnerNetworkCommunity_.COMMUNITY_ID))
                    .value(SpecificationUtils.employeesCommunityIds(employees))
            );
        }

        if (permissionFilter.hasPermission(Permission.PARTNER_NETWORK_VIEW_IF_CO_REGULAR_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(Permission.PARTNER_NETWORK_VIEW_IF_CO_REGULAR_COMMUNITY_CTM);

            predicates.add(securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(
                    cb,
                    cq,
                    nc.get(PartnerNetworkCommunity_.communityId),
                    employees,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentAndOnHold()
            ));
        }

        if (permissionFilter.hasPermission(Permission.PARTNER_NETWORK_VIEW_IF_CO_REGULAR_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(Permission.PARTNER_NETWORK_VIEW_IF_CO_REGULAR_CLIENT_CTM);

            predicates.add(securityPredicateGenerator.clientsInClientCareTeamOfCommunityPredicate(
                    cb,
                    cq,
                    nc.get(PartnerNetworkCommunity_.communityId),
                    employees,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentAndOnHold()
            ));
        }

        if (permissionFilter.hasPermission(Permission.PARTNER_NETWORK_VIEW_IF_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(Permission.PARTNER_NETWORK_VIEW_IF_CLIENT_ADDED_BY_SELF);

            predicates.add(securityPredicateGenerator.clientAddedByEmployeesToCommunity(cb, cq,
                    nc.get(PartnerNetworkCommunity_.communityId), employees));
        }

        return cb.or(predicates.toArray(new Predicate[0]));
    }

    public Specification<PartnerNetworkCommunity> communityEligibleForDiscovery() {
        return (root, criteriaQuery, criteriaBuilder) -> communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder,
                root.join(PartnerNetworkCommunity_.community));
    }

    public Specification<PartnerNetworkCommunity> byCommunityId(Long communityId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(
                root.get(PartnerNetworkCommunity_.communityId),
                communityId
        );
    }

    public Specification<PartnerNetworkCommunity> byPartnerNetworkId(Long id) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(PartnerNetworkCommunity_.partnerNetworkId), id);
    }

    public Specification<PartnerNetworkCommunity> byDetailsFilterAndEligibleForDiscovery(PartnerNetworkDetailsFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            var communityJoin = root.join(PartnerNetworkCommunity_.community);
            predicates.add(criteriaBuilder.equal(root.get(PartnerNetworkCommunity_.partnerNetworkId), filter.getPartnerNetworkId()));
            if (filter.getExcludeCommunityId() != null) {
                predicates.add(criteriaBuilder.notEqual(root.get(PartnerNetworkCommunity_.communityId), filter.getExcludeCommunityId()));
            }
            if (CollectionUtils.isNotEmpty(filter.getServiceIds())) {
                var marketplaceJoin = communityJoin.join(Community_.marketplace);
                var servicesJoin = marketplaceJoin.join(Marketplace_.serviceTypes);
                predicates.add(servicesJoin.get(ServiceType_.id).in(filter.getServiceIds()));
            }
            predicates.add(communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, communityJoin, false));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<PartnerNetwork> byFilter(PartnerNetworkFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            predicates.add(root.get(PartnerNetwork_.ID).in(
                    networkCommunitySubquery(criteriaQuery,
                            networkCommunityFrom -> communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder,
                                    networkCommunityFrom.join(PartnerNetworkCommunity_.community), false))
            ));

            if (filter.getCommunityId() != null) {
                var networkCommunitySubquery = networkCommunitySubquery(criteriaQuery,
                        networkCommunityFrom ->
                                criteriaBuilder.equal(
                                        networkCommunityFrom.get(PartnerNetworkCommunity_.communityId),
                                        filter.getCommunityId())
                );

                predicates.add(root.get(PartnerNetwork_.ID).in(networkCommunitySubquery));
            }

            if (CollectionUtils.isNotEmpty(filter.getServiceIds())) {
                boolean shouldExcludeCommunity = filter.getCommunityId() != null && !filter.getIncludeCommunityInServiceSearch();

                var networkServicesSubquery = networkCommunitySubquery(criteriaQuery,
                        networkCommunityFrom -> criteriaBuilder.and(
                                shouldExcludeCommunity
                                        ? criteriaBuilder.notEqual(networkCommunityFrom.get(PartnerNetworkCommunity_.communityId), filter.getCommunityId())
                                        : criteriaBuilder.and(),
                                networkCommunityFrom.join(PartnerNetworkCommunity_.community)
                                        .join(Community_.marketplace)
                                        .join(Marketplace_.serviceTypes)
                                        .get(ServiceType_.id)
                                        .in(filter.getServiceIds())
                        )
                );

                predicates.add(root.get(PartnerNetwork_.ID).in(networkServicesSubquery));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Subquery<Long> networkCommunitySubquery(AbstractQuery<?> criteriaQuery,
                                                    Function<Root<PartnerNetworkCommunity>, Predicate> restriction) {

        var networkCommunitySubquery = criteriaQuery.subquery(Long.class);
        var networkCommunityFrom = networkCommunitySubquery.from(PartnerNetworkCommunity.class);

        return networkCommunitySubquery
                .select(networkCommunityFrom.get(PartnerNetworkCommunity_.partnerNetworkId))
                .where(restriction.apply(networkCommunityFrom));
    }

    public Specification<PartnerNetwork> withSameCommunitiesEiligibleForDiscovery(Collection<Long> networkCommunityIds) {
        return (root, criteriaQuery, criteriaBuilder) -> partnerNetworkPredicateGenerator.networksContainingAllEligibleForDiscovery(networkCommunityIds,
                root.get(PartnerNetwork_.id), criteriaQuery, criteriaBuilder);
    }
}
