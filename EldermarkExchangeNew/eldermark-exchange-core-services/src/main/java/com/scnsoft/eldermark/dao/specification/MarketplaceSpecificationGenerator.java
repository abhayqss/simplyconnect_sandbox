package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.MarketplaceFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.CommunityPredicateGenerator;
import com.scnsoft.eldermark.entity.Marketplace;
import com.scnsoft.eldermark.entity.MarketplacePartnerNetwork;
import com.scnsoft.eldermark.entity.MarketplacePartnerNetwork_;
import com.scnsoft.eldermark.entity.Marketplace_;
import com.scnsoft.eldermark.entity.Organization_;
import com.scnsoft.eldermark.entity.PartnerNetworkCommunity;
import com.scnsoft.eldermark.entity.PartnerNetworkCommunity_;
import com.scnsoft.eldermark.entity.SavedMarketplace;
import com.scnsoft.eldermark.entity.SavedMarketplace_;
import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.entity.State_;
import com.scnsoft.eldermark.entity.community.CommunityAddress_;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.marketplace.ServiceCategory_;
import com.scnsoft.eldermark.entity.marketplace.ServiceType_;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.entity.security.Permission.MARKETPLACE_VIEW_IF_FROM_PARTNER_COMMUNITY_ORGANIZATION;
import static com.scnsoft.eldermark.entity.security.Permission.MARKETPLACE_VIEW_NOT_DISCOVERABLE_ALLOWED;

@Component
public class MarketplaceSpecificationGenerator {

    private static final String CALC_DISTANCE_FUNCTION = "dbo.GreatCircleDistanceAngleGrad";

    public Specification<Marketplace> byFilterOrdered(MarketplaceFilter filter, PermissionFilter permissionFilter) {
        return (root, criteriaQuery, cb) -> {
            if (hasUserLocation(filter)) {
                return byFilterOrdered(filter, permissionFilter, root, criteriaQuery, cb);
            } else {
                return byFilter(filter, permissionFilter, root, criteriaQuery, cb);
            }
        };
    }

    private boolean hasUserLocation(MarketplaceFilter filter) {
        return ObjectUtils.allNotNull(filter.getLatitude(), filter.getLongitude());
    }

    public Predicate byFilterOrdered(MarketplaceFilter filter, PermissionFilter permissionFilter, Root<Marketplace> root, CriteriaQuery criteriaQuery, CriteriaBuilder cb) {
        var subquery = criteriaQuery.subquery(Marketplace.class);
        var subRoot = subquery.from(Marketplace.class);

        var byFilterPredicate = byFilter(filter, permissionFilter, subRoot, criteriaQuery, cb);
        subquery.where(byFilterPredicate);
        subquery.distinct(false);

        criteriaQuery.distinct(false);
        criteriaQuery.orderBy(orderByDistanceAsc(filter, root, cb));

        return cb.in(root).value(subquery.select(subRoot));
    }

