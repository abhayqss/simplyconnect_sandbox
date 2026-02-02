package com.scnsoft.eldermark.service;

import com.google.common.collect.Sets;
import com.scnsoft.eldermark.beans.ConversationType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.AssociatedEmployeeIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.IdCommunityIdAssociatedEmployeeIdsAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.chat.GroupChatParticipantHistoryDao;
import com.scnsoft.eldermark.dao.chat.PersonalChatDao;
import com.scnsoft.eldermark.dao.specification.ChatSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.ClientSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.SpecificationUtils;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.chat.GroupChatParticipantHistory;
import com.scnsoft.eldermark.entity.chat.PersonalChat;
import com.scnsoft.eldermark.service.twilio.ChatService;
import com.scnsoft.eldermark.service.twilio.ConversationUtils;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.CareTeamUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class ChatHieConsentPolicyUpdateServiceImpl implements ChatHieConsentPolicyUpdateService {

    //todo better logging
    private static final Logger logger = LoggerFactory.getLogger(ChatHieConsentPolicyUpdateServiceImpl.class);

    @Autowired
    @Lazy
    private ChatService chatService;

    @Autowired
    private GroupChatParticipantHistoryDao groupChatParticipantHistoryDao;

    @Autowired
    private PersonalChatDao personalChatDao;

    @Autowired
    private ChatSpecificationGenerator chatSpecificationGenerator;

    @Autowired
    private ConversationAllowedBetweenUsersResolver conversationAllowedBetweenUsersResolver;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private ClientSpecificationGenerator clientSpecificationGenerator;

    @Autowired
    private ClientCareTeamHieConsentPolicyUpdateService clientCareTeamHieConsentPolicyUpdateService;

    @Autowired
    private CommunityCareTeamHieConsentPolicyUpdateService communityCareTeamHieConsentPolicyUpdateService;

    @Autowired
    private CareTeamMemberService careTeamMemberService;

    @Override
    public <T extends IdAware & AssociatedEmployeeIdAware> Map<ConversationType, Set<String>> allowedChatsBeforeOnHoldUpdate(List<T> clients, Map<Long, Set<Long>> onHoldCandidatesEmployeeIds, Function<Long, PermissionFilter> permissionFilterProvider) {
        if (!chatService.isChatEnabled()) {
            return Map.of();
        }
        return findAllowedChatsForDisconnect(
                clients,
                onHoldCandidatesEmployeeIds,
                permissionFilterProvider
        );
    }

    @Override
    public <T extends IdAware & AssociatedEmployeeIdAware> void disconnectConversations(
            List<T> clients,
            Map<Long, Set<Long>> onHoldEmployeeIds,
            Function<Long, PermissionFilter> permissionFilterProvider,
            Map<ConversationType, Set<String>> allowedChatsBeforeOnHoldUpdate
    ) {
        if (!chatService.isChatEnabled()) {
            return;
        }
        var allowedChatsAfterOnHoldUpdates = findAllowedChatsForDisconnect(clients,
                onHoldEmployeeIds,
                permissionFilterProvider);

        var personalChatsToDisconnect = Sets.difference(
                allowedChatsBeforeOnHoldUpdate.get(ConversationType.PERSONAL),
                allowedChatsAfterOnHoldUpdates.get(ConversationType.PERSONAL)
        );

        var clientIds = CareCoordinationUtils.toIdsSet(clients);
        logger.info("Disconnecting {} personal chats for clients {}", personalChatsToDisconnect.size(), clientIds);
        chatService.updateConversationsDisconnection(personalChatsToDisconnect, true, false);

        var groupChatsToDisconnect = Sets.difference(
                allowedChatsBeforeOnHoldUpdate.get(ConversationType.GROUP),
                allowedChatsAfterOnHoldUpdates.get(ConversationType.GROUP)
        );

        logger.info("Disconnecting {} group chats for clients {}", groupChatsToDisconnect.size(), clientIds);
        chatService.updateConversationsDisconnection(groupChatsToDisconnect, true, false);
    }

    private <T extends IdAware & AssociatedEmployeeIdAware> Map<ConversationType, Set<String>> findAllowedChatsForDisconnect(
            List<T> clients,
            Map<Long, Set<Long>> onHoldEmployeeIds,
            Function<Long, PermissionFilter> permissionFilterProvider
    ) {

        Specification<PersonalChat> personalChatsSpec = SpecificationUtils.or();
        Specification<GroupChatParticipantHistory> groupChatSpec = SpecificationUtils.or();
        var associatedEmployeeIdToClientIds = new HashMap<Long, List<Long>>(clients.size());
        var associatedEmployeeIdentities = new HashSet<>();
        var idIdentityCache = new IdIdentityCache();
        for (var client : clients) {
            var associatedEmployeeId = client.getAssociatedEmployeeId();
            var associatedEmployeeIdentity = Optional.ofNullable(associatedEmployeeId)
                    .map(ConversationUtils::employeeIdToIdentity)
                    .orElse(null);

            var onHoldIdentities = idIdentityCache.addAndConvertIds(onHoldEmployeeIds.getOrDefault(client.getId(), Set.of()));

            var onHoldAndCurrent = new HashSet<>(onHoldIdentities);
            if (associatedEmployeeIdentity != null) {
                idIdentityCache.add(associatedEmployeeId, associatedEmployeeIdentity);
                onHoldAndCurrent.add(associatedEmployeeIdentity);
                groupChatSpec = groupChatSpec.or(chatSpecificationGenerator.groupChatBetweenAnyUserAndUser(onHoldIdentities, associatedEmployeeIdentity));
                associatedEmployeeIdToClientIds.compute(associatedEmployeeId, (key, v) -> {
                    if (v == null) {
                        v = new ArrayList<>();
                    }
                    v.add(client.getId());
                    return v;
                });
                associatedEmployeeIdentities.add(associatedEmployeeIdentity);
            }
            if (!onHoldAndCurrent.isEmpty()) {
                personalChatsSpec = personalChatsSpec.or(chatSpecificationGenerator.personalChatsOfUsers(onHoldAndCurrent));
            }
        }

        //todo provide hint to check among chats
        var personalChats = personalChatDao.findAll(
                personalChatsSpec.and(
                        chatSpecificationGenerator.connectedPersonalChats()
                )
        );

        var groupChatHistories = associatedEmployeeIdentities.isEmpty() ?
                List.<GroupChatParticipantHistory>of() :
                groupChatParticipantHistoryDao.findAll(
                        groupChatSpec.and(chatSpecificationGenerator.groupChatEntriesOfConnectedChats())
                );
        var groupChats = groupChatHistories.stream()
                .collect(Collectors.groupingBy(GroupChatParticipantHistory::getTwilioConversationSid));

        var toCheckBetweenEmployees = resolveToCheckPermissionsBetweenEmployeesForDisconnect(
                onHoldEmployeeIds,
                associatedEmployeeIdToClientIds,
                associatedEmployeeIdentities,
                personalChats,
                groupChats,
                idIdentityCache
        );

        var allowedConversationsBetween = conversationAllowedBetweenUsersResolver.resolveConversationsAllowedBetween(
                toCheckBetweenEmployees,
                permissionFilterProvider
        );

        var result = new HashMap<ConversationType, Set<String>>(2);
        result.put(ConversationType.PERSONAL, new HashSet<>(personalChats.size()));

        for (PersonalChat personalChat : personalChats) {
            var employeeId1 = idIdentityCache.getId(personalChat.getTwilioIdentity1());
            var employeeId2 = idIdentityCache.getId(personalChat.getTwilioIdentity2());

            if (allowedConversationsBetween.getOrDefault(employeeId1, Set.of()).contains(employeeId2)) {
                result.get(ConversationType.PERSONAL).add(personalChat.getTwilioConversationSid());
            }
        }

        result.put(ConversationType.GROUP, new HashSet<>(groupChats.size()));
        groupChats.forEach((conversationSid, groupChatParticipants) -> {
                    var associatedEmployeeIdsInChat = groupChatParticipants.stream()
                            .map(GroupChatParticipantHistory::getTwilioIdentity)
                            .filter(associatedEmployeeIdentities::contains)
                            .map(idIdentityCache::getId);

                    if (associatedEmployeeIdsInChat.allMatch(associatedEmployeeIdInChat -> {
                        var allowedForAssociatedContact = allowedConversationsBetween.get(associatedEmployeeIdInChat);
                        return CollectionUtils.isNotEmpty(allowedForAssociatedContact) && findOnHoldEmployeeIdsAmongParticipants(
                                groupChatParticipants,
                                associatedEmployeeIdInChat,
                                associatedEmployeeIdToClientIds,
                                onHoldEmployeeIds,
                                idIdentityCache
                        )
                                .allMatch(allowedForAssociatedContact::contains);
                    })) {
                        result.get(ConversationType.GROUP).add(conversationSid);
                    }
                }
        );

        return result;
    }

    private HashMap<Long, Set<Long>> resolveToCheckPermissionsBetweenEmployeesForDisconnect(
            Map<Long, Set<Long>> onHoldEmployeeIds,
            Map<Long, List<Long>> associatedEmployeeIdToClientIds,
            Set<Object> associatedEmployeeIdentities,
            List<PersonalChat> personalChats,
            Map<String, List<GroupChatParticipantHistory>> groupChats,
            IdIdentityCache idIdentityCache) {
        var toCheckBetweenEmployees = new HashMap<Long, Set<Long>>(
                (int) onHoldEmployeeIds.values().stream().mapToLong(Collection::size).sum() + associatedEmployeeIdentities.size()
        );


        //for personal chats - check access between each other
        for (PersonalChat personalChat : personalChats) {
            var employeeId1 = idIdentityCache.getId(personalChat.getTwilioIdentity1());
            var employeeId2 = idIdentityCache.getId(personalChat.getTwilioIdentity2());

            CareCoordinationUtils.putBidirectionally(toCheckBetweenEmployees, employeeId1, employeeId2);
        }


        //for group chats - between client and on hold in both directions
        groupChats.forEach((conversationSid, groupChatParticipants) -> {
            var associatedContactIdsInChat = groupChatParticipants.stream()
                    .map(GroupChatParticipantHistory::getTwilioIdentity)
                    .filter(associatedEmployeeIdentities::contains)
                    .map(idIdentityCache::getId);

            associatedContactIdsInChat.forEach(associatedEmployeeIdInChat -> findOnHoldEmployeeIdsAmongParticipants(
                    groupChatParticipants,
                    associatedEmployeeIdInChat,
                    associatedEmployeeIdToClientIds,
                    onHoldEmployeeIds,
                    idIdentityCache)
                    .forEach(groupChatParticipantEmployeeId -> CareCoordinationUtils.putBidirectionally(
                                    toCheckBetweenEmployees,
                                    associatedEmployeeIdInChat,
                                    groupChatParticipantEmployeeId
                            )
                    ));
        });
        return toCheckBetweenEmployees;
    }

    private Stream<Long> findOnHoldEmployeeIdsAmongParticipants(Collection<GroupChatParticipantHistory> groupChatParticipants,
                                                                Long associatedEmployeeIdInChat,
                                                                Map<Long, List<Long>> associatedEmployeeIdToClientId,
                                                                Map<Long, Set<Long>> onHoldEmployeeIds,
                                                                IdIdentityCache idIdentityCache) {
        return groupChatParticipants.stream()
                .map(GroupChatParticipantHistory::getTwilioIdentity)
                .map(idIdentityCache::getId)
                .filter(groupChatParticipantEmployeeId ->
                        associatedEmployeeIdToClientId.getOrDefault(associatedEmployeeIdInChat, List.of()).stream()
                                .map(clientId -> onHoldEmployeeIds.getOrDefault(clientId, Set.of()))
                                .anyMatch(onHoldsOfClient -> onHoldsOfClient.contains(groupChatParticipantEmployeeId))
                );
    }

    @Override
    public <T extends IdAware & AssociatedEmployeeIdAware> void reconnectConversations(
            List<T> clients,
            Map<Long, Set<Long>> onHoldEmployeeIds,
            Function<Long, PermissionFilter> permissionFilterProvider
    ) {
        if (!chatService.isChatEnabled()) {
            return;
        }

        //todo add comment with full rules
        var idIdentityCache = new IdIdentityCache();

        var associatedEmployeeIds = clients.stream()
                .map(AssociatedEmployeeIdAware::getAssociatedEmployeeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        var associatedEmployeeIdentities = idIdentityCache.addAndConvertIds(associatedEmployeeIds);

        var allOnHoldIdentities = idIdentityCache.addAndConvertIds(onHoldEmployeeIds.values().stream()
                .flatMap(Collection::stream)
                .distinct());

        var reconnectTypeGroupedPersonalChats = findPersonalChatsWithClientsGroupedForReconnect(
                associatedEmployeeIdentities,
                allOnHoldIdentities,
                clients,
                onHoldEmployeeIds,
                idIdentityCache
        );

        var groupChatsWithClient = associatedEmployeeIdentities.isEmpty() ?
                Map.<String, List<GroupChatParticipantHistory>>of() :
                groupChatParticipantHistoryDao.findAll(
                                chatSpecificationGenerator.groupChatEntriesOfChatsContainingAnyUser(associatedEmployeeIdentities)
                                        .and(chatSpecificationGenerator.groupChatEntriesOfDisconnectedChats())
                        ).stream()
                        .collect(Collectors.groupingBy(GroupChatParticipantHistory::getTwilioConversationSid));

        var employeesWithAssociatedOptOutClients = findEmployeesWithAssociatedOptOutClients(
                groupChatsWithClient, idIdentityCache
        );

        var groupChatsWithOptOutClients = findGroupChatsWithOptOutClients(
                groupChatsWithClient,
                employeesWithAssociatedOptOutClients,
                idIdentityCache
        );

        var onHoldCareTeamForOtherClientAssociatedContact = findOnHoldCareTeamInGroupChatsForClientAssociatedContact(
                groupChatsWithClient,
                groupChatsWithOptOutClients,
                employeesWithAssociatedOptOutClients,
                idIdentityCache
        );

        var allowedConversationsBetween = checkPermissionsBetweenUsers(
                permissionFilterProvider,
                //#c1 requires permission checks
                reconnectTypeGroupedPersonalChats.getOrDefault(PersonalChatReconnectionType.CLIENT_AND_ANY, List.of()),
                //#c4 requires permission checks
                onHoldCareTeamForOtherClientAssociatedContact,
                idIdentityCache
        );


        var personalChatsToConnect = new HashSet<String>();
        var groupChatsToConnect = new HashSet<String>();


        //#c1
        reconnectTypeGroupedPersonalChats.getOrDefault(PersonalChatReconnectionType.CLIENT_AND_ANY, List.of())
                .forEach(personalChat -> {
                    var employeeId1 = idIdentityCache.getId(personalChat.getTwilioIdentity1());
                    var employeeId2 = idIdentityCache.getId(personalChat.getTwilioIdentity2());

                    if (allowedConversationsBetween.getOrDefault(employeeId1, Set.of()).contains(employeeId2) ||
                            !isShareOnHoldCareTeamForAnyClient(employeeId1, employeeId2)) {
                        personalChatsToConnect.add(personalChat.getTwilioConversationSid());
                    }
                });

        //#c2
        reconnectTypeGroupedPersonalChats.getOrDefault(PersonalChatReconnectionType.ON_HOLDS, List.of())
                .stream()
                .map(PersonalChat::getTwilioConversationSid)
                .forEach(personalChatsToConnect::add);

        //#c3
        reconnectTypeGroupedPersonalChats.getOrDefault(PersonalChatReconnectionType.ON_HOLD_AND_ANY_OTHER, List.of())
                .forEach(personalChat -> {
                    var employeeId1 = idIdentityCache.getId(personalChat.getTwilioIdentity1());
                    var employeeId2 = idIdentityCache.getId(personalChat.getTwilioIdentity2());

                    if (!isShareOnHoldCareTeamForAnyClient(employeeId1, employeeId2)) {
                        personalChatsToConnect.add(personalChat.getTwilioConversationSid());
                    }
                });

        //#c4
        groupChatsWithClient.forEach((conversationSid, participants) -> {
            if (shouldConnectGroupChat(conversationSid, participants, groupChatsWithOptOutClients,
                    employeesWithAssociatedOptOutClients, onHoldCareTeamForOtherClientAssociatedContact,
                    allowedConversationsBetween, idIdentityCache
            )) {
                groupChatsToConnect.add(conversationSid);
            }
        });

        var clientIds = CareCoordinationUtils.toIdsSet(clients);
        logger.info("Reconnecting {} personal chats for clients {}", personalChatsToConnect.size(), clientIds);
        chatService.updateConversationsDisconnection(personalChatsToConnect, false, false);

        logger.info("Reconnecting {} group chats for clients {}", groupChatsToConnect.size(), clientIds);
        chatService.updateConversationsDisconnection(groupChatsToConnect, false, false);
    }

    private Map<Long, Set<Long>> findOnHoldCareTeamInGroupChatsForClientAssociatedContact(Map<String, List<GroupChatParticipantHistory>> allGroupChats,
                                                                                          Set<String> groupChatsWithOptOutClients,
                                                                                          Map<Long, List<IdCommunityIdAssociatedEmployeeIdsAware>> employeesWithAssociatedOptOutClients,
                                                                                          IdIdentityCache idIdentityCache) {
        var otherOptOutClientToCheckOnHoldAmong =
                CareCoordinationUtils.<IdCommunityIdAssociatedEmployeeIdsAware, Set<Long>>idsComparingMap();
        groupChatsWithOptOutClients.forEach(conversationSid -> {
            var participants = allGroupChats.get(conversationSid);

            var participantIds = idIdentityCache.addAndConvertIdentities(participants.stream()
                    .map(GroupChatParticipantHistory::getTwilioIdentity));


            //find opt out clients in chat
            var optOutClientContactsInChat = participantIds.stream()
                    .filter(employeesWithAssociatedOptOutClients::containsKey)
                    .collect(Collectors.toList());

            //add other participants as users to check on hold among
            optOutClientContactsInChat.forEach(employeeId -> {
                var otherParticipants = participantIds.stream().filter(e -> !employeeId.equals(e))
                        .collect(Collectors.toList());
                employeesWithAssociatedOptOutClients.get(employeeId)
                        .forEach(optOutClient -> {
                            otherOptOutClientToCheckOnHoldAmong.computeIfAbsent(optOutClient, c -> new HashSet<>());
                            otherOptOutClientToCheckOnHoldAmong.get(optOutClient).addAll(otherParticipants);
                        });
            });
        });

        //find on hold ctm
        var onHoldCareTeamForOtherClientAssociatedContact = new HashMap<Long, Set<Long>>();
        otherOptOutClientToCheckOnHoldAmong.forEach((outOutClient, otherEmployeeIdsToCheckOnHold) -> {
            var onHoldCtm = findOnHoldCareTeamMemberEmployeeIds(outOutClient, otherEmployeeIdsToCheckOnHold);
            onHoldCareTeamForOtherClientAssociatedContact.put(outOutClient.getAssociatedEmployeeId(), onHoldCtm);
        });
        return onHoldCareTeamForOtherClientAssociatedContact;
    }

    private <T extends IdAware & AssociatedEmployeeIdAware> Map<PersonalChatReconnectionType, List<PersonalChat>> findPersonalChatsWithClientsGroupedForReconnect(
            Set<String> associatedEmployeeIdentities,
            Set<String> allOnHoldIdentities,
            List<T> clients,
            Map<Long, Set<Long>> onHoldEmployeeIds,
            IdIdentityCache idIdentityCache
    ) {
        var personalChatSpec = clients.stream().map(client -> {
                    var associatedEmployeeId = client.getAssociatedEmployeeId();
                    var associatedEmployeeIdentity = Optional.ofNullable(associatedEmployeeId)
                            .map(idIdentityCache::getIdentity)
                            .orElse(null);

                    var onHoldIdentities = onHoldEmployeeIds.getOrDefault(client.getId(), Set.of()).stream()
                            .map(idIdentityCache::getIdentity)
                            .collect(Collectors.toSet());

                    var allIdentities = new HashSet<>(onHoldIdentities);
                    if (associatedEmployeeIdentity != null) {
                        allIdentities.add(associatedEmployeeIdentity);
                    }

                    return allIdentities.isEmpty()
                            ? null
                            : chatSpecificationGenerator.personalChatsOfUsers(allIdentities);
                })
                .filter(Objects::nonNull)
                .reduce(Specification::or)
                .orElseGet(SpecificationUtils::or);

        var personalChatsOfAllUsers = personalChatDao.findAll(
                personalChatSpec.and(
                        chatSpecificationGenerator.disconnectedPersonalChats()
                )
        );

        var groupedPersonalChats = personalChatsOfAllUsers.stream()
                .collect(Collectors.groupingBy(personalChatsReconnectTypeResolver(associatedEmployeeIdentities, allOnHoldIdentities)));
        return groupedPersonalChats;
    }

    private Set<String> findGroupChatsWithOptOutClients(Map<String, List<GroupChatParticipantHistory>> groupChatsWithClient,
                                                        Map<Long, List<IdCommunityIdAssociatedEmployeeIdsAware>> employeesWithAssociatedOptOutClients,
                                                        IdIdentityCache idIdentityCache) {
        return groupChatsWithClient.entrySet().stream()
                .filter(e -> isGroupChatContainsAny(e.getValue(), employeesWithAssociatedOptOutClients.keySet(), idIdentityCache))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private boolean isGroupChatContainsAny(List<GroupChatParticipantHistory> participants,
                                           Set<Long> employeesToCheck,
                                           IdIdentityCache idIdentityCache) {
        return participants.stream()
                .map(GroupChatParticipantHistory::getTwilioIdentity)
                .map(idIdentityCache::getId)
                .anyMatch(employeesToCheck::contains);
    }

    private Map<Long, List<IdCommunityIdAssociatedEmployeeIdsAware>> findEmployeesWithAssociatedOptOutClients(
            Map<String, List<GroupChatParticipantHistory>> groupChatsWithClient,
            IdIdentityCache idIdentityCache) {
        var allOtherGroupChatParticipantsEmployeeIds = groupChatsWithClient.values()
                .stream()
                .flatMap(Collection::stream)
                .map(GroupChatParticipantHistory::getTwilioIdentity)
                .map(idIdentityCache::getId)
                .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(allOtherGroupChatParticipantsEmployeeIds)) {
            return Map.of();
        }

        var optedOutClientsAmongOtherGroupChatParticipants = clientDao.findAll(
                clientSpecificationGenerator.byAssociatedEmployeeIdIn(allOtherGroupChatParticipantsEmployeeIds).and(
                        clientSpecificationGenerator.isOptOutPolicy()
                ), IdCommunityIdAssociatedEmployeeIdsAware.class
        );

        return optedOutClientsAmongOtherGroupChatParticipants.stream()
                .collect(Collectors.groupingBy(AssociatedEmployeeIdAware::getAssociatedEmployeeId));
    }

    private Set<Long> findOnHoldCareTeamMemberEmployeeIds(IdCommunityIdAssociatedEmployeeIdsAware client, Set<Long> checkAmongEmployeeIds) {
        var clientCtmOnHold = clientCareTeamHieConsentPolicyUpdateService.onHoldAmongEmployeeIds(client.getId(), checkAmongEmployeeIds);
        var communityCtmGrouped = communityCareTeamHieConsentPolicyUpdateService.communityCareTeamCurrentOnHoldCandidatesAmongEmployeeIdsGrouped(
                client.getCommunityId(),
                checkAmongEmployeeIds
        );

        return CareTeamUtils.resolveOnHoldCareTeamEmployeeIds(clientCtmOnHold, communityCtmGrouped);
    }

    private boolean isShareOnHoldCareTeamForAnyClient(Long employeeId1, Long employeeId2) {
        return careTeamMemberService.doesAnyShareCtmWithEmployee(List.of(employeeId1), employeeId2,
                HieConsentCareTeamType.onHold(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID)
        );
    }

    private Function<PersonalChat, PersonalChatReconnectionType> personalChatsReconnectTypeResolver(Set<String> associatedEmployeeIdentities,
                                                                                                    Set<String> onHoldIdentities) {
        return personalChat -> {
            if (associatedEmployeeIdentities.contains(personalChat.getTwilioIdentity1()) ||
                    associatedEmployeeIdentities.contains(personalChat.getTwilioIdentity2())) {
                return PersonalChatReconnectionType.CLIENT_AND_ANY;
            }

            if (onHoldIdentities.contains(personalChat.getTwilioIdentity1()) && onHoldIdentities.contains(personalChat.getTwilioIdentity2())) {
                return PersonalChatReconnectionType.ON_HOLDS;
            }
            return PersonalChatReconnectionType.ON_HOLD_AND_ANY_OTHER;
        };
    }

    @Override
    public void contactChanged(Employee employee,
                               Collection<Long> optOutClientContactWhereChangedUserCtmBecameCurrent,
                               Collection<Long> optOutClientContactWhereChangedUserCtmIsOnHold,
                               Collection<Long> contactsSharingOptOutClientCtmWhereBecameCurrent,
                               Collection<Long> contactsSharingOptOutClientCtmWhereIsOnHold,
                               Function<Long, PermissionFilter> permissionFilterProvider) {
//      1. was on hold, remained on hold - chat may connect due to permissions
//      2. was on hold, became current - chat may connect due to permissions
//      3. was current, became on hold - chat may disconnect due to permissions
//      4. was current, remained current - chat will stay connected anyway
        if (!chatService.isChatEnabled()) {
            return;
        }

        if (CollectionUtils.isEmpty(optOutClientContactWhereChangedUserCtmBecameCurrent) &&
                CollectionUtils.isEmpty(optOutClientContactWhereChangedUserCtmIsOnHold) &&
                CollectionUtils.isEmpty(contactsSharingOptOutClientCtmWhereIsOnHold) &&
                CollectionUtils.isEmpty(contactsSharingOptOutClientCtmWhereBecameCurrent)) {
            return;
        }

        var idIdentityCache = new IdIdentityCache();

        var employeeIdentity = ConversationUtils.employeeIdToIdentity(employee.getId());
        idIdentityCache.add(employee.getId(), employeeIdentity);

        //names are way too long, maybe we can shorten them?
        var optOutClientContactWhereChangedUserCtmBecameCurrentIdentities = idIdentityCache.addAndConvertIds(optOutClientContactWhereChangedUserCtmBecameCurrent);
        var optOutClientContactWhereChangedUserCtmIsOnHoldIdentities = idIdentityCache.addAndConvertIds(optOutClientContactWhereChangedUserCtmIsOnHold);
        var contactsSharingOptOutClientCtmWhereBecameCurrentIdentities = idIdentityCache.addAndConvertIds(contactsSharingOptOutClientCtmWhereBecameCurrent);
        var contactsSharingOptOutClientCtmWhereIsOnHoldIdentities = idIdentityCache.addAndConvertIds(contactsSharingOptOutClientCtmWhereIsOnHold);

        //fetch chats...
        var personalChatsChangedUserIsOnHoldAndOptOutClient = personalChatDao.findAll(
                chatSpecificationGenerator.personalChatBetweenAnyUserAndUser(
                        optOutClientContactWhereChangedUserCtmIsOnHoldIdentities,
                        employeeIdentity
                )
        );

        var personalChatsChangedUserBecameCurrentAndOptOutClient = personalChatDao.findAll(
                chatSpecificationGenerator.personalChatBetweenAnyUserAndUser(
                        optOutClientContactWhereChangedUserCtmBecameCurrentIdentities,
                        employeeIdentity
                )
        );

        var personalChatsChangedUserAndCtmColleagues = personalChatDao.findAll(
                chatSpecificationGenerator.personalChatBetweenAnyUserAndUser(
                        Sets.union(
                                contactsSharingOptOutClientCtmWhereIsOnHoldIdentities,
                                contactsSharingOptOutClientCtmWhereBecameCurrentIdentities),
                        employeeIdentity
                )
        );

        var allGroupChatsWithChangedUserAndOptOutClient = groupChatParticipantHistoryDao.findAll(
                chatSpecificationGenerator.groupChatBetweenAnyUserAndUser(
                        Sets.union(
                                optOutClientContactWhereChangedUserCtmBecameCurrentIdentities,
                                optOutClientContactWhereChangedUserCtmIsOnHoldIdentities),
                        employeeIdentity
                )
        ).stream().collect(Collectors.groupingBy(GroupChatParticipantHistory::getTwilioConversationSid));

        //now find users sharing any opt client CTMs in group chats...
        var employeesWithAssociatedOptOutClients = findEmployeesWithAssociatedOptOutClients(
                allGroupChatsWithChangedUserAndOptOutClient, idIdentityCache
        );

        var onHoldCareTeamForClientAssociatedContact = findOnHoldCareTeamInGroupChatsForClientAssociatedContact(
                allGroupChatsWithChangedUserAndOptOutClient,
                allGroupChatsWithChangedUserAndOptOutClient.keySet(),
                employeesWithAssociatedOptOutClients,
                idIdentityCache
        );

        //now check permissions where needed
        var allowedConversationsBetween = checkPermissionsBetweenUsers(
                permissionFilterProvider,
                IterableUtils.chainedIterable(
                        personalChatsChangedUserIsOnHoldAndOptOutClient,
                        personalChatsChangedUserAndCtmColleagues
                ),
                onHoldCareTeamForClientAssociatedContact,
                idIdentityCache
        );

        //and now run checks
        var personalChatsToConnect = new HashSet<String>();
        var personalChatsToDisconnect = new HashSet<String>();
        var groupChatsToConnect = new HashSet<String>();
        var groupChatsToDisconnect = new HashSet<String>();

        personalChatsChangedUserBecameCurrentAndOptOutClient.forEach(personalChat -> {
            //connect because became current for this client
            personalChatsToConnect.add(personalChat.getTwilioConversationSid());
        });

        personalChatsChangedUserIsOnHoldAndOptOutClient.forEach(personalChat -> {
            var conversationSid = personalChat.getTwilioConversationSid();

            var employeeId1 = idIdentityCache.getId(personalChat.getTwilioIdentity1());
            var employeeId2 = idIdentityCache.getId(personalChat.getTwilioIdentity2());

            if (allowedConversationsBetween.getOrDefault(employeeId1, Set.of()).contains(employeeId2)) {
                personalChatsToConnect.add(conversationSid);
            } else {
                personalChatsToDisconnect.add(conversationSid);
            }
        });

        personalChatsChangedUserAndCtmColleagues.forEach(personalChat -> {
            var conversationSid = personalChat.getTwilioConversationSid();

            var employeeId1 = idIdentityCache.getId(personalChat.getTwilioIdentity1());
            var employeeId2 = idIdentityCache.getId(personalChat.getTwilioIdentity2());

            if (allowedConversationsBetween.getOrDefault(employeeId1, Set.of()).contains(employeeId2) ||
                    !isShareOnHoldCareTeamForAnyClient(employeeId1, employeeId2)) {
                personalChatsToConnect.add(conversationSid);
            } else {
                personalChatsToDisconnect.add(conversationSid);
            }
        });

        //in case personal chat appeared in multiple personal chats lists, connect has priority over disconnect
        personalChatsToDisconnect.removeAll(personalChatsToConnect);

        allGroupChatsWithChangedUserAndOptOutClient.forEach((conversationSid, participants) -> {
            if (shouldDisconnectGroupChat(conversationSid,
                    participants,
                    allGroupChatsWithChangedUserAndOptOutClient.keySet(),
                    employeesWithAssociatedOptOutClients,
                    onHoldCareTeamForClientAssociatedContact,
                    allowedConversationsBetween,
                    idIdentityCache)) {
                groupChatsToDisconnect.add(conversationSid);
            } else {
                groupChatsToConnect.add(conversationSid);
            }
        });

        logger.info("Reconnecting {} personal chats for employee {}", personalChatsToConnect.size(), employee.getId());
        chatService.updateConversationsDisconnection(personalChatsToConnect, false, false);

        logger.info("Reconnecting {} group chats for employee {}", groupChatsToConnect.size(), employee.getId());
        chatService.updateConversationsDisconnection(groupChatsToConnect, false, false);

        logger.info("Disconnecting {} personal chats for employee {}", personalChatsToConnect.size(), employee.getId());
        chatService.updateConversationsDisconnection(personalChatsToDisconnect, true, false);

        logger.info("Disconnecting {} group chats for employee {}", groupChatsToDisconnect.size(), employee.getId());
        chatService.updateConversationsDisconnection(groupChatsToDisconnect, true, false);
    }

    @Override
    @Async
    public void contactChangedAsync(Employee employee,
                                    Collection<Long> optOutClientContactWhereChangedUserCtmBecameCurrent,
                                    Collection<Long> optOutClientContactWhereChangedUserCtmIsOnHold,
                                    Collection<Long> contactsSharingOptOutClientCtmWhereBecameCurrent,
                                    Collection<Long> contactsSharingOptOutClientCtmWhereIsOnHold,
                                    Function<Long, PermissionFilter> permissionFilterProvider) {
        contactChanged(employee,
                optOutClientContactWhereChangedUserCtmBecameCurrent,
                optOutClientContactWhereChangedUserCtmIsOnHold,
                contactsSharingOptOutClientCtmWhereBecameCurrent,
                contactsSharingOptOutClientCtmWhereIsOnHold,
                permissionFilterProvider
        );
    }

    private boolean shouldConnectGroupChat(String conversationSid,
                                           List<GroupChatParticipantHistory> participants,
                                           Set<String> groupChatsWithChangedUserAndOptOutClients,
                                           Map<Long, List<IdCommunityIdAssociatedEmployeeIdsAware>> employeesWithAssociatedOptOutClients,
                                           Map<Long, Set<Long>> onHoldCareTeamForOtherClientAssociatedContact,
                                           Map<Long, Set<Long>> allowedConversationsBetween,
                                           IdIdentityCache idIdentityCache) {

        return !shouldDisconnectGroupChat(conversationSid,
                participants,
                groupChatsWithChangedUserAndOptOutClients,
                employeesWithAssociatedOptOutClients,
                onHoldCareTeamForOtherClientAssociatedContact,
                allowedConversationsBetween,
                idIdentityCache
        );
    }


    private boolean shouldDisconnectGroupChat(String conversationSid,
                                              List<GroupChatParticipantHistory> participants,
                                              Set<String> groupChatsWithChangedUserAndOptOutClients,
                                              Map<Long, List<IdCommunityIdAssociatedEmployeeIdsAware>> employeesWithAssociatedOptOutClients,
                                              Map<Long, Set<Long>> onHoldCareTeamForOtherClientAssociatedContact,
                                              Map<Long, Set<Long>> allowedConversationsBetween,
                                              IdIdentityCache idIdentityCache) {
        if (!groupChatsWithChangedUserAndOptOutClients.contains(conversationSid)) {
            return false;
        }

        var participantIds = idIdentityCache.addAndConvertIdentities(
                participants.stream()
                        .map(GroupChatParticipantHistory::getTwilioIdentity)
        );

        var optOutClientContacts = participantIds.stream()
                .filter(employeesWithAssociatedOptOutClients::containsKey);


        return optOutClientContacts.anyMatch(optOutClientContactId -> {
            //get his on holds in chat
            var allOnHoldForClientContact = onHoldCareTeamForOtherClientAssociatedContact.getOrDefault(optOutClientContactId, Set.of());
            if (CollectionUtils.isEmpty(allOnHoldForClientContact)) {
                //no on holds at all - stay connected
                return false;
            }

            var onHoldInChat = participantIds.stream().filter(allOnHoldForClientContact::contains).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(onHoldInChat)) {
                //no on holds in this chat - stay connected
                return false;
            }

            //disconnect if at least one on hold has no permissions
            return !allowedConversationsBetween.getOrDefault(optOutClientContactId, Set.of()).containsAll(onHoldInChat);
        });
    }

    private Map<Long, Set<Long>> checkPermissionsBetweenUsers
            (Function<Long, PermissionFilter> permissionFilterProvider,
             Iterable<PersonalChat> personalChatsToCheckBetweenEachOther,
             Map<Long, Set<Long>> onHoldCareTeamForOptOutClientContacts,
             IdIdentityCache idIdentityCache) {
        var toCheckPermissionsBetweenEmployees = new HashMap<Long, Set<Long>>();

        personalChatsToCheckBetweenEachOther.forEach(pc ->
                CareCoordinationUtils.putBidirectionally(
                        toCheckPermissionsBetweenEmployees,
                        idIdentityCache.getId(pc.getTwilioIdentity1()),
                        idIdentityCache.getId(pc.getTwilioIdentity2())
                ));

        onHoldCareTeamForOptOutClientContacts.forEach((associatedContactId, onHoldCareTeamMembers) -> {
            onHoldCareTeamMembers.forEach(onHoldCtm ->
                    CareCoordinationUtils.putBidirectionally(toCheckPermissionsBetweenEmployees, associatedContactId, onHoldCtm));
        });

        return conversationAllowedBetweenUsersResolver.resolveConversationsAllowedBetween(
                toCheckPermissionsBetweenEmployees,
                permissionFilterProvider
        );

    }

    private enum PersonalChatReconnectionType {
        CLIENT_AND_ANY,
        ON_HOLDS,
        ON_HOLD_AND_ANY_OTHER
    }

    private static class IdIdentityCache {
        private final Map<String, Long> identityToId = new HashMap<>();
        private final Map<Long, String> idToIdentity = new HashMap<>();

        public void add(Long id, String identity) {
            idToIdentity.put(id, identity);
            identityToId.put(identity, id);
        }

        public Set<String> addAndConvertIds(Collection<Long> ids) {
            return addAndConvertIds(ids.stream());
        }

        public Set<String> addAndConvertIds(Stream<Long> ids) {
            return ids
                    .map(id -> {
                        var identity = idToIdentity.computeIfAbsent(id, ConversationUtils::employeeIdToIdentity);
                        identityToId.put(identity, id);
                        return identity;
                    })
                    .collect(Collectors.toSet());
        }

        public Set<Long> addAndConvertIdentities(Collection<String> identities) {
            return addAndConvertIdentities(identities.stream());
        }

        public Set<Long> addAndConvertIdentities(Stream<String> identities) {
            return identities
                    .map(identity -> {
                        var id = identityToId.computeIfAbsent(identity, ConversationUtils::employeeIdFromIdentity);
                        idToIdentity.put(id, identity);
                        return id;
                    })
                    .collect(Collectors.toSet());
        }

        public Long getId(String identity) {
            var id = identityToId.computeIfAbsent(identity, ConversationUtils::employeeIdFromIdentity);
            idToIdentity.putIfAbsent(id, identity);
            return id;
        }

        public String getIdentity(Long id) {
            var identity = idToIdentity.computeIfAbsent(id, ConversationUtils::employeeIdToIdentity);
            identityToId.putIfAbsent(identity, id);
            return identity;
        }
    }
}
