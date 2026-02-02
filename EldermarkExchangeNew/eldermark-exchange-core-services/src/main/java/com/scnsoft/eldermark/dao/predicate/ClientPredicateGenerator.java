package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.specification.SpecificationUtils;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.client.MergedClientView;
import com.scnsoft.eldermark.entity.client.MergedClientView_;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.entity.history.ClientHistory;
import com.scnsoft.eldermark.entity.history.ClientHistory_;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

@Component
public class ClientPredicateGenerator {

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    @Autowired
    private OrganizationPredicateGenerator organizationPredicateGenerator;

    @Autowired
    private CommunityPredicateGenerator communityPredicateGenerator;

    @Autowired
    private EmployeePredicateGenerator employeePredicateGenerator;

    public Predicate selfRecordClients(CriteriaBuilder criteriaBuilder,
                                       Path<Long> clientIdPath,
                                       Collection<Employee> employees) {
        return SpecificationUtils.in(criteriaBuilder, clientIdPath, CareCoordinationUtils.getAssociatedClientIds(employees));
    }

    public Predicate communitiesWithSelfRecordClients(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query,
                                                      Path<Long> communityIdPath,
                                                      Collection<Employee> employees) {
        var selfClientIds = CareCoordinationUtils.getAssociatedClientIds(employees);

        if (CollectionUtils.isEmpty(selfClientIds)) {
            return criteriaBuilder.or();
        }

        var subQuery = query.subquery(Long.class);
        var subRoot = subQuery.from(Client.class);

        return criteriaBuilder.in(communityIdPath)
                .value(subQuery
                        .select(subRoot.get(Client_.communityId))
                        .where(SpecificationUtils.in(criteriaBuilder, subRoot.get(Client_.id), selfClientIds)));
    }

    public Predicate clientAndMergedClients(CriteriaBuilder criteriaBuilder,
                                            Path<Client> clientPath,
                                            AbstractQuery<?> query,
                                            Collection<Long> clientIds) {
        return withMergedClientsById(clientPath.get(Client_.id),
                clientFrom -> criteriaBuilder.in(clientFrom.get(Client_.ID)).value(clientIds),
                criteriaBuilder, query);
    }

    public Predicate clientAndMergedClientsById(CriteriaBuilder criteriaBuilder,
                                                Path<Long> clientId,
                                                AbstractQuery<?> query,
                                                Collection<Long> clientIds) {

        return withMergedClientsById(clientId,
                clientFrom -> criteriaBuilder.in(clientFrom.get(Client_.ID)).value(clientIds),
                criteriaBuilder, query);
    }

    public Predicate withMergedClients(Path<Client> clientPath,
                                       Function<From<?, Client>, Predicate> initialPredicateGenerator,
                                       CriteriaBuilder criteriaBuilder,
                                       AbstractQuery<?> query) {

        return withMergedClientsById(clientPath.get(Client_.id), initialPredicateGenerator, criteriaBuilder, query);
    }

    private Predicate withMergedClientsById(Path<Long> clientId,
                                            Function<From<?, Client>, Predicate> initialPredicateGenerator,
                                            CriteriaBuilder criteriaBuilder,
                                            AbstractQuery<?> query) {

        var mergedSubQuery = query.subquery(Integer.class);
        var mergedFrom = mergedSubQuery.from(MergedClientView.class);

        return criteriaBuilder.exists(mergedSubQuery
                .select(criteriaBuilder.literal(1))
                .where(criteriaBuilder.equal(mergedFrom.get(MergedClientView_.mergedClientId), clientId),
                        initialPredicateGenerator.apply(mergedFrom.join(MergedClientView_.client)))
        );
    }

    public Predicate addedByEmployees(CriteriaBuilder criteriaBuilder, Path<Client> clientPath, Collection<Employee> employees) {
        return criteriaBuilder.in(clientPath.get(Client_.CREATED_BY_ID)).value(CareCoordinationUtils.toIdsSet(employees));
    }

