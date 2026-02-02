package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.conversation.ConversationParticipantAccessibilityCommunityFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.*;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.marketplace.ServiceType_;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class CommunitySpecificationGenerator {

    @Autowired
    private CommunityPredicateGenerator communityPredicateGenerator;

    @Autowired
    private ReferralPredicateGenerator referralPredicateGenerator;

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    @Autowired
    private EmployeePredicateGenerator employeePredicateGenerator;

    @Autowired
    private OrganizationPredicateGenerator organizationPredicateGenerator;

    @Autowired
    private CommunityCareTeamMemberPredicateGenerator communityCareTeamMemberPredicateGenerator;

    public Specification<Community> byOrganizationIdsEligibleForDiscovery(Collection<Long> organizationIds) {
        return (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.and(
                criteriaBuilder.in(root.get(Community_.ORGANIZATION_ID)).value(organizationIds),
                communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, root)
            );
    }

    public Specification<Community> eligibleForDiscovery() {
        return (root, criteriaQuery, criteriaBuilder) ->
                        communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, root);
    }

    public Specification<Community> byOrganizationIdEligibleForDiscovery(Long organizationId, Boolean includeInactive) {
        return (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.and(
                criteriaBuilder.equal(root.get(Community_.organizationId), organizationId),
                communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, root, includeInactive)
            );
    }

    public Specification<Community> byOrganizationIdEligibleForDiscovery(Long organizationId) {
        return byOrganizationIdEligibleForDiscovery(organizationId, false);
    }

    public Specification<Community> byOrganizationId(Long organizationId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Community_.organizationId), organizationId);
    }

    public Specification<Community> isVisible() {
        return (root, criteriaQuery, criteriaBuilder) -> communityPredicateGenerator.isVisible(criteriaBuilder, root);
    }

    public Specification<Community> hasAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> communityPredicateGenerator.hasAccess(permissionFilter, root, criteriaBuilder, criteriaQuery);
    }

    public Specification<Community> byCommunityIdsEligibleForDiscovery(Collection<Long> communityIds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if ((CollectionUtils.isNotEmpty(communityIds))) {
                return criteriaBuilder.and(
                        criteriaBuilder.in(root.get(Community_.ID)).value(CollectionUtils.emptyIfNull(communityIds)),
                        communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, root)
                );
            }
            return criteriaBuilder.or();
        };
    }

    public Specification<Community> withNonNetworkFlagEnabledEligibleForDiscovery() {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.isTrue(root.get(Community_.receiveNonNetworkReferrals)),
                        communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, root)
                );
    }

    public Specification<Community> notEqual(Community community) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.notEqual(root, community);
    }

    public Specification<Community> withServices(List<Long> serviceIds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);
            var marketplaceJoin = root.join(Community_.marketplace);
            var servicesJoin = marketplaceJoin.join(Marketplace_.serviceTypes);
            return criteriaBuilder.and(servicesJoin.get(ServiceType_.id).in(serviceIds));
        };
    }

    public Specification<Community> communitiesInReferralMarketplaceAllowedCommunities(PermissionFilter permissionFilter, Community marketplaceCommunity) {
        return (root, criteriaQuery, criteriaBuilder) ->
                referralPredicateGenerator.communitiesInMarketplaceAllowedCommunities(permissionFilter, marketplaceCommunity, root, criteriaQuery, criteriaBuilder);
    }

    public Specification<Community> withinNetworksEligibleForDiscovery(Collection<Long> networkIds) {
        return (root, query, criteriaBuilder) -> {
            if (CollectionUtils.isEmpty(networkIds)) {
                return criteriaBuilder.or();
            }

            var subquery = query.subquery(Long.class);
            var subFrom = subquery.from(PartnerNetworkCommunity.class);

            subquery.select(subFrom.get(PartnerNetworkCommunity_.communityId));
            subquery.where(
                    communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, subFrom.get(PartnerNetworkCommunity_.community)),
                    SpecificationUtils.in(criteriaBuilder, subFrom.get(PartnerNetworkCommunity_.partnerNetworkId), networkIds)
            );

            return root.get(Community_.id).in(subquery);
        };
    }

    public Specification<Community> withEnableChat() {
        return (root, criteriaQuery, criteriaBuilder) -> organizationPredicateGenerator.withEnabledChat(criteriaBuilder, root.get(Community_.organization), true);
    }

    public Specification<Community> byAccessibleChatCommunityFilter(PermissionFilter permissionFilter, ConversationParticipantAccessibilityCommunityFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(root.get(Community_.organizationId).in(filter.getOrganizationIds()));
            predicates.add(communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, root));

            List<Predicate> conditionPredicates = new ArrayList<>();
            if (filter.getWithAccessibleClients() || filter.getWithAccessibleNonAssociatedClients()) {
                var subQuery = criteriaQuery.subquery(Integer.class);
                var subRoot = subQuery.from(Client.class);
                subQuery.select(criteriaBuilder.literal(1));

                var whereClause = new ArrayList<Predicate>();

                whereClause.add(criteriaBuilder.equal(root.get(Community_.id), subRoot.get(Client_.communityId)));
                whereClause.add(clientPredicateGenerator.chatAccessibleClients(permissionFilter, filter.getExcludedEmployeeId(),
                        subRoot, subQuery, criteriaBuilder));

                if (!filter.getWithAccessibleNonAssociatedClients()) {
                    whereClause.add(clientPredicateGenerator.hasActiveAssociatedEmployee(criteriaBuilder, subRoot));
                }
                if (filter.getWithExcludedOneToOneParticipants()) {
                    whereClause.add(clientPredicateGenerator.excludeAssociatedParticipatingInOneToOneChatWithAny(
                            permissionFilter.getAllEmployeeIds(), subRoot, subQuery, criteriaBuilder));
                }

                subQuery.where(whereClause.toArray(new Predicate[0]));
                conditionPredicates.add(criteriaBuilder.exists(subQuery));
            }

            if (filter.getWithAccessibleCommunityCareTeamMembers()) {
                var subQuery = criteriaQuery.subquery(Integer.class);
                var subRoot = subQuery.from(CommunityCareTeamMember.class);
                subQuery.select(criteriaBuilder.literal(1));
                subQuery.where(
                        criteriaBuilder.equal(root.get(Community_.id), subRoot.get(CommunityCareTeamMember_.communityId)),
                        communityCareTeamMemberPredicateGenerator.chatAccessible(permissionFilter,
                                filter.getExcludedEmployeeId(), subRoot, subQuery, criteriaBuilder)
                );
                conditionPredicates.add(criteriaBuilder.exists(subQuery));
            }

            if (filter.getWithAccessibleContacts()) {
                var subQuery = criteriaQuery.subquery(Integer.class);
                var subRoot = subQuery.from(Employee.class);
                subQuery.select(criteriaBuilder.literal(1));

                var whereClause = new ArrayList<Predicate>();
                whereClause.add(criteriaBuilder.equal(root.get(Community_.id), subRoot.get(Employee_.communityId)));
                whereClause.add(employeePredicateGenerator.chatAccessibleEmployeesByOrganizationIds(permissionFilter,
                        filter.getExcludedEmployeeId(), null, subRoot, subQuery, criteriaBuilder));

                if (filter.getWithExcludedOneToOneParticipants()) {
                    whereClause.add(employeePredicateGenerator
                            .excludeParticipatingInOneToOneChatWithAny(
                                    permissionFilter.getAllEmployeeIds(), subRoot.get(Employee_.id), subQuery, criteriaBuilder));
                }

                subQuery.where(whereClause.toArray(new Predicate[0]));
                conditionPredicates.add(criteriaBuilder.exists(subQuery));
            }

            if (CollectionUtils.isNotEmpty(conditionPredicates)) {
                predicates.add(criteriaBuilder.or(conditionPredicates.toArray(new Predicate[0])));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<Community> isDocutrackPharmacy() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isTrue(root.get(Community_.isDocutrackPharmacy));
    }

    public Specification<Community> withDocutrackServerDomain(String serverDomain) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Community_.docutrackServerDomain), serverDomain);
    }

    public Specification<Community> hasAnyBusinessUnitCode(Collection<String> businessUnitCodes) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (CollectionUtils.isEmpty(businessUnitCodes)) {
                return criteriaBuilder.and();
            }

            var sub = criteriaQuery.subquery(Long.class);
            var subRoot = sub.from(Community.class);
            var bucJoin = subRoot.join(Community_.businessUnitCodes);

            return root.get(Community_.id)
                    .in(sub
                            .select(subRoot.get(Community_.id))
                            .where(SpecificationUtils.in(criteriaBuilder, bucJoin, businessUnitCodes))
                    );
        };
    }

    public Specification<Community> byIdNot(Long communityId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.notEqual(root.get(Community_.id), communityId);
    }

    public Specification<Community> enabledInMarketplace() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isNotNull(root.get(Community_.marketplace)));
            var marketplaceJoin = JpaUtils.getOrCreateJoin(root, Community_.marketplace);
            predicates.add(criteriaBuilder.equal(marketplaceJoin.get(Marketplace_.discoverable), true));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<Community> hasClientsAvailableForSignatureRequest(PermissionFilter permissionFilter) {
        return (root, query, criteriaBuilder) -> {
            var subQuery = query.subquery(Long.class);
            var subQueryRoot = subQuery.from(Client.class);
            subQuery.select(subQueryRoot.get(Client_.id));
            subQuery.where(
                    criteriaBuilder.and(
                            clientPredicateGenerator.hasPermissionToRequestSignatureFrom(permissionFilter, subQueryRoot, query, criteriaBuilder),
                            criteriaBuilder.equal(subQueryRoot.get(Client_.communityId), root.get(Community_.id))
                    )
            );

            return criteriaBuilder.exists(subQuery);
        };
    }

    public Specification<Community> byOid(String oid) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Community_.oid), oid);
    }

    public Specification<Community> byOrganizationOid(String oid) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var organizationJoin = JpaUtils.getOrCreateJoin(root, Community_.organization);
            return criteriaBuilder.equal(organizationJoin.get(Organization_.oid), oid);
        };
    }
}
