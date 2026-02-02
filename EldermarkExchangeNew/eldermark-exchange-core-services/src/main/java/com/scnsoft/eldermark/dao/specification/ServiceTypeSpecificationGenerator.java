package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.predicate.CommunityPredicateGenerator;
import com.scnsoft.eldermark.entity.Marketplace_;
import com.scnsoft.eldermark.entity.PartnerNetworkCommunity;
import com.scnsoft.eldermark.entity.PartnerNetworkCommunity_;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.marketplace.ServiceType;
import com.scnsoft.eldermark.entity.marketplace.ServiceType_;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class ServiceTypeSpecificationGenerator {

    @Autowired
    private CommunityPredicateGenerator communityPredicateGenerator;

    public Specification<ServiceType> fromReferralReceivingCommunities(Long communityId, boolean searchInNetworks) {
        return searchInNetworks
                ? fromCommunityNetworks(communityId)
                : fromGlobalReferralReceivingCommunitiesExcluding(communityId);
    }

    private Specification<ServiceType> fromCommunityNetworks(Long communityId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var servicesSubquery = criteriaQuery.subquery(Long.class);
            servicesSubquery.distinct(true);
            var networkCommunityFrom = servicesSubquery.from(PartnerNetworkCommunity.class);
            var communityJoin = networkCommunityFrom.join(PartnerNetworkCommunity_.community);
            var marketplaceJoin = communityJoin.join(Community_.marketplace);
            var servicesJoin = marketplaceJoin.join(Marketplace_.serviceTypes);
            servicesSubquery.select(servicesJoin.get(ServiceType_.id));
            var subQueryPredicates =  new ArrayList<Predicate>();
            subQueryPredicates.add(communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, communityJoin));

            //exclude specified community from result as services should be provided by other communities
            subQueryPredicates.add(criteriaBuilder.notEqual(communityJoin.get(Community_.id),communityId));

            //subquery to use only networks configured for community
            var networkSubquery = criteriaQuery.subquery(Long.class);
            var networkSubqueryFrom = networkSubquery.from(PartnerNetworkCommunity.class);
            networkSubquery.select(networkSubqueryFrom.get(PartnerNetworkCommunity_.partnerNetworkId));
            networkSubquery.where(criteriaBuilder.equal(networkSubqueryFrom.get(PartnerNetworkCommunity_.communityId), communityId));

            subQueryPredicates.add(networkCommunityFrom.get(PartnerNetworkCommunity_.partnerNetworkId).in(networkSubquery));
            servicesSubquery.where(criteriaBuilder.and(subQueryPredicates.toArray(new Predicate[0])));
            return criteriaBuilder.and(root.get(ServiceType_.id).in(servicesSubquery));
        };
    }

    private Specification<ServiceType> fromGlobalReferralReceivingCommunitiesExcluding(Long communityId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var servicesSubquery = criteriaQuery.subquery(Long.class);
            servicesSubquery.distinct(true);
            var communityFrom = servicesSubquery.from(Community.class);
            var marketplaceJoin = communityFrom.join(Community_.marketplace);
            var servicesJoin = marketplaceJoin.join(Marketplace_.serviceTypes);
            servicesSubquery.select(servicesJoin.get(ServiceType_.id));
            var subQueryPredicates =  new ArrayList<Predicate>();
            subQueryPredicates.add(communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, communityFrom));

            //exclude specified community from result as services should be provided by other communities
            subQueryPredicates.add(criteriaBuilder.notEqual(communityFrom.get(Community_.id),communityId));

            subQueryPredicates.add(criteriaBuilder.equal(communityFrom.get(Community_.receiveNonNetworkReferrals), Boolean.TRUE));
            servicesSubquery.where(criteriaBuilder.and(subQueryPredicates.toArray(new Predicate[0])));
            return criteriaBuilder.and(root.get(ServiceType_.id).in(servicesSubquery));
        };
    }

    public Specification<ServiceType> byCommunityIdEligibleForDiscovery(Long communityId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var serviceIdsSubquery = criteriaQuery.subquery(Long.class);
            serviceIdsSubquery.distinct(true);
            var communityFrom = serviceIdsSubquery.from(Community.class);
            var servicesJoin = communityFrom.join(Community_.marketplace).join(Marketplace_.serviceTypes);
            serviceIdsSubquery.select(servicesJoin.get(ServiceType_.id));

            var subQueryPredicates = new ArrayList<Predicate>();
            subQueryPredicates.add(communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, communityFrom));
            subQueryPredicates.add(criteriaBuilder.equal(communityFrom.get(Community_.id), communityId));

            serviceIdsSubquery.where(criteriaBuilder.and(subQueryPredicates.toArray(new Predicate[0])));

            return criteriaBuilder.and(root.get(ServiceType_.id).in(serviceIdsSubquery));
        };
    }

    public Specification<ServiceType> byServiceCategoryIds(Collection<Long> serviceCategoryIds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (CollectionUtils.isEmpty(serviceCategoryIds)) {
                return criteriaBuilder.and();
            }
            return root.get(ServiceType_.serviceCategoryId).in(serviceCategoryIds);
        };
    }

    public Specification<ServiceType> byDisplayNameLike(String searchText) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (StringUtils.isBlank(searchText)) {
                return criteriaBuilder.and();
            }
            return criteriaBuilder.like(root.get(ServiceType_.displayName), SpecificationUtils.wrapWithWildcards(searchText));
        };
    }

    public Specification<ServiceType> fromAccessibleCommunities(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var servicesSubquery = criteriaQuery.subquery(Long.class);
            servicesSubquery.distinct(true);
            var communityFrom = servicesSubquery.from(Community.class);
            var servicesJoin = communityFrom.join(Community_.marketplace).join(Marketplace_.serviceTypes);
            servicesSubquery.select(servicesJoin.get(ServiceType_.id));

            var subQueryPredicates = new ArrayList<Predicate>();
            subQueryPredicates.add(communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, communityFrom));
            subQueryPredicates.add(communityPredicateGenerator.hasAccess(permissionFilter, communityFrom, criteriaBuilder, servicesSubquery));

            servicesSubquery.where(criteriaBuilder.and(subQueryPredicates.toArray(new Predicate[0])));

            return criteriaBuilder.and(root.get(ServiceType_.id).in(servicesSubquery));
        };
    }
}