    private Predicate byFilter(MarketplaceFilter filter, PermissionFilter permissionFilter, Root<Marketplace> root, CriteriaQuery criteriaQuery, CriteriaBuilder cb) {
        var predicates = new ArrayList<Predicate>();

        if (filter.getServiceCategoryId() != null) {
            predicates.add(cb.equal(JpaUtils.getOrCreateListJoin(root, Marketplace_.serviceCategories).get(ServiceCategory_.id), filter.getServiceCategoryId()));
        }

        if (CollectionUtils.isNotEmpty(filter.getServiceIds())) {
            predicates.add(JpaUtils.getOrCreateListJoin(root, Marketplace_.serviceTypes).get(ServiceType_.id).in(filter.getServiceIds()));
        }

        predicates.add(cb.isNotNull(root.get(Marketplace_.organization)));

        if (!permissionFilter.hasPermission(MARKETPLACE_VIEW_NOT_DISCOVERABLE_ALLOWED)) {
            predicates.add(cb.equal(root.get(Marketplace_.discoverable), Boolean.TRUE));
        }

        if (BooleanUtils.isTrue(filter.getIncludeMyCommunities()) || BooleanUtils.isTrue(filter.getIncludeInNetworkCommunities())) {
            var myCommunitiesInNetworkCommunities = new ArrayList<Predicate>();
            if (BooleanUtils.isTrue(filter.getIncludeMyCommunities())) {
                var employees = permissionFilter.getEmployees();
                var employeeOrganizationIds = CareCoordinationUtils.getOrganizationIdsSet(employees);
                myCommunitiesInNetworkCommunities.add(root.get(Marketplace_.organizationId).in(employeeOrganizationIds));
            }

            if (BooleanUtils.isTrue(filter.getIncludeInNetworkCommunities())) {
                var employees = permissionFilter.getEmployees();
                var employeeOrganizationIds = CareCoordinationUtils.getOrganizationIdsSet(employees);

                Subquery<Long> communitySubQuery = partnerNetworkCommunitiesByOrganizationIds(criteriaQuery, cb, employeeOrganizationIds);
                var inNetworkCommunities = new ArrayList<Predicate>();
                inNetworkCommunities.add(cb.in(root.get(Marketplace_.communityId)).value(communitySubQuery));
                inNetworkCommunities.add(root.get(Marketplace_.organizationId).in(employeeOrganizationIds).not());
                myCommunitiesInNetworkCommunities.add(cb.and(inNetworkCommunities.toArray(new Predicate[]{})));
            }

            predicates.add(cb.or(myCommunitiesInNetworkCommunities.toArray(new Predicate[]{})));
        }

        if (StringUtils.isNotBlank(filter.getSearchText())) {
            var joinCommunity = JpaUtils.getOrCreateJoin(root, Marketplace_.community);
            var joinCommunityAddress = JpaUtils.getOrCreateListJoin(joinCommunity, Community_.addresses);
            var joinOrganization = JpaUtils.getOrCreateJoin(root, Marketplace_.organization);
            var joinServiceCategories = JpaUtils.getOrCreateListJoin(root, Marketplace_.serviceCategories, JoinType.LEFT);

            String likeFormatSearchStr = SpecificationUtils.wrapWithWildcards(filter.getSearchText());

            /* state */
            var stateSubquery = criteriaQuery.subquery(String.class);
            var stateRoot = stateSubquery.from(State.class);

            var stateAbbrSubQueryResult = stateSubquery.select(stateRoot.get(State_.abbr))
                    .where(cb.like(stateRoot.get(State_.name), likeFormatSearchStr));

            var stateAbbrPredicate = cb.in(joinCommunityAddress.get(CommunityAddress_.state)).value(stateAbbrSubQueryResult);

            Predicate state = cb.or(cb.like(joinCommunityAddress.get(CommunityAddress_.state), likeFormatSearchStr),
                    stateAbbrPredicate);
            /* state */

            Predicate city = cb.like(joinCommunityAddress.get(CommunityAddress_.city), likeFormatSearchStr);
            Predicate zip = cb.like(joinCommunityAddress.get(CommunityAddress_.postalCode), likeFormatSearchStr);
            Predicate streetAddress = cb.like(joinCommunityAddress.get(CommunityAddress_.streetAddress), likeFormatSearchStr);
            Predicate communityName = cb.like(joinCommunity.get(Community_.name), likeFormatSearchStr);
            Predicate databaseName = cb.like(joinOrganization.get(Organization_.name), likeFormatSearchStr);
            Predicate serviceCategoryNames = cb.like(joinServiceCategories.get(ServiceCategory_.displayName), likeFormatSearchStr);

            predicates.add(cb.or(state, city, zip, streetAddress, communityName, databaseName, serviceCategoryNames));
        }

        criteriaQuery.distinct(true);

        return cb.and(predicates.toArray(new Predicate[]{}));
    }


    private Order orderByDistanceAsc(MarketplaceFilter filter, From<?, Marketplace> root, CriteriaBuilder cb) {
        var communityAddressesJoin = root.join(Marketplace_.community).join(Community_.addresses);
        final Expression<Double> distanceFunction = cb.function(CALC_DISTANCE_FUNCTION, Double.class,
                cb.literal(filter.getLongitude()),
                cb.literal(filter.getLatitude()),
                communityAddressesJoin.get(CommunityAddress_.longitude),
                communityAddressesJoin.get(CommunityAddress_.latitude)
        );

        return cb.asc(distanceFunction);
    }

    public Specification<Marketplace> byPartnerNetwork(Marketplace marketplace) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            var networks = marketplace.getMarketplacePartnerNetworks().stream().map(MarketplacePartnerNetwork::getPartnerNetworkId)
                    .collect(Collectors.toList());

            var subQuery = criteriaQuery.subquery(Marketplace.class);
            var networkRoot = subQuery.from(MarketplacePartnerNetwork.class);

            var subQueryResult = subQuery.select(networkRoot.get(MarketplacePartnerNetwork_.marketplace))
                    .where(criteriaBuilder.in(networkRoot.get(MarketplacePartnerNetwork_.PARTNER_NETWORK_ID)).value(networks));

