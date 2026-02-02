package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.specification.SpecificationUtils;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember_;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Component
public class CommunityCareTeamMemberPredicateGenerator extends CareTeamMemberPredicateGenerator<CommunityCareTeamMember> {

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    @Deprecated
    public Predicate isCommunityCareTeamMember(
            CriteriaBuilder criteriaBuilder,
            AbstractQuery<?> query,
            Path<Employee> employeePath,
            Community community,
            AffiliatedCareTeamType type
    ) {
        return isCommunityCareTeamMember(criteriaBuilder, query, employeePath, community.getId(), type);
    }

    @Deprecated
    public Predicate isCommunityCareTeamMember(
            CriteriaBuilder criteriaBuilder,
            AbstractQuery<?> query,
            Path<Employee> employeePath,
            Long communityId,
            AffiliatedCareTeamType type
    ) {
        return isCommunityCareTeamMember(criteriaBuilder, query, employeePath, communityId, type, HieConsentCareTeamType.currentAndOnHold());
    }

    public Predicate isCommunityCareTeamMember(
            CriteriaBuilder criteriaBuilder,
            AbstractQuery<?> query,
            Path<Employee> employeePath,
            Long communityId,
            AffiliatedCareTeamType type,
            HieConsentCareTeamType consentType
    ) {
        if (communityId == null) {
            return criteriaBuilder.or();
        }

        var subQuery = query.subquery(Long.class);
        var root = subQuery.from(CommunityCareTeamMember.class);
        var subCommunity = root.get(CommunityCareTeamMember_.communityId);

        subQuery.select(subCommunity)
                .where(
                        criteriaBuilder.equal(subCommunity, communityId),
                        criteriaBuilder.equal(root.get(CommunityCareTeamMember_.employeeId), employeePath.get(Employee_.id)),
                        ofAffiliationType(root, criteriaBuilder, type),
                        ofConsentType(root, criteriaBuilder, query, consentType)
                );

        return criteriaBuilder.exists(subQuery);
    }

    public Predicate communityIdsInCommunityCareTeamPredicate(CriteriaBuilder criteriaBuilder,
                                                              AbstractQuery<?> query,
                                                              Path<Long> communityIdPath,
                                                              Collection<Employee> employees,
                                                              AffiliatedCareTeamType type,
                                                              HieConsentCareTeamType consentType) {
        if (CollectionUtils.isEmpty(employees)) {
            return criteriaBuilder.or();
        }

        var employeeIds = CareCoordinationUtils.toIdsSet(employees);

        var subQuery = query.subquery(Long.class);
        var root = subQuery.from(CommunityCareTeamMember.class);
        return criteriaBuilder.in(communityIdPath)
                .value(subQuery
                        .select(root.get(CommunityCareTeamMember_.communityId))
                        .where(
                                SpecificationUtils.in(criteriaBuilder, root.get(CareTeamMember_.employeeId), employeeIds),
                                ofAffiliationType(root, criteriaBuilder, type),
                                ofConsentType(root, criteriaBuilder, query, consentType)
                        )
                );
    }

    public Predicate communityIdsInCommunityCareTeamPredicate(CriteriaBuilder criteriaBuilder,
                                                              AbstractQuery<?> query,
                                                              From<?, Client> clientFrom,
                                                              Collection<Employee> employees,
                                                              AffiliatedCareTeamType type,
                                                              HieConsentCareTeamType consentType) {
        if (CollectionUtils.isEmpty(employees)) {
            return criteriaBuilder.or();
        }

        var employeeIds = CareCoordinationUtils.toIdsSet(employees);

        var subQuery = query.subquery(Long.class);
        var root = subQuery.from(CommunityCareTeamMember.class);

        subQuery
                .select(root.get(CommunityCareTeamMember_.communityId))
                .where(
                        SpecificationUtils.in(criteriaBuilder, root.get(CareTeamMember_.employeeId), employeeIds),
                        ofAffiliationType(root, criteriaBuilder, type),
                        ofConsentType(root, criteriaBuilder, query, consentType),
                        criteriaBuilder.equal(clientFrom.get(Client_.communityId), root.get(CommunityCareTeamMember_.communityId))
                );

        return criteriaBuilder.exists(subQuery);
    }

