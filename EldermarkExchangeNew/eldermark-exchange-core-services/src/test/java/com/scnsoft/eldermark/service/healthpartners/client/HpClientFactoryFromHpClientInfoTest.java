package com.scnsoft.eldermark.service.healthpartners.client;

import com.scnsoft.eldermark.beans.projection.OrganizationIdAware;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.service.ClientHieConsentDefaultPolicyService;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.OrganizationService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HpClientFactoryFromHpClientInfoTest {
    private static final long COMMUNITY_ID = 1L;
    public static final long ORGANIZATION_ID = 2L;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private CommunityService communityService;

    @Mock
    private ClientHieConsentDefaultPolicyService clientHieConsentDefaultPolicyService;

    @InjectMocks
    HpClientFactoryFromHpClientInfo instance;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testCreate(boolean active) {
        var community = new Community();
        community.setId(COMMUNITY_ID);
        var org = new Organization();
        org.setId(ORGANIZATION_ID);
        community.setOrganization(org);
        community.setOrganizationId(org.getId());
        var first = "first";
        var middle = "middle";
        var last = "last";
        var info = new HpClientInfo("memberIdentifier", first, middle, last, LocalDate.now(), COMMUNITY_ID, active);


        when(communityService.findById(COMMUNITY_ID, OrganizationIdAware.class)).thenReturn(new OrganizationIdAware() {
            @Override
            public Long getOrganizationId() {
                return ORGANIZATION_ID;
            }
        });

        when(organizationService.getOne(ORGANIZATION_ID)).thenReturn(org);
        when(communityService.get(COMMUNITY_ID)).thenReturn(community);

        var client = instance.create(info);

        assertEquals(community.getOrganization(), client.getOrganization());
        assertEquals(community, client.getCommunity());
        assertEquals(info.getBirthDate(), client.getBirthDate());
        assertEquals(first, client.getFirstName());
        assertEquals(middle, client.getMiddleName());
        assertEquals(last, client.getLastName());
        assertEquals(active, client.getActive());

        var person = client.getPerson();
        assertNotNull(client.getPerson());
        assertEquals(community.getOrganization(), person.getOrganization());
        Assertions.assertThat(person.getNames()).hasSize(1);

        var name = person.getNames().get(0);
        assertEquals(first, name.getGiven());
        assertEquals(last, name.getFamily());
        assertEquals(middle, name.getMiddle());

        verify(clientHieConsentDefaultPolicyService).fillDefaultPolicy(client, HpClientFactoryFromHpClientInfo.POLICY_OBTAINED_FROM_CONTRACT_VALUE);
    }
}