            return criteriaBuilder.in(root).value(subQueryResult);
        };
    }

    public Specification<Marketplace> inOrganization(Long organizationId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Marketplace_.organizationId), organizationId);
    }

    public Specification<Marketplace> not(Marketplace marketplace) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.notEqual(root, marketplace);
    }

    @Autowired
    private CommunityPredicateGenerator communityPredicateGenerator;

    public Specification<Marketplace> hasAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var communityJoin = root.join(Marketplace_.community, JoinType.LEFT);
            var eligible = criteriaBuilder.or(
                    criteriaBuilder.isNull(communityJoin),
                    communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, communityJoin)
            );

            var discoverable = permissionFilter.hasPermission(Permission.MARKETPLACE_VIEW_NOT_DISCOVERABLE_ALLOWED)
                    ? criteriaBuilder.and()
                    : criteriaBuilder.isTrue(root.get(Marketplace_.discoverable));

            if (permissionFilter.hasPermission(Permission.MARKETPLACE_VIEW_ALL)) {
                return criteriaBuilder.and(
                        eligible,
                        discoverable
                );
            }

            var predicates = new ArrayList<Predicate>();

            if (permissionFilter.hasPermission(Permission.MARKETPLACE_VIEW_IF_ASSOCIATED_ORGANIZATION)) {
                var employees = permissionFilter.getEmployees(Permission.MARKETPLACE_VIEW_IF_ASSOCIATED_ORGANIZATION);
                var employeeOrganizations = CareCoordinationUtils.getOrganizationIdsSet(employees);

                predicates.add(criteriaBuilder.in(root.get(Marketplace_.ORGANIZATION_ID)).value(employeeOrganizations));
            }

            if (permissionFilter.hasPermission(MARKETPLACE_VIEW_IF_FROM_PARTNER_COMMUNITY_ORGANIZATION)) {
                var employees = permissionFilter.getEmployees(MARKETPLACE_VIEW_IF_FROM_PARTNER_COMMUNITY_ORGANIZATION);
                var employeeOrganizations = CareCoordinationUtils.getOrganizationIdsSet(employees);

                Subquery<Long> communitySubQuery = partnerNetworkCommunitiesByOrganizationIds(criteriaQuery, criteriaBuilder, employeeOrganizations);

                predicates.add(criteriaBuilder.in(root.get(Marketplace_.communityId)).value(communitySubQuery));
            }

            return criteriaBuilder.and(
                    eligible,
                    discoverable,
                    criteriaBuilder.or(predicates.toArray(new Predicate[0]))
            );
        };
    }

    public Specification<Marketplace> byCommunityId(Long communityId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Marketplace_.communityId), communityId);
    }

    public Specification<Marketplace> savedByEmployeeId(Long employeeId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            predicates.add(criteriaBuilder.isNotNull(root.get(Marketplace_.organization)));

            var subQuery = criteriaQuery.subquery(Long.class);
            var subRoot = subQuery.from(SavedMarketplace.class);
            var result = subQuery.select(subRoot.get(SavedMarketplace_.marketplaceId))
                    .where(criteriaBuilder.equal(subRoot.get(SavedMarketplace_.employeeId), employeeId));
            predicates.add(root.get(Marketplace_.id).in(result));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<Marketplace> byPartnerNetworkIds(Collection<Long> partnerNetworkIds) {
        return (root, query, criteriaBuilder) ->
                root.join(Marketplace_.marketplacePartnerNetworks)
                        .get(MarketplacePartnerNetwork_.partnerNetworkId)
                        .in(partnerNetworkIds);
    }

    public Specification<Marketplace> partnerNetworkCommunities(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var employees = permissionFilter.getEmployees();
            var employeeOrganizationIds = CareCoordinationUtils.getOrganizationIdsSet(employees);
            var communitySubQuery = partnerNetworkCommunitiesByOrganizationIds(criteriaQuery, criteriaBuilder, employeeOrganizationIds);
            return criteriaBuilder.in(root.get(Marketplace_.communityId)).value(communitySubQuery);
        };
    }

    private Subquery<Long> partnerNetworkCommunitiesByOrganizationIds(CriteriaQuery criteriaQuery, CriteriaBuilder cb, Set<Long> employeeOrganizationIds) {
        var partnerSubQuery = criteriaQuery.subquery(Long.class);
        var partnerSubRoot = partnerSubQuery.from(PartnerNetworkCommunity.class);
        var partnerCommunity = partnerSubRoot.join(PartnerNetworkCommunity_.community);
        partnerSubQuery
                .select(partnerSubRoot.get(PartnerNetworkCommunity_.partnerNetworkId))
                .where(
                        partnerCommunity.get(Community_.organizationId).in(employeeOrganizationIds),
                        communityPredicateGenerator.eligibleForDiscovery(cb, partnerCommunity)
                );

        var communitySubQuery = criteriaQuery.subquery(Long.class);
        var communitySubRoot = communitySubQuery.from(PartnerNetworkCommunity.class);
        communitySubQuery
                .select(communitySubRoot.get(PartnerNetworkCommunity_.communityId))
                .where(communitySubRoot.get(PartnerNetworkCommunity_.partnerNetworkId).in(partnerSubQuery));
        return communitySubQuery;
    }

    public Specification<Marketplace> byOrganizationIdNotIn(Collection<Long> organizationIds) {
        return (root, criteriaQuery, criteriaBuilder) -> root.get(Marketplace_.organizationId).in(organizationIds).not();
    }
}
