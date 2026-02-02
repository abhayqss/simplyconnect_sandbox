package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.*;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dto.hieconsentpolicy.ClientHieConsentPolicyData;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.CommunityCareTeamMember;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicySource;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.service.careteam.invitation.ClientCareTeamInvitationService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.CareTeamUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class HieConsentPolicyUpdateServiceImpl implements HieConsentPolicyUpdateService {
    private static final Logger logger = LoggerFactory.getLogger(HieConsentPolicyUpdateServiceImpl.class);

    @Autowired
    private ClientHieConsentPolicyUpdateService clientHieConsentPolicyUpdateService;

    @Autowired
    private ClientCareTeamHieConsentPolicyUpdateService clientCareTeamHieConsentPolicyUpdateService;

    @Autowired
    private CommunityCareTeamHieConsentPolicyUpdateService communityCareTeamHieConsentPolicyUpdateService;

    @Autowired
    private ChatHieConsentPolicyUpdateService chatHieConsentPolicyUpdateService;

    @Autowired
    @Lazy
    private DocumentSignatureRequestService signatureRequestService;

    @Autowired
    private CommunityHieConsentPolicyService communityHieConsentPolicyService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private ClientCareTeamInvitationService clientCareTeamInvitationService;

    @Autowired
    private CommunityService communityService;


    @Override
    public void updateHieConsentPolicyByClient(Client client, HieConsentPolicyType type, HieConsentPolicySource source, Employee author) {
        logger.info("Updating hie consent policy by client, id = {}, type = {}", client.getId(), type);

        //todo no updates if policy not changed
        clientHieConsentPolicyUpdateService.updateHieConsentPolicyByClient(client, type, source, author);

        updateOnHoldCareTeamAndChatsConnection(client, type, author.getId());

        if (HieConsentPolicyType.OPT_OUT.equals(type)) {
            signatureRequestService.cancelRequestsForOnHoldCtmByClientIdAsync(client.getId(), author.getId());
            clientCareTeamInvitationService.cancelInvitationsForOptOutAsync(client.getId(), client.getOrganization().getAlternativeId());
        }
    }

    @Override
    public void updateHieConsentPolicyByStaff(Client client, ClientHieConsentPolicyData data) {
        //todo no updates if policy not changed
        clientHieConsentPolicyUpdateService.updateHieConsentPolicy(client, data);

        updateOnHoldCareTeamAndChatsConnection(client, data.getType(),
                Optional.ofNullable(data.getAuthor()).map(BasicEntity::getId).orElse(null));

        if (HieConsentPolicyType.OPT_OUT.equals(data.getType())) {
            signatureRequestService.cancelRequestsForOnHoldCtmByClientIdAsync(client.getId(), data.getAuthor().getId());
            clientCareTeamInvitationService.cancelInvitationsForOptOutAsync(client.getId(), client.getOrganization().getAlternativeId());
        }
    }

    @Override
    public void updateCommunityDefaultHieConsentPolicy(Community community, HieConsentPolicyType type, HieConsentPolicySource source, Employee author) {
        var policyUpdated = communityHieConsentPolicyService.saveOrUpdate(community, type, author);
        if (policyUpdated) {

            var data = new ClientHieConsentPolicyData();
            data.setType(type);
            data.setSource(source);
            data.setObtainedBy(null);
            data.setObtainedFrom(OBTAINED_FROM_STATE_POLICY_VALUE);
            data.setUpdateDateTime(Instant.now());

            clientHieConsentPolicyUpdateService.updateHieConsentPolicyWithDefaultCommunityPolicy(community.getId(), data);

            var affectedClients = updateOnHoldCareTeamAndChatsConnection(community.getId(), type, author.getId());

            updateSignatureRequests(community.getId(), type, author.getId());

            if (type == HieConsentPolicyType.OPT_OUT) {
                clientCareTeamInvitationService.cancelInvitationsForOptOutInCommunityAsync(
                        CareCoordinationUtils.toIdsSet(affectedClients),
                        community.getOrganization().getAlternativeId()
                );
            }

        }
    }

    @Override
    public void updateSignatureRequests(Long communityId, HieConsentPolicyType type, Long authorId) {
        if (HieConsentPolicyType.OPT_OUT.equals(type)) {
            signatureRequestService.cancelRequestsForOnHoldCtmByCommunityIdAsync(communityId, authorId);
        }
    }

    @Override
    public void contactChanged(Employee employee, Long performedById) {
        var recalculatedCurrentClientCtmOnHold = clientCareTeamHieConsentPolicyUpdateService.calculateCurrentOnHoldCandidate(
                employee.getId(),
                ClientCtmEmployeeUpdateProjection.class
        );

        if (MapUtils.isEmpty(recalculatedCurrentClientCtmOnHold)) {
            return;
        }

        var toSetAsCurrent = recalculatedCurrentClientCtmOnHold.getOrDefault(true, List.of())
                .stream()
                .filter(OnHoldAware::getOnHold)
                .map(IdAware::getId)
                .collect(Collectors.toList());
        clientCareTeamHieConsentPolicyUpdateService.setCurrent(toSetAsCurrent);

        var toSetAsOnHold = recalculatedCurrentClientCtmOnHold.getOrDefault(false, List.of())
                .stream()
                .filter(ctm -> !ctm.getOnHold())
                .collect(Collectors.toList());
        clientCareTeamHieConsentPolicyUpdateService.setOnHold(toSetAsOnHold, performedById);

        Set<Long> communitiesChangedUserIsCurrentCommunityCtm = findCommunitiesChangedUserIsCurrentCommunityCtm(
                recalculatedCurrentClientCtmOnHold.values().stream().flatMap(Collection::stream),
                employee
        );

        var communitiesChangedUserIsOnHoldCommunityCtm = communityCareTeamHieConsentPolicyUpdateService.userIsOnHoldCommunityCareTeamMember(
                        employee.getId(),
                        CommunityIdAware.class).stream()
                .map(CommunityIdAware::getCommunityId)
                .collect(Collectors.toSet());

        var clientsWhereOnHoldAsCommunityCtm = clientHieConsentPolicyUpdateService.findOptOutClientsInCommunities(
                communitiesChangedUserIsOnHoldCommunityCtm,
                IdCommunityIdAssociatedEmployeeIdsAware.class
        );

        var ctmChangedUserBecameCurrent = recalculatedCurrentClientCtmOnHold.getOrDefault(true, List.of())
                .stream()
                .filter(OnHoldAware::getOnHold)
                .collect(Collectors.toList());

        var ctmChangedUserIsOnHold = recalculatedCurrentClientCtmOnHold.getOrDefault(false, List.of())
                .stream()
                //current if current as community CTM
                .filter(ctm -> !communitiesChangedUserIsCurrentCommunityCtm.contains(ctm.getClientCommunityId()))
                .collect(Collectors.toList());

        var optOutClientContactWhereChangedUserBecameCurrent = ctmChangedUserBecameCurrent.stream()
                .map(ClientAssociatedEmployeeIdAware::getClientAssociatedEmployeeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        var optOutClientContactWhereChangedUserIsOnHold =
                Stream.concat(
                                ctmChangedUserIsOnHold.stream().map(ClientAssociatedEmployeeIdAware::getClientAssociatedEmployeeId),
                                clientsWhereOnHoldAsCommunityCtm.stream().map(AssociatedEmployeeIdsAware::getAssociatedEmployeeId)
                        )
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        var contactsSharingOptOutClientCtmWhereBecameCurrent = resolveContactsSharingClientOrCommunityCtm(
                employee.getId(),
                ctmChangedUserBecameCurrent
        );

        var contactsSharingOptOutClientCtmWhereIsOnHold = resolveContactsSharingClientOrCommunityCtm(
                employee.getId(),
                Stream.concat(
                                ctmChangedUserIsOnHold.stream().map(ClientIdAware::getClientId),
                                clientsWhereOnHoldAsCommunityCtm.stream().map(IdAware::getId))
                        .collect(Collectors.toSet()),
                Stream.concat(
                                ctmChangedUserIsOnHold.stream().map(ClientCommunityIdAware::getClientCommunityId),
                                CareCoordinationUtils.getCommunityIds(clientsWhereOnHoldAsCommunityCtm))
                        .collect(Collectors.toSet())
        );

        chatHieConsentPolicyUpdateService.contactChangedAsync(
                employee,
                optOutClientContactWhereChangedUserBecameCurrent,
                optOutClientContactWhereChangedUserIsOnHold,
                contactsSharingOptOutClientCtmWhereBecameCurrent,
                contactsSharingOptOutClientCtmWhereIsOnHold,
                createPermissionFilterProvider()
        );
    }

    private Set<Long> findCommunitiesChangedUserIsCurrentCommunityCtm(Stream<ClientCtmEmployeeUpdateProjection> recalculatedCurrentClientCtmOnHold, Employee employee) {
        var communitiesToCheckChangedUserCanBeCurrent = recalculatedCurrentClientCtmOnHold
                .filter(ctm -> !communityCareTeamHieConsentPolicyUpdateService.isOnHoldCandidate(ctm.getClientOrganizationId(), employee.getOrganizationId()))
                .map(ClientCommunityIdAware::getClientCommunityId)
                .collect(Collectors.toSet());

        var communitiesChangedUserIsCurrentCommunityCtm = communityCareTeamHieConsentPolicyUpdateService.communityCareTeamMemberEntries(
                        employee.getId(),
                        communitiesToCheckChangedUserCanBeCurrent,
                        CommunityIdAware.class
                ).stream()
                .map(CommunityIdAware::getCommunityId)
                .collect(Collectors.toSet());
        return communitiesChangedUserIsCurrentCommunityCtm;
    }

    private <T extends ClientIdAware & ClientCommunityIdAware> Set<Long> resolveContactsSharingClientOrCommunityCtm(Long changedEmployeeId,
                                                                                                                    Collection<T> clients) {
        return resolveContactsSharingClientOrCommunityCtm(changedEmployeeId,
                clients.stream().map(ClientIdAware::getClientId).collect(Collectors.toList()),
                clients.stream().map(ClientCommunityIdAware::getClientCommunityId).collect(Collectors.toSet())
        );
    }

    private Set<Long> resolveContactsSharingClientOrCommunityCtm(Long changedEmployeeId,
                                                                 Collection<Long> clientIds,
                                                                 Collection<Long> clientCommunityIds) {
        return Stream.of(
                        clientCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfClients(
                                clientIds,
                                changedEmployeeId,
                                EmployeeIdAware.class),
                        communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunities(
                                clientCommunityIds,
                                changedEmployeeId,
                                EmployeeIdAware.class)
                )
                .flatMap(Collection::stream)
                .map(EmployeeIdAware::getEmployeeId)
                .collect(Collectors.toSet());
    }

    private <T extends IdAware & CommunityIdAware & AssociatedEmployeeIdAware> void updateOnHoldCareTeamAndChatsConnection(
            Client client,
            HieConsentPolicyType type,
            Long authorId
    ) {
        updateOnHoldCareTeamAndChatsConnection(
                List.of(IdCommunityIdAssociatedEmployeeIdsAware.of(client)),
                clientCareTeamHieConsentPolicyUpdateService.onHoldCandidates(client),
                type,
                authorId
        );
    }

    @Override
    public List<IdCommunityIdAssociatedEmployeeIdsAware> updateOnHoldCareTeamAndChatsConnection(Long communityId, HieConsentPolicyType type, Long authorId) {
        var clients = clientHieConsentPolicyUpdateService.findWithCommunityHieConsentPolicy(communityId);
        var clientOnHoldCandidates = clientCareTeamHieConsentPolicyUpdateService.onHoldCandidatesByClientCommunityId(communityId);
        updateOnHoldCareTeamAndChatsConnection(clients, clientOnHoldCandidates, type, authorId);

        return clients;
    }

    private <T extends IdAware & CommunityIdAware & AssociatedEmployeeIdAware> void updateOnHoldCareTeamAndChatsConnection(
            List<T> clients,
            List<ClientCareTeamMemberIdsAware> clientCtmOnHoldCandidates,
            HieConsentPolicyType type,
            Long authorId

    ) {
        if (type != HieConsentPolicyType.OPT_OUT && type != HieConsentPolicyType.OPT_IN) {
            throw new RuntimeException("Unknown consent type " + type);
        }

        var clientIds = CareCoordinationUtils.toIdsSet(clients);

        var clientCtmOnHoldCandidatesMap = new HashMap<Long, List<ClientCareTeamMemberIdsAware>>();
        clientCtmOnHoldCandidates.forEach(ctm ->
                clientCtmOnHoldCandidatesMap.computeIfAbsent(ctm.getClientId(), (k) -> new LinkedList<>())
                        .add(ctm)
        );

        var communityIds = CareCoordinationUtils.getCommunityIds(clients);
        var communityCtmMembers = communityIds
                .distinct()
                .collect(Collectors.toMap(
                                Function.identity(),
                                communityId -> communityCareTeamHieConsentPolicyUpdateService.communityCareTeamCurrentOnHoldCandidatesGrouped(communityId)
                        )
                );

        Map<Long, Set<Long>> onHoldEmployeeIds = CareTeamUtils.resolveOnHoldCareTeamEmployeeIds(
                clients, clientCtmOnHoldCandidatesMap, communityCtmMembers
        );

        var permissionFilterProvider = createPermissionFilterProvider();

        if (type == HieConsentPolicyType.OPT_OUT) {
            var allowedChatsBeforeOnHoldUpdate = chatHieConsentPolicyUpdateService.allowedChatsBeforeOnHoldUpdate(
                    clients, onHoldEmployeeIds, permissionFilterProvider
            );
            logger.info("Allowed chats before on hold update: {}", allowedChatsBeforeOnHoldUpdate);

            var clientCtmIds = clientCtmOnHoldCandidatesMap.values().stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            clientCareTeamHieConsentPolicyUpdateService.setOnHold(clientCtmIds, authorId);
            logger.info("Set {} client care team members on hold for clients {}", clientCtmIds.size(), clientIds);

            //todo async?
            chatHieConsentPolicyUpdateService.disconnectConversations(
                    clients, onHoldEmployeeIds, permissionFilterProvider, allowedChatsBeforeOnHoldUpdate
            );
            logger.info("Disconnected conversations for clients {}", clientIds);
        } else {
            var clientCtmIds = clientCtmOnHoldCandidatesMap.values().stream()
                    .flatMap(Collection::stream)
                    .map(IdAware::getId)
                    .collect(Collectors.toList());
            clientCareTeamHieConsentPolicyUpdateService.setCurrent(clientCtmIds);
            logger.info("Set {} client care team members current for client {}", clientCtmIds.size(), clientIds);

            //todo async?
            chatHieConsentPolicyUpdateService.reconnectConversations(clients, onHoldEmployeeIds, permissionFilterProvider);
            logger.info("Reconnected conversations for clients {}", clientIds);
        }
    }

    @Override
    public void communityCareTeamMemberAdded(CommunityCareTeamMember communityCareTeamMember) {
        var communityId = Optional.ofNullable(communityCareTeamMember.getCommunityId())
                .orElseGet(() ->
                        Optional.ofNullable(communityCareTeamMember.getCommunity())
                                .map(BasicEntity::getId)
                                .orElseThrow()
                );

        //affected all clients in community
        var clientsInCommunity = clientHieConsentPolicyUpdateService.findClientsInCommunity(
                communityId, ClientEntityCareTeamUpdateProjection.class
        );

        var optOutClientExistsInCommunity = clientsInCommunity.stream().anyMatch(clientHieConsentPolicyUpdateService::isOptOutPolicy);

        careTeamMemberAdded(communityCareTeamMember.getEmployee(), communityId, clientsInCommunity, optOutClientExistsInCommunity);
    }

    @Override
    public void clientCareTeamMemberAdded(ClientCareTeamMember clientCareTeamMember) {
        if (!communityCareTeamHieConsentPolicyUpdateService.isOnHoldCandidate(clientCareTeamMember.getClient().getCommunityId(),
                clientCareTeamMember.getEmployee().getOrganizationId()) && communityCareTeamHieConsentPolicyUpdateService.isUserCommunityCareTeamMember(
                clientCareTeamMember.getEmployeeId(), clientCareTeamMember.getClient().getCommunityId())
        ) {
            //already added as current community care team member, so no changes needed
            return;
        }

        var communityId = Optional.ofNullable(clientCareTeamMember.getClient().getCommunityId())
                .orElseGet(() ->
                        Optional.ofNullable(clientCareTeamMember.getClient().getCommunity())
                                .map(BasicEntity::getId)
                                .orElseThrow()
                );

        boolean optOutClientExistsInCommunity = clientHieConsentPolicyUpdateService.existsOptOutClientsInCommunity(communityId);

        careTeamMemberAdded(
                clientCareTeamMember.getEmployee(),
                communityId,
                //affected just client where added as CTM
                List.of(ClientEntityCareTeamUpdateProjection.ofClient(clientCareTeamMember.getClient())),
                optOutClientExistsInCommunity
        );
    }

    private void careTeamMemberAdded(Employee employee, Long communityId, List<ClientEntityCareTeamUpdateProjection> affectedClients,
                                     boolean optOutClientExistsInCommunity) {
        //resolve affected client contacts
        Set<Long> optOutClientContactWhereChangedUserCtmBecameCurrent = getOptOutClientsContacts(affectedClients);
        Set<Long> contactsSharingOptOutClientCtmWhereBecameCurrent = careTeamUpdateCtmBecameCurrentForAddOrOnHoldForRemove(employee, communityId, affectedClients);
        Set<Long> contactsSharingOptOutClientCtmWhereIsOnHold = careTeamUpdateCtmOnHoldForAddOrBecameCurrentForRemove(employee, communityId, affectedClients, optOutClientExistsInCommunity);


        chatHieConsentPolicyUpdateService.contactChangedAsync(employee,
                optOutClientContactWhereChangedUserCtmBecameCurrent,
                Set.of(),
                contactsSharingOptOutClientCtmWhereBecameCurrent,
                contactsSharingOptOutClientCtmWhereIsOnHold,
                createPermissionFilterProvider()
        );
    }


    @Override
    public void communityCareTeamMemberDeleted(Employee employee, Community community) {
        //1. ctm was on hold, remains on hold - no changes
        //2. ctm was on hold, becomes current (or not ctm) - do changes
        //3. ctm was current, becomes on hold - do changes
        //4. ctm was current, remains current (or not ctm) - no changes

        //also deleting care team member breaks 'share care team' permission, so some connected chats may disconnect.
        var wasOnHold = communityCareTeamHieConsentPolicyUpdateService.isOnHoldCandidate(
                community.getOrganizationId(), employee.getOrganizationId()
        );

        var clientsInCommunity = clientHieConsentPolicyUpdateService.findClientsInCommunity(
                community.getId(), ClientEntityCareTeamUpdateProjection.class
        );

        if (wasOnHold) {
            var addedAsOnHoldClientCtmClientIds = clientCareTeamHieConsentPolicyUpdateService.onHoldAmongClientIds(
                            CareCoordinationUtils.toIdsSet(clientsInCommunity),
                            employee.getId(),
                            ClientIdAware.class
                    )
                    .stream()
                    .map(ClientIdAware::getClientId)
                    .collect(Collectors.toSet());

            //affected are where not on hold as client ctm
            var affectedClients = clientsInCommunity.stream()
                    .filter(client -> !addedAsOnHoldClientCtmClientIds.contains(client.getId()))
                    .collect(Collectors.toList());

            boolean isOptOutClientExistInCommunityAndNotOnHoldCtmForAll = false;
            if (CollectionUtils.isEmpty(addedAsOnHoldClientCtmClientIds)) {
                isOptOutClientExistInCommunityAndNotOnHoldCtmForAll = clientsInCommunity.stream()
                        .anyMatch(clientHieConsentPolicyUpdateService::isOptOutPolicy);
            }

            onHoldCareTeamMemberDeleted(
                    employee,
                    community.getId(),
                    affectedClients,
                    isOptOutClientExistInCommunityAndNotOnHoldCtmForAll
            );
        } else {
            boolean optOutClientExistsInCommunity = clientsInCommunity.stream().anyMatch(clientHieConsentPolicyUpdateService::isOptOutPolicy);
            currentCareTeamMemberDeleted(
                    employee,
                    community.getId(),
                    clientsInCommunity,
                    optOutClientExistsInCommunity);
        }
    }

    @Override
    public void clientCareTeamMemberDeleted(Employee employee,
                                            Client client,
                                            boolean wasOnHold) {

        var addedAsCommunityCtms = communityCareTeamHieConsentPolicyUpdateService.communityCareTeamMemberEntries(
                employee.getId(),
                List.of(client.getCommunityId()),
                EmployeeOrgIdCommunityOrgIdAware.class
        );

        if (addedAsCommunityCtms.stream().anyMatch(ctm -> wasOnHold == communityCareTeamHieConsentPolicyUpdateService.isOnHoldCandidate(
                ctm.getCommunityOrganizationId(), ctm.getEmployeeOrganizationId()))) {
            return;
        }

        var addedAsClientCtms = clientCareTeamHieConsentPolicyUpdateService.findClientCareTeamEntries(
                employee.getId(),
                client.getId(),
                OnHoldAware.class);

        if (addedAsClientCtms.stream().anyMatch(ctm -> wasOnHold == ctm.getOnHold())) {
            return;
        }

        if (wasOnHold) {
            boolean isOptOutClientExistInCommunityAndNotOnHoldCtmForAll;
            var existOptOptClientInCommunity = clientHieConsentPolicyUpdateService.existsOptOutClientsInCommunity(client.getCommunityId());
            if (!existOptOptClientInCommunity) {
                isOptOutClientExistInCommunityAndNotOnHoldCtmForAll = false;
            } else {
                isOptOutClientExistInCommunityAndNotOnHoldCtmForAll = !clientCareTeamHieConsentPolicyUpdateService.isOnHoldForAnyClientInCommunity(
                        employee.getId(),
                        client.getCommunityId()
                );
            }

            onHoldCareTeamMemberDeleted(employee,
                    client.getCommunityId(),
                    List.of(ClientEntityCareTeamUpdateProjection.ofClient(client)),
                    isOptOutClientExistInCommunityAndNotOnHoldCtmForAll
            );
        } else {
            //curr as comm ctm or client ctm

            boolean optOutClientExistsInCommunity = clientHieConsentPolicyUpdateService.existsOptOutClientsInCommunity(client.getCommunityId());

            currentCareTeamMemberDeleted(employee,
                    client.getCommunityId(),
                    List.of(ClientEntityCareTeamUpdateProjection.ofClient(client)),
                    optOutClientExistsInCommunity
            );
        }
    }

    private void currentCareTeamMemberDeleted(Employee employee,
                                              Long communityId,
                                              List<ClientEntityCareTeamUpdateProjection> affectedClients,
                                              boolean optOutClientExistsInCommunity) {
        Set<Long> optOutClientContactWhereChangedUserCtmBecameCurrent = Set.of();
        Set<Long> optOutClientContactWhereChangedUserCtmIsOnHold = getOptOutClientsContacts(affectedClients);
        Set<Long> contactsSharingOptOutClientCtmWhereBecameCurrentOrNew = careTeamUpdateCtmOnHoldForAddOrBecameCurrentForRemove(employee, communityId, affectedClients, optOutClientExistsInCommunity);
        Set<Long> contactsSharingOptOutClientCtmWhereIsOnHold = careTeamUpdateCtmBecameCurrentForAddOrOnHoldForRemove(employee, communityId, affectedClients);

        chatHieConsentPolicyUpdateService.contactChangedAsync(employee,
                optOutClientContactWhereChangedUserCtmBecameCurrent,
                optOutClientContactWhereChangedUserCtmIsOnHold,
                contactsSharingOptOutClientCtmWhereBecameCurrentOrNew,
                contactsSharingOptOutClientCtmWhereIsOnHold,
                createPermissionFilterProvider()
        );
    }

    private void onHoldCareTeamMemberDeleted(Employee employee,
                                             Long communityId,
                                             List<ClientEntityCareTeamUpdateProjection> affectedClients,
                                             boolean isOptOutClientExistInCommunityAndNotOnHoldCtmForAll) {
        Set<Long> optOutClientContactWhereChangedUserCtmBecameCurrent;
        Set<Long> optOutClientContactWhereChangedUserCtmIsOnHold;
        Set<Long> contactsSharingOptOutClientCtmWhereBecameCurrentOrNew;
        Set<Long> contactsSharingOptOutClientCtmWhereIsOnHold;

        optOutClientContactWhereChangedUserCtmBecameCurrent = getOptOutClientsContacts(affectedClients);
        optOutClientContactWhereChangedUserCtmIsOnHold = Set.of();


        //	contactsSharingOptOutClientCtmWhereBecameCurrentOrNew*
        //1. take client CTM of affected clients (both current and OH)
        var clientCtmOfAffectedClients = clientCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfClients(
                CareCoordinationUtils.toIdsSet(affectedClients),
                employee.getId(),
                EmployeeIdAware.class
        );


        //2. current ctm of community
        var currentCtmOfCommunity = communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeCurrent(
                List.of(communityId),
                employee.getId(),
                EmployeeIdAware.class
        );

        //3. if exists opt out client in community and not added as OH ctm to any client in community - OH ctm of community
        var onHoldCommunityCtm = isOptOutClientExistInCommunityAndNotOnHoldCtmForAll ?
                communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeOnHold(
                        List.of(communityId),
                        employee.getId(),
                        EmployeeIdAware.class) :
                List.<EmployeeIdAware>of();


        //4. opt in clients contacts
        var optInClientContact = getOptInClientsContacts(affectedClients);

        //5. combine
        contactsSharingOptOutClientCtmWhereBecameCurrentOrNew =
                Stream.concat(
                        Stream.of(
                                        clientCtmOfAffectedClients,
                                        currentCtmOfCommunity,
                                        onHoldCommunityCtm
                                ).flatMap(Collection::stream)
                                .map(EmployeeIdAware::getEmployeeId),

                        optInClientContact.stream()
                ).collect(Collectors.toSet());


        //		optimization - exclude contacts with whom share OH CTM:
        //			a. take clients for whom added as client ctm OH
        //			b. take their client ctm and community ctm (among from step 6)
        //			c. take opt out clients where added as OH community ctm
        //			d. take their client ctm (among from step 2)
        //			e. take community ctm where added as OH community ctm (among from step 2)
        //			f. exclude
        //		next optimization - exclude contacts with whom keep sharing current ctm
        //			a. take clients for whom added as current client ctm
        //			b. take their current client ctm and community ctm (among from step 2)
        //			c. take opt in clients for whom added as current community ctm
        //			d. take their current client ctm (among from step 2)
        //			e. take current community ctm where added as current community ctm (among from step 2)
        //			f. exclude

        //optimization specifically not implemented so that if there are errors in other chat update triggers users will
        //be rechecked and such errors are fixed

        contactsSharingOptOutClientCtmWhereIsOnHold = Set.of();

        chatHieConsentPolicyUpdateService.contactChangedAsync(employee,
                optOutClientContactWhereChangedUserCtmBecameCurrent,
                optOutClientContactWhereChangedUserCtmIsOnHold,
                contactsSharingOptOutClientCtmWhereBecameCurrentOrNew,
                contactsSharingOptOutClientCtmWhereIsOnHold,
                createPermissionFilterProvider()
        );
    }

    private Set<Long> getOptOutClientsContacts(List<ClientEntityCareTeamUpdateProjection> affectedClients) {
        var optOutClientContactWhereChangedUserCtmBecameCurrent = CareCoordinationUtils.getAssociatedEmployeeIds(
                affectedClients.stream()
                        .filter(clientHieConsentPolicyUpdateService::isOptOutPolicy)

        );
        return optOutClientContactWhereChangedUserCtmBecameCurrent;
    }

    //todo rename
    private Set<Long> careTeamUpdateCtmBecameCurrentForAddOrOnHoldForRemove(Employee employee, Long communityId, List<ClientEntityCareTeamUpdateProjection> affectedClients) {
        // 1. take current client ctm of clients in community
        var currentClientCtmOfClientsInCommunity = clientCareTeamHieConsentPolicyUpdateService.findCurrentCareTeamMembersOfClients(
                        CareCoordinationUtils.toIdsSet(affectedClients),
                        employee.getId(),
                        EmployeeIdAware.class).stream()
                .map(EmployeeIdAware::getEmployeeId)
                .collect(Collectors.toList());

        //2. current ctm of community
        var otherCurrentCommunityCtmOfCommunity = communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeCurrent(
                        List.of(communityId), employee.getId(), EmployeeIdAware.class
                )
                .stream()
                .map(EmployeeIdAware::getEmployeeId)
                .collect(Collectors.toList());

        //3. opt in clients contacts
        Set<Long> optInClientsContacts = getOptInClientsContacts(affectedClients);

        //4. combine 1 and 2 and 3
        var result = new HashSet<>(currentClientCtmOfClientsInCommunity);
        result.addAll(otherCurrentCommunityCtmOfCommunity);
        result.addAll(optInClientsContacts);
        return result;
    }

    private Set<Long> getOptInClientsContacts(List<ClientEntityCareTeamUpdateProjection> affectedClients) {
        return CareCoordinationUtils.getAssociatedEmployeeIds(
                affectedClients.stream()
                        .filter(client -> !clientHieConsentPolicyUpdateService.isOptOutPolicy(client))
        );
    }

    private Set<Long> careTeamUpdateCtmOnHoldForAddOrBecameCurrentForRemove(Employee employee, Long communityId, List<ClientEntityCareTeamUpdateProjection> affectedClients,
                                                                            boolean optOutClientExistsInCommunity) {
        //1. take opt out clients from community for whom was not on hold
        var optOutClientIds = affectedClients.stream()
                .filter(clientHieConsentPolicyUpdateService::isOptOutPolicy)
                .map(IdAware::getId)
                .collect(Collectors.toSet());

        var optOutClientIdsForWhomOnHoldAmongAffected = clientCareTeamHieConsentPolicyUpdateService.onHoldAmongClientIds(
                        optOutClientIds,
                        employee.getId(),
                        ClientIdAware.class).stream()
                .map(ClientIdAware::getClientId)
                .collect(Collectors.toSet());

        var optOutClientIdsForWhomNotOnHoldAmongAffected = optOutClientIds.stream()
                .filter(clientId -> !optOutClientIdsForWhomOnHoldAmongAffected.contains(clientId))
                .collect(Collectors.toList());

        //2. take their on hold client ctm
        var theirOnHoldClientCtm = clientCareTeamHieConsentPolicyUpdateService.findOnHoldCareTeamMembersOfClients(
                optOutClientIdsForWhomNotOnHoldAmongAffected,
                employee.getId(),
                EmployeeIdAware.class
        );

        //3. take contacts who are OH community ctm if exist opt out client in community
        var onHoldCommunityCtm = optOutClientExistsInCommunity ?
                communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeOnHold(
                        List.of(communityId),
                        employee.getId(),
                        EmployeeIdAware.class) :
                Set.<EmployeeIdAware>of();


        var contactsSharingOptOutClientCtmWhereIsOnHold = Stream.concat(
                        theirOnHoldClientCtm.stream(),
                        onHoldCommunityCtm.stream()
                )
                .map(EmployeeIdAware::getEmployeeId)
                .collect(Collectors.toSet());
        return contactsSharingOptOutClientCtmWhereIsOnHold;
    }


    private Function<Long, PermissionFilter> createPermissionFilterProvider() {
        return new Function<>() {
            private final Map<Long, PermissionFilter> cache = new HashMap<>();

            @Override
            public PermissionFilter apply(Long employeeId) {
                if (cache.containsKey(employeeId)) {
                    return cache.get(employeeId);
                }
                var filter = permissionFilterService.createPermissionFilterForUser(employeeId);
                cache.put(employeeId, filter);
                return filter;
            }
        };
    }

    @Override
    public <T> List<T> findIncomingCareTeamInvitationsForHieConsentChangeInCommunity(Long communityId, Class<T> projectionClass) {
        var organizationAlternativeId = communityService.findById(communityId, OrganizationAlternativeIdAware.class).getOrganizationAlternativeId();

        return clientCareTeamInvitationService.findIncomingForHieConsentChangeInCommunity(
                CareCoordinationUtils.toIdsSet(clientHieConsentPolicyUpdateService.findWithCommunityHieConsentPolicy(communityId)),
                organizationAlternativeId,
                projectionClass
        );
    }

    interface ClientCtmEmployeeUpdateProjection extends IdEmployeeIdAware, OnHoldAware,
            ClientAssociatedEmployeeIdAware, ClientIdAware, ClientCommunityIdAware, ClientOrganizationIdAware {
    }

    interface ClientEntityCareTeamUpdateProjection extends IdAware, HieConsentPolicyTypeAware,
            AssociatedEmployeeIdsAware {

        static ClientEntityCareTeamUpdateProjection ofClient(Client client) {
            return new ClientEntityCareTeamUpdateProjection() {
                @Override
                public List<Long> getAssociatedEmployeeIds() {
                    return client.getAssociatedEmployeeIds();
                }

                @Override
                public HieConsentPolicyType getHieConsentPolicyType() {
                    return client.getHieConsentPolicyType();
                }

                @Override
                public Long getId() {
                    return client.getId();
                }
            };
        }
    }
}
