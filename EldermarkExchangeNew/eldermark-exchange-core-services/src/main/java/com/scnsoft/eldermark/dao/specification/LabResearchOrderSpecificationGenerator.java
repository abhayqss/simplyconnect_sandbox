package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.LabResearchOrderFilter;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.*;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole_;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.entity.lab.LabResearchOrder;
import com.scnsoft.eldermark.entity.lab.LabResearchOrderORU_;
import com.scnsoft.eldermark.entity.lab.LabResearchOrderStatus;
import com.scnsoft.eldermark.entity.lab.LabResearchOrder_;
import com.scnsoft.eldermark.entity.security.CareTeamRolePermissionMapping;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.entity.security.PermissionPredicates;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Component
public class LabResearchOrderSpecificationGenerator {

    @Autowired
    private OrganizationPredicateGenerator organizationPredicateGenerator;

    @Autowired
    private CommunityPredicateGenerator communityPredicateGenerator;

    @Autowired
    private CommunityCareTeamMemberPredicateGenerator communityCareTeamMemberPredicateGenerator;

    @Autowired
    private ClientCareTeamMemberPredicateGenerator clientCareTeamMemberPredicateGenerator;

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    public Specification<LabResearchOrder> byFilter(LabResearchOrderFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            var joinClient = JpaUtils.getOrCreateJoin(root, LabResearchOrder_.client);

            predicates.add(criteriaBuilder.equal(joinClient.get(Client_.organizationId), filter.getOrganizationId()));

            if (CollectionUtils.isNotEmpty(filter.getCommunityIds())) {
                predicates.add(joinClient.get(Client_.communityId).in(filter.getCommunityIds()));
            }

            if (CollectionUtils.isNotEmpty(filter.getStatuses())) {
                predicates.add(root.get(LabResearchOrder_.status).in(filter.getStatuses()));
            }

            if (CollectionUtils.isNotEmpty(filter.getReasons())) {
                predicates.add(root.get(LabResearchOrder_.reason).in(filter.getReasons()));
            }

            if (filter.getClientId() != null) {
                predicates.add(criteriaBuilder.equal(joinClient.get(Client_.id), filter.getClientId()));
            }

            if (StringUtils.isNotEmpty(filter.getRequisitionNumber())) {
                predicates.add(criteriaBuilder.like(
                        root.get(LabResearchOrder_.requisitionNumber),
                        SpecificationUtils.wrapWithWildcards(filter.getRequisitionNumber()))
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    public Specification<LabResearchOrder> hasAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> hasAccess(permissionFilter, root, criteriaQuery, criteriaBuilder);
    }

    public Specification<Employee> labOrderReviewers(LabResearchOrder labResearchOrder) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var requiredPredicates = new ArrayList<Predicate>();
            var predicates = new ArrayList<Predicate>();
            var systemRole = root.get(Employee_.careTeamRole).get(CareTeamRole_.CODE);
            requiredPredicates.add(criteriaBuilder.equal(root.get(Employee_.labsCoordinator), Boolean.TRUE));
            requiredPredicates.add(criteriaBuilder.equal(root.get(Employee_.organizationId), labResearchOrder.getClient().getOrganizationId()));
            requiredPredicates.add(criteriaBuilder.equal(root.get(Employee_.status), EmployeeStatus.ACTIVE));
            requiredPredicates.add(communityPredicateGenerator.eligibleForDiscovery(
                    criteriaBuilder,
                    JpaUtils.getOrCreateJoin(root, Employee_.community)
            ));
            var isClientOptedIn = labResearchOrder.getClient().getHieConsentPolicyType() == HieConsentPolicyType.OPT_IN;

            var superAdminRoles = CareTeamRolePermissionMapping.findCareTeamRoleCodesWithPermission(LAB_REVIEW_ALL_EXCEPT_OPTED_OUT_IF_COORDINATOR);
            if (isClientOptedIn) {
                predicates.add(systemRole.in(superAdminRoles));
            }

            var associatedOrgRoles = CareTeamRolePermissionMapping.findCareTeamRoleCodesWithPermission(LAB_REVIEW_IF_ASSOCIATED_ORGANIZATION_AND_COORDINATOR);
            predicates.add(systemRole.in(associatedOrgRoles));

            var associatedComm = CareTeamRolePermissionMapping.findCareTeamRoleCodesWithPermission(
                    LAB_REVIEW_IF_ASSOCIATED_COMMUNITY_AND_COORDINATOR
            );
            predicates.add(criteriaBuilder.and(
                    criteriaBuilder.in(systemRole).value(associatedComm),
                    criteriaBuilder.equal(
                            root.get(Employee_.communityId),
                            labResearchOrder.getClient().getCommunityId()
                    )
            ));

            var regularCommunityCTMs = CareTeamRolePermissionMapping.findCareTeamRoleCodesWithPermission(
                    LAB_REVIEW_IF_CURRENT_REGULAR_COMMUNITY_CTM_AND_COORDINATOR
            );
            predicates.add(criteriaBuilder.and(
                    criteriaBuilder.in(systemRole).value(regularCommunityCTMs),
                    communityCareTeamMemberPredicateGenerator.isCommunityCareTeamMember(
                            criteriaBuilder,
                            criteriaQuery,
                            root,
                            labResearchOrder.getClient().getCommunityId(),
                            AffiliatedCareTeamType.REGULAR,
                            HieConsentCareTeamType.currentWithOptimizations(labResearchOrder.getClient())
                    )
            ));

            var regularClientCTMs = CareTeamRolePermissionMapping.findCareTeamRoleCodesWithPermission(
                    LAB_REVIEW_IF_CURRENT_REGULAR_CLIENT_CTM_AND_COORDINATOR
            );
            predicates.add(criteriaBuilder.and(
                    criteriaBuilder.in(systemRole).value(regularClientCTMs),
                    clientCareTeamMemberPredicateGenerator.isClientCareTeamMember(
                            criteriaBuilder,
                            criteriaQuery,
                            root,
                            labResearchOrder.getClient(),
                            AffiliatedCareTeamType.REGULAR,
                            HieConsentCareTeamType.currentWithOptimizations(labResearchOrder.getClient())
                    )
            ));

            var clientCreatedBySelf = CareTeamRolePermissionMapping.findCareTeamRoleCodesWithPermission(
                    LAB_REVIEW_IF_OPTED_IN_CLIENT_ADDED_BY_SELF_AND_COORDINATOR
            );
            if (isClientOptedIn) {
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.in(systemRole).value(clientCreatedBySelf),
                        securityPredicateGenerator.isClientCreatedBySelf(
                                criteriaBuilder,
                                criteriaQuery,
                                root,
                                labResearchOrder.getClient()
                        )
                ));
            }