    public Predicate addedByEmployeesToCommunity(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query,
                                                 Path<Long> communityIdPath, Collection<Employee> employees) {
        var sub = query.subquery(Integer.class);
        sub.select(criteriaBuilder.literal(1));

        var subClientRoot = sub.from(Client.class);

        sub.where(criteriaBuilder.and(
                addedByEmployees(criteriaBuilder, subClientRoot, employees),
                criteriaBuilder.equal(subClientRoot.get(Client_.communityId), communityIdPath)
        ));

        return criteriaBuilder.exists(sub);
    }

    public Predicate isClientCreatedBySelf(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query, Path<Employee> employeePath, Client client) {
        if (client == null) {
            return criteriaBuilder.or();
        }

        var subQuery = query.subquery(Integer.class);
        var root = subQuery.from(Client.class);

        subQuery.select(criteriaBuilder.literal(1))
                .where(criteriaBuilder.and(
                                criteriaBuilder.equal(root.get(Client_.id), client.getId()),
                                criteriaBuilder.equal(root.get(Client_.createdById), employeePath.get(Employee_.id))
                        )
                );

        return criteriaBuilder.exists(subQuery);
    }

    public Predicate hasAccessInList(
            PermissionFilter permissionFilter,
            From<?, Client> root,
            AbstractQuery<?> query,
            CriteriaBuilder criteriaBuilder
    ) {
        return hasAccess(
                permissionFilter,
                root,
                query,
                criteriaBuilder,
                Permission.CLIENT_VIEW_IN_LIST_ALL,
                null,
                Permission.CLIENT_VIEW_IN_LIST_IF_ASSOCIATED_ORGANIZATION,
                Permission.CLIENT_VIEW_IN_LIST_IF_ASSOCIATED_COMMUNITY,
                Permission.CLIENT_VIEW_IN_LIST_IF_FROM_AFFILIATED_ORGANIZATION,
                Permission.CLIENT_VIEW_IN_LIST_IF_FROM_AFFILIATED_COMMUNITY,
                null,
                null,
                Permission.CLIENT_VIEW_IN_LIST_IF_CO_RP_CLIENT_CTM,
                Permission.CLIENT_VIEW_IN_LIST_IF_CO_RP_COMMUNITY_CTM,
                null,
                null,
                Permission.CLIENT_VIEW_IN_LIST_IF_ADDED_BY_SELF,
                null,
                Permission.CLIENT_VIEW_IN_LIST_IF_SELF_RECORD,
                Permission.CLIENT_VIEW_IN_LIST_IF_CLIENT_FOUND_IN_RECORD_SEARCH
        );
    }

    public Predicate hasDetailsAccess(
            PermissionFilter permissionFilter,
            From<?, Client> root,
            AbstractQuery<?> query,
            CriteriaBuilder criteriaBuilder
    ) {
        return hasAccess(
                permissionFilter,
                root,
                query,
                criteriaBuilder,
                null,
                Permission.CLIENT_VIEW_ALL_EXCEPT_OPTED_OUT,
                Permission.CLIENT_VIEW_IF_ASSOCIATED_ORGANIZATION,
                Permission.CLIENT_VIEW_IF_ASSOCIATED_COMMUNITY,
                null,
                null,
                Permission.CLIENT_VIEW_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
                Permission.CLIENT_VIEW_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
                null,
                null,
                Permission.CLIENT_VIEW_IF_CURRENT_RP_CLIENT_CTM,
                Permission.CLIENT_VIEW_IF_CURRENT_RP_COMMUNITY_CTM,
                null,
                Permission.CLIENT_VIEW_OPTED_IN_IF_ADDED_BY_SELF,
                Permission.CLIENT_VIEW_IF_SELF_RECORD,
                Permission.CLIENT_VIEW_IF_CLIENT_FOUND_IN_RECORD_SEARCH
        );
    }

