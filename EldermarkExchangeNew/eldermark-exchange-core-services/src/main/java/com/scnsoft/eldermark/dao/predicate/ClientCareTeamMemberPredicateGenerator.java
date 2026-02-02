package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.specification.SpecificationUtils;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember_;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole_;
import com.scnsoft.eldermark.entity.careteam.NotViewableCareTeam;
import com.scnsoft.eldermark.entity.careteam.NotViewableCareTeam_;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember_;
import com.scnsoft.eldermark.entity.client.MergedClientView;
import com.scnsoft.eldermark.entity.client.MergedClientView_;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.security.SecurityConstants;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.*;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Component
public class ClientCareTeamMemberPredicateGenerator extends CareTeamMemberPredicateGenerator<ClientCareTeamMember> {

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    public Predicate notExistsCtmWithAnyDisabledAccess(AbstractQuery<?> query, CriteriaBuilder criteriaBuilder,
                                                       Path<Client> clients, Collection<Employee> employees,
                                                       AffiliatedCareTeamType type,
                                                       AccessRight.Code... disabledRights) {

        var subQuery = query.subquery(Integer.class);
        var clientCareTeamMemberRoot = subQuery.from(ClientCareTeamMember.class);
        var employeeIds = CareCoordinationUtils.toIdsSet(employees);
        subQuery.select(criteriaBuilder.literal(1))
                .where(
                        criteriaBuilder.equal(clients.get(Client_.id), clientCareTeamMemberRoot.get(ClientCareTeamMember_.clientId)),
                        criteriaBuilder.in(clientCareTeamMemberRoot.get(ClientCareTeamMember_.EMPLOYEE_ID)).value(employeeIds),
                        criteriaBuilder.not(withAllAccessRightsEnabled(
                                clientCareTeamMemberRoot,
                                query,
                                criteriaBuilder,
                                disabledRights)
                        ),
                        ofAffiliationType(clientCareTeamMemberRoot, criteriaBuilder, type)
                        //TODO on hold needed?
                );

        return criteriaBuilder.not(criteriaBuilder.exists(subQuery));
    }

    public Predicate withAllAccessRightsEnabled(From<?, ClientCareTeamMember> careTeamMember,
                                                AbstractQuery<?> query,
                                                CriteriaBuilder criteriaBuilder,
                                                AccessRight.Code... enabledRights) {
        Objects.requireNonNull(enabledRights);

        var subQuery = query.subquery(Long.class);
        var subCareTeamMember = subQuery.from(ClientCareTeamMember.class);
        var subId = subCareTeamMember.get(ClientCareTeamMember_.id);

        return careTeamMember.get(ClientCareTeamMember_.id).in(
                subQuery.select(subId)
                        .where(
                                SpecificationUtils.in(
                                        criteriaBuilder,
                                        subCareTeamMember.join(ClientCareTeamMember_.accessRights).get(AccessRight_.CODE),
                                        Arrays.asList(enabledRights)
                                ),
                                ofAffiliationType(careTeamMember, criteriaBuilder, SecurityConstants.ACCESS_FLAGS_CHECK_AMONG_CTM_TYPE)
                                //TODO on hold needed?
                        )
                        .groupBy(subId)
                        .having(criteriaBuilder.equal(criteriaBuilder.count(subId), enabledRights.length))
        );
    }

    @Deprecated
    public Predicate isClientCareTeamMember(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query,
                                            Path<Employee> employeePath,
                                            Client client,
                                            AffiliatedCareTeamType type) {
        return isClientCareTeamMember(criteriaBuilder, query, employeePath, client, type, HieConsentCareTeamType.currentAndOnHold());
    }

    public Predicate isClientCareTeamMember(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query,
                                            Path<Employee> employeePath,
                                            Client client,
                                            AffiliatedCareTeamType type,
                                            HieConsentCareTeamType consentType) {
        if (client == null) {
            return criteriaBuilder.or();
        }

        var subQuery = query.subquery(Integer.class);
        var root = subQuery.from(ClientCareTeamMember.class);
        var subClient = root.get(ClientCareTeamMember_.clientId);

        subQuery.select(criteriaBuilder.literal(1))
                .where(criteriaBuilder.and(
                                criteriaBuilder.equal(subClient, client.getId()),
                                criteriaBuilder.equal(root.get(CareTeamMember_.employeeId), employeePath.get(Employee_.id)),
                                ofAffiliationType(root, criteriaBuilder, type),
                                ofConsentType(root, criteriaBuilder, query, consentType)
                        )
                );

        return criteriaBuilder.exists(subQuery);
    }