    public Predicate ofConsentType(From<?, CommunityCareTeamMember> root,
                                   CriteriaBuilder criteriaBuilder,
                                   AbstractQuery<?> query,
                                   HieConsentCareTeamType consentType) {
        if (consentType.isIncludesCurrent() && consentType.isIncludesOnHold()) {
            return criteriaBuilder.and();
        }

        if (consentType.isIncludesCurrent()) {
            //exclude on hold

            if (consentType.getOptimizationHints() != null) {
                //no need for additional subqueries in case we already know if specific client is opted out and his organization

                if (consentType.getOptimizationHints().stream()
                        .map(HieConsentCareTeamType.OptimizationHints::getPolicyType)
                        .anyMatch(HieConsentPolicyType.OPT_IN::equals)) {
                    //everyone is current in case client is opted in
                    return criteriaBuilder.and();
                }

                //on hold are community ctm from different organization (i.e. affiliated)
                return JpaUtils.getOrCreateJoin(root, CommunityCareTeamMember_.employee)
                        .get(Employee_.organizationId)
                        .in(
                                consentType.getOptimizationHints().stream()
                                        .map(HieConsentCareTeamType.OptimizationHints::getClientOrganizationId)
                                        .collect(Collectors.toSet())
                        );
            }
            if (consentType.getClientPath() != null) {
                var clientFrom = consentType.getClientPath();
                return criteriaBuilder.and(
                        clientFromCommunityOfCareTeamMember(clientFrom, root, criteriaBuilder),
                        criteriaBuilder.not(isOnHold(clientFrom, root, criteriaBuilder))
                );
            }
            var consentSubquery = query.subquery(Integer.class);
            var clientFrom = consentSubquery.from(Client.class);
            consentSubquery.select(criteriaBuilder.literal(1))
                    .where(
                            clientFromCommunityOfCareTeamMember(clientFrom, root, criteriaBuilder, consentType),
                            criteriaBuilder.not(isOnHold(clientFrom, root, criteriaBuilder))
                    );
            return criteriaBuilder.exists(consentSubquery);
        }

        if (consentType.isIncludesOnHold()) {
            //on hold only

            //todo refactor: think how to reuse includesCurrent logic because onHold should just be negation

            if (consentType.getOptimizationHints() != null) {
                //no need for additional subqueries in case we already know if specific client is opted out and his organization

                if (consentType.getOptimizationHints().stream()
                        .map(HieConsentCareTeamType.OptimizationHints::getPolicyType)
                        .allMatch(HieConsentPolicyType.OPT_IN::equals)) {
                    //nobody is on hold in case client is opted in
                    return criteriaBuilder.or();
                }
                //on hold are community ctm from different organization (i.e. affiliated)
                return JpaUtils.getOrCreateJoin(root, CommunityCareTeamMember_.employee)
                        .get(Employee_.organizationId)
                        .in(
                                consentType.getOptimizationHints().stream()
                                        .map(HieConsentCareTeamType.OptimizationHints::getClientOrganizationId)
                                        .collect(Collectors.toSet())
                        )
                        .not();
            }

            if (consentType.getClientPath() != null) {
                var clientFrom = consentType.getClientPath();
                return criteriaBuilder.and(
                        clientFromCommunityOfCareTeamMember(clientFrom, root, criteriaBuilder),
                        isOnHold(clientFrom, root, criteriaBuilder)
                );
            }

            var consentSubquery = query.subquery(Long.class);
            var clientFrom = consentSubquery.from(Client.class);
            consentSubquery.select(clientFrom.get(Client_.communityId))
                    .where(
                            clientFromCommunityOfCareTeamMember(clientFrom, root, criteriaBuilder, consentType),
                            isOnHold(clientFrom, root, criteriaBuilder)
                    );
            return criteriaBuilder.exists(consentSubquery);
        }

        return criteriaBuilder.or();
    }

    private Predicate clientFromCommunityOfCareTeamMember(Path<Client> clientFrom,
                                                          From<?, CommunityCareTeamMember> cctmFrom,
                                                          CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(cctmFrom.get(CommunityCareTeamMember_.communityId), clientFrom.get(Client_.communityId));
    }

    private Predicate clientFromCommunityOfCareTeamMember(From<?, Client> clientFrom,
                                                          From<?, CommunityCareTeamMember> cctmFrom,
                                                          CriteriaBuilder criteriaBuilder,
                                                          HieConsentCareTeamType consentType) {
        return HieConsentCareTeamType.ANY_TARGET_CLIENT_ID.equals(consentType.getClientId()) ?
                clientFromCommunityOfCareTeamMember(clientFrom, cctmFrom, criteriaBuilder) :
                criteriaBuilder.equal(clientFrom.get(Client_.id), consentType.getClientId());
    }