    private Predicate hasAccess(
            PermissionFilter permissionFilter,
            From<?, Client> root,
            AbstractQuery<?> query,
            CriteriaBuilder criteriaBuilder,
            Permission allPermission,
            Permission allExceptOptedOutPermission,
            Permission ifAssociatedOrganizationPermission,
            Permission ifAssociatedCommunityPermission,
            Permission ifFromAffiliatedOrganizationPermission,
            Permission ifFromAffiliatedCommunityPermission,
            Permission optedInIfFromAffiliatedOrganizationPermission,
            Permission optedInIfFromAffiliatedCommunityPermission,
            Permission ifCoRpClientCtmPermission,
            Permission ifCoRpCommunityCtmPermission,
            Permission ifCurrentRpClientCtmPermission,
            Permission ifCurrentRpCommunityCtmPermission,
            Permission ifAddedBySelfPermission,
            Permission optedInIfAddedBySelfPermission,
            Permission ifSelfRecordPermission,
            Permission ifClientFoundInRecordSearchPermission
    ) {
        var eligible = clientInEligibleForDiscoveryCommunity(root, criteriaBuilder);

        if (allPermission != null && permissionFilter.hasPermission(allPermission)) {
            return eligible;
        }

        var predicates = new ArrayList<Predicate>();

        if (allExceptOptedOutPermission != null && permissionFilter.hasPermission(allExceptOptedOutPermission)) {
            predicates.add(isOptedOut(root, criteriaBuilder).not());
        }

        if (ifAssociatedOrganizationPermission != null && permissionFilter.hasPermission(ifAssociatedOrganizationPermission)) {
            var employees = permissionFilter.getEmployees(ifAssociatedOrganizationPermission);
            predicates.add(criteriaBuilder.in(root.get(Client_.ORGANIZATION_ID))
                    .value(SpecificationUtils.employeesOrganizationIds(employees)));
        }

        if (ifAssociatedCommunityPermission != null && permissionFilter.hasPermission(ifAssociatedCommunityPermission)) {
            var employees = permissionFilter.getEmployees(ifAssociatedCommunityPermission);
            predicates.add(criteriaBuilder.in(root.get(Client_.COMMUNITY_ID))
                    .value(SpecificationUtils.employeesCommunityIds(employees)));
        }

        if (ifFromAffiliatedOrganizationPermission != null && permissionFilter.hasPermission(ifFromAffiliatedOrganizationPermission)) {
            var employees = permissionFilter.getEmployees(ifFromAffiliatedOrganizationPermission);
            predicates.add(
                    securityPredicateGenerator.primaryCommunitiesOfOrganizations(criteriaBuilder, query, root.get(Client_.communityId), employees)
            );
        }

        if (ifFromAffiliatedCommunityPermission != null && permissionFilter.hasPermission(ifFromAffiliatedCommunityPermission)) {
            var employees = permissionFilter.getEmployees(ifFromAffiliatedCommunityPermission);
            predicates.add(
                    securityPredicateGenerator.primaryCommunities(criteriaBuilder, query, root.get(Client_.communityId), employees)
            );
        }

        if (optedInIfFromAffiliatedOrganizationPermission != null && permissionFilter.hasPermission(optedInIfFromAffiliatedOrganizationPermission)) {
            var employees = permissionFilter.getEmployees(optedInIfFromAffiliatedOrganizationPermission);
            predicates.add(
                    criteriaBuilder.and(
                            securityPredicateGenerator.primaryCommunitiesOfOrganizations(criteriaBuilder, query, root.get(Client_.communityId), employees),
                            isOptedOut(root, criteriaBuilder).not()
                    )
            );
        }

        if (optedInIfFromAffiliatedCommunityPermission != null && permissionFilter.hasPermission(optedInIfFromAffiliatedCommunityPermission)) {
            var employees = permissionFilter.getEmployees(optedInIfFromAffiliatedCommunityPermission);
            predicates.add(
                    criteriaBuilder.and(
                            securityPredicateGenerator.primaryCommunities(criteriaBuilder, query, root.get(Client_.communityId), employees),
                            isOptedOut(root, criteriaBuilder).not()
                    )
            );
        }

        if (ifCoRpCommunityCtmPermission != null && permissionFilter.hasPermission(ifCoRpCommunityCtmPermission)) {
            var employees = permissionFilter.getEmployees(ifCoRpCommunityCtmPermission);
            predicates.add(securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(criteriaBuilder, query,
                    root.get(Client_.communityId), employees, AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold()));
        }

        if (ifCoRpClientCtmPermission != null && permissionFilter.hasPermission(ifCoRpClientCtmPermission)) {
            var employees = permissionFilter.getEmployees(ifCoRpClientCtmPermission);
            var resultCriteria = securityPredicateGenerator.clientsInClientCareTeamPredicate(criteriaBuilder, query,
                    root, employees, AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold());
            predicates.add(resultCriteria);
        }

        if (ifCurrentRpCommunityCtmPermission != null && permissionFilter.hasPermission(ifCurrentRpCommunityCtmPermission)) {
            var employees = permissionFilter.getEmployees(ifCurrentRpCommunityCtmPermission);

            predicates.add(securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(criteriaBuilder, query,
                    root.get(Client_.communityId), employees, AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.current(root)));
        }

        if (ifCurrentRpClientCtmPermission != null && permissionFilter.hasPermission(ifCurrentRpClientCtmPermission)) {
            var employees = permissionFilter.getEmployees(ifCurrentRpClientCtmPermission);
            var resultCriteria = securityPredicateGenerator.clientsInClientCareTeamPredicate(criteriaBuilder, query,
                    root, employees, AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.current(root));
            predicates.add(resultCriteria);
        }

        if (ifAddedBySelfPermission != null && permissionFilter.hasPermission(ifAddedBySelfPermission)) {
            var employees = permissionFilter.getEmployees(ifAddedBySelfPermission);
            var resultCriteria = securityPredicateGenerator.clientAddedByEmployees(criteriaBuilder, root, employees);
            predicates.add(resultCriteria);
        }

        if (optedInIfAddedBySelfPermission != null && permissionFilter.hasPermission(optedInIfAddedBySelfPermission)) {
            var employees = permissionFilter.getEmployees(optedInIfAddedBySelfPermission);
            predicates.add(
                    criteriaBuilder.and(
                            securityPredicateGenerator.clientAddedByEmployees(criteriaBuilder, root, employees),
                            isOptedOut(root, criteriaBuilder).not()
                    )
            );
        }

        if (ifSelfRecordPermission != null && permissionFilter.hasPermission(ifSelfRecordPermission)) {
            var employees = permissionFilter.getEmployees(ifSelfRecordPermission);
            var resultCriteria = securityPredicateGenerator.selfRecordClients(criteriaBuilder,
                    root.get(Client_.id), employees);
            predicates.add(resultCriteria);
        }

        if (ifClientFoundInRecordSearchPermission != null && permissionFilter.hasPermission(ifClientFoundInRecordSearchPermission) &&
                CollectionUtils.isNotEmpty(permissionFilter.getClientRecordSearchFoundIds())) {
            predicates.add(root.get(Client_.id).in(permissionFilter.getClientRecordSearchFoundIds()));
        }

        return criteriaBuilder.and(
                eligible,
                criteriaBuilder.or(predicates.toArray(new Predicate[0]))
        );
    }