            return criteriaBuilder.and(
                    criteriaBuilder.and(requiredPredicates.toArray(new Predicate[0])),
                    criteriaBuilder.or(predicates.toArray(new Predicate[0]))
            );
        };
    }

    private Predicate hasAccess(PermissionFilter permissionFilter, Root<LabResearchOrder> root,
                                CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        var accessibleStatues = accessibleStatuses(permissionFilter);

        if (accessibleStatues.isEmpty()) {
            return criteriaBuilder.or();
        }

        var accessibleLabStatus = accessibleStatues.size() == LabResearchOrderStatus.values().length ?
                criteriaBuilder.and() :
                SpecificationUtils.in(criteriaBuilder, root.get(LabResearchOrder_.status), accessibleStatues);

        var clientJoin = JpaUtils.getOrCreateJoin(root, LabResearchOrder_.client);

        var eligible = securityPredicateGenerator.clientInEligibleForDiscoveryCommunity(
                clientJoin, criteriaBuilder
        );

        var hasAccessByClientPredicates = new ArrayList<Predicate>();

        var labViewAllExceptOptedOut = EnumSet.of(
                LAB_VIEW_SENT_ALL_EXCEPT_OPTED_OUT,
                LAB_VIEW_PENDING_ALL_EXCEPT_OPTED_OUT_IF_COORDINATOR,
                LAB_PARTLY_VIEW_PENDING_ALL_EXCEPT_OPTED_OUT,
                LAB_VIEW_REVIEWED_ALL_EXCEPT_OPTED_OUT
        );
        if (permissionFilter.hasAnyPermission(labViewAllExceptOptedOut)) {
            hasAccessByClientPredicates.add(criteriaBuilder.and(
                    eligible,
                    accessibleLabStatus,
                    clientPredicateGenerator.isOptedIn(clientJoin, criteriaBuilder)
            ));
        }

        var labViewIfAssociatedOrganization = EnumSet.of(
                LAB_VIEW_SENT_IF_ASSOCIATED_ORGANIZATION,
                LAB_VIEW_PENDING_IF_ASSOCIATED_ORGANIZATION_AND_COORDINATOR,
                LAB_PARTLY_VIEW_PENDING_IF_ASSOCIATED_ORGANIZATION,
                LAB_VIEW_REVIEWED_IF_ASSOCIATED_ORGANIZATION
        );
        if (permissionFilter.hasAnyPermission(labViewIfAssociatedOrganization)) {
            var employees = permissionFilter.getEmployeesWithAny(labViewIfAssociatedOrganization);
            var employeeOrganizationIds = SpecificationUtils.employeesOrganizationIds(employees);
            hasAccessByClientPredicates.add(clientJoin.get(Client_.organizationId).in(employeeOrganizationIds));
        }

        var labViewIfAssociatedCommunity = EnumSet.of(
                LAB_VIEW_SENT_IF_ASSOCIATED_COMMUNITY,
                LAB_VIEW_PENDING_IF_ASSOCIATED_COMMUNITY_AND_COORDINATOR,
                LAB_PARTLY_VIEW_PENDING_IF_ASSOCIATED_COMMUNITY,
                LAB_VIEW_REVIEWED_IF_ASSOCIATED_COMMUNITY
        );
        if (permissionFilter.hasAnyPermission(labViewIfAssociatedCommunity)) {
            var employees = permissionFilter.getEmployeesWithAny(labViewIfAssociatedCommunity);
            var employeeCommunityIds = SpecificationUtils.employeesCommunityIds(employees);
            hasAccessByClientPredicates.add(clientJoin.get(Client_.communityId).in(employeeCommunityIds));
        }

        var labViewIfRegularCommunityCtm = EnumSet.of(
                LAB_VIEW_SENT_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                LAB_VIEW_PENDING_IF_CURRENT_REGULAR_COMMUNITY_CTM_AND_COORDINATOR,
                LAB_PARTLY_VIEW_PENDING_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                LAB_VIEW_REVIEWED_IF_CURRENT_REGULAR_COMMUNITY_CTM
        );
        if (permissionFilter.hasAnyPermission(labViewIfRegularCommunityCtm)) {
            var employees = permissionFilter.getEmployeesWithAny(labViewIfRegularCommunityCtm);

            hasAccessByClientPredicates.add(securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(
                    criteriaBuilder,
                    criteriaQuery,
                    clientJoin.get(Client_.communityId),
                    employees,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.current(clientJoin)
            ));
        }

        var labViewIfRegularClientCtm = EnumSet.of(
                LAB_VIEW_SENT_IF_CURRENT_REGULAR_CLIENT_CTM,
                LAB_VIEW_PENDING_IF_CURRENT_REGULAR_CLIENT_CTM_AND_COORDINATOR,
                LAB_PARTLY_VIEW_PENDING_IF_CURRENT_REGULAR_CLIENT_CTM,
                LAB_VIEW_REVIEWED_IF_CURRENT_REGULAR_CLIENT_CTM
        );
        if (permissionFilter.hasAnyPermission(labViewIfRegularClientCtm)) {
            var employees = permissionFilter.getEmployeesWithAny(labViewIfRegularClientCtm);

            hasAccessByClientPredicates.add(securityPredicateGenerator.clientsInClientCareTeamPredicate(
                    criteriaBuilder,
                    criteriaQuery,
                    clientJoin,
                    employees,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.current(clientJoin)
            ));
        }

        var labViewIfAddedBySelf = EnumSet.of(
                LAB_VIEW_SENT_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                LAB_VIEW_PENDING_IF_OPTED_IN_CLIENT_ADDED_BY_SELF_AND_COORDINATOR,
                LAB_PARTLY_VIEW_PENDING_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                LAB_VIEW_REVIEWED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF
        );
        if (permissionFilter.hasAnyPermission(labViewIfAddedBySelf)) {
            var employees = permissionFilter.getEmployeesWithAny(labViewIfAddedBySelf);
            hasAccessByClientPredicates.add(
                    criteriaBuilder.and(
                            securityPredicateGenerator.clientAddedByEmployees(criteriaBuilder, clientJoin, employees),
                            clientPredicateGenerator.isOptedIn(clientJoin, criteriaBuilder)
                    )
            );
        }

        if (permissionFilter.hasPermission(Permission.LAB_VIEW_REVIEWED_IF_SELF_RECORD)) {
            var employees = permissionFilter.getEmployees(Permission.LAB_VIEW_REVIEWED_IF_SELF_RECORD);
            hasAccessByClientPredicates.add(securityPredicateGenerator.selfRecordClients(
                    criteriaBuilder, clientJoin.get(Client_.id), employees));
        }

        return criteriaBuilder.and(
                eligible,
                accessibleLabStatus,
                criteriaBuilder.or(hasAccessByClientPredicates.toArray(new Predicate[0]))
        );
    }

    private Set<LabResearchOrderStatus> accessibleStatuses(PermissionFilter permissionFilter) {
        boolean hasViewSentPermission = permissionFilter.hasAnyPermission(EnumSet.of(
                LAB_VIEW_SENT_ALL_EXCEPT_OPTED_OUT,
                LAB_VIEW_SENT_IF_ASSOCIATED_ORGANIZATION,
                LAB_VIEW_SENT_IF_ASSOCIATED_COMMUNITY,
                LAB_VIEW_SENT_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                LAB_VIEW_SENT_IF_CURRENT_REGULAR_CLIENT_CTM,
                LAB_VIEW_SENT_IF_OPTED_IN_CLIENT_ADDED_BY_SELF));
        boolean hasViewPendingCoordinatorPermission = permissionFilter.hasAnyPermission(EnumSet.of(
                LAB_VIEW_PENDING_ALL_EXCEPT_OPTED_OUT_IF_COORDINATOR,
                LAB_VIEW_PENDING_IF_ASSOCIATED_ORGANIZATION_AND_COORDINATOR,
                LAB_VIEW_PENDING_IF_ASSOCIATED_COMMUNITY_AND_COORDINATOR,
                LAB_VIEW_PENDING_IF_CURRENT_REGULAR_COMMUNITY_CTM_AND_COORDINATOR,
                LAB_VIEW_PENDING_IF_CURRENT_REGULAR_CLIENT_CTM_AND_COORDINATOR,
                LAB_VIEW_PENDING_IF_OPTED_IN_CLIENT_ADDED_BY_SELF_AND_COORDINATOR));
        boolean hasPartlyViewPendingPermission = permissionFilter.hasAnyPermission(EnumSet.of(
                LAB_PARTLY_VIEW_PENDING_ALL_EXCEPT_OPTED_OUT,
                LAB_PARTLY_VIEW_PENDING_IF_ASSOCIATED_ORGANIZATION,
                LAB_PARTLY_VIEW_PENDING_IF_ASSOCIATED_COMMUNITY,
                LAB_PARTLY_VIEW_PENDING_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                LAB_PARTLY_VIEW_PENDING_IF_CURRENT_REGULAR_CLIENT_CTM,
                LAB_PARTLY_VIEW_PENDING_IF_OPTED_IN_CLIENT_ADDED_BY_SELF));
        boolean hasViewReviewedPermission = permissionFilter.hasAnyPermission(EnumSet.of(
                LAB_VIEW_REVIEWED_ALL_EXCEPT_OPTED_OUT,
                LAB_VIEW_REVIEWED_IF_ASSOCIATED_ORGANIZATION,
                LAB_VIEW_REVIEWED_IF_ASSOCIATED_COMMUNITY,
                LAB_VIEW_REVIEWED_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                LAB_VIEW_REVIEWED_IF_CURRENT_REGULAR_CLIENT_CTM,
                LAB_VIEW_REVIEWED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                LAB_VIEW_REVIEWED_IF_SELF_RECORD));

        var accessibleStatues = new HashSet<LabResearchOrderStatus>(LabResearchOrderStatus.values().length);
        if (hasViewSentPermission) {
            accessibleStatues.add(LabResearchOrderStatus.SENT_TO_LAB);
        }

        if (hasViewPendingCoordinatorPermission || hasPartlyViewPendingPermission) {
            accessibleStatues.add(LabResearchOrderStatus.PENDING_REVIEW);
        }

        if (hasViewReviewedPermission) {
            accessibleStatues.add(LabResearchOrderStatus.REVIEWED);
        }

        return accessibleStatues;
    }

    public Specification<Organization> accessibleLabsOrganizations(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var organizationIdPath = root.get(Organization_.id);
            var eligible = criteriaBuilder.or(
                    organizationPredicateGenerator.hasEligibleForDiscoveryCommunities(
                            organizationIdPath,
                            criteriaQuery,
                            criteriaBuilder
                    ),
                    organizationPredicateGenerator.hasVisibleCommunities(
                            organizationIdPath,
                            criteriaQuery,
                            criteriaBuilder
                    ).not()
            );

            var labViewAll = EnumSet.of(
                    LAB_VIEW_SENT_ALL_EXCEPT_OPTED_OUT,
                    LAB_VIEW_PENDING_ALL_EXCEPT_OPTED_OUT_IF_COORDINATOR,
                    LAB_PARTLY_VIEW_PENDING_ALL_EXCEPT_OPTED_OUT,
                    LAB_VIEW_REVIEWED_ALL_EXCEPT_OPTED_OUT
            );
            if (permissionFilter.hasAnyPermission(labViewAll)) {
                return criteriaBuilder.and(
                        eligible,
                        criteriaBuilder.equal(root.get(Organization_.labsEnabled), Boolean.TRUE)
                );
            }

            var predicates = new ArrayList<Predicate>();

            var labViewIfAssociatedOrganization = EnumSet.of(
                    LAB_VIEW_SENT_IF_ASSOCIATED_ORGANIZATION,
                    LAB_VIEW_PENDING_IF_ASSOCIATED_ORGANIZATION_AND_COORDINATOR,
                    LAB_PARTLY_VIEW_PENDING_IF_ASSOCIATED_ORGANIZATION,
                    LAB_VIEW_REVIEWED_IF_ASSOCIATED_ORGANIZATION
            );
            if (permissionFilter.hasAnyPermission(labViewIfAssociatedOrganization)) {
                var employees = permissionFilter.getEmployeesWithAny(labViewIfAssociatedOrganization);
                var employeeOrganizationIds = SpecificationUtils.employeesOrganizationIds(employees);
                predicates.add(organizationIdPath.in(employeeOrganizationIds));
            }

            var labViewIfAssociatedCommunity = EnumSet.of(
                    LAB_VIEW_SENT_IF_ASSOCIATED_COMMUNITY,
                    LAB_VIEW_PENDING_IF_ASSOCIATED_COMMUNITY_AND_COORDINATOR,
                    LAB_PARTLY_VIEW_PENDING_IF_ASSOCIATED_COMMUNITY,
                    LAB_VIEW_REVIEWED_IF_ASSOCIATED_COMMUNITY
            );
            if (permissionFilter.hasAnyPermission(labViewIfAssociatedCommunity)) {
                var employees = permissionFilter.getEmployeesWithAny(labViewIfAssociatedCommunity);
                var employeeCommunityIds = SpecificationUtils.employeesCommunityIds(employees);
                var communitiesOrganizationIdsSubquery = communityOrganizationIdsSubquery(criteriaQuery, communityRoot -> criteriaBuilder.and(communityRoot.get(Community_.id).in(employeeCommunityIds),
                        communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, communityRoot)));
                predicates.add(organizationIdPath.in(communitiesOrganizationIdsSubquery));
            }

            var labViewIfRegularCommunityCtm = EnumSet.of(
                    LAB_VIEW_SENT_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                    LAB_VIEW_PENDING_IF_CURRENT_REGULAR_COMMUNITY_CTM_AND_COORDINATOR,
                    LAB_PARTLY_VIEW_PENDING_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                    LAB_VIEW_REVIEWED_IF_CURRENT_REGULAR_COMMUNITY_CTM
            );
            if (permissionFilter.hasAnyPermission(labViewIfRegularCommunityCtm)) {
                var employees = permissionFilter.getEmployeesWithAny(labViewIfRegularCommunityCtm);
                var communitiesOrganizationIdsSubquery = communityOrganizationIdsSubquery(
                        criteriaQuery,
                        communityRoot -> criteriaBuilder.and(
                                securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(criteriaBuilder,
                                        criteriaQuery, communityRoot.get(Community_.id), employees, AffiliatedCareTeamType.REGULAR),
                                communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, communityRoot)
                        )
                );
                predicates.add(root.get(Organization_.id).in(communitiesOrganizationIdsSubquery));
            }

            var labViewIfRegularClientCtm = EnumSet.of(
                    LAB_VIEW_SENT_IF_CURRENT_REGULAR_CLIENT_CTM,
                    LAB_VIEW_PENDING_IF_CURRENT_REGULAR_CLIENT_CTM_AND_COORDINATOR,
                    LAB_PARTLY_VIEW_PENDING_IF_CURRENT_REGULAR_CLIENT_CTM,
                    LAB_VIEW_REVIEWED_IF_CURRENT_REGULAR_CLIENT_CTM
            );
            if (permissionFilter.hasAnyPermission(labViewIfRegularClientCtm)) {
                var employees = permissionFilter.getEmployeesWithAny(labViewIfRegularClientCtm);
                var clientsOrganizationIdsSubquery = clientOrganizationIdsSubquery(
                        criteriaQuery,
                        clientRoot -> securityPredicateGenerator.clientsInClientCareTeamPredicate(
                                criteriaBuilder, criteriaQuery, clientRoot, employees,
                                AffiliatedCareTeamType.REGULAR));
                predicates.add(organizationIdPath.in(clientsOrganizationIdsSubquery));
            }

            var labViewIfAddedBySelf = EnumSet.of(
                    LAB_VIEW_SENT_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                    LAB_VIEW_PENDING_IF_OPTED_IN_CLIENT_ADDED_BY_SELF_AND_COORDINATOR,
                    LAB_PARTLY_VIEW_PENDING_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                    LAB_VIEW_REVIEWED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF
            );
            if (permissionFilter.hasAnyPermission(labViewIfAddedBySelf)) {
                var employees = permissionFilter.getEmployeesWithAny(labViewIfAddedBySelf);
                var clientsOrganizationIdsSubquery = clientOrganizationIdsSubquery(criteriaQuery, clientRoot -> securityPredicateGenerator.clientAddedByEmployees(criteriaBuilder, clientRoot, employees));
                predicates.add(organizationIdPath.in(clientsOrganizationIdsSubquery));
            }

            if (permissionFilter.hasPermission(Permission.LAB_VIEW_REVIEWED_IF_SELF_RECORD)) {
                var employees = permissionFilter.getEmployees(Permission.LAB_VIEW_REVIEWED_IF_SELF_RECORD);
                var clientsOrganizationIdsSubquery = clientOrganizationIdsSubquery(criteriaQuery,
                        clientRoot -> securityPredicateGenerator.selfRecordClients(criteriaBuilder,
                                clientRoot.get(Client_.id), employees));
                predicates.add(organizationIdPath.in(clientsOrganizationIdsSubquery));
            }

            //Also allow labs in case user is coordinator in his own organization.
            //It handles case when, for example, there is no care team set up for some user so there are no
            //accessible labs, but he can still see his organization in Labs menu so that he can add new LAB from UI.
            var loggedCoordinatorEmployeeOrganizationIds = SpecificationUtils.employeesOrganizationIds(
                    permissionFilter.getEmployees(PermissionPredicates.LABS_COORDINATOR)
            );
            if (CollectionUtils.isNotEmpty(loggedCoordinatorEmployeeOrganizationIds)) {
                predicates.add(SpecificationUtils.in(
                        criteriaBuilder,
                        organizationIdPath,
                        loggedCoordinatorEmployeeOrganizationIds));
            }

            return criteriaBuilder.and(
                    eligible,
                    criteriaBuilder.equal(root.get(Organization_.labsEnabled), Boolean.TRUE),
                    criteriaBuilder.or(predicates.toArray(new Predicate[0]))
            );
        };
    }

    private Subquery<Long> communityOrganizationIdsSubquery(CriteriaQuery<?> criteriaQuery, Function<Root<Community>, Predicate> whereClause) {
        var communitiesSubquery = criteriaQuery.subquery(Long.class);
        var subqueryRoot = communitiesSubquery.from(Community.class);
        return communitiesSubquery.select(subqueryRoot.get(Community_.organizationId)).where(whereClause.apply(subqueryRoot));
    }

    private Subquery<Long> clientOrganizationIdsSubquery(CriteriaQuery<?> criteriaQuery, Function<Root<Client>, Predicate> whereClause) {
        var communitiesSubquery = criteriaQuery.subquery(Long.class);
        var subqueryRoot = communitiesSubquery.from(Client.class);
        return communitiesSubquery.select(subqueryRoot.get(Client_.organizationId)).where(whereClause.apply(subqueryRoot));
    }

    public Specification<LabResearchOrder> isCovid19() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isTrue(root.get(LabResearchOrder_.isCovid19));
    }

    public Specification<LabResearchOrder> betweenSpecimenDates(Instant from, Instant to) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var orderSpecimenDate = root.get(LabResearchOrder_.specimenDate);
            return criteriaBuilder.and(
                    from == null ? criteriaBuilder.or() : criteriaBuilder.greaterThanOrEqualTo(orderSpecimenDate, from),
                    to == null ? criteriaBuilder.or() : criteriaBuilder.lessThanOrEqualTo(orderSpecimenDate, to)
            );
        };
    }

    public Specification<LabResearchOrder> isSuccessTestResult() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isTrue(root.join(LabResearchOrder_.orderORU).get(LabResearchOrderORU_.success));
    }

    public <T extends IdAware> Specification<LabResearchOrder> byCommunities(List<T> communities) {
        Set<Long> communityIds = CareCoordinationUtils.toIdsSet(communities);
        return (root, criteriaQuery, criteriaBuilder) -> root.join(LabResearchOrder_.client).get(Client_.communityId).in(communityIds);
    }
}
