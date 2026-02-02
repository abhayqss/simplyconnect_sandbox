package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.specification.SpecificationUtils;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember_;
import com.scnsoft.eldermark.entity.client.MergedClientView;
import com.scnsoft.eldermark.entity.client.MergedClientView_;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.prospect.Prospect;
import com.scnsoft.eldermark.entity.prospect.Prospect_;
import com.scnsoft.eldermark.entity.referral.ReferralRequest;
import com.scnsoft.eldermark.entity.referral.ReferralRequest_;
import com.scnsoft.eldermark.entity.referral.Referral_;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.entity.security.SecurityConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Component
//todo there are more checks which can be switched to id Paths
public class SecurityPredicateGenerator {

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    @Autowired
    private CommunityPredicateGenerator communityPredicateGenerator;

    @Autowired
    private ClientCareTeamMemberPredicateGenerator clientCareTeamMemberPredicateGenerator;

    @Autowired
    private CommunityCareTeamMemberPredicateGenerator communityCareTeamMemberPredicateGenerator;

    @Autowired
    private ExternalEmployeeInboundReferralCommunityPredicateGenerator externalEmployeeInboundReferralCommunityPredicateGenerator;

    @Autowired
    private ReferralPredicateGenerator referralPredicateGenerator;

    public Predicate primaryCommunities(CriteriaBuilder criteriaBuilder,
                                        AbstractQuery<?> query,
                                        Path<Long> primaryCommunityIdPath,
                                        Collection<Employee> employees) {
        return communityPredicateGenerator.primaryCommunities(criteriaBuilder, query, primaryCommunityIdPath, employees);
    }

    public Predicate primaryCommunities(CriteriaBuilder criteriaBuilder,
                                        Root<AffiliatedRelationship> affiliatedRoot,
                                        Collection<Employee> employees) {
        return communityPredicateGenerator.primaryCommunities(criteriaBuilder, affiliatedRoot, employees);
    }

    public Predicate primaryCommunitiesOfOrganizations(CriteriaBuilder criteriaBuilder,
                                                       AbstractQuery<?> query,
                                                       Path<Long> primaryCommunityIdPath,
                                                       Collection<Employee> employees) {
        return communityPredicateGenerator.primaryCommunitiesOfOrganizations(criteriaBuilder, query, primaryCommunityIdPath, employees);
    }

    public Predicate primaryCommunitiesOfOrganizations(CriteriaBuilder criteriaBuilder,
                                                       Root<AffiliatedRelationship> affiliatedRoot,
                                                       Collection<Employee> employees) {
        return communityPredicateGenerator.primaryCommunitiesOfOrganizations(criteriaBuilder, affiliatedRoot, employees);
    }

    public Predicate affiliatedCommunities(CriteriaBuilder criteriaBuilder,
                                           AbstractQuery<?> query,
                                           Path<Long> affiliatedCommunityIdPath,
                                           Collection<Employee> employees) {
        return communityPredicateGenerator.affiliatedCommunities(criteriaBuilder, query, affiliatedCommunityIdPath, employees);
    }

    public Predicate affiliatedCommunities(CriteriaBuilder criteriaBuilder,
                                           Root<AffiliatedRelationship> affiliatedRoot,
                                           Collection<Employee> employees) {
        return communityPredicateGenerator.affiliatedCommunities(criteriaBuilder, affiliatedRoot, employees);
    }

    public Predicate affiliatedCommunitiesOfOrganizations(CriteriaBuilder criteriaBuilder,
                                                          AbstractQuery<?> query,
                                                          Path<Long> affiliatedCommunityIdPath,
                                                          Collection<Employee> employees) {
        return communityPredicateGenerator.affiliatedCommunitiesOfOrganizations(criteriaBuilder, query, affiliatedCommunityIdPath, employees);
    }

    public Predicate affiliatedCommunitiesOfOrganizations(CriteriaBuilder criteriaBuilder,
                                                          Root<AffiliatedRelationship> affiliatedRoot,
                                                          Collection<Employee> employees) {
        return communityPredicateGenerator.affiliatedCommunitiesOfOrganizations(criteriaBuilder, affiliatedRoot, employees);
    }

