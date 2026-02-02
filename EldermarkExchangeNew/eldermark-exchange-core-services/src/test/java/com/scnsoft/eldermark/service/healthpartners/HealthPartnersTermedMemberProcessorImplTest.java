package com.scnsoft.eldermark.service.healthpartners;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersTermedMember;
import com.scnsoft.eldermark.jms.dto.ResidentUpdateType;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.healthpartners.client.HpClientFactory;
import com.scnsoft.eldermark.service.healthpartners.client.HpClientInfo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthPartnersTermedMemberProcessorImplTest {

    private static final Long COMMUNITY_ID = 154231L;

    @Mock
    private ClientService clientService;

    @Mock
    private HpClientFactory<HpClientInfo> hpClientInfoHpClientFactory;

    @Captor
    private ArgumentCaptor<Set<ResidentUpdateType>> resudentUpdateTypesCaptor;

    @Captor
    private ArgumentCaptor<HpClientInfo> hpClientInfoArgumentCaptor;

    @InjectMocks
    private HealthPartnersTermedMemberProcessorImpl instance;

    @Test
    void process_findClientThrows_successIsFalse() {
        var termedMember = createClaim();
        var errorMsg = "find client exception";

        doThrow(new RuntimeException(errorMsg)).when(clientService).findHealthPartnersClient(
                termedMember.getMemberIdentifier(),
                COMMUNITY_ID
        );
        instance.process(termedMember, COMMUNITY_ID);

        assertFalse(termedMember.isSuccess());
        assertNotNull(termedMember.getErrorMessage());

        verifyNoInteractions(hpClientInfoHpClientFactory);
        verifyNoMoreInteractions(clientService);
    }

    @Test
    void process_findsInactiveClient_returnsFoundClient() {
        var termedMember = createClaim();
        var inactiveClient = createClient(false);

        when(clientService.findHealthPartnersClient(termedMember.getMemberIdentifier(), COMMUNITY_ID))
                .thenReturn(Optional.of(inactiveClient));
        when(clientService.getById(inactiveClient.getId())).thenReturn(inactiveClient);

        instance.process(termedMember, COMMUNITY_ID);

        assertTrue(termedMember.isSuccess());
        assertNull(termedMember.getErrorMessage());
        assertFalse(termedMember.getClientIsNew());
        assertEquals(inactiveClient, termedMember.getClient());
        assertEquals(inactiveClient.getId(), termedMember.getClientId());

        Assertions.assertThat(termedMember.getUpdateTypes()).isEmpty();

        verifyNoInteractions(hpClientInfoHpClientFactory);
        verifyNoMoreInteractions(clientService);
    }

    @Test
    void process_findsActiveClient_returnsDeactivatedFoundClient() {
        var termedMember = createClaim();
        var activeClient = createClient(true);
        var savedClient = createClient(false);

        when(clientService.findHealthPartnersClient(termedMember.getMemberIdentifier(), COMMUNITY_ID))
                .thenReturn(Optional.of(activeClient));
        when(clientService.getById(activeClient.getId())).thenReturn(savedClient);

        instance.process(termedMember, COMMUNITY_ID);

        assertTrue(termedMember.isSuccess());
        assertNull(termedMember.getErrorMessage());
        assertFalse(termedMember.getClientIsNew());

        assertEquals(savedClient, termedMember.getClient());
        assertEquals(savedClient.getId(), termedMember.getClientId());

        Assertions.assertThat(termedMember.getUpdateTypes()).containsOnly(ResidentUpdateType.RESIDENT);

        verifyNoInteractions(hpClientInfoHpClientFactory);
        verify(clientService).deactivateClient(activeClient.getId());

        verifyNoMoreInteractions(clientService);
    }

    @Test
    void process_clientNotFound_callsClientFactory() {
        var termedMember = createClaim();
        var createdClient = createClient(false);

        when(clientService.findHealthPartnersClient(termedMember.getMemberIdentifier(), COMMUNITY_ID))
                .thenReturn(Optional.empty());
        when(hpClientInfoHpClientFactory.create(hpClientInfoArgumentCaptor.capture()))
                .thenReturn(createdClient);
        when(clientService.save(createdClient)).thenReturn(createdClient);

        instance.process(termedMember, COMMUNITY_ID);


        assertTrue(termedMember.isSuccess());
        assertNull(termedMember.getErrorMessage());
        assertTrue(termedMember.getClientIsNew());

        assertEquals(termedMember.getMemberIdentifier(), hpClientInfoArgumentCaptor.getValue().getMemberIdentifier());
        assertEquals(termedMember.getMemberFirstName(), hpClientInfoArgumentCaptor.getValue().getMemberFirstName());
        assertEquals(termedMember.getMemberMiddleName(), hpClientInfoArgumentCaptor.getValue().getMemberMiddleName());
        assertEquals(termedMember.getMemberLastName(), hpClientInfoArgumentCaptor.getValue().getMemberLastName());
        assertEquals(termedMember.getBirthDate(), hpClientInfoArgumentCaptor.getValue().getBirthDate());
        assertEquals(COMMUNITY_ID, hpClientInfoArgumentCaptor.getValue().getCommunityId());
        assertFalse(hpClientInfoArgumentCaptor.getValue().isActive());

        assertEquals(createdClient, termedMember.getClient());
        assertEquals(createdClient.getId(), termedMember.getClientId());

        Assertions.assertThat(termedMember.getUpdateTypes()).containsOnly(ResidentUpdateType.RESIDENT);
    }


    private HealthPartnersTermedMember createClaim() {
        var termedMember = new HealthPartnersTermedMember();
        termedMember.setMemberIdentifier("memberIdentifier");
        termedMember.setMemberFirstName("firstName");
        termedMember.setMemberMiddleName("memberName");
        termedMember.setMemberLastName("lastName");
        termedMember.setBirthDate(LocalDate.of(2019, 5, 24));
        return termedMember;
    }


    private Client createClient(boolean active) {
        var client = new Client(1L);
        client.setActive(active);
        return client;
    }
}