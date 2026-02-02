package com.scnsoft.eldermark.service.healthpartners.medication;

import com.scnsoft.eldermark.beans.projection.IdOrganizationIdActiveAware;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.MedicationDispense;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.OrganizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HpMedicationDispenseFactoryImplTest {

    @Mock
    private OrganizationService organizationService;

    @Mock
    private CommunityService communityService;

    @InjectMocks
    private HpMedicationDispenseFactoryImpl instance;

    @Test
    void testCreate_ProviderNotFound_CreatesProvider() {
        var client = createClient();
        var clientProjection = createClientProjection(client);
        var claim = createClaim();

        when(organizationService.getOne(clientProjection.getOrganizationId())).thenReturn(client.getOrganization());
        when(communityService.findByHealthPartnersBillingProviderRef(claim.getClaimBillingProvider(), client.getOrganizationId()))
                .thenReturn(Optional.empty());
        doAnswer(returnsFirstArg()).when(communityService).save(any());

        var dispense = instance.create(claim, client);


        verifyMedicationDispense(dispense, client, claim);
        verifyProvider(dispense.getProvider(), client, claim);
    }

    @Test
    void testCreate_ProviderFound_usesFoundProvider() {
        var client = createClient();
        var clientProjection = createClientProjection(client);

        var claim = createClaim();
        var providerId = 44432L;
        var provider = new Community();

        when(organizationService.getOne(clientProjection.getOrganizationId())).thenReturn(client.getOrganization());
        when(communityService.findByHealthPartnersBillingProviderRef(claim.getClaimBillingProvider(), clientProjection.getOrganizationId()))
                .thenReturn(Optional.of(() -> providerId));
        when(communityService.get(providerId)).thenReturn(provider);

        var dispense = instance.create(claim, clientProjection);

        verifyMedicationDispense(dispense, client, claim);
        assertEquals(provider, dispense.getProvider());
    }

    private HealthPartnersRxClaim createClaim() {
        var claim = new HealthPartnersRxClaim();
        claim.setServiceDate(Instant.now());
        claim.setDaysSupply(5);
        claim.setRefillNumber(44);
        claim.setRXNumber("rxNumber");
        claim.setQuantityDispensed(BigDecimal.valueOf(0.5));
        claim.setQuantityQualifierCode("quantity code");
        claim.setPharmacyName("pharmacy name");
        claim.setPharmacyNPI("pharmacy npi");
        claim.setClaimBillingProvider("provider ref");
        return claim;
    }

    private Client createClient() {
        var client = new Client(1L);

        var org = new Organization();
        org.setId(3L);
        client.setOrganization(org);
        client.setOrganizationId(org.getId());

        return client;
    }

    private IdOrganizationIdActiveAware createClientProjection(Client client) {
        return new IdOrganizationIdActiveAware() {
            @Override
            public Boolean getActive() {
                return client.getActive();
            }

            @Override
            public Long getId() {
                return client.getId();
            }

            @Override
            public Long getOrganizationId() {
                return client.getOrganizationId();
            }
        };
    }


    private void verifyMedicationDispense(MedicationDispense dispense, Client client, HealthPartnersRxClaim claim) {
        assertEquals(client.getOrganization(), dispense.getOrganization());
        assertEquals(claim.getServiceDate().toEpochMilli(), dispense.getDispenseDateLow().getTime());
        assertEquals(
                claim.getServiceDate().plus(claim.getDaysSupply(), ChronoUnit.DAYS).toEpochMilli(),
                dispense.getDispenseDateHigh().getTime()
        );

        assertEquals(claim.getRefillNumber(), dispense.getFillNumber());
        assertEquals(claim.getRXNumber(), dispense.getPrescriptionNumber());
        assertEquals(claim.getQuantityDispensed(), dispense.getQuantity());
        assertEquals(claim.getQuantityQualifierCode(), dispense.getQuantityQualifierCode());
    }

    private void verifyProvider(Community provider, Client client, HealthPartnersRxClaim claim) {
        assertEquals(claim.getPharmacyName(), provider.getName());
        assertEquals(claim.getPharmacyNPI(), provider.getProviderNpi());
        assertEquals(client.getOrganization(), provider.getOrganization());
        assertTrue(provider.getModuleHie());
        assertEquals(claim.getClaimBillingProvider(), provider.getHealthPartnersBillingProviderRef());
    }
}