    @Deprecated
    public Predicate clientsInClientCareTeamPredicate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query,
                                                      Path<Client> clientFrom,
                                                      Collection<Employee> employees,
                                                      AffiliatedCareTeamType type) {
        return clientCareTeamMemberPredicateGenerator.clientsInClientCareTeamPredicate(
                criteriaBuilder, query, clientFrom, employees, type);
    }

    public Predicate clientsInClientCareTeamPredicate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query,
                                                      Path<Client> clientFrom,
                                                      Collection<Employee> employees,
                                                      AffiliatedCareTeamType type,
                                                      HieConsentCareTeamType consentType) {
        return clientCareTeamMemberPredicateGenerator.clientsInClientCareTeamPredicate(
                criteriaBuilder, query, clientFrom, employees, type, consentType);
    }

    @Deprecated
    public Predicate clientsInClientCareTeamOfCommunityPredicate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query,
                                                                 Path<Long> communityIdPath,
                                                                 Collection<Employee> employees,
                                                                 AffiliatedCareTeamType type) {
        return clientsInClientCareTeamOfCommunityPredicate(
                criteriaBuilder, query, communityIdPath, employees, type, HieConsentCareTeamType.currentAndOnHold());
    }

    public Predicate clientsInClientCareTeamOfCommunityPredicate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query,
                                                                 Path<Long> communityIdPath,
                                                                 Collection<Employee> employees,
                                                                 AffiliatedCareTeamType type,
                                                                 HieConsentCareTeamType consentType) {
        return clientCareTeamMemberPredicateGenerator.clientsInClientCareTeamOfCommunityPredicate(
                criteriaBuilder, query, communityIdPath, employees, type, consentType);
    }

    @Deprecated
    public Predicate communityIdsInCommunityCareTeamPredicate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query,
                                                              Path<Long> communityIdPath,
                                                              Collection<Employee> employees,
                                                              AffiliatedCareTeamType type) {
        return communityCareTeamMemberPredicateGenerator.communityIdsInCommunityCareTeamPredicate(
                criteriaBuilder, query, communityIdPath, employees, type, HieConsentCareTeamType.currentAndOnHold());
    }

    public Predicate communityIdsInCommunityCareTeamPredicate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query,
                                                              Path<Long> communityIdPath,
                                                              Collection<Employee> employees,
                                                              AffiliatedCareTeamType type,
                                                              HieConsentCareTeamType consentType) {
        return communityCareTeamMemberPredicateGenerator.communityIdsInCommunityCareTeamPredicate(
                criteriaBuilder, query, communityIdPath, employees, type, consentType);
    }

    public Predicate selfRecordClients(CriteriaBuilder criteriaBuilder,
                                       Path<Long> clientIdPath,
                                       Collection<Employee> employees) {
        return clientPredicateGenerator.selfRecordClients(
                criteriaBuilder, clientIdPath, employees);
    }

    public Predicate isClientCreatedBySelf(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query, Path<Employee> employeePath, Client client) {
        return clientPredicateGenerator.isClientCreatedBySelf(criteriaBuilder, query, employeePath, client);
    }

    public Predicate communitiesWithSelfRecordClients(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query,
                                                      Path<Long> communityIdPath,
                                                      Collection<Employee> employees) {
        return clientPredicateGenerator.communitiesWithSelfRecordClients(
                criteriaBuilder, query, communityIdPath, employees);
    }

    public Predicate clientAddedByEmployees(CriteriaBuilder criteriaBuilder, Path<Client> clientPath, Collection<Employee> employees) {
        return clientPredicateGenerator.addedByEmployees(criteriaBuilder, clientPath, employees);
    }

    public Predicate clientAddedByEmployeesToCommunity(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query,
                                                       Path<Long> communityIdPath,
                                                       Collection<Employee> employees) {
        return clientPredicateGenerator.addedByEmployeesToCommunity(criteriaBuilder, query, communityIdPath, employees);
    }

    public Predicate clientInAssociatedOrganization(CriteriaBuilder criteriaBuilder, From<?, Client> clientFrom,
                                                    Collection<Employee> employees) {
        var employeeOrganizationIds = SpecificationUtils.employeesOrganizationIds(employees);
        return criteriaBuilder.in(clientFrom.get(Client_.ORGANIZATION_ID)).value(employeeOrganizationIds);
    }

    public Predicate clientInAssociatedCommunity(CriteriaBuilder criteriaBuilder, From<?, Client> clientFrom,
                                                 List<Employee> employees) {
        var employeeCommunityIds = SpecificationUtils.employeesCommunityIds(employees);
        return criteriaBuilder.in(clientFrom.get(Client_.COMMUNITY_ID)).value(employeeCommunityIds);
    }

    public Predicate associatedOrganizationWithMergedPredicate(CriteriaBuilder criteriaBuilder, From<?, Client> root,
                                                               AbstractQuery<?> query, List<Employee> employees) {
        return clientPredicateGenerator.withMergedClients(root,
                clientFrom -> clientInAssociatedOrganization(criteriaBuilder, clientFrom, employees),
                criteriaBuilder, query);
    }

    public Predicate associatedCommunityWithMergedClients(CriteriaBuilder criteriaBuilder, From<?, Client> root,
                                                          AbstractQuery<?> query, List<Employee> employees) {
        return clientPredicateGenerator.withMergedClients(
                root,
                clientFrom -> clientInAssociatedCommunity(criteriaBuilder, clientFrom, employees),
                criteriaBuilder, query);
    }

    public Predicate primaryCommunitiesOfOrganizationsWithMergedClient(CriteriaBuilder criteriaBuilder, From<?, Client> root,
                                                                       AbstractQuery<?> query, List<Employee> employees) {
        return clientPredicateGenerator.withMergedClients(
                root,
                clientFrom -> primaryCommunitiesOfOrganizations(criteriaBuilder, query, clientFrom.get(Client_.communityId), employees),
                criteriaBuilder, query);
    }

    public Predicate primaryCommunitiesWithMergedClient(CriteriaBuilder criteriaBuilder, From<?, Client> root,
                                                        AbstractQuery<?> query, List<Employee> employees) {
        return clientPredicateGenerator.withMergedClients(
                root,
                clientFrom -> primaryCommunities(criteriaBuilder, query, clientFrom.get(Client_.communityId), employees),
                criteriaBuilder, query);
    }

    public Predicate communityCareTeamWithMergedPredicate(CriteriaBuilder criteriaBuilder,
                                                          Path<Client> clientPath,
                                                          AbstractQuery<?> query,
                                                          Collection<Employee> employees,
                                                          AffiliatedCareTeamType type,
                                                          HieConsentCareTeamType consentType) {
        return clientPredicateGenerator.withMergedClients(clientPath,
                clientFrom -> communityIdsInCommunityCareTeamPredicate(criteriaBuilder, query,
                        clientFrom.get(Client_.communityId), employees, type, consentType),
                criteriaBuilder, query);
    }

    @Deprecated
    public Predicate communityCareTeamWithMergedPredicate(CriteriaBuilder criteriaBuilder, Path<Client> clientPath,
                                                          AbstractQuery<?> query, Collection<Employee> employees,
                                                          AffiliatedCareTeamType type) {
        return clientPredicateGenerator.withMergedClients(clientPath,
                clientFrom -> communityIdsInCommunityCareTeamPredicate(criteriaBuilder, query,
                        clientFrom.get(Client_.communityId), employees, type),
                criteriaBuilder, query);
    }

    @Deprecated
    public Predicate clientCareTeamWithMergedPredicate(CriteriaBuilder criteriaBuilder, Path<Client> clientPath,
                                                       AbstractQuery<?> query, List<Employee> employees,
                                                       AffiliatedCareTeamType type) {
        return clientPredicateGenerator.withMergedClients(clientPath,
                clientFrom -> clientsInClientCareTeamPredicate(criteriaBuilder, query, clientFrom, employees, type),
                criteriaBuilder, query);
    }

    public Predicate clientCareTeamWithMergedPredicate(CriteriaBuilder criteriaBuilder,
                                                       Path<Client> clientPath,
                                                       AbstractQuery<?> query,
                                                       List<Employee> employees,
                                                       AffiliatedCareTeamType type,
                                                       HieConsentCareTeamType consentType) {
        return clientPredicateGenerator.withMergedClients(clientPath,
                clientFrom -> clientsInClientCareTeamPredicate(criteriaBuilder, query, clientFrom, employees, type, consentType),
                criteriaBuilder, query);
    }

    public Predicate selfRecordWithMergedPredicate(CriteriaBuilder criteriaBuilder, From<?, Client> root,
                                                   AbstractQuery<?> query, List<Employee> employees) {
        return clientPredicateGenerator.withMergedClients(root,
                clientFrom -> selfRecordClients(criteriaBuilder, clientFrom.get(Client_.id), employees),
                criteriaBuilder, query);
    }

    public Predicate addedByEmployeesWithMergedPredicate(CriteriaBuilder criteriaBuilder, From<?, Client> root,
                                                         AbstractQuery<?> query, List<Employee> employees) {
        return clientPredicateGenerator.withMergedClients(root,
                clientFrom -> clientAddedByEmployees(criteriaBuilder, clientFrom, employees),
                criteriaBuilder, query);
    }

    public Predicate mergedClientsPermissions(From<?, Client> root, AbstractQuery<?> query,
                                              CriteriaBuilder criteriaBuilder,
                                              Function<From<?, Client>, List<Predicate>> permissionChecks
    ) {
        return clientPredicateGenerator.withMergedClients(
                root,
                clientFrom -> criteriaBuilder.or(permissionChecks.apply(clientFrom).toArray(new Predicate[0])),
                criteriaBuilder, query);
    }

    public Predicate hasAccessWithPHRFlagsAndMerged(
            From<?, Client> clientFrom,
            CriteriaQuery<?> criteriaQuery,
            CriteriaBuilder criteriaBuilder,
            PermissionFilter permissionFilter,
            Permission allExceptOptedOut,
            Permission associatedOrganization,
            Permission associatedCommunity,
            Permission optedInFromAffiliatedOrganization,
            Permission optedInFromAffiliatedCommunity,
            Permission currentRpCommunityCTM,
            Permission currentRpClientCTM,
            Permission optedInClientAddedBySelf,
            Permission selfRecord,
            Permission accessibleReferralRequest,
            Permission foundInRecordSearch,
            AccessRight.Code... accessRights
    ) {
        return hasAccessWithPHRFlagsAndMerged(
                clientFrom,
                criteriaQuery,
                criteriaBuilder,
                permissionFilter,
                allExceptOptedOut,
                associatedOrganization,
                associatedCommunity,
                optedInFromAffiliatedOrganization,
                optedInFromAffiliatedCommunity,
                currentRpCommunityCTM,
                currentRpClientCTM,
                optedInClientAddedBySelf,
                selfRecord,
                accessibleReferralRequest,
                foundInRecordSearch,
                null,
                accessRights
        );
    }

    //refer to https://confluence.scnsoft.com/display/CCNP/Security+checks+with+PHR+Access+flags
    //for better understanding of underlying logic
    public Predicate hasAccessWithPHRFlagsAndMerged(
            From<?, Client> clientFrom,
            CriteriaQuery<?> criteriaQuery,
            CriteriaBuilder criteriaBuilder,
            PermissionFilter permissionFilter,
            Permission allExceptOptedOut,
            Permission associatedOrganization,
            Permission associatedCommunity,
            Permission optedInFromAffiliatedOrganization,
            Permission optedInFromAffiliatedCommunity,
            Permission currentRpCommunityCTM,
            Permission currentRpClientCTM,
            Permission optedInClientAddedBySelf,
            Permission selfRecord,
            Permission accessibleReferralRequest,
            Permission foundInRecordSearch,
            SpecificationUtils.PathSpecification<Client> additionalClientSpecification, // is used for optimization
            AccessRight.Code... accessRights
    ) {

        var employeeSubQueries = new ArrayList<Subquery<Long>>();

        var allEmployees = permissionFilter.getAllEmployees();

        allEmployees.forEach(employee -> {
            var subQuery = criteriaQuery.subquery(Long.class);

            var mergedFrom = subQuery.from(MergedClientView.class);
            subQuery.select(mergedFrom.get(MergedClientView_.mergedClientId));

            var client = mergedFrom.get(MergedClientView_.client);
            var mergedClients = mergedFrom.join(MergedClientView_.mergedClient);

            var employeePredicates = new ArrayList<Predicate>();

            var permissions = permissionFilter.getEmployeePermissions(employee);

            if (permissions.contains(allExceptOptedOut)) {
                employeePredicates.add(
                        criteriaBuilder.and(clientPredicateGenerator.isOptedIn(client, criteriaBuilder))
                );
            }

            if (permissions.contains(associatedOrganization)) {
                employeePredicates.add(
                        criteriaBuilder.equal(client.get(Client_.organizationId), employee.getOrganizationId())
                );
            }

            if (permissions.contains(associatedCommunity)) {
                employeePredicates.add(
                        criteriaBuilder.equal(client.get(Client_.communityId), employee.getCommunityId())
                );
            }

            if (permissions.contains(optedInFromAffiliatedOrganization)) {
                employeePredicates.add(
                        criteriaBuilder.and(
                                criteriaBuilder.and(clientPredicateGenerator.isOptedIn(client, criteriaBuilder)),
                                primaryCommunitiesOfOrganizations(criteriaBuilder, criteriaQuery,
                                        client.get(Client_.communityId),
                                        Collections.singletonList(employee)
                                )
                        )
                );
            }

            if (permissions.contains(optedInFromAffiliatedCommunity)) {
                employeePredicates.add(
                        criteriaBuilder.and(
                                criteriaBuilder.and(clientPredicateGenerator.isOptedIn(client, criteriaBuilder)),
                                primaryCommunities(criteriaBuilder, criteriaQuery,
                                        client.get(Client_.communityId),
                                        Collections.singletonList(employee)
                                )
                        )
                );
            }

            if (permissions.contains(currentRpCommunityCTM)) {
                employeePredicates.add(
                        communityIdsInCommunityCareTeamPredicate(criteriaBuilder, criteriaQuery,
                                client.get(Client_.communityId),
                                Collections.singletonList(employee),
                                AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                                HieConsentCareTeamType.current(client)
                        )
                );
            }

            if (permissions.contains(currentRpClientCTM)) {
                employeePredicates.add(
                        clientCareTeamMemberPredicateGenerator.clientsInClientCareTeamPredicate(criteriaBuilder, criteriaQuery,
                                client,
                                Collections.singletonList(employee),
                                AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                                HieConsentCareTeamType.current(client),
                                accessRights
                        )
                );
            }

            if (permissions.contains(optedInClientAddedBySelf)) {
                employeePredicates.add(
                        criteriaBuilder.and(
                                criteriaBuilder.and(clientPredicateGenerator.isOptedIn(client, criteriaBuilder)),
                                clientAddedByEmployees(criteriaBuilder,
                                        client,
                                        Collections.singletonList(employee)
                                )
                        )
                );
            }

            if (permissions.contains(selfRecord)) {
                employeePredicates.add(
                        selfRecordClients(criteriaBuilder, client.get(Client_.id), Collections.singletonList(employee))
                );
            }

            if (accessibleReferralRequest != null && permissions.contains(accessibleReferralRequest)) {
                var referralIds = criteriaQuery.subquery(Long.class);
                var referralRoot = referralIds.from(ReferralRequest.class);
                referralIds.select(criteriaBuilder.literal(1L));
                referralIds.where(criteriaBuilder.equal(referralRoot.get(ReferralRequest_.referral).get(Referral_.client), client),
                        referralPredicateGenerator.hasAccessToInbound(permissionFilter, referralRoot, criteriaQuery, criteriaBuilder));
                employeePredicates.add(criteriaBuilder.exists(referralIds));
            }

            if (permissions.contains(foundInRecordSearch) &&
                    CollectionUtils.isNotEmpty(permissionFilter.getClientRecordSearchFoundIds())) {
                employeePredicates.add(mergedClients.get(Client_.id).in(permissionFilter.getClientRecordSearchFoundIds()));
            }

            var excludeDisabledForEmployeeAndMergedClients = clientCareTeamMemberPredicateGenerator.notExistsCtmWithAnyDisabledAccess(
                    criteriaQuery, criteriaBuilder, client, Collections.singletonList(employee),
                    SecurityConstants.ACCESS_FLAGS_CHECK_AMONG_CTM_TYPE,
                    accessRights);

            var excludeMergedDisabledForAllClients = criteriaBuilder.or(
                    criteriaBuilder.equal(client, mergedClients),
                    clientCareTeamMemberPredicateGenerator.notExistsCtmWithAnyDisabledAccess(criteriaQuery, criteriaBuilder,
                            mergedClients, allEmployees, SecurityConstants.ACCESS_FLAGS_CHECK_AMONG_CTM_TYPE, accessRights)

            );
            var additionalClientPredicate = additionalClientSpecification != null
                    ? additionalClientSpecification.toPredicate(client, criteriaQuery, criteriaBuilder)
                    : criteriaBuilder.and();

            subQuery.where(criteriaBuilder.and(
                    criteriaBuilder.or(employeePredicates.toArray(new Predicate[0])),
                    excludeDisabledForEmployeeAndMergedClients,
                    excludeMergedDisabledForAllClients,
                    additionalClientPredicate
            ));

            employeeSubQueries.add(subQuery);

        });

        return criteriaBuilder.and(
                clientPredicateGenerator.clientInEligibleForDiscoveryCommunity(clientFrom, criteriaBuilder),
                criteriaBuilder.or(employeeSubQueries.stream()
                        .map(clientFrom::in)
                        .toArray(Predicate[]::new)
                )
        );
    }

    public Predicate clientInEligibleForDiscoveryCommunity(From<?, Client> clientFrom, CriteriaBuilder cb) {
        return clientPredicateGenerator.clientInEligibleForDiscoveryCommunity(clientFrom, cb);
    }

    public Predicate eligibleForDiscoveryCommunity(From<?, Community> communityFrom, CriteriaBuilder cb) {
        return communityPredicateGenerator.eligibleForDiscovery(cb, communityFrom);
    }

    public Predicate selfRecordClientCareTeamMember(From<?, Employee> targetEmployeeFrom,
                                                    AbstractQuery<?> query,
                                                    CriteriaBuilder criteriaBuilder,
                                                    List<Employee> employees,
                                                    AffiliatedCareTeamType type,
                                                    HieConsentCareTeamType consentType) {
        var sub = query.subquery(Integer.class);
        var subClientCtm = sub.from(ClientCareTeamMember.class);
        sub.select(criteriaBuilder.literal(1))
                .where(
                        selfRecordClients(criteriaBuilder, subClientCtm.get(ClientCareTeamMember_.clientId), employees),
                        clientCareTeamMemberPredicateGenerator.byEmployeeId(subClientCtm, criteriaBuilder, targetEmployeeFrom.get(Employee_.id)),
                        clientCareTeamMemberPredicateGenerator.ofAffiliationType(subClientCtm, criteriaBuilder, type),
                        clientCareTeamMemberPredicateGenerator.ofConsentType(subClientCtm, criteriaBuilder, query, consentType)
                );
        return criteriaBuilder.exists(sub);
    }
    public Predicate prospectInAssociatedOrganization(CriteriaBuilder criteriaBuilder, From<?, Prospect> prospectFrom,
                                                      Collection<Employee> employees) {
        var employeeOrganizationIds = SpecificationUtils.employeesOrganizationIds(employees);
        return criteriaBuilder.in(prospectFrom.get(Prospect_.ORGANIZATION_ID)).value(employeeOrganizationIds);
    }

    public Predicate prospectInAssociatedCommunity(CriteriaBuilder criteriaBuilder, From<?, Prospect> prospectFrom, List<Employee> employees) {
        var employeeCommunityIds = SpecificationUtils.employeesCommunityIds(employees);
        return criteriaBuilder.in(prospectFrom.get(Prospect_.COMMUNITY_ID)).value(employeeCommunityIds);
    }
}