    public Predicate hasPermissionToRequestSignatureFrom(PermissionFilter permissionFilter, From<?, Client> root, AbstractQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var isActive = isActive(root, criteriaBuilder);
        var clientOrganizationHasSignatureFeatureEnabled = criteriaBuilder.isTrue(
                JpaUtils.getOrCreateJoin(root, Client_.organization)
                        .get(Organization_.isSignatureEnabled)
        );

        var predicates = new ArrayList<Predicate>();
        if (permissionFilter.hasPermission(Permission.DOCUMENT_SIGNATURE_REQUEST_ADD_ALL_EXCEPT_OPTED_OUT)) {
            predicates.add(isOptedIn(root, criteriaBuilder));
        }

        if (permissionFilter.hasPermission(Permission.DOCUMENT_SIGNATURE_REQUEST_ADD_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(Permission.DOCUMENT_SIGNATURE_REQUEST_ADD_IF_ASSOCIATED_ORGANIZATION);
            predicates.add(criteriaBuilder.in(root.get(Client_.ORGANIZATION_ID))
                    .value(SpecificationUtils.employeesOrganizationIds(employees)));
        }

        if (permissionFilter.hasPermission(Permission.DOCUMENT_SIGNATURE_REQUEST_ADD_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(Permission.DOCUMENT_SIGNATURE_REQUEST_ADD_IF_ASSOCIATED_COMMUNITY);
            predicates.add(criteriaBuilder.in(root.get(Client_.COMMUNITY_ID))
                    .value(SpecificationUtils.employeesCommunityIds(employees)));
        }

        if (permissionFilter.hasPermission(Permission.DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_RP_COMMUNITY_CTM)) {
            predicates.add(securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(
                    criteriaBuilder,
                    query,
                    root.get(Client_.communityId),
                    permissionFilter.getEmployees(Permission.DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_RP_COMMUNITY_CTM),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.current(root)
            ));
        }

        if (permissionFilter.hasPermission(Permission.DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM)) {
            predicates.add(securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(
                    criteriaBuilder,
                    query,
                    root.get(Client_.communityId),
                    permissionFilter.getEmployees(Permission.DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.current(root)
            ));
        }

        if (permissionFilter.hasPermission(Permission.DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_RP_CLIENT_CTM)) {
            predicates.add(securityPredicateGenerator.clientsInClientCareTeamPredicate(
                    criteriaBuilder,
                    query,
                    root,
                    permissionFilter.getEmployees(Permission.DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_RP_CLIENT_CTM),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.current(root)
            ));
        }

        if (permissionFilter.hasPermission(Permission.DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_REGULAR_CLIENT_CTM)) {
            predicates.add(securityPredicateGenerator.clientsInClientCareTeamPredicate(
                    criteriaBuilder,
                    query,
                    root,
                    permissionFilter.getEmployees(Permission.DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_REGULAR_CLIENT_CTM),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.current(root)
            ));
        }

        return criteriaBuilder.and(
                isActive,
                clientOrganizationHasSignatureFeatureEnabled,
                criteriaBuilder.or(predicates.toArray(Predicate[]::new))
        );
    }

    public <T extends IdAware> Predicate byCommunities(Collection<T> communities, From<?, Client> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        var communityIds = CareCoordinationUtils.toIdsSet(communities);
        return root.get(Client_.communityId).in(communityIds);
    }

    public Predicate createdBeforeOrWithoutDateCreated(Instant dateCreated, From<?, Client> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.or(
                criteriaBuilder.isNull(root.get(Client_.createdDate)),
                criteriaBuilder.lessThanOrEqualTo(root.get(Client_.createdDate), dateCreated)
        );
    }

    public Predicate isActive(Path<Client> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(root.get(Client_.active), true);
    }

    public Predicate lastUpdatedAfterOrEqual(Instant date, From<?, Client> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.greaterThanOrEqualTo(root.get(Client_.lastUpdated), date);
    }

    public Predicate hasActiveAssociatedEmployee(CriteriaBuilder criteriaBuilder, From<?, Client> clientPath) {
        var associatedEmployee = JpaUtils.getOrCreateJoin(clientPath, Client_.associatedEmployee);
        return criteriaBuilder.and(
                criteriaBuilder.isNotNull(associatedEmployee),
                employeePredicateGenerator.isActive(associatedEmployee, criteriaBuilder)
        );
    }

    public Predicate excludeAssociatedEmployee(Long employeeId, From<?, Client> root, CriteriaBuilder criteriaBuilder) {
        //left join to allow clients without associated employees
        var join = JpaUtils.getOrCreateListJoin(root, Client_.associatedEmployeeIds, JoinType.LEFT);

        return criteriaBuilder.or(
                criteriaBuilder.isNull(join),
                criteriaBuilder.notEqual(join, employeeId)
        );
    }

    public Predicate chatAccessibleClients(PermissionFilter permissionFilter, Long excludedEmployeeId, From<?, Client> root,
                                           AbstractQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var hasAccess = hasDetailsAccess(permissionFilter, root, query, criteriaBuilder);

        var orgJoin = JpaUtils.getOrCreateJoin(root, Client_.organization);
        var withEnabledChat = organizationPredicateGenerator.withEnabledChat(criteriaBuilder, orgJoin, true);

        var exclude = excludedEmployeeId == null ? criteriaBuilder.and() :
                excludeAssociatedEmployee(excludedEmployeeId, root, criteriaBuilder);


        return criteriaBuilder.and(hasAccess, withEnabledChat, exclude);
    }

    public Predicate excludeAssociatedParticipatingInOneToOneChatWithAny(Collection<Long> employeeIds, From<?, Client> root,
                                                                         AbstractQuery<?> abstractQuery,
                                                                         CriteriaBuilder criteriaBuilder) {
        //we exclude associated employees instead of excluding clients in chat because chats are created between
        //employees and it doesn't matter if associated employee was added to chat as client or as contact if
        //you try to create new chat

        var associatedEmployee = JpaUtils.getOrCreateJoin(root, Client_.associatedEmployee, JoinType.LEFT, false);

        return criteriaBuilder.or(criteriaBuilder.isNull(associatedEmployee),
                employeePredicateGenerator.excludeParticipatingInOneToOneChatWithAny(employeeIds, associatedEmployee.get(Employee_.id),
                        abstractQuery, criteriaBuilder));
    }

    public Predicate clientInEligibleForDiscoveryCommunity(From<?, Client> clientFrom, CriteriaBuilder cb) {
        return communityPredicateGenerator.eligibleForDiscovery(
                cb,
                JpaUtils.getOrCreateJoin(clientFrom, Client_.community)
        );
    }

    public Predicate isActiveInPeriod(
            Instant instantFrom,
            Instant instantTo,
            Path<Client> client,
            CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder
    ) {

        var latestClientHistoryUpdateTime = latestClientHistoryUpdateTime(client, query, criteriaBuilder);
        var existsActiveClientHistoryInPeriod = existsActiveClientHistoryInPeriod(instantFrom, instantTo, client, query, criteriaBuilder);

        return criteriaBuilder.or(
                criteriaBuilder.and(
                        criteriaBuilder.lessThan(latestClientHistoryUpdateTime, instantTo),
                        criteriaBuilder.equal(client.get(Client_.active), Boolean.TRUE)
                ),
                existsActiveClientHistoryInPeriod
        );
    }

    private Predicate existsActiveClientHistoryInPeriod(
            Instant instantFrom,
            Instant instantTo,
            Path<Client> client,
            CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder
    ) {
        var subQuery = query.subquery(Long.class);
        var subRoot = subQuery.from(ClientHistory.class);

        subQuery.select(criteriaBuilder.literal(1L));
        subQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.equal(subRoot.get(ClientHistory_.clientId), client.get(Client_.id)),
                        criteriaBuilder.or(
                                criteriaBuilder.and(
                                        criteriaBuilder.greaterThanOrEqualTo(subRoot.get(ClientHistory_.updatedDatetime), instantFrom),
                                        criteriaBuilder.lessThanOrEqualTo(subRoot.get(ClientHistory_.updatedDatetime), instantTo)
                                ),
                                criteriaBuilder.equal(firstClientHistoryIdAfterDate(client, query, criteriaBuilder, instantTo), subRoot.get(ClientHistory_.id))
                        ),
                        criteriaBuilder.equal(subRoot.get(ClientHistory_.active), Boolean.TRUE)
                )
        );

        return criteriaBuilder.exists(subQuery);
    }

    private Subquery<Long> firstClientHistoryIdAfterDate(
            Path<Client> client,
            CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder,
            Instant date
    ) {
        var subQuery = query.subquery(Long.class);
        var subRoot = subQuery.from(ClientHistory.class);
        subQuery.select(criteriaBuilder.min(subRoot.get(ClientHistory_.id)));
        subQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.equal(subRoot.get(ClientHistory_.clientId), client.get(Client_.id)),
                        criteriaBuilder.greaterThan(subRoot.get(ClientHistory_.updatedDatetime), date)
                )
        );
        return subQuery;
    }

    private Subquery<Instant> latestClientHistoryUpdateTime(
            Path<Client> client,
            CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder
    ) {
        var subQuery = query.subquery(Instant.class);
        var subRoot = subQuery.from(ClientHistory.class);
        subQuery.select(criteriaBuilder.greatest(subRoot.get(ClientHistory_.updatedDatetime)));
        subQuery.where(criteriaBuilder.equal(subRoot.get(ClientHistory_.clientId), client.get(Client_.id)));
        return subQuery;
    }

    public Predicate isOptedOut(Path<Client> clientFrom, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(clientFrom.get(Client_.hieConsentPolicyType), HieConsentPolicyType.OPT_OUT);
    }

    public Predicate isOptedIn(Path<Client> clientFrom, CriteriaBuilder criteriaBuilder) {
        return isOptedOut(clientFrom, criteriaBuilder).not();
    }
}

