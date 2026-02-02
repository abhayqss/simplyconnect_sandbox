package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.EmployeeIdAware;
import com.scnsoft.eldermark.beans.projection.IdCommunityIdAssociatedEmployeeIdsAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.chat.GroupChatParticipantHistoryDao;
import com.scnsoft.eldermark.dao.chat.PersonalChatDao;
import com.scnsoft.eldermark.dao.specification.ChatSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.ClientSpecificationGenerator;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.chat.GroupChatParticipantHistory;
import com.scnsoft.eldermark.entity.chat.PersonalChat;
import com.scnsoft.eldermark.service.twilio.ChatService;
import com.scnsoft.eldermark.service.twilio.ConversationUtils;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.TestUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatHieConsentPolicyUpdateServiceImplTest {

    //todo tests for disconnect and contact change
    @Mock
    private ChatService chatService;

    @Mock
    private GroupChatParticipantHistoryDao groupChatParticipantHistoryDao;

    @Mock
    private PersonalChatDao personalChatDao;

    @Mock
    private ChatSpecificationGenerator chatSpecificationGenerator;

    @Mock
    private ConversationAllowedBetweenUsersResolver conversationAllowedBetweenUsersResolver;

    @Mock
    private ClientDao clientDao;

    @Mock
    private ClientSpecificationGenerator clientSpecificationGenerator;

    @Mock
    private ClientCareTeamHieConsentPolicyUpdateService clientCareTeamHieConsentPolicyUpdateService;

    @Mock
    private CommunityCareTeamHieConsentPolicyUpdateService communityCareTeamHieConsentPolicyUpdateService;

    @Mock
    private CareTeamMemberService careTeamMemberService;

    @InjectMocks
    private ChatHieConsentPolicyUpdateServiceImpl instance;

    @Test
    void reconnectConversations() {
        //conversation 1: #c1. disconnected 1-1 with client's contact and any other user: was hold (has permission) -> connect
        //conversation 2: #c1. disconnected 1-1 with client's contact and any other user: no permission, no on hold in common -> connect
        //conversation 3: #c1. disconnected 1-1 with client's contact and any other user: no permission, has on hold in common -> disconnect
        //conversation 4: #c2. disconnected 1-1 between two on hold (other user is on hold and both stop being on hold) -> connect
        //conversation 5: #c3. disconnected 1-1 between on hold and any other not on hold user: has on hold in common -> disconnect
        //conversation 6: #c3. disconnected 1-1 between on hold and any other not on hold user: no on hold in common -> connect

        //conversation 7: #c4. group chat with client's contact (doesn't matter if on hold is also in chat): no other opt out -> connect
        //conversation 8: #c4. group chat with client's contact (doesn't matter if on hold is also in chat): 2 opt out clients, for each there are no on hold in chat -> connect
        //conversation 9: #c4. group chat with client's contact (doesn't matter if on hold is also in chat): 2 opt out clients, for the first client 2 on hold in chat, one of whom has no permission -> disconnect
        //conversation 10: #c4. group chat with client's contact (doesn't matter if on hold is also in chat): 2 opt out clients, for the first client 2 on hold in chat, all of whom has permissions -> connect
        //conversation 11: #c4. group chat with client's contact (doesn't matter if on hold is also in chat): 2 opt out clients, for the first client 2 on hold in chat, all of whom has permissions. For the second client 1 on hold in chat without permissions -> disconnect
        //conversation 12: #c4. group chat with client's contact (doesn't matter if on hold is also in chat): 2 opt out clients, for the first client 2 on hold in chat, all of whom has permissions. For the second client 1 on hold in chat with permissions -> connect

        var communityId = 44321L;
        //given
        var personalChatsOfUsers = (Specification<PersonalChat>) Mockito.mock(Specification.class);
        var disconnectedPersonalChats = (Specification<PersonalChat>) Mockito.mock(Specification.class);
        var personalChatsSpec = (Specification<PersonalChat>) Mockito.mock(Specification.class);

        var groupChatEntriesOfChatsContainingUser = (Specification<GroupChatParticipantHistory>) Mockito.mock(Specification.class);
        var groupChatEntriesOfDisconnectedChats = (Specification<GroupChatParticipantHistory>) Mockito.mock(Specification.class);
        var groupChatEntriesSpec = (Specification<GroupChatParticipantHistory>) Mockito.mock(Specification.class);

        var byAssociatedEmployeeIdIn = (Specification<Client>) Mockito.mock(Specification.class);
        var isOutOutPolicy = (Specification<Client>) Mockito.mock(Specification.class);
        var clientSpec = (Specification<Client>) Mockito.mock(Specification.class);

        var associatedContact = new Employee();
        associatedContact.setId(33L);
        var associatedIdentity = ConversationUtils.employeeIdToIdentity(associatedContact.getId());
        var client = createAssociatedClient(1L, 1L, associatedContact.getId());

        var onHoldEmployee1 = 41L;
        var onHoldEmployee2 = 42L;
        var onHoldEmployeeIds = Set.of(
                onHoldEmployee1,
                onHoldEmployee2
        );

        var otherEmployee1 = 51L;
        var otherEmployee2 = 52L;

        var otherOptOutEmployee1 = 61L;
        var otherOptOutEmployee2 = 62L;

        var otherOptOutClient1 = createAssociatedClient(2L, communityId, otherOptOutEmployee1);
        var otherOptOutClient2 = createAssociatedClient(3L, communityId, otherOptOutEmployee2);

        var otherClient1OnHoldCtm1WithPermission = 71L;
        var otherClient1OnHoldCtm1WithPermissionAware = createEmployeeIdAware(otherClient1OnHoldCtm1WithPermission);
        var otherClient1OnHoldCtm2NoPermission = 72L;
        var otherClient1OnHoldCtm2AwareNoPermission = createEmployeeIdAware(otherClient1OnHoldCtm2NoPermission);
        var otherClient1OnHoldCtm3WithPermission = 73L;
        var otherClient1OnHoldCtm3WithPermissionAware = createEmployeeIdAware(otherClient1OnHoldCtm3WithPermission);

        var otherClient2OnHoldCtm1WithPermission = 81L;
        var otherClient2OnHoldCtm1WithPermissionAware = createEmployeeIdAware(otherClient2OnHoldCtm1WithPermission);
        var otherClient2OnHoldCtm2NoPermission = 82L;
        var otherClient2OnHoldCtm2AwareNoPermission = createEmployeeIdAware(otherClient2OnHoldCtm2NoPermission);

        var allIdentities = new HashSet<String>();
        onHoldEmployeeIds.stream().map(ConversationUtils::employeeIdToIdentity).forEach(allIdentities::add);
        allIdentities.add(associatedIdentity);

        var permissionsShouldBeCheckedBetween = new HashMap<Long, Set<Long>>();
        var permissionsAllowedBetween = new HashMap<Long, Set<Long>>();
        var shareOnHoldCtmEmployeeIds = new ArrayList<Pair<Long, Long>>();
        var notShareOnHoldCtmEmployeeIds = new ArrayList<Pair<Long, Long>>();
        var allGroupChatEmployeeIds = new HashSet<Long>();
        var personalChatsShouldConnect = new HashSet<String>();
        var groupChatsShouldConnect = new HashSet<String>();
        var otherClientCareTeamToCheckAmong = CareCoordinationUtils.<IdCommunityIdAssociatedEmployeeIdsAware, Set<Long>>idsComparingMap();
        var otherClientCareTeamOnHold = new HashMap<Long, List<EmployeeIdAware>>();

        otherClientCareTeamToCheckAmong.put(otherOptOutClient1, new HashSet<>());
        otherClientCareTeamToCheckAmong.put(otherOptOutClient2, new HashSet<>());

        otherClientCareTeamOnHold.put(otherOptOutClient1.getId(), new ArrayList<>());
        otherClientCareTeamOnHold.put(otherOptOutClient2.getId(), new ArrayList<>());


        var permissionFilterProvider = new PermissionFilterProvider();


        //conversation 1: #c1. disconnected 1-1 with client's contact and any other user: was hold (has permission) -> connect
        var personalChat1 = newPersonalChat(
                "personalChat1",
                associatedIdentity,
                ConversationUtils.employeeIdToIdentity(onHoldEmployee1)
        );

        CareCoordinationUtils.putBidirectionally(permissionsShouldBeCheckedBetween, associatedContact.getId(), onHoldEmployee1);
        CareCoordinationUtils.putBidirectionally(permissionsAllowedBetween, associatedContact.getId(), onHoldEmployee1);
        personalChatsShouldConnect.add(personalChat1.getTwilioConversationSid());


        //conversation 2: #c1. disconnected 1-1 with client's contact and any other user: no permission, no on hold in common -> connect
        var personalChat2 = newPersonalChat(
                "personalChat2",
                ConversationUtils.employeeIdToIdentity(otherEmployee1),
                associatedIdentity
        );

        CareCoordinationUtils.putBidirectionally(permissionsShouldBeCheckedBetween, associatedContact.getId(), otherEmployee1);
        notShareOnHoldCtmEmployeeIds.add(new Pair<>(otherEmployee1, associatedContact.getId()));
        personalChatsShouldConnect.add(personalChat2.getTwilioConversationSid());


        //conversation 3: #c1. disconnected 1-1 with client's contact and any other user: no permission, has on hold in common -> disconnect
        var personalChat3 = newPersonalChat(
                "personalChat3",
                associatedIdentity,
                ConversationUtils.employeeIdToIdentity(otherEmployee2)
        );

        CareCoordinationUtils.putBidirectionally(permissionsShouldBeCheckedBetween, associatedContact.getId(), otherEmployee2);
        shareOnHoldCtmEmployeeIds.add(new Pair<>(associatedContact.getId(), otherEmployee2));

        //conversation 4: #c2. disconnected 1-1 between two on hold (other user is on hold and both stop being on hold) -> connect
        var personalChat4 = newPersonalChat(
                "personalChat4",
                ConversationUtils.employeeIdToIdentity(onHoldEmployee1),
                ConversationUtils.employeeIdToIdentity(onHoldEmployee2)
        );

        personalChatsShouldConnect.add(personalChat4.getTwilioConversationSid());

        //conversation 5: #c3. disconnected 1-1 between on hold and any other not on hold user: has on hold in common -> disconnect
        var personalChat5 = newPersonalChat(
                "personalChat5",
                ConversationUtils.employeeIdToIdentity(onHoldEmployee1),
                ConversationUtils.employeeIdToIdentity(otherEmployee1)
        );

        shareOnHoldCtmEmployeeIds.add(new Pair<>(onHoldEmployee1, otherEmployee1));

        //conversation 6: #c3. disconnected 1-1 between on hold and any other not on hold user: no on hold in common -> connect
        var personalChat6 = newPersonalChat(
                "personalChat6",
                ConversationUtils.employeeIdToIdentity(onHoldEmployee1),
                ConversationUtils.employeeIdToIdentity(otherEmployee2)
        );

        notShareOnHoldCtmEmployeeIds.add(new Pair<>(onHoldEmployee1, otherEmployee2));
        personalChatsShouldConnect.add(personalChat6.getTwilioConversationSid());

        //conversation 7: #c4. group chat with client's contact (doesn't matter if on hold is also in chat): no other opt out -> connect
        var groupChat7Sid = "groupChat7";
        var groupChat7 = newGroupChat(
                groupChat7Sid,
                associatedIdentity,
                ConversationUtils.employeeIdToIdentity(onHoldEmployee1),
                ConversationUtils.employeeIdToIdentity(onHoldEmployee2),
                ConversationUtils.employeeIdToIdentity(otherEmployee1),
                ConversationUtils.employeeIdToIdentity(otherEmployee2)
        );
        allGroupChatEmployeeIds.add(associatedContact.getId());
        allGroupChatEmployeeIds.add(onHoldEmployee1);
        allGroupChatEmployeeIds.add(onHoldEmployee2);
        allGroupChatEmployeeIds.add(otherEmployee1);
        allGroupChatEmployeeIds.add(otherEmployee2);

        groupChatsShouldConnect.add(groupChat7Sid);


        //conversation 8: #c4. group chat with client's contact (doesn't matter if on hold is also in chat): 2 opt out clients, for each there are no on hold in chat -> connect
        var groupChat8Sid = "groupChat8";
        var groupChat8 = newGroupChat(
                groupChat8Sid,
                associatedIdentity,
                ConversationUtils.employeeIdToIdentity(otherOptOutEmployee1),
                ConversationUtils.employeeIdToIdentity(otherOptOutEmployee2)
        );
        allGroupChatEmployeeIds.add(associatedContact.getId());
        allGroupChatEmployeeIds.add(otherOptOutEmployee1);
        allGroupChatEmployeeIds.add(otherOptOutEmployee2);

        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(associatedContact.getId());
        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(otherOptOutEmployee2);

        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(associatedContact.getId());
        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(otherOptOutEmployee1);


        groupChatsShouldConnect.add(groupChat8Sid);

        //conversation 9: #c4. group chat with client's contact (doesn't matter if on hold is also in chat): 2 opt out clients, for the first client 2 on hold in chat, one of whom has no permission -> disconnect
        var groupChat9Sid = "groupChat9";
        var groupChat9 = newGroupChat(
                groupChat9Sid,
                associatedIdentity,
                ConversationUtils.employeeIdToIdentity(otherOptOutEmployee1),
                ConversationUtils.employeeIdToIdentity(otherOptOutEmployee2),
                ConversationUtils.employeeIdToIdentity(otherClient1OnHoldCtm1WithPermission),
                ConversationUtils.employeeIdToIdentity(otherClient1OnHoldCtm2NoPermission)
        );
        allGroupChatEmployeeIds.add(associatedContact.getId());
        allGroupChatEmployeeIds.add(otherOptOutEmployee1);
        allGroupChatEmployeeIds.add(otherOptOutEmployee2);
        allGroupChatEmployeeIds.add(otherClient1OnHoldCtm1WithPermission);
        allGroupChatEmployeeIds.add(otherClient1OnHoldCtm2NoPermission);

        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(associatedContact.getId());
        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(otherOptOutEmployee2);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(otherClient1OnHoldCtm1WithPermission);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(otherClient1OnHoldCtm2NoPermission);

        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(associatedContact.getId());
        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(otherOptOutEmployee1);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(otherClient1OnHoldCtm1WithPermission);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(otherClient1OnHoldCtm2NoPermission);

        otherClientCareTeamOnHold.get(otherOptOutClient1.getId()).add(otherClient1OnHoldCtm1WithPermissionAware);
        otherClientCareTeamOnHold.get(otherOptOutClient1.getId()).add(otherClient1OnHoldCtm2AwareNoPermission);

        CareCoordinationUtils.putBidirectionally(permissionsShouldBeCheckedBetween, otherOptOutEmployee1, otherClient1OnHoldCtm1WithPermission);
        CareCoordinationUtils.putBidirectionally(permissionsShouldBeCheckedBetween, otherOptOutEmployee1, otherClient1OnHoldCtm2NoPermission);

        CareCoordinationUtils.putBidirectionally(permissionsAllowedBetween, otherOptOutEmployee1, otherClient1OnHoldCtm1WithPermission);


        //conversation 10: #c4. group chat with client's contact (doesn't matter if on hold is also in chat): 2 opt out clients, for the first client 2 on hold in chat, all of whom has permissions -> connect
        var groupChat10Sid = "groupChat10";
        var groupChat10 = newGroupChat(
                groupChat10Sid,
                associatedIdentity,
                ConversationUtils.employeeIdToIdentity(otherOptOutEmployee1),
                ConversationUtils.employeeIdToIdentity(otherOptOutEmployee2),
                ConversationUtils.employeeIdToIdentity(otherClient1OnHoldCtm1WithPermission),
                ConversationUtils.employeeIdToIdentity(otherClient1OnHoldCtm3WithPermission)
        );
        allGroupChatEmployeeIds.add(associatedContact.getId());
        allGroupChatEmployeeIds.add(otherOptOutEmployee1);
        allGroupChatEmployeeIds.add(otherOptOutEmployee2);
        allGroupChatEmployeeIds.add(otherClient1OnHoldCtm1WithPermission);
        allGroupChatEmployeeIds.add(otherClient1OnHoldCtm3WithPermission);

        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(associatedContact.getId());
        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(otherOptOutEmployee2);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(otherClient1OnHoldCtm1WithPermission);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(otherClient1OnHoldCtm3WithPermission);

        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(associatedContact.getId());
        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(otherOptOutEmployee1);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(otherClient1OnHoldCtm1WithPermission);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(otherClient1OnHoldCtm3WithPermission);

//        otherClientCareTeamOnHold.get(otherOptOutClient1.getId()).add(otherClient1OnHoldCtm1WithPermissionAware); already added to list
        otherClientCareTeamOnHold.get(otherOptOutClient1.getId()).add(otherClient1OnHoldCtm3WithPermissionAware);

        CareCoordinationUtils.putBidirectionally(permissionsShouldBeCheckedBetween, otherOptOutEmployee1, otherClient1OnHoldCtm1WithPermission);
        CareCoordinationUtils.putBidirectionally(permissionsShouldBeCheckedBetween, otherOptOutEmployee1, otherClient1OnHoldCtm3WithPermission);

        CareCoordinationUtils.putBidirectionally(permissionsAllowedBetween, otherOptOutEmployee1, otherClient1OnHoldCtm1WithPermission);
        CareCoordinationUtils.putBidirectionally(permissionsAllowedBetween, otherOptOutEmployee1, otherClient1OnHoldCtm3WithPermission);

        groupChatsShouldConnect.add(groupChat10Sid);


        //conversation 11: #c4. group chat with client's contact (doesn't matter if on hold is also in chat): 2 opt out clients, for the first client 2 on hold in chat, all of whom has permissions. For the second client 1 on hold in chat without permissions -> disconnect
        var groupChat11Sid = "groupChat11";
        var groupChat11 = newGroupChat(
                groupChat11Sid,
                associatedIdentity,
                ConversationUtils.employeeIdToIdentity(otherOptOutEmployee1),
                ConversationUtils.employeeIdToIdentity(otherOptOutEmployee2),
                ConversationUtils.employeeIdToIdentity(otherClient1OnHoldCtm1WithPermission),
                ConversationUtils.employeeIdToIdentity(otherClient1OnHoldCtm3WithPermission),
                ConversationUtils.employeeIdToIdentity(otherClient2OnHoldCtm2NoPermission)

        );
        allGroupChatEmployeeIds.add(associatedContact.getId());
        allGroupChatEmployeeIds.add(otherOptOutEmployee1);
        allGroupChatEmployeeIds.add(otherOptOutEmployee2);
        allGroupChatEmployeeIds.add(otherClient1OnHoldCtm1WithPermission);
        allGroupChatEmployeeIds.add(otherClient1OnHoldCtm3WithPermission);
        allGroupChatEmployeeIds.add(otherClient2OnHoldCtm2NoPermission);

        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(associatedContact.getId());
        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(otherOptOutEmployee2);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(otherClient1OnHoldCtm1WithPermission);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(otherClient1OnHoldCtm3WithPermission);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(otherClient2OnHoldCtm2NoPermission);

        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(associatedContact.getId());
        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(otherOptOutEmployee1);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(otherClient1OnHoldCtm1WithPermission);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(otherClient1OnHoldCtm3WithPermission);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(otherClient2OnHoldCtm2NoPermission);

//        otherClientCareTeamOnHold.get(otherOptOutClient1.getId()).add(otherClient1OnHoldCtm1WithPermissionAware); already added to list
//        otherClientCareTeamOnHold.get(otherOptOutClient1.getId()).add(otherClient1OnHoldCtm3WithPermissionAware); already added to list
        otherClientCareTeamOnHold.get(otherOptOutClient2.getId()).add(otherClient2OnHoldCtm2AwareNoPermission);

        CareCoordinationUtils.putBidirectionally(permissionsShouldBeCheckedBetween, otherOptOutEmployee1, otherClient1OnHoldCtm1WithPermission);
        CareCoordinationUtils.putBidirectionally(permissionsShouldBeCheckedBetween, otherOptOutEmployee1, otherClient1OnHoldCtm3WithPermission);
        CareCoordinationUtils.putBidirectionally(permissionsShouldBeCheckedBetween, otherOptOutEmployee2, otherClient2OnHoldCtm2NoPermission);

        CareCoordinationUtils.putBidirectionally(permissionsAllowedBetween, otherOptOutEmployee1, otherClient1OnHoldCtm1WithPermission);
        CareCoordinationUtils.putBidirectionally(permissionsAllowedBetween, otherOptOutEmployee1, otherClient1OnHoldCtm3WithPermission);

        //conversation 12: #c4. group chat with client's contact (doesn't matter if on hold is also in chat): 2 opt out clients, for the first client 2 on hold in chat, all of whom has permissions. For the second client 1 on hold in chat with permissions -> connect
        var groupChat12Sid = "groupChat12";
        var groupChat12 = newGroupChat(
                groupChat12Sid,
                associatedIdentity,
                ConversationUtils.employeeIdToIdentity(otherOptOutEmployee1),
                ConversationUtils.employeeIdToIdentity(otherOptOutEmployee2),
                ConversationUtils.employeeIdToIdentity(otherClient1OnHoldCtm1WithPermission),
                ConversationUtils.employeeIdToIdentity(otherClient1OnHoldCtm3WithPermission),
                ConversationUtils.employeeIdToIdentity(otherClient2OnHoldCtm1WithPermission)

        );
        allGroupChatEmployeeIds.add(associatedContact.getId());
        allGroupChatEmployeeIds.add(otherOptOutEmployee1);
        allGroupChatEmployeeIds.add(otherOptOutEmployee2);
        allGroupChatEmployeeIds.add(otherClient1OnHoldCtm1WithPermission);
        allGroupChatEmployeeIds.add(otherClient1OnHoldCtm3WithPermission);
        allGroupChatEmployeeIds.add(otherClient2OnHoldCtm1WithPermission);

        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(associatedContact.getId());
        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(otherOptOutEmployee2);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(otherClient1OnHoldCtm1WithPermission);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(otherClient1OnHoldCtm3WithPermission);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient1).add(otherClient2OnHoldCtm1WithPermission);

        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(associatedContact.getId());
        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(otherOptOutEmployee1);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(otherClient1OnHoldCtm1WithPermission);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(otherClient1OnHoldCtm3WithPermission);
        otherClientCareTeamToCheckAmong.get(otherOptOutClient2).add(otherClient2OnHoldCtm1WithPermission);

//        otherClientCareTeamOnHold.get(otherOptOutClient1.getId()).add(otherClient1OnHoldCtm1WithPermissionAware); already added to list
//        otherClientCareTeamOnHold.get(otherOptOutClient1.getId()).add(otherClient1OnHoldCtm3WithPermissionAware); already added to list
//        otherClientCareTeamOnHold.get(otherOptOutClient2.getId()).add(otherClient2OnHoldCtm2AwareNoPermission); already added to list
        otherClientCareTeamOnHold.get(otherOptOutClient2.getId()).add(otherClient2OnHoldCtm1WithPermissionAware);

        CareCoordinationUtils.putBidirectionally(permissionsShouldBeCheckedBetween, otherOptOutEmployee1, otherClient1OnHoldCtm1WithPermission);
        CareCoordinationUtils.putBidirectionally(permissionsShouldBeCheckedBetween, otherOptOutEmployee1, otherClient1OnHoldCtm3WithPermission);
        CareCoordinationUtils.putBidirectionally(permissionsShouldBeCheckedBetween, otherOptOutEmployee2, otherClient2OnHoldCtm1WithPermission);

        CareCoordinationUtils.putBidirectionally(permissionsAllowedBetween, otherOptOutEmployee1, otherClient1OnHoldCtm1WithPermission);
        CareCoordinationUtils.putBidirectionally(permissionsAllowedBetween, otherOptOutEmployee1, otherClient1OnHoldCtm3WithPermission);
        CareCoordinationUtils.putBidirectionally(permissionsAllowedBetween, otherOptOutEmployee2, otherClient2OnHoldCtm1WithPermission);

        groupChatsShouldConnect.add(groupChat12Sid);


        //when

        when(chatService.isChatEnabled()).thenReturn(true);

        when(chatSpecificationGenerator.personalChatsOfUsers(TestUtil.sameElementsCollection(allIdentities))).thenReturn(personalChatsOfUsers);
        when(chatSpecificationGenerator.disconnectedPersonalChats()).thenReturn(disconnectedPersonalChats);
        when(personalChatsOfUsers.and(disconnectedPersonalChats)).thenReturn(personalChatsSpec);

        when(chatSpecificationGenerator.groupChatEntriesOfChatsContainingAnyUser(TestUtil.sameElementsCollection(List.of(associatedIdentity)))).thenReturn(groupChatEntriesOfChatsContainingUser);
        when(chatSpecificationGenerator.groupChatEntriesOfDisconnectedChats()).thenReturn(groupChatEntriesOfDisconnectedChats);
        when(groupChatEntriesOfChatsContainingUser.and(groupChatEntriesOfDisconnectedChats)).thenReturn(groupChatEntriesSpec);

        when(personalChatDao.findAll(personalChatsSpec)).thenReturn(List.of(
                        personalChat1,
                        personalChat2,
                        personalChat3,
                        personalChat4,
                        personalChat5,
                        personalChat6
                )
        );

        when(groupChatParticipantHistoryDao.findAll(groupChatEntriesSpec)).thenReturn(Stream.of(
                                groupChat7,
                                groupChat8,
                                groupChat9,
                                groupChat10,
                                groupChat11,
                                groupChat12
                        )
                        .reduce(Stream::concat)
                        .orElseGet(Stream::empty)
                        .collect(Collectors.toList())
        );

        when(clientSpecificationGenerator.byAssociatedEmployeeIdIn(TestUtil.sameElementsCollection(allGroupChatEmployeeIds)))
                .thenReturn(byAssociatedEmployeeIdIn);
        when(clientSpecificationGenerator.isOptOutPolicy()).thenReturn(isOutOutPolicy);
        when(byAssociatedEmployeeIdIn.and(isOutOutPolicy)).thenReturn(clientSpec);

        when(clientDao.findAll(clientSpec, IdCommunityIdAssociatedEmployeeIdsAware.class))
                .thenReturn(List.of(
                        otherOptOutClient1,
                        otherOptOutClient2
                ));

        otherClientCareTeamToCheckAmong.forEach((c, employeeIds) ->
                when(clientCareTeamHieConsentPolicyUpdateService.onHoldAmongEmployeeIds(
                        eq(c.getId()),
                        TestUtil.sameElementsCollection(employeeIds)
                )).thenReturn(otherClientCareTeamOnHold.get(c.getId()))
        );


        when(conversationAllowedBetweenUsersResolver.resolveConversationsAllowedBetween(
                sameElementsMap(permissionsShouldBeCheckedBetween),
                eq(permissionFilterProvider)
        )).thenReturn(permissionsAllowedBetween);

        notShareOnHoldCtmEmployeeIds.forEach(pair ->
                when(careTeamMemberService.doesAnyShareCtmWithEmployee(
                        TestUtil.sameElementsCollection(List.of(pair.getFirst())),
                        eq(pair.getSecond()),
                        onHoldPolicyMatcher())
                ).thenReturn(false));

        shareOnHoldCtmEmployeeIds.forEach(pair ->
                when(careTeamMemberService.doesAnyShareCtmWithEmployee(
                        TestUtil.sameElementsCollection(List.of(pair.getFirst())),
                        eq(pair.getSecond()),
                        onHoldPolicyMatcher())
                ).thenReturn(true));


        //test
        instance.reconnectConversations(List.of(client), Map.of(client.getId(), onHoldEmployeeIds), permissionFilterProvider);


        //then
        verify(chatService).updateConversationsDisconnection(personalChatsShouldConnect, false, false);
        verify(chatService).updateConversationsDisconnection(groupChatsShouldConnect, false, false);

        verifyNoMoreInteractions(chatService);
    }

    private PersonalChat newPersonalChat(String conversationSid, String identity1, String identity2) {
        var personalChat = new PersonalChat();
        personalChat.setTwilioConversationSid(conversationSid);
        personalChat.setTwilioIdentity1(identity1);
        personalChat.setTwilioIdentity2(identity2);
        return personalChat;
    }

    private GroupChatParticipantHistory newGroupChatEntry(String conversationSid, String identity) {
        var entry = new GroupChatParticipantHistory();
        entry.setTwilioConversationSid(conversationSid);
        entry.setTwilioIdentity(identity);
        return entry;
    }

    private Stream<GroupChatParticipantHistory> newGroupChat(String conversationSid, String... identities) {
        return Stream.of(identities).map(identity -> newGroupChatEntry(conversationSid, identity));
    }

    private Map<Long, Set<Long>> sameElementsMap(Map<Long, Set<Long>> m2) {
        return TestUtil.sameElementsMap(m2, CollectionUtils::isEqualCollection);
    }

    private HieConsentCareTeamType onHoldPolicyMatcher() {
        return argThat(type -> type.isIncludesOnHold() && !type.isIncludesCurrent() && HieConsentCareTeamType.ANY_TARGET_CLIENT_ID.equals(type.getClientId()));
    }

    private IdCommunityIdAssociatedEmployeeIdsAware createAssociatedClient(Long id, Long communityId, Long associatedContactId) {
        return IdCommunityIdAssociatedEmployeeIdsAware.of(id, communityId, associatedContactId);
    }

    private EmployeeIdAware createEmployeeIdAware(Long employeeId) {
        return () -> employeeId;
    }

    private static class PermissionFilterProvider implements Function<Long, PermissionFilter> {
        private final Map<Long, PermissionFilter> cache = new HashMap<>();

        @Override
        public PermissionFilter apply(Long aLong) {
            if (cache.containsKey(aLong)) {
                return cache.get(aLong);
            }
            var filter = new PermissionFilter(Map.of(), Map.of(), Set.of());
            cache.put(aLong, new PermissionFilter(Map.of(), Map.of(), Set.of()));
            return filter;
        }
    }
}
