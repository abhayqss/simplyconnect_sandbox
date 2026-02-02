package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.specification.SpecificationUtils;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole_;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.CareCoordinationConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class CommunityPredicateGenerator {

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    @Autowired
    private ExternalEmployeeInboundReferralCommunityPredicateGenerator externalEmployeeInboundReferralCommunityPredicateGenerator;

    public Predicate eligibleForDiscovery(CriteriaBuilder cb, Path<Community> fromFacility) {
        return eligibleForDiscovery(cb, fromFacility, false);
    }

    public Predicate eligibleForDiscovery(CriteriaBuilder cb, Path<Community> fromFacility, Boolean includeInactive) {
        var isVisible = isVisible(cb, fromFacility, includeInactive);
        Path<Boolean> eligibleForExchange = fromFacility.get(Community_.moduleHie);
        return cb.and(
                isVisible,
                cb.isTrue(eligibleForExchange)
        );

        //        Path<Boolean> eligibleForCloud = fromFacility.get(Community_.moduleCloudStorage);

        //boolean isEldermarkUser = SecurityUtils.isEldermarkUser() || true; //TODO NEED TO ADD ELDERMARK USER TO CARE COORDINATION USER
        //boolean isCloudUser = SecurityUtils.isCloudUser() || SecurityUtils.isCloudManager();
        /*if (isEldermarkUser && isCloudUser) {
            predicates.add(cb.or(cb.isTrue(eligibleForExchange), cb.isTrue(eligibleForCloud)));
        } else if (isEldermarkUser) {
            predicates.add(cb.isTrue(eligibleForExchange));
        } else if (isCloudUser) {
            predicates.add(cb.isTrue(eligibleForCloud));
        }*/
    }

    public Predicate isVisible(CriteriaBuilder cb, Path<Community> fromFacility) {
        return isVisible(cb, fromFacility, false);
    }

    public Predicate isVisible(CriteriaBuilder cb, Path<Community> fromFacility, Boolean includeInactive) {
        // Community is NOT testing training and NOT inactive
        Path<Boolean> isTestingTraining = fromFacility.get(Community_.testingTraining);
        Predicate isInactive;
        if (Boolean.TRUE.equals(includeInactive)) {
            isInactive = cb.and();
        } else {
            var isInactivePath = fromFacility.get(Community_.inactive);
            isInactive = cb.or(cb.isFalse(isInactivePath), cb.isNull(isInactivePath));
        }

        return cb.and(
                cb.equal(fromFacility.get(Community_.legacyTable),
                        CareCoordinationConstants.COMMUNITY_ELIGIBLE_FOR_DISCOVERY_LEGACY_TABLE),
                isInactive,
                cb.or(cb.isFalse(isTestingTraining), cb.isNull(isTestingTraining))
        );
    }

    /**
     * Returns predicate: primaryCommunityIdPath in primary communities of communities employees are created under.
     *
     * @param criteriaBuilder
     * @param query
     * @param primaryCommunityIdPath
     * @param employees
     * @return
     */
    public Predicate primaryCommunities(CriteriaBuilder criteriaBuilder,
                                        AbstractQuery<?> query,
                                        Path<Long> primaryCommunityIdPath,
                                        Collection<Employee> employees) {
        var subQuery = query.subquery(Long.class);
        var affiliatedRoot = subQuery.from(AffiliatedRelationship.class);

        var affiliatedCommunitiesSelect = subQuery
                .select(affiliatedRoot.get(AffiliatedRelationship_.primaryCommunityId))
                .where(primaryCommunities(criteriaBuilder, affiliatedRoot, employees));


        return criteriaBuilder.in(primaryCommunityIdPath).value(affiliatedCommunitiesSelect);
    }

    public Predicate primaryCommunities(CriteriaBuilder criteriaBuilder,
                                        Root<AffiliatedRelationship> affiliatedRoot,
                                        Collection<Employee> employees) {
        var employeeCommunities = SpecificationUtils.employeesCommunityIds(employees);
        return criteriaBuilder.in(affiliatedRoot.get(AffiliatedRelationship_.AFFILIATED_COMMUNITY_ID)).value(employeeCommunities);
    }

    /**
     * Returns predicate: primaryCommunityIdPath in primary communities of all communities in organizations employees are created under.
     *
     * @param criteriaBuilder
     * @param query
     * @param primaryCommunityIdPath
     * @param employees
     * @return
     */
    public Predicate primaryCommunitiesOfOrganizations(CriteriaBuilder criteriaBuilder,
                                                       AbstractQuery<?> query,
                                                       Path<Long> primaryCommunityIdPath,
                                                       Collection<Employee> employees) {
        var subQuery = query.subquery(Long.class);
        var affiliatedRoot = subQuery.from(AffiliatedRelationship.class);

        var affiliatedCommunitiesSelect = subQuery
                .select(affiliatedRoot.get(AffiliatedRelationship_.primaryCommunityId))
                .where(primaryCommunitiesOfOrganizations(criteriaBuilder, affiliatedRoot, employees));

        return criteriaBuilder.in(primaryCommunityIdPath).value(affiliatedCommunitiesSelect);
    }

    public Predicate primaryCommunitiesOfOrganizations(CriteriaBuilder criteriaBuilder,
                                                       Root<AffiliatedRelationship> affiliatedRoot,
                                                       Collection<Employee> employees) {
        var employeeOrganizations = SpecificationUtils.employeesOrganizationIds(employees);

        return SpecificationUtils.in(criteriaBuilder,
                        affiliatedRoot.get(AffiliatedRelationship_.affiliatedOrganizationId), employeeOrganizations);
    }

    /**
     * Returns predicate: affiliatedCommunityIdPath in affiliated communities of communities employees are created under.
     *
     * @param criteriaBuilder
     * @param query
     * @param affiliatedCommunityIdPath
     * @param employees
     * @return
     */
    public Predicate affiliatedCommunities(CriteriaBuilder criteriaBuilder,
                                           AbstractQuery<?> query,
                                           Path<Long> affiliatedCommunityIdPath,
                                           Collection<Employee> employees) {
        var subQuery = query.subquery(Long.class);
        var affiliatedRoot = subQuery.from(AffiliatedRelationship.class);

        var affiliatedCommunitiesSelect = subQuery
                .select(affiliatedRoot.get(AffiliatedRelationship_.affiliatedCommunityId))
                .where(affiliatedCommunities(criteriaBuilder, affiliatedRoot, employees));


        return criteriaBuilder.in(affiliatedCommunityIdPath).value(affiliatedCommunitiesSelect);
    }

    public Predicate affiliatedCommunities(CriteriaBuilder criteriaBuilder,
                                           Root<AffiliatedRelationship> affiliatedRoot,
                                           Collection<Employee> employees) {
        var employeeCommunities = SpecificationUtils.employeesCommunityIds(employees);
        return SpecificationUtils.in(criteriaBuilder, affiliatedRoot.get(AffiliatedRelationship_.primaryCommunityId), employeeCommunities);
    }


    /**
     * Returns predicate: affiliatedCommunityIdPath in affiliated communities of all communities in organizations employees are created under.
     *
     * @param criteriaBuilder
     * @param query
     * @param affiliatedCommunityIdPath
     * @param employees
     * @return
     */
    public Predicate affiliatedCommunitiesOfOrganizations(CriteriaBuilder criteriaBuilder,
                                                          AbstractQuery<?> query,
                                                          Path<Long> affiliatedCommunityIdPath,
                                                          Collection<Employee> employees) {
        var subQuery = query.subquery(Long.class);
        var affiliatedRoot = subQuery.from(AffiliatedRelationship.class);

        var affiliatedCommunitiesSelect = subQuery
                .select(affiliatedRoot.get(AffiliatedRelationship_.affiliatedCommunityId))
                .where(affiliatedCommunitiesOfOrganizations(criteriaBuilder, affiliatedRoot, employees));

        return criteriaBuilder.in(affiliatedCommunityIdPath).value(affiliatedCommunitiesSelect);
    }

    public Predicate affiliatedCommunitiesOfOrganizations(
            CriteriaBuilder criteriaBuilder,
            Root<AffiliatedRelationship> affiliatedRoot,
            Collection<Employee> employees
    ) {
        var employeeOrganizations = SpecificationUtils.employeesOrganizationIds(employees);
        return SpecificationUtils.in(criteriaBuilder, affiliatedRoot.get(AffiliatedRelationship_.primaryOrganizationId), employeeOrganizations);
    }

    public Predicate hasAccess(PermissionFilter permissionFilter, Path<Community> communityPath, CriteriaBuilder criteriaBuilder, AbstractQuery<?> query) {
        if (permissionFilter.hasPermission(Permission.COMMUNITY_VIEW_IN_LIST_ALL)) {
            return criteriaBuilder.and();
        }

        var predicates = new ArrayList<Predicate>();

        if (permissionFilter.hasPermission(Permission.COMMUNITY_VIEW_IN_LIST_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(Permission.COMMUNITY_VIEW_IN_LIST_IF_ASSOCIATED_ORGANIZATION);
            var employeeOrganizations = employees.stream().map(Employee::getOrganizationId).collect(Collectors.toList());

            predicates.add(criteriaBuilder.in(communityPath.get(Community_.ORGANIZATION_ID)).value(employeeOrganizations));
        }

        if (permissionFilter.hasPermission(Permission.COMMUNITY_VIEW_IN_LIST_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(Permission.COMMUNITY_VIEW_IN_LIST_IF_ASSOCIATED_COMMUNITY);
            var employeeCommunities = SpecificationUtils.employeesCommunityIds(employees);

            predicates.add(criteriaBuilder.in(communityPath.get(Community_.ID)).value(employeeCommunities));
        }

        if (permissionFilter.hasPermission(Permission.COMMUNITY_VIEW_IN_LIST_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(Permission.COMMUNITY_VIEW_IN_LIST_IF_FROM_AFFILIATED_ORGANIZATION);

            predicates.add(securityPredicateGenerator.primaryCommunitiesOfOrganizations(criteriaBuilder, query, communityPath.get(Community_.id), employees));
        }

        if (permissionFilter.hasPermission(Permission.COMMUNITY_VIEW_IN_LIST_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(Permission.COMMUNITY_VIEW_IN_LIST_IF_FROM_AFFILIATED_COMMUNITY);

            predicates.add(securityPredicateGenerator.primaryCommunities(criteriaBuilder, query, communityPath.get(Community_.id), employees));
        }

        if (permissionFilter.hasPermission(Permission.COMMUNITY_VIEW_IN_LIST_IF_CO_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(Permission.COMMUNITY_VIEW_IN_LIST_IF_CO_RP_COMMUNITY_CTM);
            predicates.add(securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(
                    criteriaBuilder,
                    query,
                    communityPath.get(Community_.id),
                    employees,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold()
            ));
        }

        if (permissionFilter.hasPermission(Permission.COMMUNITY_VIEW_IN_LIST_IF_CO_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(Permission.COMMUNITY_VIEW_IN_LIST_IF_CO_RP_CLIENT_CTM);

            predicates.add(securityPredicateGenerator.clientsInClientCareTeamOfCommunityPredicate(
                            criteriaBuilder,
                            query,
                            communityPath.get(Community_.id),
                            employees,
                            AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                            HieConsentCareTeamType.currentAndOnHold()
                    ));
        }

        if (permissionFilter.hasPermission(Permission.COMMUNITY_VIEW_IN_LIST_IF_EXTERNAL_REFERRAL_REQUEST)) {
            var employees = permissionFilter.getEmployees(Permission.COMMUNITY_VIEW_IN_LIST_IF_EXTERNAL_REFERRAL_REQUEST);

            predicates.add(externalEmployeeInboundReferralCommunityPredicateGenerator.communityIdsInReferralSharedCommunities(communityPath.get(Community_.id), query, employees));
        }

        return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
    }

    public Predicate isOrganizationAdminInCommunity(
            From<?, Employee> employee,
            AbstractQuery<?> query,
            CriteriaBuilder criteriaBuilder,
            Long communityId
    ) {
        var subQuery = query.subquery(Long.class);
        var subRoot = subQuery.from(Community.class);
        subQuery.select(subRoot.get(Community_.id));
        subQuery.where(
                criteriaBuilder.and(
                        JpaUtils.getOrCreateJoin(employee, Employee_.careTeamRole).get(CareTeamRole_.code).in(CareTeamRoleCode.ROLE_ADMINISTRATOR),
                        criteriaBuilder.equal(subRoot.get(Community_.organizationId), employee.get(Employee_.organizationId)),
                        criteriaBuilder.equal(subRoot.get(Community_.id), communityId)
                )
        );

        return criteriaBuilder.exists(subQuery);
    }
}
