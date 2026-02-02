package com.scnsoft.eldermark.service.healthpartners.client;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.healthpartner.BaseHealthPartnersRecord;
import com.scnsoft.eldermark.jms.dto.ResidentUpdateType;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.healthpartners.ctx.ClaimProcessingContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HpClaimClientProviderImplTest {

    private static final Long COMMUNITY_ID = 154231L;
    private static final String memberIdentifier = "memberIdentifier";

    @Mock
    private ClientService clientService;

    @Mock
    private HpClientFactory<HpClientInfo> hpClientInfoHpClientFactory;

    @InjectMocks
    private HpClaimClientProviderImpl instance;

    @Test
    void getClient_foundActiveClient_returnsFoundClient() {
        var claim = new BaseHealthPartnersRecord();
        claim.setMemberIdentifier(memberIdentifier);
        var client = new Client();
        client.setActive(true);
        var ctx = new ClaimProcessingContext();


        when(clientService.findHealthPartnersClient(memberIdentifier, COMMUNITY_ID))
                .thenReturn(Optional.of(client));


        var actual = instance.getClient(claim, COMMUNITY_ID, ctx);


        assertFalse(ctx.isClientIsNewHint());
        assertEquals(client, actual);

        verifyNoInteractions(hpClientInfoHpClientFactory);
        verifyNoMoreInteractions(clientService);
    }

    @Test
    void getClient_FoundInactiveClient_activatesAndReturnsFoundClient() {
        var claim = new BaseHealthPartnersRecord();
        claim.setMemberIdentifier(memberIdentifier);
        var inactiveClient = new Client();
        inactiveClient.setActive(false);

        var ctx = new ClaimProcessingContext();

        when(clientService.findHealthPartnersClient(memberIdentifier, COMMUNITY_ID))
                .thenReturn(Optional.of(inactiveClient));

        var actual = instance.getClient(claim, COMMUNITY_ID, ctx);

        assertFalse(ctx.isClientIsNewHint());
        assertEquals(inactiveClient.getId(), actual.getId());
        assertEquals(inactiveClient.getOrganizationId(), actual.getOrganizationId());
        assertTrue(actual.getActive());
        Assertions.assertThat(ctx.getUpdateTypes()).contains(ResidentUpdateType.RESIDENT);

        verifyNoInteractions(hpClientInfoHpClientFactory);
        verify(clientService).activateClient(inactiveClient.getId());
        verifyNoMoreInteractions(clientService);
    }

    @Test
    void getClient_clientNotFound_callsClientFactory() {
        var claim = new BaseHealthPartnersRecord();
        claim.setMemberIdentifier(memberIdentifier);
        claim.setMemberFirstName("test");
        claim.setMemberLastName("name");
        claim.setBirthDate(LocalDate.now());
        var ctx = new ClaimProcessingContext();
        var clientInfo = new HpClientInfo(memberIdentifier,
                claim.getMemberFirstName(),
                claim.getMemberMiddleName(),
                claim.getMemberLastName(),
                claim.getBirthDate(),
                COMMUNITY_ID,
                true);
        var client = new Client();


        when(clientService.findHealthPartnersClient(memberIdentifier, COMMUNITY_ID))
                .thenReturn(Optional.empty());
        when(hpClientInfoHpClientFactory.create(eq(clientInfo))).thenReturn(client);
        when(clientService.save(client)).thenReturn(client);


        var actual = instance.getClient(claim, COMMUNITY_ID, ctx);

        assertEquals(client, actual);
        assertTrue(ctx.isClientIsNewHint());
        Assertions.assertThat(ctx.getUpdateTypes()).contains(ResidentUpdateType.RESIDENT);
    }
}