    public Predicate isOnHold(Path<Client> clientFrom,
                              From<?, CommunityCareTeamMember> cctmFrom,
                              CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
                //opt out is true...
                clientPredicateGenerator.isOptedOut(clientFrom, criteriaBuilder),

                //and from different organization (i.e. affiliated)
                criteriaBuilder.notEqual(
                        JpaUtils.getOrCreateJoin(cctmFrom, CommunityCareTeamMember_.employee).get(Employee_.organizationId),
                        clientFrom.get(Client_.organizationId)
                )
        );
    }

    @Override
    protected Path<Long> getEntityOrganizationId(From<?, CommunityCareTeamMember> careTeamMember) {
        var community = JpaUtils.getOrCreateJoin(careTeamMember, CommunityCareTeamMember_.community);
        return community.get(Community_.organizationId);
    }

    @Override
    public Predicate hasAccess(PermissionFilter permissionFilter, From<?, CommunityCareTeamMember> root, AbstractQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var communityJoin = JpaUtils.getOrCreateJoin(root, CommunityCareTeamMember_.community);
        var eligible = securityPredicateGenerator.eligibleForDiscoveryCommunity(communityJoin, criteriaBuilder);

        if (permissionFilter.hasPermission(COMMUNITY_CARE_TEAM_VIEW_ALL)) {
            return eligible;
        }

        var predicates = new ArrayList<Predicate>();

        if (permissionFilter.hasPermission(COMMUNITY_CARE_TEAM_VIEW_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(COMMUNITY_CARE_TEAM_VIEW_IF_ASSOCIATED_ORGANIZATION);

            predicates.add(SpecificationUtils.in(criteriaBuilder,
                    communityJoin.get(Community_.organizationId),
                    SpecificationUtils.employeesOrganizationIds(employees))
            );
        }

        if (permissionFilter.hasPermission(COMMUNITY_CARE_TEAM_VIEW_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(COMMUNITY_CARE_TEAM_VIEW_IF_ASSOCIATED_COMMUNITY);

            predicates.add(SpecificationUtils.in(criteriaBuilder,
                    root.get(CommunityCareTeamMember_.communityId),
                    SpecificationUtils.employeesCommunityIds(employees))
            );
        }

        if (permissionFilter.hasPermission(COMMUNITY_CARE_TEAM_VIEW_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(COMMUNITY_CARE_TEAM_VIEW_IF_FROM_AFFILIATED_ORGANIZATION);
            predicates.add(securityPredicateGenerator.primaryCommunitiesOfOrganizations(criteriaBuilder, query,
                    root.get(CommunityCareTeamMember_.communityId), employees));
        }

        if (permissionFilter.hasPermission(COMMUNITY_CARE_TEAM_VIEW_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(COMMUNITY_CARE_TEAM_VIEW_IF_FROM_AFFILIATED_COMMUNITY);

            predicates.add(securityPredicateGenerator.primaryCommunities(criteriaBuilder, query,
                    root.get(CommunityCareTeamMember_.communityId), employees));
        }

        if (permissionFilter.hasPermission(COMMUNITY_CARE_TEAM_VIEW_IF_CO_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(COMMUNITY_CARE_TEAM_VIEW_IF_CO_RP_COMMUNITY_CTM);
            predicates.add(criteriaBuilder.and(
                    securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(
                            criteriaBuilder,
                            query,
                            root.get(CommunityCareTeamMember_.communityId),
                            employees,
                            AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                            HieConsentCareTeamType.currentAndOnHold()
                    )
            ));
        }

        if (permissionFilter.hasPermission(COMMUNITY_CARE_TEAM_VIEW_IF_CO_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(COMMUNITY_CARE_TEAM_VIEW_IF_CO_RP_CLIENT_CTM);

            predicates.add(criteriaBuilder.and(
                    securityPredicateGenerator.clientsInClientCareTeamOfCommunityPredicate(
                            criteriaBuilder,
                            query,
                            root.get(CommunityCareTeamMember_.communityId),
                            employees,
                            AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                            HieConsentCareTeamType.currentAndOnHold()
                    )
            ));
        }

        if (permissionFilter.hasPermission(COMMUNITY_CARE_TEAM_VIEW_IF_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(COMMUNITY_CARE_TEAM_VIEW_IF_CLIENT_ADDED_BY_SELF);

            predicates.add(criteriaBuilder.and(
                    securityPredicateGenerator.clientAddedByEmployeesToCommunity(criteriaBuilder, query,
                            root.get(CommunityCareTeamMember_.communityId), employees)
            ));
        }

        if (permissionFilter.hasPermission(COMMUNITY_CARE_TEAM_VIEW_IF_SELF_RECORD)) {
            var employees = permissionFilter.getEmployees(COMMUNITY_CARE_TEAM_VIEW_IF_SELF_RECORD);

            predicates.add(criteriaBuilder.and(
                    securityPredicateGenerator.communitiesWithSelfRecordClients(criteriaBuilder, query,
                            root.get(CommunityCareTeamMember_.communityId), employees)
            ));
        }

        //todo figure out on hold

        return criteriaBuilder.and(
                eligible,
                criteriaBuilder.or(predicates.toArray(new Predicate[0]))
        );
    }
}