    @Deprecated
    public Predicate clientsInClientCareTeamPredicate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query,
                                                      Path<Client> clientPath,
                                                      Collection<Employee> employees,
                                                      AffiliatedCareTeamType type,
                                                      AccessRight.Code... enabledRights) {

        return clientsInClientCareTeamPredicate(criteriaBuilder, query, clientPath, employees, type,
                HieConsentCareTeamType.currentAndOnHold(), enabledRights);
    }

    public Predicate clientsInClientCareTeamPredicate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query,
                                                      Path<Client> clientPath,
                                                      Collection<Employee> employees,
                                                      AffiliatedCareTeamType type,
                                                      HieConsentCareTeamType consentType,
                                                      AccessRight.Code... enabledRights) {
        if (CollectionUtils.isEmpty(employees)) {
            return criteriaBuilder.or();
        }

        var employeeIds = CareCoordinationUtils.toIdsSet(employees);

        var subQuery = query.subquery(Long.class);
        var root = subQuery.from(ClientCareTeamMember.class);
        return criteriaBuilder.in(clientPath.get(Client_.id))
                .value(subQuery
                        .select(root.get(ClientCareTeamMember_.clientId))
                        .where(
                                SpecificationUtils.in(criteriaBuilder, root.get(ClientCareTeamMember_.EMPLOYEE_ID), employeeIds),
                                ArrayUtils.isEmpty(enabledRights) ?
                                        criteriaBuilder.and() :
                                        withAllAccessRightsEnabled(root, query, criteriaBuilder, enabledRights),
                                ofAffiliationType(root, criteriaBuilder, type),
                                ofConsentType(root, criteriaBuilder, query, consentType)
                        )
                );
    }

    @Deprecated
    public Predicate clientsInClientCareTeamPredicate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query,
                                                      Path<Client> clientPath,
                                                      Collection<Employee> employees,
                                                      AffiliatedCareTeamType type) {

        return clientsInClientCareTeamPredicate(criteriaBuilder, query, clientPath, employees, type,
                new AccessRight.Code[0]);
    }

    public Predicate clientsInClientCareTeamPredicate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query,
                                                      Path<Client> clientPath,
                                                      Collection<Employee> employees,
                                                      AffiliatedCareTeamType type,
                                                      HieConsentCareTeamType consentType) {

        return clientsInClientCareTeamPredicate(criteriaBuilder, query, clientPath, employees, type,
                consentType, new AccessRight.Code[0]);
    }

    public Predicate clientsInClientCareTeamOfCommunityPredicate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query,
                                                                 Path<Long> communityIdPath,
                                                                 Collection<Employee> employees,
                                                                 AffiliatedCareTeamType type,
                                                                 HieConsentCareTeamType consentType) {
        if (CollectionUtils.isEmpty(employees)) {
            return criteriaBuilder.or();
        }

        var employeeIds = CareCoordinationUtils.toIdsSet(employees);

        var subQuery = query.subquery(Long.class);
        var root = subQuery.from(ClientCareTeamMember.class);
        return criteriaBuilder.in(communityIdPath)
                .value(subQuery
                        .select(root.join(ClientCareTeamMember_.client).get(Client_.communityId))
                        .where(
                                SpecificationUtils.in(criteriaBuilder, root.get(ClientCareTeamMember_.EMPLOYEE_ID), employeeIds),
                                ofAffiliationType(root, criteriaBuilder, type),
                                ofConsentType(root, criteriaBuilder, query, consentType)
                        )
                );
    }

    @Override
    protected Path<Long> getEntityOrganizationId(From<?, ClientCareTeamMember> careTeamMember) {
        var client = JpaUtils.getOrCreateJoin(careTeamMember, ClientCareTeamMember_.client);
        return client.get(Community_.organizationId);
    }

    @Override
    public Predicate hasAccess(PermissionFilter permissionFilter, From<?, ClientCareTeamMember> root, AbstractQuery<?> query,
                               CriteriaBuilder criteriaBuilder) {
        //todo - use join clientIds where necessary instead of join
        var clientJoin = JpaUtils.getOrCreateJoin(root, ClientCareTeamMember_.client);
        var clientIdPath = root.get(ClientCareTeamMember_.clientId);

        var eligible = securityPredicateGenerator.clientInEligibleForDiscoveryCommunity(clientJoin, criteriaBuilder);

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_IN_LIST_MERGED_VISIBLE_ALL_EXCEPT_OPTED_OUT)) {
            return criteriaBuilder.and(
                    eligible,
                    viewableCareTeamForAnyPredicate(
                            clientIdPath,
                            permissionFilter.getAllEmployeeIds(),
                            query,
                            criteriaBuilder
                    ),
                    clientPredicateGenerator.isOptedOut(clientJoin, criteriaBuilder).not()
            );
        }

        var mergedSubQueryLazy = Lazy.of(() -> query.subquery(Integer.class));
        var mergedFromLazy = Lazy.of(() -> mergedSubQueryLazy.get().from(MergedClientView.class));

        var mergedClientLazy = Lazy.of(() -> JpaUtils.getOrCreateJoin(mergedFromLazy.get(), MergedClientView_.mergedClient));
        var mergedClientIdLazy = Lazy.of(() -> mergedFromLazy.get().get(MergedClientView_.mergedClientId));

        var mergedSubQueryPredicates = new ArrayList<Predicate>();

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_IN_LIST_MERGED_VISIBLE_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(CLIENT_CARE_TEAM_VIEW_IN_LIST_MERGED_VISIBLE_IF_ASSOCIATED_ORGANIZATION);
            mergedSubQueryPredicates.add(securityPredicateGenerator.clientInAssociatedOrganization(criteriaBuilder, mergedClientLazy.get(), employees));
        }

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_IN_LIST_MERGED_VISIBLE_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(CLIENT_CARE_TEAM_VIEW_IN_LIST_MERGED_VISIBLE_IF_ASSOCIATED_COMMUNITY);
            mergedSubQueryPredicates.add(securityPredicateGenerator.clientInAssociatedCommunity(criteriaBuilder, mergedClientLazy.get(), employees));
        }

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_IN_LIST_MERGED_VISIBLE_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(CLIENT_CARE_TEAM_VIEW_IN_LIST_MERGED_VISIBLE_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION);
            mergedSubQueryPredicates.add(
                    criteriaBuilder.and(
                            securityPredicateGenerator.primaryCommunitiesOfOrganizations(
                                    criteriaBuilder,
                                    query,
                                    mergedClientLazy.get().get(Client_.communityId),
                                    employees
                            ),
                            clientPredicateGenerator.isOptedOut(clientJoin, criteriaBuilder).not()
                    )
            );
        }

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_IN_LIST_MERGED_VISIBLE_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(CLIENT_CARE_TEAM_VIEW_IN_LIST_MERGED_VISIBLE_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY);
            mergedSubQueryPredicates.add(
                    criteriaBuilder.and(
                            securityPredicateGenerator.primaryCommunities(
                                    criteriaBuilder,
                                    query,
                                    mergedClientLazy.get().get(Client_.communityId),
                                    employees
                            ),
                            clientPredicateGenerator.isOptedOut(clientJoin, criteriaBuilder).not()
                    )
            );
        }

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_IN_LIST_MERGED_VISIBLE_IF_CURRENT_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(CLIENT_CARE_TEAM_VIEW_IN_LIST_MERGED_VISIBLE_IF_CURRENT_RP_COMMUNITY_CTM);
            mergedSubQueryPredicates.add(securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(
                    criteriaBuilder,
                    query,
                    mergedClientLazy.get().get(Client_.communityId),
                    employees,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.current(clientJoin)
            ));
        }

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_IN_LIST_MERGED_VISIBLE_OPTED_IN_IF_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(CLIENT_CARE_TEAM_VIEW_IN_LIST_MERGED_VISIBLE_OPTED_IN_IF_CLIENT_ADDED_BY_SELF);
            mergedSubQueryPredicates.add(
                    criteriaBuilder.and(
                            securityPredicateGenerator.clientAddedByEmployees(criteriaBuilder, mergedClientLazy.get(), employees),
                            clientPredicateGenerator.isOptedOut(clientJoin, criteriaBuilder).not()
                    )
            );
        }

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_IN_LIST_MERGED_VISIBLE_IF_CURRENT_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(CLIENT_CARE_TEAM_VIEW_IN_LIST_MERGED_VISIBLE_IF_CURRENT_RP_CLIENT_CTM);

            mergedSubQueryPredicates.add(securityPredicateGenerator.clientsInClientCareTeamPredicate(
                    criteriaBuilder,
                    query,
                    mergedClientLazy.get(),
                    employees,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.current(clientJoin)
            ));
        }

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_IN_LIST_MERGED_VISIBLE_IF_SELF_CLIENT_RECORD)) {
            var employees = permissionFilter.getEmployees(CLIENT_CARE_TEAM_VIEW_IN_LIST_MERGED_VISIBLE_IF_SELF_CLIENT_RECORD);
            mergedSubQueryPredicates.add(securityPredicateGenerator.selfRecordClients(criteriaBuilder,
                    mergedClientIdLazy.get(), employees));
        }

        if (permissionFilter.hasPermission(CLIENT_CARE_TEAM_VIEW_IN_LIST_MERGED_VISIBLE_IF_CLIENT_FOUND_IN_RECORD_SEARCH) &&
                CollectionUtils.isNotEmpty(permissionFilter.getClientRecordSearchFoundIds())) {
            mergedSubQueryPredicates.add(mergedClientIdLazy.get().in(permissionFilter.getClientRecordSearchFoundIds()));
        }

        var predicates = new ArrayList<Predicate>();

        if (!mergedSubQueryPredicates.isEmpty()) {

            var mergedSubQuery = mergedSubQueryLazy.get();

            mergedSubQuery.select(criteriaBuilder.literal(1));
            mergedSubQuery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.or(mergedSubQueryPredicates.toArray(Predicate[]::new)),
                            criteriaBuilder.equal(clientJoin, mergedFromLazy.get().get(MergedClientView_.clientId))
                    )
            );

            predicates.add(criteriaBuilder.exists(mergedSubQuery));
        }

        //todo figure out onHold
        return criteriaBuilder.and(
                eligible,
                criteriaBuilder.or(predicates.toArray(new Predicate[0])),
                viewableCareTeamForAnyPredicate(clientIdPath, permissionFilter.getAllEmployeeIds(), query, criteriaBuilder)
        );
    }

    public Predicate notViewableCareTeamForAllPredicate(Path<Long> clientIdPath, Collection<Long> employeeIds,
                                                        AbstractQuery<?> criteriaQuery, CriteriaBuilder cb) {
        if (CollectionUtils.isEmpty(employeeIds)) {
            return cb.and();
        }

        var subquery = criteriaQuery.subquery(Long.class);
        var notViewableCareTeamRoot = subquery.from(NotViewableCareTeam.class);

        var result = subquery.select(cb.literal(1L))
                .where(
                        SpecificationUtils.in(cb, notViewableCareTeamRoot.get(NotViewableCareTeam_.employeeId), employeeIds),
                        cb.equal(clientIdPath, notViewableCareTeamRoot.get(NotViewableCareTeam_.clientId))
                        //todo not onHold?
                )
                //checking that care team is disabled for all provided employeeIds
                .groupBy(notViewableCareTeamRoot.get(NotViewableCareTeam_.clientId))
                .having(cb.equal(cb.countDistinct(notViewableCareTeamRoot.get(NotViewableCareTeam_.employeeId)), employeeIds.size()));
        return cb.exists(result);
    }

    private Predicate viewableCareTeamForAnyPredicate(Path<Long> clientIdPath, Collection<Long> employeeIds,
                                                      AbstractQuery<?> query, CriteriaBuilder cb) {
        return cb.not(notViewableCareTeamForAllPredicate(clientIdPath, employeeIds, query, cb));
    }

    public  <T extends IdAware> Predicate onHoldCandidates(T client, Root<ClientCareTeamMember> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
                onHoldCandidates(root, criteriaBuilder),
                criteriaBuilder.equal(root.get(ClientCareTeamMember_.clientId), client.getId())
        );
    }

    public Predicate onHoldCandidates(From<?, ClientCareTeamMember> root, CriteriaBuilder criteriaBuilder) {

        //"ON Hold" = PRS and FM in same community, Any role in other community in any org
        var employeeJoin = JpaUtils.getOrCreateJoin(root, ClientCareTeamMember_.employee);
        var client = JpaUtils.getOrCreateJoin(root, ClientCareTeamMember_.client);
        var systemRoleJoin = JpaUtils.getOrCreateJoin(employeeJoin, Employee_.careTeamRole);

        return criteriaBuilder.or(
                criteriaBuilder.notEqual(employeeJoin.get(Employee_.communityId), client.get(Client_.communityId)),
                SpecificationUtils.in(
                        criteriaBuilder,
                        systemRoleJoin.get(CareTeamRole_.code),
                        List.of(
                                CareTeamRoleCode.ROLE_SUPER_ADMINISTRATOR,
                                CareTeamRoleCode.ROLE_PARENT_GUARDIAN,
                                CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES
                        ))
        );
    }

    @Override
    public Predicate ofConsentType(From<?, ClientCareTeamMember> root, CriteriaBuilder criteriaBuilder,
                                   AbstractQuery<?> query, HieConsentCareTeamType consentType) {
        if (consentType.isIncludesCurrent() && consentType.isIncludesOnHold()) {
            return criteriaBuilder.and();
        }

        if (consentType.isIncludesCurrent()) {
            return criteriaBuilder.isFalse(root.get(ClientCareTeamMember_.onHold));
        }

        if (consentType.isIncludesOnHold()) {
            return criteriaBuilder.isTrue(root.get(ClientCareTeamMember_.onHold));
        }

        return criteriaBuilder.or();
    }
}
