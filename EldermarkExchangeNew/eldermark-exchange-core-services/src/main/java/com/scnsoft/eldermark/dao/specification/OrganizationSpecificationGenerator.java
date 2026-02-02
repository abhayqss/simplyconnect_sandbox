package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.conversation.ConversationParticipantAccessibilityFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.predicate.*;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.CareCoordinationConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OrganizationSpecificationGenerator {

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    @Autowired
    private ExternalEmployeeInboundReferralCommunityPredicateGenerator externalEmployeeInboundReferralCommunityPredicateGenerator;

    @Autowired
    private ReferralPredicateGenerator referralPredicateGenerator;

    @Autowired
    private OrganizationPredicateGenerator organizationPredicateGenerator;

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    @Autowired
    private EmployeePredicateGenerator employeePredicateGenerator;

    @Autowired
    private CommunityCareTeamMemberPredicateGenerator communityCareTeamMemberPredicateGenerator;

    public Specification<Organization> checkIfUniqueSpecification(Organization organizationToBeChecked) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Optional.ofNullable(organizationToBeChecked.getId())
                    .ifPresent(id -> predicates.add(criteriaBuilder.notEqual(root.get(Organization_.id), id)));
            if (StringUtils.isNotEmpty(organizationToBeChecked.getName())) {
                predicates.add(criteriaBuilder.like(root.get(Organization_.name), organizationToBeChecked.getName()));
            }
            if (StringUtils.isNotEmpty(Optional.ofNullable(organizationToBeChecked.getSystemSetup())
                    .orElse(new SystemSetup()).getLoginCompanyId())) {
                predicates.add(criteriaBuilder.equal(root.join(Organization_.systemSetup).get(SystemSetup_.loginCompanyId),
                        organizationToBeChecked.getSystemSetup().getLoginCompanyId()));
            }
            if (StringUtils.isNotEmpty(organizationToBeChecked.getOid())) {
                predicates.add(criteriaBuilder.like(root.get(Organization_.oid), organizationToBeChecked.getOid()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<Organization> byFilter(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            if (StringUtils.isNotEmpty(name)) {
                predicates.add(criteriaBuilder.like(root.get(Organization_.name), SpecificationUtils.wrapWithWildcards(name)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<Organization> hasAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (permissionFilter.hasPermission(Permission.ORGANIZATION_VIEW_IN_LIST_ALL)) {
                return criteriaBuilder.and(); // always true
            }

            var predicates = new ArrayList<Predicate>();
            if (permissionFilter.hasPermission(Permission.ORGANIZATION_VIEW_IN_LIST_IF_ASSOCIATED_ORGANIZATION)) {
                var employees = permissionFilter.getEmployees(Permission.ORGANIZATION_VIEW_IN_LIST_IF_ASSOCIATED_ORGANIZATION);
                predicates.add(criteriaBuilder.in(root.get(Organization_.ID))
                        .value(employees.stream().map(Employee::getOrganizationId).collect(Collectors.toList())));
            }

            if (permissionFilter.hasPermission(Permission.ORGANIZATION_VIEW_IN_LIST_IF_FROM_AFFILIATED_ORGANIZATION)) {
                var employees = permissionFilter.getEmployees(Permission.ORGANIZATION_VIEW_IN_LIST_IF_FROM_AFFILIATED_ORGANIZATION);
                var employeeOrganizationIds = SpecificationUtils.employeesOrganizationIds(employees);

                var subQuery = criteriaQuery.subquery(Organization.class);
                var affiliatedRoot = subQuery.from(AffiliatedRelationship.class);

                var affiliatedOrganizationsSelect = subQuery
                        .select(affiliatedRoot.get(AffiliatedRelationship_.primaryOrganization))
                        .where(criteriaBuilder.in(affiliatedRoot.get(AffiliatedRelationship_.AFFILIATED_ORGANIZATION_ID)).value(employeeOrganizationIds));

                predicates.add(
                        criteriaBuilder.in(root).value(affiliatedOrganizationsSelect)
                );
            }

            if (permissionFilter.hasPermission(Permission.ORGANIZATION_VIEW_IN_LIST_IF_FROM_AFFILIATED_COMMUNITY)) {
                var employees = permissionFilter.getEmployees(Permission.ORGANIZATION_VIEW_IN_LIST_IF_FROM_AFFILIATED_COMMUNITY);
                var employeeCommunitiesIds = SpecificationUtils.employeesCommunityIds(employees);

                var subQuery = criteriaQuery.subquery(Organization.class);
                var affiliatedRoot = subQuery.from(AffiliatedRelationship.class);

                var affiliatedOrganizationsSelect = subQuery
                        .select(affiliatedRoot.get(AffiliatedRelationship_.primaryOrganization))
                        .where(criteriaBuilder.in(affiliatedRoot.get(AffiliatedRelationship_.AFFILIATED_COMMUNITY_ID)).value(employeeCommunitiesIds));

                predicates.add(
                        criteriaBuilder.in(root).value(affiliatedOrganizationsSelect)
                );
            }

            if (permissionFilter.hasPermission(Permission.ORGANIZATION_VIEW_IN_LIST_IF_CO_RP_COMMUNITY_CTM)) {
                var employees = permissionFilter.getEmployees(Permission.ORGANIZATION_VIEW_IN_LIST_IF_CO_RP_COMMUNITY_CTM);

                var subQuery = criteriaQuery.subquery(Long.class);
                var communityRoot = subQuery.from(Community.class);

                predicates.add(
                        criteriaBuilder.in(root.get(Organization_.id)).value(
                                subQuery.select(communityRoot.get(Community_.organizationId))
                                        .where(securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(
                                                criteriaBuilder,
                                                criteriaQuery,
                                                communityRoot.get(Community_.id),
                                                employees,
                                                AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                                                HieConsentCareTeamType.currentAndOnHold()
                                        ))
                        )
                );
            }

            if (permissionFilter.hasPermission(Permission.ORGANIZATION_VIEW_IN_LIST_IF_CO_RP_CLIENT_CTM)) {
                var employees = permissionFilter.getEmployees(Permission.ORGANIZATION_VIEW_IN_LIST_IF_CO_RP_CLIENT_CTM);

                var subQuery = criteriaQuery.subquery(Long.class);
                var clientRoot = subQuery.from(Client.class);

                predicates.add(
                        criteriaBuilder.in(root.get(Organization_.id)).value(
                                subQuery.select(clientRoot.get(Client_.organizationId))
                                        .where(securityPredicateGenerator.clientsInClientCareTeamPredicate(
                                                criteriaBuilder,
                                                criteriaQuery,
                                                clientRoot,
                                                employees,
                                                AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                                                HieConsentCareTeamType.currentAndOnHold()
                                        ))
                        )
                );
            }

            if (permissionFilter.hasPermission(Permission.COMMUNITY_VIEW_IN_LIST_IF_EXTERNAL_REFERRAL_REQUEST)) {
                var employees = permissionFilter.getEmployees(Permission.COMMUNITY_VIEW_IN_LIST_IF_EXTERNAL_REFERRAL_REQUEST);

                var subQuery = criteriaQuery.subquery(Long.class);
                var communityRoot = subQuery.from(Community.class);

                predicates.add(criteriaBuilder.in(root.get(Organization_.id)).value(subQuery.select(communityRoot.get(Community_.organizationId))
                        .where(externalEmployeeInboundReferralCommunityPredicateGenerator
                                .communityIdsInReferralSharedCommunities(communityRoot.get(Community_.id), criteriaQuery, employees)))
                );
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<Organization> byId(Long organizationId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Organization_.id), organizationId);
    }

    public Specification<Organization> byIds(Collection<Long> organizationIds) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.in(
                root.get(Organization_.ID)).value(organizationIds);
    }

    public Specification<Organization> byAssociatedEmployeeOrganizations(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var employeeOrganizationIds = SpecificationUtils.employeesOrganizationIds(permissionFilter.getEmployees());
            if (permissionFilter.hasPermission(Permission.ROLE_SUPER_ADMINISTRATOR)) {
                return criteriaBuilder.and();
            }
            return criteriaBuilder.in(root.get(Organization_.ID)).value(employeeOrganizationIds);
        };
    }

    public Specification<Organization> affiliatedOrganizations(Long organizationId) {
        return (root, query, criteriaBuilder) -> {
            var subquery = query.subquery(Long.class);
            var affFrom = subquery.from(AffiliatedRelationship.class);

            subquery = subquery.select(affFrom.get(AffiliatedRelationship_.affiliatedOrganizationId))
                    .where(criteriaBuilder.equal(affFrom.get(AffiliatedRelationship_.primaryOrganizationId), organizationId));

            return root.get(Organization_.id).in(subquery);
        };
    }

    public Specification<Organization> excludeExternal() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var systemSetupJoin = root.join(Organization_.systemSetup, JoinType.LEFT);
            return criteriaBuilder.or(criteriaBuilder.equal(systemSetupJoin.get(SystemSetup_.loginCompanyId), CareCoordinationConstants.EXTERNAL_COMPANY_ID).not(),
                    systemSetupJoin.get(SystemSetup_.loginCompanyId).isNull());
        };
    }

    public Specification<Organization> allowedReferralMarketplaceOrganizations(PermissionFilter permissionFilter, Community marketplaceCommunity) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var communityQuery = criteriaQuery.subquery(Long.class);
            var communityRoot = communityQuery.from(Community.class);
            communityQuery.select(communityRoot.get(Community_.organizationId));
            communityQuery.where(referralPredicateGenerator.communitiesInMarketplaceAllowedCommunities(permissionFilter, marketplaceCommunity, communityRoot, criteriaQuery, criteriaBuilder));

            return root.get(Organization_.id).in(communityQuery);
        };
    }

    public Specification<Organization> withEnabledChat(boolean withChat) {
        return (root, criteriaQuery, criteriaBuilder) -> organizationPredicateGenerator.withEnabledChat(criteriaBuilder, root, withChat);
    }

    public Specification<Organization> byAccessibleChatFilter(PermissionFilter permissionFilter, ConversationParticipantAccessibilityFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getWithAccessibleClients() || filter.getWithAccessibleNonAssociatedClients()) {
                var subQuery = criteriaQuery.subquery(Integer.class);
                var subRoot = subQuery.from(Client.class);
                subQuery.select(criteriaBuilder.literal(1));

                var whereClause = new ArrayList<Predicate>();
                whereClause.add(criteriaBuilder.equal(root.get(Organization_.id), subRoot.get(Client_.organizationId)));
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
                predicates.add(criteriaBuilder.exists(subQuery));
            }

            if (filter.getWithAccessibleCommunityCareTeamMembers()) {
                var subQuery = criteriaQuery.subquery(Integer.class);
                var subRoot = subQuery.from(CommunityCareTeamMember.class);
                subQuery.select(criteriaBuilder.literal(1));
                subQuery.where(
                        criteriaBuilder.equal(
                                root.get(Organization_.id),
                                subRoot.get(CommunityCareTeamMember_.community).get(Community_.organizationId)
                        ),
                        communityCareTeamMemberPredicateGenerator.chatAccessible(permissionFilter,
                                filter.getExcludedEmployeeId(), subRoot, subQuery, criteriaBuilder)
                );
                predicates.add(criteriaBuilder.exists(subQuery));
            }

            if (filter.getWithAccessibleContacts()) {
                var subQuery = criteriaQuery.subquery(Integer.class);
                var subRoot = subQuery.from(Employee.class);
                subQuery.select(criteriaBuilder.literal(1));

                var whereClause = new ArrayList<Predicate>();
                whereClause.add(criteriaBuilder.equal(root.get(Organization_.id), subRoot.get(Employee_.organizationId)));
                whereClause.add(employeePredicateGenerator.chatAccessibleEmployeesByOrganizationIds(permissionFilter,
                        filter.getExcludedEmployeeId(), null, subRoot, subQuery, criteriaBuilder));

                if (filter.getWithExcludedOneToOneParticipants()) {
                    whereClause.add(employeePredicateGenerator
                            .excludeParticipatingInOneToOneChatWithAny(
                                    permissionFilter.getAllEmployeeIds(), subRoot.get(Employee_.id), subQuery, criteriaBuilder));
                }

                subQuery.where(whereClause.toArray(new Predicate[0]));
                predicates.add(criteriaBuilder.exists(subQuery));
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<Organization> byAlternativeId(String alternativeId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Organization_.alternativeId), alternativeId);
    }

    //goal is to exclude communities, where all visible communities have Cloud enabled only
    public Specification<Organization> hasEligibleForDiscoveryOrNoVisibleCommunities() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var idPath = root.get(Organization_.id);
            return criteriaBuilder.or(
                    organizationPredicateGenerator.hasEligibleForDiscoveryCommunities(
                            idPath, criteriaQuery, criteriaBuilder
                    ),
                    organizationPredicateGenerator.hasVisibleCommunities(
                            idPath, criteriaQuery, criteriaBuilder
                    ).not()
            );
        };
    }

    public Specification<Organization> hasEligibleForDiscoveryCommunities() {
        return (root, criteriaQuery, criteriaBuilder) ->
                organizationPredicateGenerator.hasEligibleForDiscoveryCommunities(
                        root.get(Organization_.id), criteriaQuery, criteriaBuilder);
    }

    public Specification<Organization> withESignEnabled(Boolean eSignEnabled) {
        return (root, criteriaQuery, criteriaBuilder) -> organizationPredicateGenerator.withEsignEnabled(criteriaBuilder, root, eSignEnabled);
    }

    public Specification<Organization> hasClientsAvailableForSignatureRequest(PermissionFilter permissionFilter) {
        return (root, query, criteriaBuilder) -> {
            var subQuery = query.subquery(Long.class);
            var subQueryRoot = subQuery.from(Client.class);
            subQuery.select(subQueryRoot.get(Client_.id));
            subQuery.where(
                    criteriaBuilder.and(
                            clientPredicateGenerator.hasPermissionToRequestSignatureFrom(permissionFilter, subQueryRoot, query, criteriaBuilder),
                            criteriaBuilder.equal(subQueryRoot.get(Client_.organizationId), root.get(Organization_.id))
                    )
            );

            return criteriaBuilder.exists(subQuery);
        };
    }

    public Specification<Organization> withEnabledAppointments() {
        return (root, criteriaQuery, criteriaBuilder) -> organizationPredicateGenerator.withEnabledAppointments(criteriaBuilder, root, true);
    }
}
