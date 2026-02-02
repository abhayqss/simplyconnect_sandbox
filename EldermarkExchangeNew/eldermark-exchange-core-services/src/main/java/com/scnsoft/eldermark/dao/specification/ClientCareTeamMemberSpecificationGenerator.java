package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.CareTeamMemberPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.ClientCareTeamMemberPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole_;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember_;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.From;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@Component
public class ClientCareTeamMemberSpecificationGenerator extends CareTeamMemberSpecificationGenerator<ClientCareTeamMember> {

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    @Autowired
    private ClientCareTeamMemberPredicateGenerator clientCareTeamMemberPredicateGenerator;

    public Specification<ClientCareTeamMember> ofMergedByClientId(Long clientId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (clientId == null) {
                return criteriaBuilder.or();
            }

            var clientJoin = root.join(ClientCareTeamMember_.client);

            return clientPredicateGenerator.withMergedClients(clientJoin,
                    clientFrom -> criteriaBuilder.equal(clientFrom.get(Client_.id), clientId)
                    , criteriaBuilder, criteriaQuery);
        };
    }

    public Specification<ClientCareTeamMember> ofMergedByClients(List<Client> clients) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (CollectionUtils.isEmpty(clients)) {
                return criteriaBuilder.or();
            }

            var clientJoin = root.join(ClientCareTeamMember_.client);

            return clientPredicateGenerator.withMergedClients(clientJoin,
                    clientFrom -> clientFrom.in(clients)
                    , criteriaBuilder, criteriaQuery);
        };
    }

    public Specification<ClientCareTeamMember> notViewableCareTeamForAll(Collection<Long> employeeIds) {
        return (root, criteriaQuery, criteriaBuilder) ->
                clientCareTeamMemberPredicateGenerator.notViewableCareTeamForAllPredicate(
                        root.get(ClientCareTeamMember_.clientId),
                        employeeIds,
                        criteriaQuery,
                        criteriaBuilder
                );
    }

    public Specification<ClientCareTeamMember> byClientId(Long clientId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(JpaUtils.getOrCreateJoin(root, ClientCareTeamMember_.client).get(Client_.id), clientId);
    }

    public Specification<ClientCareTeamMember> byClientHieConsentPolicy(HieConsentPolicyType policy) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(
                        JpaUtils.getOrCreateJoin(root, ClientCareTeamMember_.client)
                                .get(Client_.hieConsentPolicyType),
                        policy
                );
    }

    public Specification<ClientCareTeamMember> withAllAccessRightsEnabled(AccessRight.Code... enabledRights) {
        return (root, criteriaQuery, criteriaBuilder) -> clientCareTeamMemberPredicateGenerator.withAllAccessRightsEnabled(root,
                criteriaQuery, criteriaBuilder, enabledRights);
    }

    public Specification<ClientCareTeamMember> byClientOrganizationId(Long organizationId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(ClientCareTeamMember_.client).get(Client_.organizationId), organizationId);
    }

    public Specification<ClientCareTeamMember> byClientCommunityId(Long communityId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(JpaUtils.getOrCreateJoin(root, ClientCareTeamMember_.client).get(Client_.communityId), communityId);
    }

    public Specification<ClientCareTeamMember> byClientCommunityIds(Collection<Long> communityIds) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(root.get(ClientCareTeamMember_.client).get(Client_.COMMUNITY_ID)).value(communityIds);
    }

    public Specification<ClientCareTeamMember> byClientIdIn(Collection<Long> clientIds) {
        return (root, criteriaQuery, criteriaBuilder) ->
                CollectionUtils.isEmpty(clientIds) ?
                        criteriaBuilder.or() :
                        SpecificationUtils.in(criteriaBuilder, root.get(ClientCareTeamMember_.clientId), clientIds);
    }

    public Specification<ClientCareTeamMember> isActive() {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.isTrue(JpaUtils.getOrCreateJoin(root, ClientCareTeamMember_.client).get(Client_.active));
    }

    public Specification<ClientCareTeamMember> clientHasActiveAssociatedEmployee() {
        return (root, criteriaQuery, criteriaBuilder) -> clientPredicateGenerator.hasActiveAssociatedEmployee(criteriaBuilder, JpaUtils.getOrCreateJoin(root, ClientCareTeamMember_.client));
    }

    @Override
    protected CareTeamMemberPredicateGenerator<ClientCareTeamMember> getPredicateGenerator() {
        return clientCareTeamMemberPredicateGenerator;
    }

    public Specification<ClientCareTeamMember> isContactActive() {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(JpaUtils.getOrCreateJoin(root, ClientCareTeamMember_.employee).get(Employee_.status), EmployeeStatus.ACTIVE);
    }

    public Specification<ClientCareTeamMember> byContactRoleIn(Collection<CareTeamRoleCode> roles) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var employeeJoin = JpaUtils.getOrCreateJoin(root, ClientCareTeamMember_.employee);
            return JpaUtils.getOrCreateJoin(employeeJoin, Employee_.careTeamRole).get(CareTeamRole_.code).in(roles);
        };
    }

    public Specification<ClientCareTeamMember> byEmployeeStatusIn(List<EmployeeStatus> statuses) {
        return (root, query, criteriaBuilder) -> {
            var employeeJoin = JpaUtils.getOrCreateJoin(root, ClientCareTeamMember_.employee);
            return employeeJoin.get(Employee_.status).in(statuses);
        };
    }

    public Specification<ClientCareTeamMember> isClientActiveInPeriod(
            Instant instantFrom,
            Instant instantTo
    ) {
        return (root, query, criteriaBuilder) -> clientPredicateGenerator.isActiveInPeriod(
                instantFrom,
                instantTo,
                JpaUtils.getOrCreateJoin(root, ClientCareTeamMember_.client),
                query,
                criteriaBuilder
        );
    }

    public Specification<ClientCareTeamMember> onHoldCandidates() {
        return (root, criteriaQuery, criteriaBuilder) -> clientCareTeamMemberPredicateGenerator.onHoldCandidates(
                root, criteriaBuilder);
    }

    public <T extends IdAware & CommunityIdAware> Specification<ClientCareTeamMember> onHoldCandidates(T client) {
        return (root, criteriaQuery, criteriaBuilder) -> clientCareTeamMemberPredicateGenerator.onHoldCandidates(
                client,
                root,
                criteriaBuilder
        );
    }

    public Specification<ClientCareTeamMember> ofConsentType(HieConsentCareTeamType consentType) {
        return (root, criteriaQuery, criteriaBuilder) -> clientCareTeamMemberPredicateGenerator.ofConsentType(
                root, criteriaBuilder, criteriaQuery, consentType
        );
    }

    public Specification<ClientCareTeamMember> ofConsentType(Function<From<?, Client>, HieConsentCareTeamType> consentType) {
        return (root, criteriaQuery, criteriaBuilder) -> clientCareTeamMemberPredicateGenerator.ofConsentType(
                root, criteriaBuilder, criteriaQuery, consentType.apply(JpaUtils.getOrCreateJoin(root, ClientCareTeamMember_.client))
        );
    }

    public Specification<ClientCareTeamMember> clientOptOutPolicy() {
        return (root, criteriaQuery, criteriaBuilder) -> clientPredicateGenerator.isOptedOut(
                JpaUtils.getOrCreateJoin(root, ClientCareTeamMember_.client),
                criteriaBuilder
        );

    }
}
