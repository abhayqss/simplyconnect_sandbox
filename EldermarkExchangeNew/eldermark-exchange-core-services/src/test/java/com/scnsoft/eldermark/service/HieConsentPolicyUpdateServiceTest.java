package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.EmployeeIdAware;
import com.scnsoft.eldermark.beans.projection.EmployeeOrgIdCommunityOrgIdAware;
import com.scnsoft.eldermark.beans.projection.OnHoldAware;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.CommunityCareTeamMember;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HieConsentPolicyUpdateServiceTest {

    @Mock
    private ClientServiceImpl clientHieConsentPolicyUpdateService;

    @Mock
    private ClientCareTeamHieConsentPolicyUpdateService clientCareTeamHieConsentPolicyUpdateService;

    @Mock
    private CommunityCareTeamMemberServiceImpl communityCareTeamHieConsentPolicyUpdateService;

    @Mock
    private ChatHieConsentPolicyUpdateService chatHieConsentPolicyUpdateService;

    @InjectMocks
    private HieConsentPolicyUpdateServiceImpl instance;

    @Test
        //input data schema in confluence https://confluence.scnsoft.com/pages/viewpage.action?pageId=266773946
    void communityCareTeamMemberAdded() {
        //given
        var id = 61L;
        var organization1 = createOrganization(id++);
        var organization2 = createOrganization(id++);

        id = 51L;
        var community1 = createCommunity(id++, organization1);
        var community2 = createCommunity(id++, organization1);
        var community3 = createCommunity(id++, organization2);
        var community4 = createCommunity(id++, organization1);
        var community5 = createCommunity(id++, organization1);

        id = 31L;
        var client1 = createClient(id++, community1, HieConsentPolicyType.OPT_OUT);
        var client2 = createClient(id++, community2, HieConsentPolicyType.OPT_OUT);
        var client3 = createClient(id++, community1, HieConsentPolicyType.OPT_IN);
        var client4 = createClient(id++, community2, HieConsentPolicyType.OPT_OUT);
        var client5 = createClient(id++, community3, HieConsentPolicyType.OPT_OUT);
        var client6 = createClient(id++, community4, HieConsentPolicyType.OPT_OUT);
        var client7 = createClient(id++, community5, HieConsentPolicyType.OPT_IN);
        var client8 = createClient(id++, community1, HieConsentPolicyType.OPT_OUT);
        var allClients = List.of(
                client1, client2, client3, client4, client5, client6, client7, client8
        );

        id = 1L;
        var employee1 = createEmployee(id++, community2);
        var employee2 = createEmployee(id++, community2);
        var employee3 = createEmployee(id++, community2);
        var employee4 = createEmployee(id++, community1);
        var employee5 = createEmployee(id++, community1);
        var employee6 = createEmployee(id++, community1);
        var employee7 = createEmployee(id++, community2);
        var employee8 = createEmployee(id++, community3);
        var employee9 = createEmployee(id++, community4);
        var employee10 = createEmployee(id++, community1);
        var employee11 = createEmployee(id++, community1);
        var employee12 = createEmployee(id++, community1);
        var employee13 = createEmployee(id++, community2);
        var employee14 = createEmployee(id++, community2);
        var employee15 = createEmployee(id++, community2);
        var employee16 = createEmployee(id++, community2);
        var employee17 = createEmployee(id++, community1);
        var employee18 = createEmployee(id++, community1);
        var employee19 = createEmployee(id++, community1);
        var employee20 = createEmployee(id++, community1);
        var employee21 = createEmployee(id++, community3);
        var employee22 = createEmployee(id++, community2);
        var employee23 = createEmployee(id++, community2);


        id = 101L;
        var clientCtms = List.of(
                createClientCtm(id++, employee1, client3, false),
                createClientCtm(id++, employee1, client2, true),

                createClientCtm(id++, employee2, client3, false),
                createClientCtm(id++, employee2, client2, false),

                createClientCtm(id++, employee4, client1, true),
                createClientCtm(id++, employee4, client4, true),
                createClientCtm(id++, employee4, client7, false),
                createClientCtm(id++, employee4, client2, true),

                createClientCtm(id++, employee5, client5, true),

                createClientCtm(id++, employee7, client4, false),
                createClientCtm(id++, employee7, client7, false),

                createClientCtm(id++, employee8, client5, false),
                createClientCtm(id++, employee8, client3, true),

                createClientCtm(id++, employee9, client3, false),

                createClientCtm(id++, employee10, client1, true),
                createClientCtm(id++, employee10, client3, false),

                createClientCtm(id++, employee11, client1, true),

                createClientCtm(id++, employee12, client1, false),

                createClientCtm(id++, employee13, client1, true),

                createClientCtm(id++, employee14, client8, true),

                createClientCtm(id++, employee15, client4, false),

                createClientCtm(id++, employee16, client1, false),

                createClientCtm(id++, employee17, client2, true),

                createClientCtm(id++, employee18, client1, true),

                createClientCtm(id++, employee19, client5, true),

                createClientCtm(id++, employee22, client1, true),
                createClientCtm(id++, employee22, client3, false),

                createClientCtm(id++, employee23, client3, false)
        );

        id = 201L;
        var addedCommunityCtm = createCommunityCtm(id++, employee4, community1);

        var communityCtms = List.of(
                addedCommunityCtm,
                createCommunityCtm(id++, employee4, community3),
                createCommunityCtm(id++, employee4, community4),

                createCommunityCtm(id++, employee9, community4),

                createCommunityCtm(id++, employee18, community1),

                createCommunityCtm(id++, employee19, community1),

                createCommunityCtm(id++, employee20, community1),

                createCommunityCtm(id++, employee21, community1)
        );

        associateClientAndContact(client1, employee6);
        associateClientAndContact(client3, employee5);
        associateClientAndContact(client4, employee3);
        associateClientAndContact(client8, employee17);


        //when

        var clientsInCommunity = allClients.stream()
                .filter(client -> client.getCommunityId().equals(addedCommunityCtm.getCommunityId()))
                .map(this::clientEntityCareTeamUpdateProjectionAdapter)
                .collect(Collectors.toList());
        when(clientHieConsentPolicyUpdateService.findClientsInCommunity(
                addedCommunityCtm.getCommunityId(),
                HieConsentPolicyUpdateServiceImpl.ClientEntityCareTeamUpdateProjection.class)
        )
                .thenReturn(clientsInCommunity);


        when(clientCareTeamHieConsentPolicyUpdateService.findCurrentCareTeamMembersOfClients(
                TestUtil.sameElementsCollection(CareCoordinationUtils.toIdsSet(clientsInCommunity)),
                eq(addedCommunityCtm.getEmployeeId()),
                eq(EmployeeIdAware.class))
        )
                .thenReturn(
                        clientCtms.stream()
                                .filter(clientCtm -> !clientCtm.getEmployeeId().equals(addedCommunityCtm.getEmployeeId()))
                                .filter(clientCtm -> !clientCtm.getOnHold())
                                .filter(clientCtm -> CareCoordinationUtils.toIdsSet(clientsInCommunity).contains(clientCtm.getClientId()))
                                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                                .collect(Collectors.toList())
                );

        when(communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeCurrent(
                TestUtil.sameElementsCollection(List.of(addedCommunityCtm.getCommunityId())),
                eq(addedCommunityCtm.getEmployeeId()),
                eq(EmployeeIdAware.class))
        )
                .thenReturn(
                        communityCtms.stream()
                                .filter(ctm -> !ctm.getEmployeeId().equals(addedCommunityCtm.getEmployeeId()))
                                .filter(ctm -> ctm.getCommunity().getOrganizationId().equals(ctm.getEmployee().getOrganizationId()))
                                .filter(ctm -> addedCommunityCtm.getCommunityId().equals(ctm.getCommunityId()))
                                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                                .collect(Collectors.toList())
                );

        when(clientHieConsentPolicyUpdateService.isOptOutPolicy(any())).thenCallRealMethod();

        var optOutClientIdsInCommunity = allClients.stream()
                .filter(client -> client.getCommunityId().equals(addedCommunityCtm.getCommunityId()))
                .filter(client -> client.getHieConsentPolicyType() == HieConsentPolicyType.OPT_OUT)
                .map(BasicEntity::getId)
                .collect(Collectors.toSet());
        var onHoldClientsAmongOptOutInCommunity = clientCtms.stream()
                .filter(ClientCareTeamMember::getOnHold)
                .filter(ctm -> ctm.getEmployeeId().equals(addedCommunityCtm.getEmployeeId()))
                .filter(ctm -> optOutClientIdsInCommunity.contains(ctm.getClientId()))
                .map(ctm -> (ClientIdAware) ctm::getClientId)
                .collect(Collectors.toList());

        when(clientCareTeamHieConsentPolicyUpdateService.onHoldAmongClientIds(
                TestUtil.sameElementsCollection(optOutClientIdsInCommunity),
                eq(addedCommunityCtm.getEmployeeId()),
                eq(ClientIdAware.class)
        )).thenReturn(onHoldClientsAmongOptOutInCommunity);

        var notOnHoldClientsAmongOptOutInCommunity = new HashSet<>(optOutClientIdsInCommunity);
        notOnHoldClientsAmongOptOutInCommunity.removeAll(onHoldClientsAmongOptOutInCommunity.stream().map(ClientIdAware::getClientId).collect(Collectors.toList()));
        when(clientCareTeamHieConsentPolicyUpdateService.findOnHoldCareTeamMembersOfClients(
                TestUtil.sameElementsCollection(notOnHoldClientsAmongOptOutInCommunity),
                eq(addedCommunityCtm.getEmployeeId()),
                eq(EmployeeIdAware.class)
        )).thenReturn(clientCtms.stream()
                .filter(ctm -> ctm.getOnHold())
                .filter(ctm -> !ctm.getEmployeeId().equals(addedCommunityCtm.getEmployeeId()))
                .filter(ctm -> notOnHoldClientsAmongOptOutInCommunity.contains(ctm.getClientId()))
                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                .collect(Collectors.toList())
        );

        when(communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeOnHold(
                TestUtil.sameElementsCollection(Set.of(addedCommunityCtm.getCommunityId())),
                eq(addedCommunityCtm.getEmployeeId()),
                eq(EmployeeIdAware.class)
        )).thenReturn(communityCtms.stream()
                .filter(ctm -> !ctm.getEmployee().getOrganizationId().equals(ctm.getCommunity().getOrganizationId()))
                .filter(ctm -> !ctm.getEmployeeId().equals(addedCommunityCtm.getEmployeeId()))
                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                .collect(Collectors.toList())
        );


        //then
        instance.communityCareTeamMemberAdded(addedCommunityCtm);

        verifyNoMoreInteractions(
                clientHieConsentPolicyUpdateService,
                clientCareTeamHieConsentPolicyUpdateService,
                communityCareTeamHieConsentPolicyUpdateService
        );
        verify(chatHieConsentPolicyUpdateService).contactChangedAsync(
                eq(addedCommunityCtm.getEmployee()),
                TestUtil.sameElementsCollection(Set.of(
                        employee6.getId(),
                        employee17.getId()
                )),
                eq(Set.of()),
                TestUtil.sameElementsCollection(Set.of(
                        employee1.getId(),
                        employee2.getId(),

                        //although access gained not through share ctm, but as accessible client group chats are not affected
                        //and so can't pass in opt out clients. Permission checks are the same, so should be OK
                        employee5.getId(),

                        //employee7.getId(),  //this one is optional
                        employee9.getId(),
                        employee10.getId(),
                        employee12.getId(),
                        employee16.getId(),
                        employee18.getId(),
                        employee19.getId(),
                        employee20.getId(),
                        employee22.getId(),
                        employee23.getId()
                )),
                TestUtil.sameElementsCollection(Set.of(
                        employee14.getId(),
                        employee21.getId()
                )),
                any()
        );
    }

    @Test
        //input data schema in confluence https://confluence.scnsoft.com/pages/viewpage.action?pageId=266773946
    void clientCareTeamMemberAddedToOptInClient() {
        //given
        var id = 61L;
        var organization1 = createOrganization(id++);
        var organization2 = createOrganization(id++);

        id = 51L;
        var community1 = createCommunity(id++, organization1);
        var community2 = createCommunity(id++, organization1);
        var community3 = createCommunity(id++, organization2);
        var community4 = createCommunity(id++, organization1);
        var community5 = createCommunity(id++, organization1);

        id = 31L;
        var client1 = createClient(id++, community1, HieConsentPolicyType.OPT_OUT);
        var client2 = createClient(id++, community2, HieConsentPolicyType.OPT_OUT);
        var client3 = createClient(id++, community1, HieConsentPolicyType.OPT_IN);
        var client4 = createClient(id++, community2, HieConsentPolicyType.OPT_OUT);
        var client5 = createClient(id++, community3, HieConsentPolicyType.OPT_OUT);
        var client6 = createClient(id++, community4, HieConsentPolicyType.OPT_OUT);
        var client7 = createClient(id++, community5, HieConsentPolicyType.OPT_IN);
        var client8 = createClient(id++, community1, HieConsentPolicyType.OPT_OUT);
        var allClients = List.of(
                client1, client2, client3, client4, client5, client6, client7, client8
        );

        id = 1L;
        var employee1 = createEmployee(id++, community2);
        var employee2 = createEmployee(id++, community2);
        var employee3 = createEmployee(id++, community2);
        var employee4 = createEmployee(id++, community1);
        var employee5 = createEmployee(id++, community1);
        var employee6 = createEmployee(id++, community1);
        var employee7 = createEmployee(id++, community2);
        var employee8 = createEmployee(id++, community3);
        var employee9 = createEmployee(id++, community4);
        var employee10 = createEmployee(id++, community1);
        var employee11 = createEmployee(id++, community1);
        var employee12 = createEmployee(id++, community1);
        var employee13 = createEmployee(id++, community2);
        var employee14 = createEmployee(id++, community2);
        var employee15 = createEmployee(id++, community2);
        var employee16 = createEmployee(id++, community2);
        var employee17 = createEmployee(id++, community1);
        var employee18 = createEmployee(id++, community1);
        var employee19 = createEmployee(id++, community1);
        var employee20 = createEmployee(id++, community1);
        var employee21 = createEmployee(id++, community3);
        var employee22 = createEmployee(id++, community2);
        var employee23 = createEmployee(id++, community2);


        id = 101L;
        var addedClientCtm = createClientCtm(id++, employee4, client3, false);
        var clientCtms = List.of(
                addedClientCtm,

                createClientCtm(id++, employee1, client3, false),
                createClientCtm(id++, employee1, client2, true),

                createClientCtm(id++, employee2, client3, false),
                createClientCtm(id++, employee2, client2, false),

                createClientCtm(id++, employee4, client1, true),
                createClientCtm(id++, employee4, client4, true),
                createClientCtm(id++, employee4, client7, false),
                createClientCtm(id++, employee4, client2, true),

                createClientCtm(id++, employee5, client5, true),

                createClientCtm(id++, employee7, client4, false),
                createClientCtm(id++, employee7, client7, false),

                createClientCtm(id++, employee8, client5, false),
                createClientCtm(id++, employee8, client3, true),

                createClientCtm(id++, employee9, client3, false),

                createClientCtm(id++, employee10, client1, true),
                createClientCtm(id++, employee10, client3, false),

                createClientCtm(id++, employee11, client1, true),

                createClientCtm(id++, employee12, client1, false),

                createClientCtm(id++, employee13, client1, true),

                createClientCtm(id++, employee14, client8, true),

                createClientCtm(id++, employee15, client4, false),

                createClientCtm(id++, employee16, client1, false),

                createClientCtm(id++, employee17, client2, true),

                createClientCtm(id++, employee18, client1, true),

                createClientCtm(id++, employee19, client5, true),

                createClientCtm(id++, employee22, client1, true),
                createClientCtm(id++, employee22, client3, false),

                createClientCtm(id++, employee23, client3, false)
        );

        id = 201L;
        var communityCtms = List.of(
                createCommunityCtm(id++, employee4, community3),
                createCommunityCtm(id++, employee4, community4),

                createCommunityCtm(id++, employee9, community4),

                createCommunityCtm(id++, employee18, community1),

                createCommunityCtm(id++, employee19, community1),

                createCommunityCtm(id++, employee20, community1),

                createCommunityCtm(id++, employee21, community1)
        );

        associateClientAndContact(client1, employee6);
        associateClientAndContact(client3, employee5);
        associateClientAndContact(client4, employee3);
        associateClientAndContact(client8, employee17);


        //when

        when(communityCareTeamHieConsentPolicyUpdateService.isOnHoldCandidate(any(), any())).thenCallRealMethod();
        //todo added as community ctm

        when(clientHieConsentPolicyUpdateService.existsOptOutClientsInCommunity(addedClientCtm.getClient().getCommunityId()))
                .thenReturn(allClients.stream()
                        .filter(client -> client.getCommunityId().equals(addedClientCtm.getClient().getCommunityId()))
                        .anyMatch(client -> client.getHieConsentPolicyType() == HieConsentPolicyType.OPT_OUT));
        when(clientCareTeamHieConsentPolicyUpdateService.findCurrentCareTeamMembersOfClients(
                TestUtil.sameElementsCollection(List.of(addedClientCtm.getClientId())),
                eq(addedClientCtm.getEmployeeId()),
                eq(EmployeeIdAware.class))
        )
                .thenReturn(
                        clientCtms.stream()
                                .filter(clientCtm -> !clientCtm.getEmployeeId().equals(addedClientCtm.getEmployeeId()))
                                .filter(clientCtm -> !clientCtm.getOnHold())
                                .filter(clientCtm -> addedClientCtm.getClientId().equals(clientCtm.getClientId()))
                                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                                .collect(Collectors.toList())
                );

        when(communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeCurrent(
                TestUtil.sameElementsCollection(List.of(addedClientCtm.getClient().getCommunityId())),
                eq(addedClientCtm.getEmployeeId()),
                eq(EmployeeIdAware.class))
        )
                .thenReturn(
                        communityCtms.stream()
                                .filter(ctm -> !ctm.getEmployeeId().equals(addedClientCtm.getEmployeeId()))
                                .filter(ctm -> ctm.getCommunity().getOrganizationId().equals(ctm.getEmployee().getOrganizationId()))
                                .filter(ctm -> addedClientCtm.getClient().getCommunityId().equals(ctm.getCommunityId()))
                                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                                .collect(Collectors.toList())
                );

        when(clientHieConsentPolicyUpdateService.isOptOutPolicy(any())).thenCallRealMethod();

        var optOutClientIdsAmongAffected = Stream.of(addedClientCtm.getClient())
                .filter(client -> client.getHieConsentPolicyType() == HieConsentPolicyType.OPT_OUT)
                .map(BasicEntity::getId)
                .collect(Collectors.toSet());
        var onHoldClientsAmongAffectedOptOut = clientCtms.stream()
                .filter(ClientCareTeamMember::getOnHold)
                .filter(ctm -> ctm.getEmployeeId().equals(addedClientCtm.getEmployeeId()))
                .filter(ctm -> optOutClientIdsAmongAffected.contains(ctm.getClientId()))
                .map(ctm -> (ClientIdAware) ctm::getClientId)
                .collect(Collectors.toList());

        when(clientCareTeamHieConsentPolicyUpdateService.onHoldAmongClientIds(
                TestUtil.sameElementsCollection(optOutClientIdsAmongAffected),
                eq(addedClientCtm.getEmployeeId()),
                eq(ClientIdAware.class)
        )).thenReturn(onHoldClientsAmongAffectedOptOut);

        var notOnHoldClientsAmongOptOutInCommunity = new HashSet<>(optOutClientIdsAmongAffected);
        notOnHoldClientsAmongOptOutInCommunity.removeAll(onHoldClientsAmongAffectedOptOut.stream().map(ClientIdAware::getClientId).collect(Collectors.toList()));
        when(clientCareTeamHieConsentPolicyUpdateService.findOnHoldCareTeamMembersOfClients(
                TestUtil.sameElementsCollection(notOnHoldClientsAmongOptOutInCommunity),
                eq(addedClientCtm.getEmployeeId()),
                eq(EmployeeIdAware.class)
        )).thenReturn(clientCtms.stream()
                .filter(ClientCareTeamMember::getOnHold)
                .filter(ctm -> !ctm.getEmployeeId().equals(addedClientCtm.getEmployeeId()))
                .filter(ctm -> notOnHoldClientsAmongOptOutInCommunity.contains(ctm.getClientId()))
                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                .collect(Collectors.toList())
        );

        if (allClients.stream()
                .anyMatch(client -> client.getHieConsentPolicyType() == HieConsentPolicyType.OPT_OUT &&
                        client.getCommunityId().equals(addedClientCtm.getClient().getCommunityId()))) {
            when(communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeOnHold(
                    TestUtil.sameElementsCollection(Set.of(addedClientCtm.getClient().getCommunityId())),
                    eq(addedClientCtm.getEmployeeId()),
                    eq(EmployeeIdAware.class)
            )).thenReturn(communityCtms.stream()
                    .filter(ctm -> !ctm.getEmployee().getOrganizationId().equals(ctm.getCommunity().getOrganizationId()))
                    .filter(ctm -> !ctm.getEmployeeId().equals(addedClientCtm.getEmployeeId()))
                    .filter(ctm -> ctm.getCommunityId().equals(addedClientCtm.getClient().getCommunityId()))
                    .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                    .collect(Collectors.toList())
            );
        }

        //then
        instance.clientCareTeamMemberAdded(addedClientCtm);

        verifyNoMoreInteractions(
                clientHieConsentPolicyUpdateService,
                clientCareTeamHieConsentPolicyUpdateService,
                communityCareTeamHieConsentPolicyUpdateService
        );
        verify(chatHieConsentPolicyUpdateService).contactChangedAsync(
                eq(addedClientCtm.getEmployee()),
                eq(Set.of()),
                eq(Set.of()),
                TestUtil.sameElementsCollection(Set.of(
                        employee1.getId(),
                        employee2.getId(),

                        //although access gained not through share ctm, but as accessible client group chats are not affected
                        //and so can't pass in opt out clients. Permission checks are the same, so should be OK
                        employee5.getId(),

                        employee9.getId(),
                        employee10.getId(),
                        employee18.getId(),
                        employee19.getId(),
                        employee20.getId(),
                        employee22.getId(),
                        employee23.getId()
                )),
                TestUtil.sameElementsCollection(Set.of(
                        employee21.getId()
                )),
                any()
        );
    }

    @Test
        //input data schema in confluence https://confluence.scnsoft.com/pages/viewpage.action?pageId=266773946
    void clientCareTeamMemberAddedToOptOutClient() {
        //given
        var id = 61L;
        var organization1 = createOrganization(id++);
        var organization2 = createOrganization(id++);

        id = 51L;
        var community1 = createCommunity(id++, organization1);
        var community2 = createCommunity(id++, organization1);
        var community3 = createCommunity(id++, organization2);
        var community4 = createCommunity(id++, organization1);
        var community5 = createCommunity(id++, organization1);

        id = 31L;
        var client1 = createClient(id++, community1, HieConsentPolicyType.OPT_OUT);
        var client2 = createClient(id++, community2, HieConsentPolicyType.OPT_OUT);
        var client3 = createClient(id++, community1, HieConsentPolicyType.OPT_IN);
        var client4 = createClient(id++, community2, HieConsentPolicyType.OPT_OUT);
        var client5 = createClient(id++, community3, HieConsentPolicyType.OPT_OUT);
        var client6 = createClient(id++, community4, HieConsentPolicyType.OPT_OUT);
        var client7 = createClient(id++, community5, HieConsentPolicyType.OPT_IN);
        var client8 = createClient(id++, community1, HieConsentPolicyType.OPT_OUT);
        var allClients = List.of(
                client1, client2, client3, client4, client5, client6, client7, client8
        );

        id = 1L;
        var employee1 = createEmployee(id++, community2);
        var employee2 = createEmployee(id++, community2);
        var employee3 = createEmployee(id++, community2);
        var employee4 = createEmployee(id++, community1);
        var employee5 = createEmployee(id++, community1);
        var employee6 = createEmployee(id++, community1);
        var employee7 = createEmployee(id++, community2);
        var employee8 = createEmployee(id++, community3);
        var employee9 = createEmployee(id++, community4);
        var employee10 = createEmployee(id++, community1);
        var employee11 = createEmployee(id++, community1);
        var employee12 = createEmployee(id++, community1);
        var employee13 = createEmployee(id++, community2);
        var employee14 = createEmployee(id++, community2);
        var employee15 = createEmployee(id++, community2);
        var employee16 = createEmployee(id++, community2);
        var employee17 = createEmployee(id++, community1);
        var employee18 = createEmployee(id++, community1);
        var employee19 = createEmployee(id++, community1);
        var employee20 = createEmployee(id++, community1);
        var employee21 = createEmployee(id++, community3);
        var employee22 = createEmployee(id++, community2);
        var employee23 = createEmployee(id++, community2);


        id = 101L;
        var addedClientCtm = createClientCtm(id++, employee4, client8, true);
        var clientCtms = List.of(
                addedClientCtm,

                createClientCtm(id++, employee1, client3, false),
                createClientCtm(id++, employee1, client2, true),

                createClientCtm(id++, employee2, client3, false),
                createClientCtm(id++, employee2, client2, false),

                createClientCtm(id++, employee4, client1, true),
                createClientCtm(id++, employee4, client4, true),
                createClientCtm(id++, employee4, client7, false),
                createClientCtm(id++, employee4, client2, true),

                createClientCtm(id++, employee5, client5, true),

                createClientCtm(id++, employee7, client4, false),
                createClientCtm(id++, employee7, client7, false),

                createClientCtm(id++, employee8, client5, false),
                createClientCtm(id++, employee8, client3, true),

                createClientCtm(id++, employee9, client3, false),

                createClientCtm(id++, employee10, client1, true),
                createClientCtm(id++, employee10, client3, false),

                createClientCtm(id++, employee11, client1, true),

                createClientCtm(id++, employee12, client1, false),

                createClientCtm(id++, employee13, client1, true),

                createClientCtm(id++, employee14, client8, true),

                createClientCtm(id++, employee15, client4, false),

                createClientCtm(id++, employee16, client1, false),

                createClientCtm(id++, employee17, client2, true),

                createClientCtm(id++, employee18, client1, true),

                createClientCtm(id++, employee19, client5, true),

                createClientCtm(id++, employee22, client1, true),
                createClientCtm(id++, employee22, client3, false),

                createClientCtm(id++, employee23, client3, false)
        );

        id = 201L;
        var communityCtms = List.of(
                createCommunityCtm(id++, employee4, community3),
                createCommunityCtm(id++, employee4, community4),

                createCommunityCtm(id++, employee9, community4),

                createCommunityCtm(id++, employee18, community1),

                createCommunityCtm(id++, employee19, community1),

                createCommunityCtm(id++, employee20, community1),

                createCommunityCtm(id++, employee21, community1)
        );

        associateClientAndContact(client1, employee6);
        associateClientAndContact(client3, employee5);
        associateClientAndContact(client4, employee3);
        associateClientAndContact(client8, employee17);


        //when

        when(communityCareTeamHieConsentPolicyUpdateService.isOnHoldCandidate(any(), any())).thenCallRealMethod();
        //todo added as community ctm

        when(clientHieConsentPolicyUpdateService.existsOptOutClientsInCommunity(addedClientCtm.getClient().getCommunityId()))
                .thenReturn(allClients.stream()
                        .filter(client -> client.getCommunityId().equals(addedClientCtm.getClient().getCommunityId()))
                        .anyMatch(client -> client.getHieConsentPolicyType() == HieConsentPolicyType.OPT_OUT));

        when(clientCareTeamHieConsentPolicyUpdateService.findCurrentCareTeamMembersOfClients(
                TestUtil.sameElementsCollection(List.of(addedClientCtm.getClientId())),
                eq(addedClientCtm.getEmployeeId()),
                eq(EmployeeIdAware.class))
        )
                .thenReturn(
                        clientCtms.stream()
                                .filter(clientCtm -> !clientCtm.getEmployeeId().equals(addedClientCtm.getEmployeeId()))
                                .filter(clientCtm -> !clientCtm.getOnHold())
                                .filter(clientCtm -> addedClientCtm.getClientId().equals(clientCtm.getClientId()))
                                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                                .collect(Collectors.toList())
                );

        when(communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeCurrent(
                TestUtil.sameElementsCollection(List.of(addedClientCtm.getClient().getCommunityId())),
                eq(addedClientCtm.getEmployeeId()),
                eq(EmployeeIdAware.class))
        )
                .thenReturn(
                        communityCtms.stream()
                                .filter(ctm -> !ctm.getEmployeeId().equals(addedClientCtm.getEmployeeId()))
                                .filter(ctm -> ctm.getCommunity().getOrganizationId().equals(ctm.getEmployee().getOrganizationId()))
                                .filter(ctm -> addedClientCtm.getClient().getCommunityId().equals(ctm.getCommunityId()))
                                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                                .collect(Collectors.toList())
                );

        when(clientHieConsentPolicyUpdateService.isOptOutPolicy(any())).thenCallRealMethod();

        var optOutClientIdsAmongAffected = Stream.of(addedClientCtm.getClient())
                .filter(client -> client.getHieConsentPolicyType() == HieConsentPolicyType.OPT_OUT)
                .map(BasicEntity::getId)
                .collect(Collectors.toSet());
        var onHoldClientsAmongAffectedOptOut = clientCtms.stream()
                .filter(ClientCareTeamMember::getOnHold)
                .filter(ctm -> ctm.getEmployeeId().equals(addedClientCtm.getEmployeeId()))
                .filter(ctm -> optOutClientIdsAmongAffected.contains(ctm.getClientId()))
                .map(ctm -> (ClientIdAware) ctm::getClientId)
                .collect(Collectors.toList());

        when(clientCareTeamHieConsentPolicyUpdateService.onHoldAmongClientIds(
                TestUtil.sameElementsCollection(optOutClientIdsAmongAffected),
                eq(addedClientCtm.getEmployeeId()),
                eq(ClientIdAware.class)
        )).thenReturn(onHoldClientsAmongAffectedOptOut);

        var notOnHoldClientsAmongOptOutInCommunity = new HashSet<>(optOutClientIdsAmongAffected);
        notOnHoldClientsAmongOptOutInCommunity.removeAll(onHoldClientsAmongAffectedOptOut.stream().map(ClientIdAware::getClientId).collect(Collectors.toList()));
        when(clientCareTeamHieConsentPolicyUpdateService.findOnHoldCareTeamMembersOfClients(
                TestUtil.sameElementsCollection(notOnHoldClientsAmongOptOutInCommunity),
                eq(addedClientCtm.getEmployeeId()),
                eq(EmployeeIdAware.class)
        )).thenReturn(clientCtms.stream()
                .filter(ClientCareTeamMember::getOnHold)
                .filter(ctm -> !ctm.getEmployeeId().equals(addedClientCtm.getEmployeeId()))
                .filter(ctm -> notOnHoldClientsAmongOptOutInCommunity.contains(ctm.getClientId()))
                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                .collect(Collectors.toList())
        );

        if (addedClientCtm.getClient().getHieConsentPolicyType() == HieConsentPolicyType.OPT_OUT) {
            when(communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeOnHold(
                    TestUtil.sameElementsCollection(Set.of(addedClientCtm.getClient().getCommunityId())),
                    eq(addedClientCtm.getEmployeeId()),
                    eq(EmployeeIdAware.class)
            )).thenReturn(communityCtms.stream()
                    .filter(ctm -> !ctm.getEmployee().getOrganizationId().equals(ctm.getCommunity().getOrganizationId()))
                    .filter(ctm -> !ctm.getEmployeeId().equals(addedClientCtm.getEmployeeId()))
                    .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                    .collect(Collectors.toList())
            );
        }

        //then
        instance.clientCareTeamMemberAdded(addedClientCtm);

        verifyNoMoreInteractions(
                clientHieConsentPolicyUpdateService,
                clientCareTeamHieConsentPolicyUpdateService,
                communityCareTeamHieConsentPolicyUpdateService
        );
        verify(chatHieConsentPolicyUpdateService).contactChangedAsync(
                eq(addedClientCtm.getEmployee()),
                TestUtil.sameElementsCollection(Set.of(
                        employee17.getId()
                )),
                eq(Set.of()),
                TestUtil.sameElementsCollection(Set.of(
                        employee18.getId(),
                        employee19.getId(),
                        employee20.getId()
                )),
                TestUtil.sameElementsCollection(Set.of(
                        employee21.getId()
                )),
                any()
        );
    }

    @Test
        //input data schema in confluence https://confluence.scnsoft.com/pages/viewpage.action?pageId=266773946
    void communityCareTeamMemberCurrentDeleted() {
        //given
        var id = 61L;
        var organization1 = createOrganization(id++);
        var organization2 = createOrganization(id++);

        id = 51L;
        var community1 = createCommunity(id++, organization1);
        var community2 = createCommunity(id++, organization1);
        var community3 = createCommunity(id++, organization2);
        var community4 = createCommunity(id++, organization1);
        var community5 = createCommunity(id++, organization1);

        id = 31L;
        var client1 = createClient(id++, community1, HieConsentPolicyType.OPT_OUT);
        var client2 = createClient(id++, community2, HieConsentPolicyType.OPT_OUT);
        var client3 = createClient(id++, community1, HieConsentPolicyType.OPT_IN);
        var client4 = createClient(id++, community2, HieConsentPolicyType.OPT_OUT);
        var client5 = createClient(id++, community3, HieConsentPolicyType.OPT_OUT);
        var client6 = createClient(id++, community4, HieConsentPolicyType.OPT_OUT);
        var client7 = createClient(id++, community5, HieConsentPolicyType.OPT_IN);
        var client8 = createClient(id++, community1, HieConsentPolicyType.OPT_OUT);
        var allClients = List.of(
                client1, client2, client3, client4, client5, client6, client7, client8
        );

        id = 1L;
        var employee1 = createEmployee(id++, community2);
        var employee2 = createEmployee(id++, community2);
        var employee3 = createEmployee(id++, community2);
        var employee4 = createEmployee(id++, community1);
        var employee5 = createEmployee(id++, community1);
        var employee6 = createEmployee(id++, community1);
        var employee7 = createEmployee(id++, community2);
        var employee8 = createEmployee(id++, community3);
        var employee9 = createEmployee(id++, community4);
        var employee10 = createEmployee(id++, community1);
        var employee11 = createEmployee(id++, community1);
        var employee12 = createEmployee(id++, community1);
        var employee13 = createEmployee(id++, community2);
        var employee14 = createEmployee(id++, community2);
        var employee15 = createEmployee(id++, community2);
        var employee16 = createEmployee(id++, community2);
        var employee17 = createEmployee(id++, community1);
        var employee18 = createEmployee(id++, community1);
        var employee19 = createEmployee(id++, community1);
        var employee20 = createEmployee(id++, community1);
        var employee21 = createEmployee(id++, community3);
        var employee22 = createEmployee(id++, community2);
        var employee23 = createEmployee(id++, community2);


        id = 101L;
        var clientCtms = List.of(
                createClientCtm(id++, employee1, client3, false),
                createClientCtm(id++, employee1, client2, true),

                createClientCtm(id++, employee2, client3, false),
                createClientCtm(id++, employee2, client2, false),

                createClientCtm(id++, employee4, client1, true),
                createClientCtm(id++, employee4, client4, true),
                createClientCtm(id++, employee4, client7, false),
                createClientCtm(id++, employee4, client2, true),

                createClientCtm(id++, employee5, client5, true),

                createClientCtm(id++, employee7, client4, false),
                createClientCtm(id++, employee7, client7, false),

                createClientCtm(id++, employee8, client5, false),
                createClientCtm(id++, employee8, client3, true),

                createClientCtm(id++, employee9, client3, false),

                createClientCtm(id++, employee10, client1, true),
                createClientCtm(id++, employee10, client3, false),

                createClientCtm(id++, employee11, client1, true),

                createClientCtm(id++, employee12, client1, false),

                createClientCtm(id++, employee13, client1, true),

                createClientCtm(id++, employee14, client8, true),

                createClientCtm(id++, employee15, client4, false),

                createClientCtm(id++, employee16, client1, false),

                createClientCtm(id++, employee17, client2, true),

                createClientCtm(id++, employee18, client1, true),

                createClientCtm(id++, employee19, client5, true),

                createClientCtm(id++, employee22, client1, true),
                createClientCtm(id++, employee22, client3, false),

                createClientCtm(id++, employee23, client3, false)
        );

        id = 201L;
        var removedCommunityCtm = createCommunityCtm(id++, employee4, community1);

        var communityCtms = List.of(
                createCommunityCtm(id++, employee4, community3),
                createCommunityCtm(id++, employee4, community4),

                createCommunityCtm(id++, employee9, community4),

                createCommunityCtm(id++, employee18, community1),

                createCommunityCtm(id++, employee19, community1),

                createCommunityCtm(id++, employee20, community1),

                createCommunityCtm(id++, employee21, community1)
        );

        associateClientAndContact(client1, employee6);
        associateClientAndContact(client3, employee5);
        associateClientAndContact(client4, employee3);
        associateClientAndContact(client8, employee17);


        //when

        when(communityCareTeamHieConsentPolicyUpdateService.isOnHoldCandidate(any(), any())).thenCallRealMethod();

        var clientsInCommunity = allClients.stream()
                .filter(client -> client.getCommunityId().equals(removedCommunityCtm.getCommunityId()))
                .map(this::clientEntityCareTeamUpdateProjectionAdapter)
                .collect(Collectors.toList());
        when(clientHieConsentPolicyUpdateService.findClientsInCommunity(
                removedCommunityCtm.getCommunityId(),
                HieConsentPolicyUpdateServiceImpl.ClientEntityCareTeamUpdateProjection.class)
        )
                .thenReturn(clientsInCommunity);


        when(clientCareTeamHieConsentPolicyUpdateService.findCurrentCareTeamMembersOfClients(
                TestUtil.sameElementsCollection(CareCoordinationUtils.toIdsSet(clientsInCommunity)),
                eq(removedCommunityCtm.getEmployeeId()),
                eq(EmployeeIdAware.class))
        )
                .thenReturn(
                        clientCtms.stream()
                                .filter(clientCtm -> !clientCtm.getEmployeeId().equals(removedCommunityCtm.getEmployeeId()))
                                .filter(clientCtm -> !clientCtm.getOnHold())
                                .filter(clientCtm -> CareCoordinationUtils.toIdsSet(clientsInCommunity).contains(clientCtm.getClientId()))
                                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                                .collect(Collectors.toList())
                );

        when(communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeCurrent(
                TestUtil.sameElementsCollection(List.of(removedCommunityCtm.getCommunityId())),
                eq(removedCommunityCtm.getEmployeeId()),
                eq(EmployeeIdAware.class))
        )
                .thenReturn(
                        communityCtms.stream()
                                .filter(ctm -> !ctm.getEmployeeId().equals(removedCommunityCtm.getEmployeeId()))
                                .filter(ctm -> ctm.getCommunity().getOrganizationId().equals(ctm.getEmployee().getOrganizationId()))
                                .filter(ctm -> removedCommunityCtm.getCommunityId().equals(ctm.getCommunityId()))
                                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                                .collect(Collectors.toList())
                );

        when(clientHieConsentPolicyUpdateService.isOptOutPolicy(any())).thenCallRealMethod();

        var optOutClientIdsInCommunity = allClients.stream()
                .filter(client -> client.getCommunityId().equals(removedCommunityCtm.getCommunityId()))
                .filter(client -> client.getHieConsentPolicyType() == HieConsentPolicyType.OPT_OUT)
                .map(BasicEntity::getId)
                .collect(Collectors.toSet());
        var onHoldClientsAmongOptOutInCommunity = clientCtms.stream()
                .filter(ClientCareTeamMember::getOnHold)
                .filter(ctm -> ctm.getEmployeeId().equals(removedCommunityCtm.getEmployeeId()))
                .filter(ctm -> optOutClientIdsInCommunity.contains(ctm.getClientId()))
                .map(ctm -> (ClientIdAware) ctm::getClientId)
                .collect(Collectors.toList());

        when(clientCareTeamHieConsentPolicyUpdateService.onHoldAmongClientIds(
                TestUtil.sameElementsCollection(optOutClientIdsInCommunity),
                eq(removedCommunityCtm.getEmployeeId()),
                eq(ClientIdAware.class)
        )).thenReturn(onHoldClientsAmongOptOutInCommunity);

        var notOnHoldClientsAmongOptOutInCommunity = new HashSet<>(optOutClientIdsInCommunity);
        notOnHoldClientsAmongOptOutInCommunity.removeAll(onHoldClientsAmongOptOutInCommunity.stream().map(ClientIdAware::getClientId).collect(Collectors.toList()));
        when(clientCareTeamHieConsentPolicyUpdateService.findOnHoldCareTeamMembersOfClients(
                TestUtil.sameElementsCollection(notOnHoldClientsAmongOptOutInCommunity),
                eq(removedCommunityCtm.getEmployeeId()),
                eq(EmployeeIdAware.class)
        )).thenReturn(clientCtms.stream()
                .filter(ctm -> ctm.getOnHold())
                .filter(ctm -> !ctm.getEmployeeId().equals(removedCommunityCtm.getEmployeeId()))
                .filter(ctm -> notOnHoldClientsAmongOptOutInCommunity.contains(ctm.getClientId()))
                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                .collect(Collectors.toList())
        );

        when(communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeOnHold(
                TestUtil.sameElementsCollection(Set.of(removedCommunityCtm.getCommunityId())),
                eq(removedCommunityCtm.getEmployeeId()),
                eq(EmployeeIdAware.class)
        )).thenReturn(communityCtms.stream()
                .filter(ctm -> !ctm.getEmployee().getOrganizationId().equals(ctm.getCommunity().getOrganizationId()))
                .filter(ctm -> !ctm.getEmployeeId().equals(removedCommunityCtm.getEmployeeId()))
                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                .collect(Collectors.toList())
        );


        //then
        instance.communityCareTeamMemberDeleted(removedCommunityCtm.getEmployee(), removedCommunityCtm.getCommunity());

        verifyNoMoreInteractions(
                clientHieConsentPolicyUpdateService,
                clientCareTeamHieConsentPolicyUpdateService,
                communityCareTeamHieConsentPolicyUpdateService
        );
        verify(chatHieConsentPolicyUpdateService).contactChangedAsync(
                eq(removedCommunityCtm.getEmployee()),
                eq(Set.of()),
                TestUtil.sameElementsCollection(Set.of(
                        employee6.getId(),
                        employee17.getId()
                )),
                TestUtil.sameElementsCollection(Set.of(
                        employee14.getId(),
                        employee21.getId()
                )),
                TestUtil.sameElementsCollection(Set.of(
                        employee1.getId(),
                        employee2.getId(),

                        //although access gained not through share ctm, but as accessible client group chats are not affected
                        //and so can't pass in opt out clients. Permission checks are the same, so should be OK
                        employee5.getId(),

                        //employee7.getId(),  //this one is optional
                        employee9.getId(),
                        employee10.getId(),
                        employee12.getId(),
                        employee16.getId(),
                        employee18.getId(),
                        employee19.getId(),
                        employee20.getId(),
                        employee22.getId(),
                        employee23.getId()
                )),
                any()
        );
    }

    @Test
        //input data schema in confluence https://confluence.scnsoft.com/pages/viewpage.action?pageId=266773946
    void communityCareTeamMemberOnHoldDeleted() {
        //given
        var id = 61L;
        var organization1 = createOrganization(id++);
        var organization2 = createOrganization(id++);
        var organization3 = createOrganization(id++);

        id = 51L;
        var community1 = createCommunity(id++, organization1);
        var community2 = createCommunity(id++, organization1);
        var community3 = createCommunity(id++, organization2);
        var community4 = createCommunity(id++, organization1);
        var community5 = createCommunity(id++, organization1);

        var communityForOnHoldEmployee = createCommunity(id++, organization3);

        id = 31L;
        var client1 = createClient(id++, community1, HieConsentPolicyType.OPT_OUT);
        var client2 = createClient(id++, community2, HieConsentPolicyType.OPT_OUT);
        var client3 = createClient(id++, community1, HieConsentPolicyType.OPT_IN);
        var client4 = createClient(id++, community2, HieConsentPolicyType.OPT_OUT);
        var client5 = createClient(id++, community3, HieConsentPolicyType.OPT_OUT);
        var client6 = createClient(id++, community4, HieConsentPolicyType.OPT_OUT);
        var client7 = createClient(id++, community5, HieConsentPolicyType.OPT_IN);
        var client8 = createClient(id++, community1, HieConsentPolicyType.OPT_OUT);
        var allClients = List.of(
                client1, client2, client3, client4, client5, client6, client7, client8
        );

        id = 1L;
        var employee1 = createEmployee(id++, community2);
        var employee2 = createEmployee(id++, community2);
        var employee3 = createEmployee(id++, community2);
        var employee4 = createEmployee(id++, communityForOnHoldEmployee);
        var employee5 = createEmployee(id++, community1);
        var employee6 = createEmployee(id++, community1);
        var employee7 = createEmployee(id++, community2);
        var employee8 = createEmployee(id++, community3);
        var employee9 = createEmployee(id++, community4);
        var employee10 = createEmployee(id++, community1);
        var employee11 = createEmployee(id++, community1);
        var employee12 = createEmployee(id++, community1);
        var employee13 = createEmployee(id++, community2);
        var employee14 = createEmployee(id++, community2);
        var employee15 = createEmployee(id++, community2);
        var employee16 = createEmployee(id++, community2);
        var employee17 = createEmployee(id++, community1);
        var employee18 = createEmployee(id++, community1);
        var employee19 = createEmployee(id++, community1);
        var employee20 = createEmployee(id++, community1);
        var employee21 = createEmployee(id++, community3);
        var employee22 = createEmployee(id++, community2);
        var employee23 = createEmployee(id++, community2);


        id = 101L;
        var clientCtms = List.of(
                createClientCtm(id++, employee1, client3, false),
                createClientCtm(id++, employee1, client2, true),

                createClientCtm(id++, employee2, client3, false),
                createClientCtm(id++, employee2, client2, false),

                createClientCtm(id++, employee4, client1, true),
                createClientCtm(id++, employee4, client4, true),
                createClientCtm(id++, employee4, client7, false),
                createClientCtm(id++, employee4, client2, true),

                createClientCtm(id++, employee5, client5, true),

                createClientCtm(id++, employee7, client4, false),
                createClientCtm(id++, employee7, client7, false),

                createClientCtm(id++, employee8, client5, false),
                createClientCtm(id++, employee8, client3, true),

                createClientCtm(id++, employee9, client3, false),

                createClientCtm(id++, employee10, client1, true),
                createClientCtm(id++, employee10, client3, false),

                createClientCtm(id++, employee11, client1, true),

                createClientCtm(id++, employee12, client1, false),

                createClientCtm(id++, employee13, client1, true),

                createClientCtm(id++, employee14, client8, true),

                createClientCtm(id++, employee15, client4, false),

                createClientCtm(id++, employee16, client1, false),

                createClientCtm(id++, employee17, client2, true),

                createClientCtm(id++, employee18, client1, true),

                createClientCtm(id++, employee19, client5, true),

                createClientCtm(id++, employee22, client1, true),
                createClientCtm(id++, employee22, client3, false),

                createClientCtm(id++, employee23, client3, false)
        );

        id = 201L;
        var removedCommunityCtm = createCommunityCtm(id++, employee4, community1);

        var communityCtms = List.of(
                createCommunityCtm(id++, employee4, community3),
                createCommunityCtm(id++, employee4, community4),

                createCommunityCtm(id++, employee9, community4),

                createCommunityCtm(id++, employee18, community1),

                createCommunityCtm(id++, employee19, community1),

                createCommunityCtm(id++, employee20, community1),

                createCommunityCtm(id++, employee21, community1)
        );

        associateClientAndContact(client1, employee6);
        associateClientAndContact(client3, employee5);
        associateClientAndContact(client4, employee3);
        associateClientAndContact(client8, employee17);


        //when

        when(communityCareTeamHieConsentPolicyUpdateService.isOnHoldCandidate(any(), any())).thenCallRealMethod();

        var clientsInCommunity = allClients.stream()
                .filter(client -> client.getCommunityId().equals(removedCommunityCtm.getCommunityId()))
                .map(this::clientEntityCareTeamUpdateProjectionAdapter)
                .collect(Collectors.toList());
        when(clientHieConsentPolicyUpdateService.findClientsInCommunity(
                removedCommunityCtm.getCommunityId(),
                HieConsentPolicyUpdateServiceImpl.ClientEntityCareTeamUpdateProjection.class)
        )
                .thenReturn(clientsInCommunity);


        var addedAsOnHoldToClientsInCommunity = clientCtms.stream()
                .filter(ctm -> ctm.getEmployeeId().equals(removedCommunityCtm.getEmployeeId()))
                .filter(ClientCareTeamMember::getOnHold)
                .filter(ctm -> CareCoordinationUtils.toIdsSet(clientsInCommunity).contains(ctm.getClientId()))
                .map(ctm -> (ClientIdAware) ctm::getClientId)
                .collect(Collectors.toList());
        when(clientCareTeamHieConsentPolicyUpdateService.onHoldAmongClientIds(
                TestUtil.sameElementsCollection(CareCoordinationUtils.toIdsSet(clientsInCommunity)),
                eq(removedCommunityCtm.getEmployeeId()),
                eq(ClientIdAware.class)
        )).thenReturn(addedAsOnHoldToClientsInCommunity);

        var affectedClients = clientsInCommunity.stream()
                .filter(client -> addedAsOnHoldToClientsInCommunity.stream().map(ClientIdAware::getClientId).noneMatch(client.getId()::equals))
                .collect(Collectors.toList());

        when(clientHieConsentPolicyUpdateService.isOptOutPolicy(any())).thenCallRealMethod();


        when(clientCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfClients(
                TestUtil.sameElementsCollection(CareCoordinationUtils.toIdsSet(affectedClients)),
                eq(removedCommunityCtm.getEmployeeId()),
                eq(EmployeeIdAware.class))
        )
                .thenReturn(
                        clientCtms.stream()
                                .filter(clientCtm -> !clientCtm.getEmployeeId().equals(removedCommunityCtm.getEmployeeId()))
                                .filter(clientCtm -> CareCoordinationUtils.toIdsSet(affectedClients).contains(clientCtm.getClientId()))
                                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                                .collect(Collectors.toList())
                );

        when(communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeCurrent(
                TestUtil.sameElementsCollection(List.of(removedCommunityCtm.getCommunityId())),
                eq(removedCommunityCtm.getEmployeeId()),
                eq(EmployeeIdAware.class))
        )
                .thenReturn(
                        communityCtms.stream()
                                .filter(ctm -> !ctm.getEmployeeId().equals(removedCommunityCtm.getEmployeeId()))
                                .filter(ctm -> ctm.getCommunity().getOrganizationId().equals(ctm.getEmployee().getOrganizationId()))
                                .filter(ctm -> removedCommunityCtm.getCommunityId().equals(ctm.getCommunityId()))
                                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                                .collect(Collectors.toList())
                );

        //todo boolean flag
        //boolean isOptOutClientExistInCommunityAndNotOnHoldCtmForAll = false;
        //            if (CollectionUtils.isEmpty(addedAsOnHoldClientCtmClientIds)) {
        //                isOptOutClientExistInCommunityAndNotOnHoldCtmForAll = clientsInCommunity.stream()
        //                        .anyMatch(clientHieConsentPolicyUpdateService::isOptOutPolicy);
        //            }
        if (false) {
            when(communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeOnHold(
                    TestUtil.sameElementsCollection(List.of(removedCommunityCtm.getCommunityId())),
                    eq(removedCommunityCtm.getEmployeeId()),
                    eq(EmployeeIdAware.class)
            ))
                    .thenReturn(communityCtms.stream()
                            .filter(ctm -> !ctm.getEmployeeId().equals(removedCommunityCtm.getEmployeeId()))
                            .filter(ctm -> ctm.getCommunityId().equals(removedCommunityCtm.getCommunityId()))
                            .filter(ctm -> !ctm.getEmployee().getOrganizationId().equals(ctm.getCommunity().getOrganizationId()))
                            .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                            .collect(Collectors.toList())
                    );
        }

        //then
        instance.communityCareTeamMemberDeleted(removedCommunityCtm.getEmployee(), removedCommunityCtm.getCommunity());

        verifyNoMoreInteractions(
                clientHieConsentPolicyUpdateService,
                clientCareTeamHieConsentPolicyUpdateService,
                communityCareTeamHieConsentPolicyUpdateService
        );
        verify(chatHieConsentPolicyUpdateService).contactChangedAsync(
                eq(removedCommunityCtm.getEmployee()),
                TestUtil.sameElementsCollection(Set.of(
                        employee17.getId()
                )),
                eq(Set.of()),
                TestUtil.sameElementsCollection(Set.of(
                        employee1.getId(),
                        employee2.getId(),

                        //although access gained not through share ctm, but as accessible client group chats are not affected
                        //and so can't pass in opt out clients. Permission checks are the same, so should be OK
                        employee5.getId(),

                        //employee7.getId(),  //this one is optional
                        employee8.getId(),
                        employee9.getId(),
                        employee10.getId(),
                        employee14.getId(),
                        employee18.getId(),
                        employee19.getId(),
                        employee20.getId(),
                        employee22.getId(),
                        employee23.getId()
                )),
                eq(Set.of()),
                any()
        );
    }

    @Test
        //input data schema in confluence https://confluence.scnsoft.com/pages/viewpage.action?pageId=266773946
    void clientCareTeamMemberCurrentDeletedOptInClient() {
        //given
        var id = 61L;
        var organization1 = createOrganization(id++);
        var organization2 = createOrganization(id++);

        id = 51L;
        var community1 = createCommunity(id++, organization1);
        var community2 = createCommunity(id++, organization1);
        var community3 = createCommunity(id++, organization2);
        var community4 = createCommunity(id++, organization1);
        var community5 = createCommunity(id++, organization1);

        id = 31L;
        var client1 = createClient(id++, community1, HieConsentPolicyType.OPT_OUT);
        var client2 = createClient(id++, community2, HieConsentPolicyType.OPT_OUT);
        var client3 = createClient(id++, community1, HieConsentPolicyType.OPT_IN);
        var client4 = createClient(id++, community2, HieConsentPolicyType.OPT_OUT);
        var client5 = createClient(id++, community3, HieConsentPolicyType.OPT_OUT);
        var client6 = createClient(id++, community4, HieConsentPolicyType.OPT_OUT);
        var client7 = createClient(id++, community5, HieConsentPolicyType.OPT_IN);
        var client8 = createClient(id++, community1, HieConsentPolicyType.OPT_OUT);
        var allClients = List.of(
                client1, client2, client3, client4, client5, client6, client7, client8
        );

        id = 1L;
        var employee1 = createEmployee(id++, community2);
        var employee2 = createEmployee(id++, community2);
        var employee3 = createEmployee(id++, community2);
        var employee4 = createEmployee(id++, community1);
        var employee5 = createEmployee(id++, community1);
        var employee6 = createEmployee(id++, community1);
        var employee7 = createEmployee(id++, community2);
        var employee8 = createEmployee(id++, community3);
        var employee9 = createEmployee(id++, community4);
        var employee10 = createEmployee(id++, community1);
        var employee11 = createEmployee(id++, community1);
        var employee12 = createEmployee(id++, community1);
        var employee13 = createEmployee(id++, community2);
        var employee14 = createEmployee(id++, community2);
        var employee15 = createEmployee(id++, community2);
        var employee16 = createEmployee(id++, community2);
        var employee17 = createEmployee(id++, community1);
        var employee18 = createEmployee(id++, community1);
        var employee19 = createEmployee(id++, community1);
        var employee20 = createEmployee(id++, community1);
        var employee21 = createEmployee(id++, community3);
        var employee22 = createEmployee(id++, community2);
        var employee23 = createEmployee(id++, community2);


        id = 101L;
        var removedClientCtm = createClientCtm(id++, employee4, client3, false);
        var clientCtms = List.of(

                createClientCtm(id++, employee1, client3, false),
                createClientCtm(id++, employee1, client2, true),

                createClientCtm(id++, employee2, client3, false),
                createClientCtm(id++, employee2, client2, false),

                createClientCtm(id++, employee4, client1, true),
                createClientCtm(id++, employee4, client4, true),
                createClientCtm(id++, employee4, client7, false),
                createClientCtm(id++, employee4, client2, true),

                createClientCtm(id++, employee5, client5, true),

                createClientCtm(id++, employee7, client4, false),
                createClientCtm(id++, employee7, client7, false),

                createClientCtm(id++, employee8, client5, false),
                createClientCtm(id++, employee8, client3, true),

                createClientCtm(id++, employee9, client3, false),

                createClientCtm(id++, employee10, client1, true),
                createClientCtm(id++, employee10, client3, false),

                createClientCtm(id++, employee11, client1, true),

                createClientCtm(id++, employee12, client1, false),

                createClientCtm(id++, employee13, client1, true),

                createClientCtm(id++, employee14, client8, true),

                createClientCtm(id++, employee15, client4, false),

                createClientCtm(id++, employee16, client1, false),

                createClientCtm(id++, employee17, client2, true),

                createClientCtm(id++, employee18, client1, true),

                createClientCtm(id++, employee19, client5, true),

                createClientCtm(id++, employee22, client1, true),
                createClientCtm(id++, employee22, client3, false),

                createClientCtm(id++, employee23, client3, false)
        );

        id = 201L;
        var communityCtms = List.of(
                createCommunityCtm(id++, employee4, community3),
                createCommunityCtm(id++, employee4, community4),

                createCommunityCtm(id++, employee9, community4),

                createCommunityCtm(id++, employee18, community1),

                createCommunityCtm(id++, employee19, community1),

                createCommunityCtm(id++, employee20, community1),

                createCommunityCtm(id++, employee21, community1)
        );

        associateClientAndContact(client1, employee6);
        associateClientAndContact(client3, employee5);
        associateClientAndContact(client4, employee3);
        associateClientAndContact(client8, employee17);


        //when

        when(communityCareTeamHieConsentPolicyUpdateService.communityCareTeamMemberEntries(
                eq(removedClientCtm.getEmployeeId()),
                TestUtil.sameElementsCollection(List.of(removedClientCtm.getClient().getCommunityId())),
                eq(EmployeeOrgIdCommunityOrgIdAware.class)
        ))
                .thenReturn(communityCtms.stream()
                        .filter(ctm -> ctm.getCommunityId().equals(removedClientCtm.getClient().getCommunityId()))
                        .filter(ctm -> ctm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                        .map(this::employeeOrgIdCommunityOrgIdAwareAdapter)
                        .collect(Collectors.toList())
                );

        lenient().when(communityCareTeamHieConsentPolicyUpdateService.isOnHoldCandidate(any(), any())).thenCallRealMethod();

        //todo added as community ctm

        when(clientCareTeamHieConsentPolicyUpdateService.findClientCareTeamEntries(
                eq(removedClientCtm.getEmployeeId()),
                eq(removedClientCtm.getClientId()),
                eq(OnHoldAware.class)
        ))
                .thenReturn(clientCtms.stream()
                        .filter(ctm -> ctm.getClientId().equals(removedClientCtm.getClientId()))
                        .filter(ctm -> ctm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                        .map(ctm -> (OnHoldAware) ctm::getOnHold)
                        .collect(Collectors.toList())
                );

        when(clientHieConsentPolicyUpdateService.existsOptOutClientsInCommunity(removedClientCtm.getClient().getCommunityId()))
                .thenReturn(allClients.stream()
                        .filter(client -> client.getCommunityId().equals(removedClientCtm.getClient().getCommunityId()))
                        .anyMatch(client -> client.getHieConsentPolicyType() == HieConsentPolicyType.OPT_OUT)
                );

        when(clientCareTeamHieConsentPolicyUpdateService.findCurrentCareTeamMembersOfClients(
                TestUtil.sameElementsCollection(List.of(removedClientCtm.getClientId())),
                eq(removedClientCtm.getEmployeeId()),
                eq(EmployeeIdAware.class))
        )
                .thenReturn(
                        clientCtms.stream()
                                .filter(clientCtm -> !clientCtm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                                .filter(clientCtm -> !clientCtm.getOnHold())
                                .filter(clientCtm -> removedClientCtm.getClientId().equals(clientCtm.getClientId()))
                                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                                .collect(Collectors.toList())
                );

        when(communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeCurrent(
                TestUtil.sameElementsCollection(List.of(removedClientCtm.getClient().getCommunityId())),
                eq(removedClientCtm.getEmployeeId()),
                eq(EmployeeIdAware.class))
        )
                .thenReturn(
                        communityCtms.stream()
                                .filter(ctm -> !ctm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                                .filter(ctm -> ctm.getCommunity().getOrganizationId().equals(ctm.getEmployee().getOrganizationId()))
                                .filter(ctm -> removedClientCtm.getClient().getCommunityId().equals(ctm.getCommunityId()))
                                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                                .collect(Collectors.toList())
                );

        when(clientHieConsentPolicyUpdateService.isOptOutPolicy(any())).thenCallRealMethod();

        var optOutClientIdsAmongAffected = Stream.of(removedClientCtm.getClient())
                .filter(client -> client.getHieConsentPolicyType() == HieConsentPolicyType.OPT_OUT)
                .map(BasicEntity::getId)
                .collect(Collectors.toSet());
        var onHoldClientsAmongAffectedOptOut = clientCtms.stream()
                .filter(ClientCareTeamMember::getOnHold)
                .filter(ctm -> ctm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                .filter(ctm -> optOutClientIdsAmongAffected.contains(ctm.getClientId()))
                .map(ctm -> (ClientIdAware) ctm::getClientId)
                .collect(Collectors.toList());

        when(clientCareTeamHieConsentPolicyUpdateService.onHoldAmongClientIds(
                TestUtil.sameElementsCollection(optOutClientIdsAmongAffected),
                eq(removedClientCtm.getEmployeeId()),
                eq(ClientIdAware.class)
        )).thenReturn(onHoldClientsAmongAffectedOptOut);

        var notOnHoldClientsAmongOptOutInCommunity = new HashSet<>(optOutClientIdsAmongAffected);
        notOnHoldClientsAmongOptOutInCommunity.removeAll(onHoldClientsAmongAffectedOptOut.stream().map(ClientIdAware::getClientId).collect(Collectors.toList()));
        when(clientCareTeamHieConsentPolicyUpdateService.findOnHoldCareTeamMembersOfClients(
                TestUtil.sameElementsCollection(notOnHoldClientsAmongOptOutInCommunity),
                eq(removedClientCtm.getEmployeeId()),
                eq(EmployeeIdAware.class)
        )).thenReturn(clientCtms.stream()
                .filter(ClientCareTeamMember::getOnHold)
                .filter(ctm -> !ctm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                .filter(ctm -> notOnHoldClientsAmongOptOutInCommunity.contains(ctm.getClientId()))
                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                .collect(Collectors.toList())
        );

        if (allClients.stream()
                .anyMatch(client -> client.getHieConsentPolicyType() == HieConsentPolicyType.OPT_OUT &&
                        client.getCommunityId().equals(removedClientCtm.getClient().getCommunityId()))) {
            when(communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeOnHold(
                    TestUtil.sameElementsCollection(Set.of(removedClientCtm.getClient().getCommunityId())),
                    eq(removedClientCtm.getEmployeeId()),
                    eq(EmployeeIdAware.class)
            )).thenReturn(communityCtms.stream()
                    .filter(ctm -> !ctm.getEmployee().getOrganizationId().equals(ctm.getCommunity().getOrganizationId()))
                    .filter(ctm -> !ctm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                    .filter(ctm -> ctm.getCommunityId().equals(removedClientCtm.getClient().getCommunityId()))
                    .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                    .collect(Collectors.toList())
            );
        }

        //then
        instance.clientCareTeamMemberDeleted(removedClientCtm.getEmployee(), removedClientCtm.getClient(), removedClientCtm.getOnHold());

        verifyNoMoreInteractions(
                clientHieConsentPolicyUpdateService,
                clientCareTeamHieConsentPolicyUpdateService,
                communityCareTeamHieConsentPolicyUpdateService
        );

        verify(chatHieConsentPolicyUpdateService).contactChangedAsync(
                eq(removedClientCtm.getEmployee()),
                eq(Set.of()),
                eq(Set.of()),
                TestUtil.sameElementsCollection(Set.of(
                        employee21.getId()
                )),
                TestUtil.sameElementsCollection(Set.of(
                        employee1.getId(),
                        employee2.getId(),

                        //although access gained not through share ctm, but as accessible client group chats are not affected
                        //and so can't pass in opt out clients. Permission checks are the same, so should be OK
                        employee5.getId(),

                        employee9.getId(),
                        employee10.getId(),
                        employee18.getId(),
                        employee19.getId(),
                        employee20.getId(),
                        employee22.getId(),
                        employee23.getId()
                )),
                any()
        );

    }

    @Test
        //input data schema in confluence https://confluence.scnsoft.com/pages/viewpage.action?pageId=266773946
    void clientCareTeamMemberCurrentDeletedOptOutClient() {
        //given
        var id = 61L;
        var organization1 = createOrganization(id++);
        var organization2 = createOrganization(id++);

        id = 51L;
        var community1 = createCommunity(id++, organization1);
        var community2 = createCommunity(id++, organization1);
        var community3 = createCommunity(id++, organization2);
        var community4 = createCommunity(id++, organization1);
        var community5 = createCommunity(id++, organization1);

        id = 31L;
        var client1 = createClient(id++, community1, HieConsentPolicyType.OPT_OUT);
        var client2 = createClient(id++, community2, HieConsentPolicyType.OPT_OUT);
        var client3 = createClient(id++, community1, HieConsentPolicyType.OPT_IN);
        var client4 = createClient(id++, community2, HieConsentPolicyType.OPT_OUT);
        var client5 = createClient(id++, community3, HieConsentPolicyType.OPT_OUT);
        var client6 = createClient(id++, community4, HieConsentPolicyType.OPT_OUT);
        var client7 = createClient(id++, community5, HieConsentPolicyType.OPT_IN);
        var client8 = createClient(id++, community1, HieConsentPolicyType.OPT_OUT);
        var allClients = List.of(
                client1, client2, client3, client4, client5, client6, client7, client8
        );

        id = 1L;
        var employee1 = createEmployee(id++, community2);
        var employee2 = createEmployee(id++, community2);
        var employee3 = createEmployee(id++, community2);
        var employee4 = createEmployee(id++, community1);
        var employee5 = createEmployee(id++, community1);
        var employee6 = createEmployee(id++, community1);
        var employee7 = createEmployee(id++, community2);
        var employee8 = createEmployee(id++, community3);
        var employee9 = createEmployee(id++, community4);
        var employee10 = createEmployee(id++, community1);
        var employee11 = createEmployee(id++, community1);
        var employee12 = createEmployee(id++, community1);
        var employee13 = createEmployee(id++, community2);
        var employee14 = createEmployee(id++, community2);
        var employee15 = createEmployee(id++, community2);
        var employee16 = createEmployee(id++, community2);
        var employee17 = createEmployee(id++, community1);
        var employee18 = createEmployee(id++, community1);
        var employee19 = createEmployee(id++, community1);
        var employee20 = createEmployee(id++, community1);
        var employee21 = createEmployee(id++, community3);
        var employee22 = createEmployee(id++, community2);
        var employee23 = createEmployee(id++, community2);


        id = 101L;
        var removedClientCtm = createClientCtm(id++, employee4, client8, false);
        var clientCtms = List.of(

                createClientCtm(id++, employee1, client3, false),
                createClientCtm(id++, employee1, client2, true),

                createClientCtm(id++, employee2, client3, false),
                createClientCtm(id++, employee2, client2, false),

                createClientCtm(id++, employee4, client1, true),
                createClientCtm(id++, employee4, client4, true),
                createClientCtm(id++, employee4, client7, false),
                createClientCtm(id++, employee4, client2, true),

                createClientCtm(id++, employee5, client5, true),

                createClientCtm(id++, employee7, client4, false),
                createClientCtm(id++, employee7, client7, false),

                createClientCtm(id++, employee8, client5, false),
                createClientCtm(id++, employee8, client3, true),

                createClientCtm(id++, employee9, client3, false),

                createClientCtm(id++, employee10, client1, true),
                createClientCtm(id++, employee10, client3, false),

                createClientCtm(id++, employee11, client1, true),

                createClientCtm(id++, employee12, client1, false),

                createClientCtm(id++, employee13, client1, true),

                createClientCtm(id++, employee14, client8, true),

                createClientCtm(id++, employee15, client4, false),

                createClientCtm(id++, employee16, client1, false),

                createClientCtm(id++, employee17, client2, true),

                createClientCtm(id++, employee18, client1, true),

                createClientCtm(id++, employee19, client5, true),

                createClientCtm(id++, employee22, client1, true),
                createClientCtm(id++, employee22, client3, false),

                createClientCtm(id++, employee23, client3, false)
        );

        id = 201L;
        var communityCtms = List.of(
                createCommunityCtm(id++, employee4, community3),
                createCommunityCtm(id++, employee4, community4),

                createCommunityCtm(id++, employee9, community4),

                createCommunityCtm(id++, employee18, community1),

                createCommunityCtm(id++, employee19, community1),

                createCommunityCtm(id++, employee20, community1),

                createCommunityCtm(id++, employee21, community1)
        );

        associateClientAndContact(client1, employee6);
        associateClientAndContact(client3, employee5);
        associateClientAndContact(client4, employee3);
        associateClientAndContact(client8, employee17);


        //when

        when(communityCareTeamHieConsentPolicyUpdateService.communityCareTeamMemberEntries(
                eq(removedClientCtm.getEmployeeId()),
                TestUtil.sameElementsCollection(List.of(removedClientCtm.getClient().getCommunityId())),
                eq(EmployeeOrgIdCommunityOrgIdAware.class)
        ))
                .thenReturn(communityCtms.stream()
                        .filter(ctm -> ctm.getCommunityId().equals(removedClientCtm.getClient().getCommunityId()))
                        .filter(ctm -> ctm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                        .map(this::employeeOrgIdCommunityOrgIdAwareAdapter)
                        .collect(Collectors.toList())
                );

        lenient().when(communityCareTeamHieConsentPolicyUpdateService.isOnHoldCandidate(any(), any())).thenCallRealMethod();

        //todo added as community ctm

        when(clientCareTeamHieConsentPolicyUpdateService.findClientCareTeamEntries(
                eq(removedClientCtm.getEmployeeId()),
                eq(removedClientCtm.getClientId()),
                eq(OnHoldAware.class)
        ))
                .thenReturn(clientCtms.stream()
                        .filter(ctm -> ctm.getClientId().equals(removedClientCtm.getClientId()))
                        .filter(ctm -> ctm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                        .map(ctm -> (OnHoldAware) ctm::getOnHold)
                        .collect(Collectors.toList())
                );

        when(clientHieConsentPolicyUpdateService.existsOptOutClientsInCommunity(removedClientCtm.getClient().getCommunityId()))
                .thenReturn(allClients.stream()
                        .filter(client -> client.getCommunityId().equals(removedClientCtm.getClient().getCommunityId()))
                        .anyMatch(client -> client.getHieConsentPolicyType() == HieConsentPolicyType.OPT_OUT)
                );

        when(clientCareTeamHieConsentPolicyUpdateService.findCurrentCareTeamMembersOfClients(
                TestUtil.sameElementsCollection(List.of(removedClientCtm.getClientId())),
                eq(removedClientCtm.getEmployeeId()),
                eq(EmployeeIdAware.class))
        )
                .thenReturn(
                        clientCtms.stream()
                                .filter(clientCtm -> !clientCtm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                                .filter(clientCtm -> !clientCtm.getOnHold())
                                .filter(clientCtm -> removedClientCtm.getClientId().equals(clientCtm.getClientId()))
                                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                                .collect(Collectors.toList())
                );

        when(communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeCurrent(
                TestUtil.sameElementsCollection(List.of(removedClientCtm.getClient().getCommunityId())),
                eq(removedClientCtm.getEmployeeId()),
                eq(EmployeeIdAware.class))
        )
                .thenReturn(
                        communityCtms.stream()
                                .filter(ctm -> !ctm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                                .filter(ctm -> ctm.getCommunity().getOrganizationId().equals(ctm.getEmployee().getOrganizationId()))
                                .filter(ctm -> removedClientCtm.getClient().getCommunityId().equals(ctm.getCommunityId()))
                                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                                .collect(Collectors.toList())
                );

        when(clientHieConsentPolicyUpdateService.isOptOutPolicy(any())).thenCallRealMethod();

        var optOutClientIdsAmongAffected = Stream.of(removedClientCtm.getClient())
                .filter(client -> client.getHieConsentPolicyType() == HieConsentPolicyType.OPT_OUT)
                .map(BasicEntity::getId)
                .collect(Collectors.toSet());
        var onHoldClientsAmongAffectedOptOut = clientCtms.stream()
                .filter(ClientCareTeamMember::getOnHold)
                .filter(ctm -> ctm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                .filter(ctm -> optOutClientIdsAmongAffected.contains(ctm.getClientId()))
                .map(ctm -> (ClientIdAware) ctm::getClientId)
                .collect(Collectors.toList());

        when(clientCareTeamHieConsentPolicyUpdateService.onHoldAmongClientIds(
                TestUtil.sameElementsCollection(optOutClientIdsAmongAffected),
                eq(removedClientCtm.getEmployeeId()),
                eq(ClientIdAware.class)
        )).thenReturn(onHoldClientsAmongAffectedOptOut);

        var notOnHoldClientsAmongOptOutInCommunity = new HashSet<>(optOutClientIdsAmongAffected);
        notOnHoldClientsAmongOptOutInCommunity.removeAll(onHoldClientsAmongAffectedOptOut.stream().map(ClientIdAware::getClientId).collect(Collectors.toList()));
        when(clientCareTeamHieConsentPolicyUpdateService.findOnHoldCareTeamMembersOfClients(
                TestUtil.sameElementsCollection(notOnHoldClientsAmongOptOutInCommunity),
                eq(removedClientCtm.getEmployeeId()),
                eq(EmployeeIdAware.class)
        )).thenReturn(clientCtms.stream()
                .filter(ClientCareTeamMember::getOnHold)
                .filter(ctm -> !ctm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                .filter(ctm -> notOnHoldClientsAmongOptOutInCommunity.contains(ctm.getClientId()))
                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                .collect(Collectors.toList())
        );

        if (allClients.stream()
                .anyMatch(client -> client.getHieConsentPolicyType() == HieConsentPolicyType.OPT_OUT &&
                        client.getCommunityId().equals(removedClientCtm.getClient().getCommunityId()))) {
            when(communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeOnHold(
                    TestUtil.sameElementsCollection(Set.of(removedClientCtm.getClient().getCommunityId())),
                    eq(removedClientCtm.getEmployeeId()),
                    eq(EmployeeIdAware.class)
            )).thenReturn(communityCtms.stream()
                    .filter(ctm -> !ctm.getEmployee().getOrganizationId().equals(ctm.getCommunity().getOrganizationId()))
                    .filter(ctm -> !ctm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                    .filter(ctm -> ctm.getCommunityId().equals(removedClientCtm.getClient().getCommunityId()))
                    .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                    .collect(Collectors.toList())
            );
        }

        //then
        instance.clientCareTeamMemberDeleted(removedClientCtm.getEmployee(), removedClientCtm.getClient(), removedClientCtm.getOnHold());

        verifyNoMoreInteractions(
                clientHieConsentPolicyUpdateService,
                clientCareTeamHieConsentPolicyUpdateService,
                communityCareTeamHieConsentPolicyUpdateService
        );

        verify(chatHieConsentPolicyUpdateService).contactChangedAsync(
                eq(removedClientCtm.getEmployee()),
                eq(Set.of()),
                TestUtil.sameElementsCollection(Set.of(
                        employee17.getId()
                )),
                TestUtil.sameElementsCollection(Set.of(
                        employee14.getId(),
                        employee21.getId()
                )),
                TestUtil.sameElementsCollection(Set.of(
                        employee18.getId(),
                        employee19.getId(),
                        employee20.getId()
                )),
                any()
        );

    }

    @Test
        //input data schema in confluence https://confluence.scnsoft.com/pages/viewpage.action?pageId=266773946
    void clientCareTeamMemberOnHoldDeleted() {
        //given
        var id = 61L;
        var organization1 = createOrganization(id++);
        var organization2 = createOrganization(id++);

        id = 51L;
        var community1 = createCommunity(id++, organization1);
        var community2 = createCommunity(id++, organization1);
        var community3 = createCommunity(id++, organization2);
        var community4 = createCommunity(id++, organization1);
        var community5 = createCommunity(id++, organization1);

        id = 31L;
        var client1 = createClient(id++, community1, HieConsentPolicyType.OPT_OUT);
        var client2 = createClient(id++, community2, HieConsentPolicyType.OPT_OUT);
        var client3 = createClient(id++, community1, HieConsentPolicyType.OPT_IN);
        var client4 = createClient(id++, community2, HieConsentPolicyType.OPT_OUT);
        var client5 = createClient(id++, community3, HieConsentPolicyType.OPT_OUT);
        var client6 = createClient(id++, community4, HieConsentPolicyType.OPT_OUT);
        var client7 = createClient(id++, community5, HieConsentPolicyType.OPT_IN);
        var client8 = createClient(id++, community1, HieConsentPolicyType.OPT_OUT);
        var allClients = List.of(
                client1, client2, client3, client4, client5, client6, client7, client8
        );

        id = 1L;
        var employee1 = createEmployee(id++, community2);
        var employee2 = createEmployee(id++, community2);
        var employee3 = createEmployee(id++, community2);
        var employee4 = createEmployee(id++, community1);
        var employee5 = createEmployee(id++, community1);
        var employee6 = createEmployee(id++, community1);
        var employee7 = createEmployee(id++, community2);
        var employee8 = createEmployee(id++, community3);
        var employee9 = createEmployee(id++, community4);
        var employee10 = createEmployee(id++, community1);
        var employee11 = createEmployee(id++, community1);
        var employee12 = createEmployee(id++, community1);
        var employee13 = createEmployee(id++, community2);
        var employee14 = createEmployee(id++, community2);
        var employee15 = createEmployee(id++, community2);
        var employee16 = createEmployee(id++, community2);
        var employee17 = createEmployee(id++, community1);
        var employee18 = createEmployee(id++, community1);
        var employee19 = createEmployee(id++, community1);
        var employee20 = createEmployee(id++, community1);
        var employee21 = createEmployee(id++, community3);
        var employee22 = createEmployee(id++, community2);
        var employee23 = createEmployee(id++, community2);


        id = 101L;
        var removedClientCtm = createClientCtm(id++, employee4, client8, true);
        var clientCtms = List.of(
                createClientCtm(id++, employee1, client3, false),
                createClientCtm(id++, employee1, client2, true),

                createClientCtm(id++, employee2, client3, false),
                createClientCtm(id++, employee2, client2, false),

                createClientCtm(id++, employee4, client1, true),
                createClientCtm(id++, employee4, client4, true),
                createClientCtm(id++, employee4, client7, false),
                createClientCtm(id++, employee4, client2, true),

                createClientCtm(id++, employee5, client5, true),

                createClientCtm(id++, employee7, client4, false),
                createClientCtm(id++, employee7, client7, false),

                createClientCtm(id++, employee8, client5, false),
                createClientCtm(id++, employee8, client3, true),

                createClientCtm(id++, employee9, client3, false),

                createClientCtm(id++, employee10, client1, true),
                createClientCtm(id++, employee10, client3, false),

                createClientCtm(id++, employee11, client1, true),

                createClientCtm(id++, employee12, client1, false),

                createClientCtm(id++, employee13, client1, true),

                createClientCtm(id++, employee14, client8, true),

                createClientCtm(id++, employee15, client4, false),

                createClientCtm(id++, employee16, client1, false),

                createClientCtm(id++, employee17, client2, true),

                createClientCtm(id++, employee18, client1, true),

                createClientCtm(id++, employee19, client5, true),

                createClientCtm(id++, employee22, client1, true),
                createClientCtm(id++, employee22, client3, false),

                createClientCtm(id++, employee23, client3, false)
        );

        id = 201L;
        var communityCtms = List.of(
                createCommunityCtm(id++, employee4, community3),
                createCommunityCtm(id++, employee4, community4),

                createCommunityCtm(id++, employee9, community4),

                createCommunityCtm(id++, employee18, community1),

                createCommunityCtm(id++, employee19, community1),

                createCommunityCtm(id++, employee20, community1),

                createCommunityCtm(id++, employee21, community1)
        );

        associateClientAndContact(client1, employee6);
        associateClientAndContact(client3, employee5);
        associateClientAndContact(client4, employee3);
        associateClientAndContact(client8, employee17);


        //when

        when(communityCareTeamHieConsentPolicyUpdateService.communityCareTeamMemberEntries(
                eq(removedClientCtm.getEmployeeId()),
                TestUtil.sameElementsCollection(List.of(removedClientCtm.getClient().getCommunityId())),
                eq(EmployeeOrgIdCommunityOrgIdAware.class)
        ))
                .thenReturn(communityCtms.stream()
                        .filter(ctm -> ctm.getCommunityId().equals(removedClientCtm.getClient().getCommunityId()))
                        .filter(ctm -> ctm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                        .map(this::employeeOrgIdCommunityOrgIdAwareAdapter)
                        .collect(Collectors.toList())
                );

        lenient().when(communityCareTeamHieConsentPolicyUpdateService.isOnHoldCandidate(any(), any())).thenCallRealMethod();

        //todo added as community ctm

        when(clientCareTeamHieConsentPolicyUpdateService.findClientCareTeamEntries(
                eq(removedClientCtm.getEmployeeId()),
                eq(removedClientCtm.getClientId()),
                eq(OnHoldAware.class)
        ))
                .thenReturn(clientCtms.stream()
                        .filter(ctm -> ctm.getClientId().equals(removedClientCtm.getClientId()))
                        .filter(ctm -> ctm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                        .map(ctm -> (OnHoldAware) ctm::getOnHold)
                        .collect(Collectors.toList())
                );

        when(clientHieConsentPolicyUpdateService.existsOptOutClientsInCommunity(removedClientCtm.getClient().getCommunityId()))
                .thenReturn(allClients.stream()
                        .filter(client -> client.getCommunityId().equals(removedClientCtm.getClient().getCommunityId()))
                        .anyMatch(client -> client.getHieConsentPolicyType() == HieConsentPolicyType.OPT_OUT)
                );

        when(clientCareTeamHieConsentPolicyUpdateService.isOnHoldForAnyClientInCommunity(
                eq(removedClientCtm.getEmployeeId()),
                eq(removedClientCtm.getClient().getCommunityId())
        )).thenReturn(
                clientCtms.stream()
                        .filter(ctm -> ctm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                        .filter(ClientCareTeamMember::getOnHold)
                        .anyMatch(ctm -> ctm.getClient().getCommunityId().equals(removedClientCtm.getClient().getCommunityId()))
        );

        var affectedClients = List.of(removedClientCtm.getClient());

        when(clientHieConsentPolicyUpdateService.isOptOutPolicy(any())).thenCallRealMethod();


        when(clientCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfClients(
                TestUtil.sameElementsCollection(CareCoordinationUtils.toIdsSet(affectedClients)),
                eq(removedClientCtm.getEmployeeId()),
                eq(EmployeeIdAware.class))
        )
                .thenReturn(
                        clientCtms.stream()
                                .filter(clientCtm -> !clientCtm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                                .filter(clientCtm -> CareCoordinationUtils.toIdsSet(affectedClients).contains(clientCtm.getClientId()))
                                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                                .collect(Collectors.toList())
                );

        when(communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeCurrent(
                TestUtil.sameElementsCollection(List.of(removedClientCtm.getClient().getCommunityId())),
                eq(removedClientCtm.getEmployeeId()),
                eq(EmployeeIdAware.class))
        )
                .thenReturn(
                        communityCtms.stream()
                                .filter(ctm -> !ctm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                                .filter(ctm -> ctm.getCommunity().getOrganizationId().equals(ctm.getEmployee().getOrganizationId()))
                                .filter(ctm -> removedClientCtm.getClient().getCommunityId().equals(ctm.getCommunityId()))
                                .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                                .collect(Collectors.toList())
                );

        //todo boolean flag
        //boolean isOptOutClientExistInCommunityAndNotOnHoldCtmForAll = false;
        //            if (CollectionUtils.isEmpty(addedAsOnHoldClientCtmClientIds)) {
        //                isOptOutClientExistInCommunityAndNotOnHoldCtmForAll = clientsInCommunity.stream()
        //                        .anyMatch(clientHieConsentPolicyUpdateService::isOptOutPolicy);
        //            }
        if (false) {
            when(communityCareTeamHieConsentPolicyUpdateService.findCareTeamMembersOfCommunitiesWhereCanBeOnHold(
                    TestUtil.sameElementsCollection(List.of(removedClientCtm.getClient().getCommunityId())),
                    eq(removedClientCtm.getEmployeeId()),
                    eq(EmployeeIdAware.class)
            ))
                    .thenReturn(communityCtms.stream()
                            .filter(ctm -> !ctm.getEmployeeId().equals(removedClientCtm.getEmployeeId()))
                            .filter(ctm -> ctm.getCommunityId().equals(removedClientCtm.getClient().getCommunityId()))
                            .filter(ctm -> !ctm.getEmployee().getOrganizationId().equals(ctm.getCommunity().getOrganizationId()))
                            .map(ctm -> (EmployeeIdAware) ctm::getEmployeeId)
                            .collect(Collectors.toList())
                    );
        }

        //then
        instance.clientCareTeamMemberDeleted(removedClientCtm.getEmployee(), removedClientCtm.getClient(), removedClientCtm.getOnHold());

        verifyNoMoreInteractions(
                clientHieConsentPolicyUpdateService,
                clientCareTeamHieConsentPolicyUpdateService,
                communityCareTeamHieConsentPolicyUpdateService
        );
        verify(chatHieConsentPolicyUpdateService).contactChangedAsync(
                eq(removedClientCtm.getEmployee()),
                TestUtil.sameElementsCollection(Set.of(
                        employee17.getId()
                )),
                eq(Set.of()),
                TestUtil.sameElementsCollection(Set.of(
                        employee14.getId(),
                        employee18.getId(),
                        employee19.getId(),
                        employee20.getId()
                )),
                eq(Set.of()),
                any()
        );
    }

    private void associateClientAndContact(Client client, Employee employee) {
        client.setAssociatedEmployee(employee);
        client.setAssociatedEmployeeIds(List.of(employee.getId()));

        employee.setAssociatedClients(List.of(client));
        employee.setAssociatedClientIds(Set.of(client.getId()));
    }

    private Organization createOrganization(Long organizationId) {
        var organization = new Organization();
        organization.setId(organizationId);
        return organization;
    }

    private Community createCommunity(Long communityId, Organization organization) {
        var community = new Community();
        community.setOrganization(organization);
        community.setOrganizationId(organization.getId());
        community.setId(communityId);
        return community;
    }

    private Client createClient(long id, Community community, HieConsentPolicyType consentType) {
        var client = new Client();
        client.setId(id);
        client.setCommunityId(community.getId());
        client.setCommunity(community);
        client.setOrganization(community.getOrganization());
        client.setOrganizationId(community.getOrganizationId());
        client.setHieConsentPolicyType(consentType);
        return client;
    }

    private Employee createEmployee(long id, Community community) {
        var employee = new Employee();
        employee.setId(id);

        employee.setCommunity(community);
        employee.setCommunityId(community.getId());

        employee.setOrganization(community.getOrganization());
        employee.setOrganizationId(community.getOrganizationId());

        return employee;
    }

    private ClientCareTeamMember createClientCtm(Long id, Employee employee, Client client, boolean onHold) {
        var clientCtm = new ClientCareTeamMember();

        clientCtm.setId(id);

        clientCtm.setClient(client);
        clientCtm.setClientId(client.getId());

        clientCtm.setEmployee(employee);
        clientCtm.setEmployeeId(employee.getId());

        clientCtm.setOnHold(onHold);

        return clientCtm;
    }

    private CommunityCareTeamMember createCommunityCtm(Long id, Employee employee, Community community) {
        var communityCtm = new CommunityCareTeamMember();

        communityCtm.setId(id);

        communityCtm.setCommunity(community);
        communityCtm.setCommunityId(community.getId());

        communityCtm.setEmployee(employee);
        communityCtm.setEmployeeId(employee.getId());

        return communityCtm;
    }

    private HieConsentPolicyUpdateServiceImpl.ClientEntityCareTeamUpdateProjection clientEntityCareTeamUpdateProjectionAdapter(Client c) {
        return new HieConsentPolicyUpdateServiceImpl.ClientEntityCareTeamUpdateProjection() {

            @Override
            public Long getId() {
                return c.getId();
            }

            @Override
            public HieConsentPolicyType getHieConsentPolicyType() {
                return c.getHieConsentPolicyType();
            }

            @Override
            public List<Long> getAssociatedEmployeeIds() {
                return c.getAssociatedEmployeeIds();
            }
        };
    }

    private EmployeeOrgIdCommunityOrgIdAware employeeOrgIdCommunityOrgIdAwareAdapter(CommunityCareTeamMember communityCareTeamMember) {
        return new EmployeeOrgIdCommunityOrgIdAware() {
            @Override
            public Long getCommunityOrganizationId() {
                return communityCareTeamMember.getCommunity().getOrganizationId();
            }

            @Override
            public Long getEmployeeOrganizationId() {
                return communityCareTeamMember.getEmployee().getOrganizationId();
            }
        };
    }
}